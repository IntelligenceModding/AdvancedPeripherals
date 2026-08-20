package de.srendi.advancedperipherals.common.blocks.blockentities;

import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.SmartRailPeripheral;
import de.srendi.advancedperipherals.common.blocks.SmartRailBlock;
import de.srendi.advancedperipherals.common.blocks.base.VarNameable;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.lib.peripherals.DisabledPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmartRailBlockEntity extends BlockEntity implements IPeripheralBlockEntity, VarNameable {
    private static final String PERIPHERAL_SETTINGS_KEY = "peripheralSettings";
    private static final String ACTIVATING_KEY = "activating";
    private static final String STATE_KEY = "state";
    protected CompoundTag peripheralSettings = new CompoundTag();
    private LazyOptional<IPeripheral> peripheralCap = LazyOptional.empty();
    private Component name = null;

    private volatile boolean activating = false;
    private volatile SmartRailBlock.RailPoweredState state = SmartRailBlock.RailPoweredState.STOP;

    public SmartRailBlockEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.SMART_RAIL.get(), pos, state);
    }

    @Override
    public Component getName() {
        return this.name;
    }

    @Override
    public Component getCustomName() {
        return this.getName();
    }

    @Override
    public void setName(Component name) {
        this.name = name;
        this.setChanged();
    }

    public boolean isActivating() {
        return this.activating;
    }

    public void setActivating(boolean activating) {
        if (this.activating == activating) {
            return;
        }
        this.activating = activating;
        this.setChanged();
        this.queueRefreshPoweredState();
    }

    public SmartRailBlock.RailPoweredState getState() {
        return this.state;
    }

    public void setState(SmartRailBlock.RailPoweredState state) {
        if (this.state == state) {
            return;
        }
        this.state = state;
        this.setChanged();
        this.queueRefreshPoweredState();
    }

    protected void queueRefreshPoweredState() {
        this.getLevel().getServer().execute(
            () -> this.getLevel().setBlock(
                this.getBlockPos(),
                this.getBlockState()
                    .setValue(BlockStateProperties.POWERED, this.activating || this.state != SmartRailBlock.RailPoweredState.STOP),
                Block.UPDATE_ALL
            )
        );
    }

    @NotNull
    public LazyOptional<IPeripheral> getLazyPeripheral() {
        if (!this.peripheralCap.isPresent()) {
            this.peripheralCap = LazyOptional.of(this::createPeripheralOrDisabled);
        }
        return this.peripheralCap;
    }

    @NotNull
    protected SmartRailPeripheral buildPeripheral() {
        return new SmartRailPeripheral(this);
    }

    protected IPeripheral createPeripheralOrDisabled() {
        SmartRailPeripheral peripheral = this.buildPeripheral();
        return peripheral.isEnabled() ? peripheral : new DisabledPeripheral(peripheral);
    }

    public void queueEvent(String event, Object... args) {
        if (this.getLevel().isClientSide()) {
            return;
        }
        SmartRailPeripheral peripheral = this.getPeripheral();
        if (peripheral != null) {
            peripheral.queueEvent(event, args);
        }
    }

    @Nullable
    public SmartRailPeripheral getPeripheral() {
        IPeripheral peripheral = this.getLazyPeripheral().orElse(null);
        if (peripheral == null || peripheral instanceof DisabledPeripheral) {
            return null;
        }
        return (SmartRailPeripheral) peripheral;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.peripheralSettings = tag.getCompound(PERIPHERAL_SETTINGS_KEY);
        this.activating = tag.getBoolean(ACTIVATING_KEY);
        this.state = SmartRailBlock.RailPoweredState.values()[tag.getByte(STATE_KEY)];
        if (tag.contains("CustomName", Tag.TAG_STRING)) {
            this.name = Component.Serializer.fromJson(tag.getString("CustomName"));
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (!this.peripheralSettings.isEmpty()) {
            tag.put(PERIPHERAL_SETTINGS_KEY, peripheralSettings);
        }
        tag.putBoolean(ACTIVATING_KEY, this.activating);
        tag.putByte(STATE_KEY, (byte) this.state.ordinal());
        if (this.name != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.name));
        }
    }

    @Override
    public CompoundTag getPeripheralSettings() {
        return this.peripheralSettings;
    }

    @Override
    public void setPeripheralSettings(CompoundTag tag) {
        this.peripheralSettings = tag;
        this.markSettingsChanged();
    }

    @Override
    public void markSettingsChanged() {
        this.setChanged();
    }

    @Override
    public <U extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<U> type) {
        if (level.isClientSide()) {
            return;
        }
        SmartRailPeripheral peripheral = this.getPeripheral();
        if (peripheral != null) {
            peripheral.update();
        }
    }

    public Vec3 getBottomCenter() {
        return Vec3.atBottomCenterOf(this.getBlockPos());
    }

    public void collectCarts(List<? super AbstractMinecart> carts) {
        this.getLevel().getEntities(EntityTypeTest.forClass(AbstractMinecart.class), this.getSearchBox(), AbstractMinecart::isAlive, carts, 64);
    }

    protected AABB getSearchBox() {
        double shrinkRange = 0.2;
        BlockPos pos = this.getBlockPos();
        return new AABB(
            pos.getX() + shrinkRange,
            pos.getY(),
            pos.getZ() + shrinkRange,
            pos.getX() + 1 - shrinkRange,
            pos.getY() + 1 - shrinkRange,
            pos.getZ() + 1 - shrinkRange
        );
    }
}
