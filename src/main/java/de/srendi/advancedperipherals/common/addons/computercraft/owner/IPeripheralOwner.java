package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;

public interface IPeripheralOwner {

    @Nullable String getCustomName();

    @Nullable Level getLevel();

    @NotNull BlockPos getPos();

    @NotNull Direction getFacing();

    @NotNull FrontAndTop getOrientation();

    @Nullable Player getOwner();

    @NotNull CompoundTag getDataStorage();

    void markDataStorageDirty();

    <T> T withPlayer(Function<APFakePlayer, T> function);

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
}
