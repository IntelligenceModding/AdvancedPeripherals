package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PocketPeripheralOwner extends BasePeripheralOwner {
    private final IPocketAccess pocket;
    private final IPocketUpgrade upgrade;

    public PocketPeripheralOwner(IPocketAccess pocket, IPocketUpgrade upgrade) {
        super();
        this.pocket = pocket;
        this.upgrade = upgrade;
        if (APConfig.PERIPHERALS_CONFIG.disablePocketFuelConsumption.get()) {
            attachAbility(PeripheralOwnerAbility.FUEL, new InfinitePocketFuelAbility(this));
        }
    }

    @Nullable
    @Override
    public String getCustomName() {
        return null;
    }

    @Nullable
    @Override
    public Level getLevel() {
        Entity owner = pocket.getEntity();
        return owner == null ? null : owner.getCommandSenderWorld();
    }

    @NotNull
    @Override
    public BlockPos getPos() {
        Entity owner = pocket.getEntity();
        return owner == null ? BlockPos.ZERO : new BlockPos(owner.getEyePosition());
    }

    @NotNull
    @Override
    public Vec3 getCenterPos() {
        Entity owner = pocket.getEntity();
        return owner == null ? Vec3.ZERO : owner.getEyePosition();
    }

    @NotNull
    @Override
    public Direction getFacing() {
        Vec3 dir = getDirection();
        return Direction.getNearest(dir.x, dir.y, dir.z);
    }

    @NotNull
    @Override
    public FrontAndTop getOrientation() {
        Entity owner = pocket.getEntity();
        if (owner == null) {
            return FrontAndTop.NORTH_UP;
        }
        Vec3 up = owner.getUpVector(1.0f);
        return FrontAndTop.fromFrontAndTop(getFacing(), Direction.getNearest(up.x, up.y, up.z));
    }

    @NotNull
    @Override
    public Vec3 getDirection() {
        Entity owner = pocket.getEntity();
        return owner == null ? /* North */ new Vec3(0, 0, -1) : owner.getLookAngle();
    }

    @Nullable
    @Override
    public Entity getHoldingEntity() {
        return pocket.getEntity();
    }

    @NotNull
    @Override
    public CompoundTag getDataStorage() {
        return DataStorageUtil.getDataStorage(pocket, upgrade);
    }

    @Override
    public void markDataStorageDirty() {
        pocket.updateUpgradeNBTData();
    }

    @Override
    public <T> T withPlayer(APFakePlayer.Action<T> function) {
        throw new NotImplementedException();
    }

    @Override
    public ItemStack getToolInMainHand() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack storeItem(ItemStack stored) {
        throw new NotImplementedException();
    }

    @Override
    public void destroyUpgrade() {
        throw new NotImplementedException();
    }

    @Override
    public boolean isMovementPossible(@NotNull Level level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public boolean move(@NotNull Level level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public <T extends IPeripheral> T getConnectedPeripheral(Class<T> type) {
        IPeripheral foundPeripheral = pocket.getUpgrades().values().stream()
            .filter(peripheral -> {
                if (peripheral == null || type.isInstance(peripheral)) {
                    return false;
                }
                return peripheral instanceof IBasePeripheral basePeripheral ? basePeripheral.isEnabled() : true;
            })
            .findFirst()
            .orElse(null);
        return (T) foundPeripheral;
    }
}
