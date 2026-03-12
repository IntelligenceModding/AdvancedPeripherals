package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.pocket.core.PocketBrain;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PocketPeripheralOwner extends BasePeripheralOwner {
    private final IPocketAccess pocket;

    public PocketPeripheralOwner(IPocketAccess pocket) {
        super();
        this.pocket = pocket;
        if(APConfig.PERIPHERALS_CONFIG.disablePocketFuelConsumption.get())
            attachAbility(PeripheralOwnerAbility.FUEL, new InfinitePocketFuelAbility(this));
    }

    @Nullable
    @Override
    public String getCustomName() {
        return null;
    }

    @Nullable
    @Override
    public Level getLevel() {
        // TODO: Certain version of CC will make pocket computer has null level while changing dimensions.
        // Not sure if this is fixed in later CC so bunch of null checks can be removed. :3
        return pocket.getLevel();
    }

    @NotNull
    @Override
    public BlockPos getPos() {
        return BlockPos.containing(pocket.getPosition());
    }

    @NotNull
    @Override
    public Vec3 getCenterPos() {
        return pocket.getPosition();
    }

    @NotNull
    @Override
    public Vec3 getDirection() {
        Entity owner = pocket.getEntity();
        return owner == null ? /* North */ new Vec3(0, 0, -1) : owner.getLookAngle();
    }

    @NotNull
    @Override
    public Direction getFacing() {
        Entity owner = pocket.getEntity();
        return owner == null ? Direction.NORTH : owner.getDirection();
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

    @Nullable
    @Override
    public Entity getHoldingEntity() {
        return pocket.getEntity();
    }

    @Override
    public DataComponentPatch getDataStorage() {
        return DataStorageUtil.getDataStorage(pocket);
    }

    @Override
    public CompoundTag getNbtStorage() {
        AdvancedPeripherals.debug("Pocket peripheral at " + getPos() + " tried to use nbt storage but it should instead use data component storage, report to github!", org.apache.logging.log4j.Level.WARN);
        return null;
    }

    @Override
    public void putDataStorage(DataComponentPatch dataStorage) {
        DataStorageUtil.putDataStorage(pocket, dataStorage);
    }

    @Override
    public void markDataStorageDirty() {
    }

    @Override
    public <T> T withPlayer(APFakePlayer.Action<T> function) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public ItemStack getToolInMainHand() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack storeItem(ItemStack stored) {
        // Tricks with inventory needed
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void destroyUpgrade() {
        throw new RuntimeException("Not implemented yet");
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
        if (pocket instanceof PocketBrain pocketBrain) {
            for (ComputerSide side : ComputerSide.values()) {
                IPeripheral peripheral = pocketBrain.computer().getPeripheral(side);
                if (peripheral == null || !type.isInstance(peripheral)) {
                    continue;
                }
                if (peripheral instanceof IBasePeripheral basePeripheral && !basePeripheral.isEnabled()) {
                    continue;
                }
                return (T) peripheral;
            }
            return null;
        }
        return null;
    }
}
