package de.srendi.advancedperipherals.common.blocks.blockentities;

import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.SmartRailPeripheral;
import de.srendi.advancedperipherals.common.blocks.SmartRailBlock;
import de.srendi.advancedperipherals.common.blocks.base.BlockCapabilityProviders;
import de.srendi.advancedperipherals.common.blocks.base.VarNameable;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.lib.peripherals.DisabledPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmartRailBlockEntity extends BlockEntity implements IPeripheralBlockEntity, BlockCapabilityProviders.Peripheral, VarNameable {
    private static final String PERIPHERAL_SETTINGS_KEY = "peripheralSettings";
    private static final String ACTIVATING_KEY = "activating";
    private static final String STATE_KEY = "state";
    protected CompoundTag peripheralSettings = new CompoundTag();
    private IPeripheral peripheral = null;
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

    @Override
    @Nullable
    public IPeripheral createPeripheralCap(@Nullable Direction side) {
        if (this.peripheral == null) {
            this.peripheral = this.createPeripheralOrDisabled();
        }
        return this.peripheral;
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
        IPeripheral peripheral = this.createPeripheralCap(null);
        if (peripheral == null || peripheral instanceof DisabledPeripheral) {
            return null;
        }
        return (SmartRailPeripheral) peripheral;
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.peripheralSettings = tag.getCompound(PERIPHERAL_SETTINGS_KEY);
        this.activating = tag.getBoolean(ACTIVATING_KEY);
        this.state = SmartRailBlock.RailPoweredState.values()[tag.getByte(STATE_KEY)];
        if (tag.contains("CustomName", Tag.TAG_STRING)) {
            this.name = BlockEntity.parseCustomNameSafe(tag.getString("CustomName"), provider);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (!this.peripheralSettings.isEmpty()) {
            tag.put(PERIPHERAL_SETTINGS_KEY, peripheralSettings);
        }
        tag.putBoolean(ACTIVATING_KEY, this.activating);
        tag.putByte(STATE_KEY, (byte) this.state.ordinal());
        if (this.name != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.name, provider));
        }
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput components) {
        super.applyImplicitComponents(components);
        this.name = components.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, this.name);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove("CustomName");
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
