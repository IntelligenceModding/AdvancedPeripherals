package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.valkyrienskies.ValkyrienSkies;
import de.srendi.advancedperipherals.common.util.StringUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface IPeripheralOwner {

    @Nullable
    default String getCustomName() {
        Component name = Component.Serializer.fromJson(this.getDataStorage().getString("CustomName"));
        if (name == null) {
            return null;
        }
        return name.getString();
    }

    default void setCustomName(String name) {
        name = StringUtil.validateName(name);
        CompoundTag data = this.getDataStorage();
        if (name == null || name.isEmpty()) {
            data.remove("CustomName");
        } else {
            data.putString("CustomName", Component.Serializer.toJson(Component.literal(name)));
        }
        this.putDataStorage(data);
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
        if (!APAddon.VALKYRIENSKIES.isLoaded()) {
            return pos;
        }
        return ValkyrienSkies.transformToWorldPos(getLevel(), getPos(), pos);
    }

    @NotNull
    default Vec3 getDirection() {
        Vec3 dir = Vec3.atLowerCornerOf(getFacing().getNormal());
        if (!APAddon.VALKYRIENSKIES.isLoaded()) {
            return dir;
        }
        return ValkyrienSkies.transformToWorldDir(getLevel(), getPos(), dir);
    }

    @NotNull Direction getFacing();

    @NotNull FrontAndTop getFrontAndTop();

    /**
     * default implementation respect CC:Tweaked turtle's relative direction rule:
     * <pre>{@code
     * |      BACK
     * | RIGHT O LEFT
     * |     FRONT
     * }</pre>
     *
     * Computer-like block's should follow different rule:
     * <pre>{@code
     * |     BACK
     * | LEFT O RIGHT
     * |    FRONT
     * }</pre>
     *
     * The behaviour can be fixed for computer-like blocks by simply overriding this method and get the opposite direction:
     * <pre>{@code
     * @Override
     * public Direction getRightDirection() {
     *     return [IPeripheralOwner.]super.getRightDirection().getOpposite();
     * }
     * }</pre>
     *
     * @return the relative right direction based on front and top
     */
    @NotNull
    default Direction getRightDirection() {
        return switch (this.getFrontAndTop()) {
            case DOWN_EAST -> Direction.SOUTH;
            case DOWN_NORTH -> Direction.EAST;
            case DOWN_SOUTH -> Direction.WEST;
            case DOWN_WEST -> Direction.NORTH;
            case UP_EAST -> Direction.NORTH;
            case UP_NORTH -> Direction.WEST;
            case UP_SOUTH -> Direction.EAST;
            case UP_WEST -> Direction.SOUTH;
            case WEST_UP -> Direction.NORTH;
            case EAST_UP -> Direction.SOUTH;
            case NORTH_UP -> Direction.EAST;
            case SOUTH_UP -> Direction.WEST;
        };
    }

    @NotNull
    default Matrix3dc getOrientation() {
        FrontAndTop fat = this.getFrontAndTop();
        Direction front = fat.front();
        Direction up = fat.top();
        Direction right = this.getRightDirection();
        int fx = front.getStepX(), fy = front.getStepY(), fz = front.getStepZ();
        int ux = up.getStepX(), uy = up.getStepY(), uz = up.getStepZ();
        int rx = right.getStepX(), ry = right.getStepY(), rz = right.getStepZ();
        return new Matrix3d(
            rx, ry, rz,
            ux, uy, uz,
            fx, fy, fz
        );
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

    CompoundTag getDataStorage();

    void putDataStorage(CompoundTag dataStorage);

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
