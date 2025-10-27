package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.APAddons;
import de.srendi.advancedperipherals.common.addons.valkyrienskies.ValkyrienSkies;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IPeripheralOwner {

    @Nullable String getCustomName();

    @Nullable Level getLevel();

    @NotNull BlockPos getPos();

    @NotNull
    default Vec3 getCenterPos() {
        return Vec3.atCenterOf(getPos());
    }

    @NotNull Direction getFacing();

    @NotNull FrontAndTop getOrientation();

    @NotNull
    default Vec3 getDirection() {
        Vec3 dir = Vec3.atLowerCornerOf(getFacing().getNormal());
        /*if (!APAddon. {
            return dir;
        }
        return ValkyrienSkies.transformToWorldDir(getLevel(), getPos(), dir);*/
        return dir;
    }

    @Nullable Entity getHoldingEntity();

    @Nullable
    default Player getOwner() {
        Entity owner = getHoldingEntity();
        Set<Entity> checked = new HashSet<>();
        while (owner != null && checked.add(owner)) {
            if (owner instanceof Player player) {
                return player;
            }
            if (!(owner instanceof OwnableEntity ownable)) {
                break;
            }
            owner = ownable.getOwner();
        }
        return null;
    }

    DataComponentPatch getDataStorage();

    // Not everything from MC uses the new data component system, so we provide a nbt data storage too
    CompoundTag getNbtStorage();

    void putDataStorage(DataComponentPatch dataStorage);

    void markDataStorageDirty();

    <T> T withPlayer(APFakePlayer.Action<T> function);

    ItemStack getToolInMainHand();

    ItemStack storeItem(ItemStack stored);

    void destroyUpgrade();

    boolean isMovementPossible(@NotNull Level level, @NotNull BlockPos pos);

    boolean move(@NotNull Level level, @NotNull BlockPos pos);

    <T extends IOwnerAbility> void attachAbility(PeripheralOwnerAbility<T> ability, T abilityImplementation);

    @Nullable <T extends IOwnerAbility> T getAbility(PeripheralOwnerAbility<T> ability);

    Collection<IOwnerAbility> getAbilities();

    default void attachOperation(IPeripheralOperation<?>... operations) {
        OperationAbility operationAbility = new OperationAbility(this);
        attachAbility(PeripheralOwnerAbility.OPERATION, operationAbility);
        for (IPeripheralOperation<?> operation : operations)
            operationAbility.registerOperation(operation);
    }

    default void attachOperation(Collection<IPeripheralOperation<?>> operations) {
        OperationAbility operationAbility = new OperationAbility(this);
        attachAbility(PeripheralOwnerAbility.OPERATION, operationAbility);
        for (IPeripheralOperation<?> operation : operations)
            operationAbility.registerOperation(operation);
    }

    <T extends IPeripheral> T getConnectedPeripheral(Class<T> type);

    default boolean hasConnectedPeripheral(Class<? extends IPeripheral> type) {
        return getConnectedPeripheral(type) != null;
    }
}
