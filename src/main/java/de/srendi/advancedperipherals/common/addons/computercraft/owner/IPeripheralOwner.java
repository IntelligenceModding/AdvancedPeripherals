package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.util.StringUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public interface IPeripheralOwner {

    @Nullable
    default String getCustomName() {
        Optional<? extends Component> component = this.getDataStorage().get(DataComponents.CUSTOM_NAME);
        if (component == null || !component.isPresent()) {
            return null;
        }
        return component.get().getString();
    }

    default void setCustomName(String name) {
        name = StringUtil.validateName(name);
        PatchedDataComponentMap data = this.getPatchedDataStorage();
        if (name == null || name.isEmpty()) {
            data.remove(DataComponents.CUSTOM_NAME);
        } else {
            data.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        }
        this.putDataStorage(data.asPatch());
    }

    @NotNull Level getLevel();

    @NotNull BlockPos getPos();

    @NotNull
    default Vec3 getCenterPos() {
        return getPos().getCenter();
    }

    @NotNull
    default Vec3 getPhysicsPos() {
        Vec3 pos = this.getCenterPos();
        return pos;
        // if (!APAddons.vs2Loaded) {
        //     return pos;
        // }
        // return ValkyrienSkies.transformToWorldPos(getLevel(), getPos(), pos);
    }

    @NotNull
    default Vec3 getDirection() {
        Vec3 dir = Vec3.atLowerCornerOf(getFacing().getNormal());
        return dir;
        // if (!APAddons.vs2Loaded) {
        //     return dir;
        // }
        // return ValkyrienSkies.transformToWorldDir(getLevel(), getPos(), dir);
    }

    @NotNull Direction getFacing();

    @NotNull FrontAndTop getFrontAndTop();

    @NotNull
    default Quaterniondc getOrientation() {
        FrontAndTop fat = this.getFrontAndTop();
        Direction front = fat.front();
        Direction up = fat.top();
        int fx = front.getStepX(), fy = front.getStepY(), fz = front.getStepZ();
        int ux = up.getStepX(), uy = up.getStepY(), uz = up.getStepZ();
        int rx = fy * uz - fz * uy, ry = fz * ux - fx * uz, rz = fx * uy - fy * ux;
        return new Quaterniond().setFromNormalized(new Matrix3f(
            rx, ux, -fx,
            ry, uy, -fy,
            rz, uz, -fz
        ));
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

    default PatchedDataComponentMap getPatchedDataStorage() {
        return this.getPatchedDataStorage(DataComponentMap.EMPTY);
    }

    default PatchedDataComponentMap getPatchedDataStorage(DataComponentMap defaults) {
        return PatchedDataComponentMap.fromPatch(defaults, this.getDataStorage());
    }

    void putDataStorage(DataComponentPatch dataStorage);

    <T> T withPlayer(APFakePlayer.Action<T> function) throws LuaException;

    ItemStack getToolInMainHand();

    ItemStack storeItem(ItemStack stored);

    void destroyUpgrade();

    default boolean isMovementPossible(@NotNull Level level, @NotNull BlockPos pos) {
        return false;
    }

    default boolean move(@NotNull Level level, @NotNull BlockPos pos) {
        return false;
    }

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

    @Nullable
    <T extends IPeripheral> T getConnectedPeripheral(Class<T> type);

    default boolean hasConnectedPeripheral(Class<? extends IPeripheral> type) {
        return getConnectedPeripheral(type) != null;
    }
}
