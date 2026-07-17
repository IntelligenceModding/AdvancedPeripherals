package de.srendi.advancedperipherals.common.blocks.blockentities;

import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.SmartRailPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BlockCapabilityProviders;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.lib.peripherals.DisabledPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmartRailBlockEntity extends BlockEntity implements IPeripheralBlockEntity, BlockCapabilityProviders.Peripheral {
    private static final String PERIPHERAL_SETTINGS_KEY = "peripheralSettings";
    protected CompoundTag peripheralSettings = new CompoundTag();
    private IPeripheral peripheral = null;

    public SmartRailBlockEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.SMART_RAIL.get(), pos, state);
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
        this.peripheralSettings = tag.getCompound(PERIPHERAL_SETTINGS_KEY);
        super.loadAdditional(tag, provider);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (!this.peripheralSettings.isEmpty()) {
            tag.put(PERIPHERAL_SETTINGS_KEY, peripheralSettings);
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
