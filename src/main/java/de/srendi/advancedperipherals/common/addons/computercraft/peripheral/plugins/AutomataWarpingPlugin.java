package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperationContext;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.FuelAbility;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.PeripheralOwnerAbility;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.NBTUtil;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.ServerWorker;
import de.srendi.advancedperipherals.common.entity.TurtleEnderPearl;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.WARP;
import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.PREPARE_PORTAL;
import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.ACTIVE_PORTAL;

public class AutomataWarpingPlugin extends AutomataCorePlugin {

    private static final String POINT_DATA_MARK = "warp_points";

    private final Map<String, TurtleEnderPearl> shipPearls = new HashMap<>();

    public AutomataWarpingPlugin(AutomataCorePeripheral automataCore) {
        super(automataCore);
    }

    @Override
    public @Nullable IPeripheralOperation<?>[] getOperations() {
        return new IPeripheralOperation[]{WARP, PREPARE_PORTAL, ACTIVE_PORTAL};
    }

    @NotNull
    protected Pair<MethodResult, CompoundTag> getPointData() {
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        CompoundTag settings = owner.getDataStorage();
        if (!settings.contains(POINT_DATA_MARK)) {
            settings.put(POINT_DATA_MARK, new CompoundTag());
        }

        return Pair.onlyRight(settings.getCompound(POINT_DATA_MARK));
    }

    protected Pair<MethodResult, CompoundTag> getPoint(String name) {
        Pair<MethodResult, CompoundTag> res = getPointData();
        if (res.leftPresent()) {
            return Pair.onlyLeft(res.getLeft());
        }
        CompoundTag points = res.getRight();
        if (!points.contains(name)) {
            return Pair.onlyLeft(MethodResult.of(null, "Warp point not exists"));
        }
        return Pair.onlyRight(points.getCompound(name));
    }

    private SingleOperationContext getWarpContext(Level level, BlockPos pos) {
        return level == automataCore.getPeripheralOwner().getLevel() ? automataCore.toDistance(pos) : new SingleOperationContext(getCostsToLevel(level), 1);
    }

    private int getWarpCost(SingleOperationContext context) {
        FuelAbility<?> fuelAbility = automataCore.getPeripheralOwner().getAbility(PeripheralOwnerAbility.FUEL);
        Objects.requireNonNull(fuelAbility);
        return WARP.getCost(context) * fuelAbility.getFuelConsumptionMultiply();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult savePoint(String name) {
        automataCore.addRotationCycle();
        Pair<MethodResult, CompoundTag> pairData = getPointData();
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }

        CompoundTag data = pairData.getRight();
        if (data.getAllKeys().size() >= APConfig.METAPHYSICS_CONFIG.endAutomataCoreWarpPointLimit.get()) {
            return MethodResult.of(null, "Cannot add new point, limit reached");
        }

        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        Level level = owner.getLevel();
        data.put(name, NBTUtil.toNBT(level, owner.getPos()));
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult deletePoint(String name) {
        automataCore.addRotationCycle();
        Pair<MethodResult, CompoundTag> pairData = getPointData();
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }

        CompoundTag data = pairData.getRight();
        if (!data.contains(name)) {
            return MethodResult.of(null, "Cannot find point to delete");
        }

        data.remove(name);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult points() {
        Pair<MethodResult, CompoundTag> pairData = getPointData();
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }

        CompoundTag data = pairData.getRight();
        return MethodResult.of(data.getAllKeys());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult warpToPoint(String name) throws LuaException {
        Pair<MethodResult, CompoundTag> pairData = getPoint(name);
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }

        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        Pair<Level, BlockPos> newLevelAndPosition = NBTUtil.levelAndBlockPosFromNBT(pairData.getRight());
        Level newLevel = newLevelAndPosition.getLeft();
        BlockPos newPosition = newLevelAndPosition.getRight();

        return automataCore.withOperation(WARP, getWarpContext(newLevel, newPosition), context -> {
            boolean result = owner.move(newLevel, newPosition);
            if (!result) {
                return MethodResult.of(null, "Cannot teleport to location");
            }
            return MethodResult.of(true);
        }, context -> {
            if (!owner.isMovementPossible(newLevel, newPosition)) {
                return MethodResult.of(null, "Move forbidden");
            }
            return null;
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult estimateWarpCost(String name) {
        Pair<MethodResult, CompoundTag> pairData = getPoint(name);
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }

        Pair<Level, BlockPos> newLevelAndPosition = NBTUtil.levelAndBlockPosFromNBT(pairData.getRight());
        return MethodResult.of(getWarpCost(getWarpContext(newLevelAndPosition.getLeft(), newLevelAndPosition.getRight())));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult distanceToPoint(String name) {
        Pair<MethodResult, CompoundTag> pairData = getPoint(name);
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }

        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        Pair<Level, BlockPos> newLevelAndPosition = NBTUtil.levelAndBlockPosFromNBT(pairData.getRight());
        if (newLevelAndPosition.getLeft() != owner.getLevel()) {
            return MethodResult.of(-1);
        }
        return MethodResult.of(newLevelAndPosition.getRight().distManhattan(owner.getPos()));
    }

    /**
     * This method will prepare to teleport to the other side of the portal.
     * Prepare will always costs constant fuel.
     *
     * @return table | nil, string
     *   the result will looks like:
     * <pre>
     * <code>
     * {
     *   name = "minecraft:nether", -- the target dimension's name
     *   pos = { -- the position turtle will teleport to
     *     x = 0,
     *     y = 0,
     *     z = 0,
     *   },
     *   facing = "north", -- where will the turtle face after teleport
     *   costs = 10000, -- the costs to cross the portal
     *   canSpawn = true, -- if the target position is not blocked and turtle are able to spawn there
     *   shipId = "xxx", -- the random id used for ship the portal. You have to call portalShipActive(shipId) if needed.
     * }
     * </code>
     * </pre>
     */
    @LuaFunction
    public final MethodResult portalShipPrepare(IArguments arguments) throws LuaException {
        Direction direction;
        switch (arguments.optString(0).orElse("").toLowerCase()) {
            case "up":
            case "top":
                direction = Direction.UP;
                break;
            case "down":
            case "bottom":
                direction = Direction.DOWN;
                break;
            case "front":
            case "":
                direction = null;
                break;
            default:
                return MethodResult.of(null, "Direction can only be 'up', 'top', 'down', 'bottom', or 'front'");
        }

        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        ITurtleAccess turtle = owner.getTurtle();
        TurtleEnderPearl shipPearl = new TurtleEnderPearl(turtle, direction);
        String shipId = shipPearl.getStringUUID();
        ServerWorker.add(() -> {
            MethodResult res;
            try {
                res = automataCore.withOperation(PREPARE_PORTAL, new SingleOperationContext(1, 1), context -> {
                    shipPearl.setCallback(pearl -> {
                        if (pearl == null || pearl.isRemoved()) {
                            automataCore.queueEvent(PortalPrepareCallback.FAILED_EVENT_ID, shipId, "PEARL_GONE");
                            shipPearls.remove(shipId);
                            return;
                        }
                        Level level = pearl.getLevel();
                        if (level == turtle.getLevel()) {
                            automataCore.queueEvent(PortalPrepareCallback.FAILED_EVENT_ID, shipId, "NO_PORTAL_FOUND");
                            pearl.discard();
                            return;
                        }
                        BlockPos pos = pearl.blockPosition();
                        Map<String, Object> data = new HashMap<>();
                        data.put("name", level.dimension().location().toString());
                        data.put("pos", LuaConverter.posToObject(pos));
                        data.put("facing", pearl.getDirection().getName());
                        data.put("costs", getCostsToLevel(level));
                        data.put("canSpawn", owner.isMovementPossible(level, pos));
                        data.put("shipId", shipId);
                        shipPearls.put(shipId, pearl);
                        automataCore.queueEvent(PortalPrepareCallback.EVENT_ID, data);
                    });
                    turtle.getLevel().addFreshEntity(shipPearl);
                    return null;
                }, null);
            } catch (LuaException e) {
                res = MethodResult.of(null, "Unexpected java error: " + e.toString());
            }
            if (res != null) {
                Object err = res.getResult()[1];
                automataCore.queueEvent(PortalPrepareCallback.FAILED_EVENT_ID, shipId, err);
            }
        });
        return new PortalPrepareCallback(shipId).pull;
    }

    /**
     * @param id the random shipId, one of the result from portalShipPrepare()
     * @return true | nil, string
     */
    @LuaFunction(mainThread = true)
    public final MethodResult portalShipActive(String id) throws LuaException {
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        TurtleEnderPearl shipPearl = shipPearls.get(id);
        if (shipPearl == null) {
            return MethodResult.of(null, "ID_NOT_EXISTS");
        }
        if (shipPearl.isRemoved()) {
            shipPearls.remove(id);
            return MethodResult.of(null, "ID_NOT_EXISTS");
        }
        Level level = shipPearl.getLevel();
        BlockPos newPosition = shipPearl.blockPosition();
        return automataCore.withOperation(ACTIVE_PORTAL, new SingleOperationContext(getCostsToLevel(level), 1), context -> {
            shipPearl.discard();
            boolean result = owner.move(level, newPosition);
            if (!result) {
                return MethodResult.of(null, "Cannot teleport to location");
            }
            shipPearls.remove(id);
            return MethodResult.of(true);
        }, context -> {
            if (!owner.isMovementPossible(level, newPosition)) {
                return MethodResult.of(null, "Move forbidden");
            }
            return null;
        });
    }

    private static int getCostsToLevel(Level level) {
        String dimension = level.dimension().location().toString();
        // TODO: load fuel costs from config
        switch (dimension) {
            case "minecraft:overworld":
                return 10000;
            case "minecraft:the_nether":
                return 20000;
            case "minecraft:the_end":
                return 50000;
        }
        return 10000;
    }

    private static final class PortalPrepareCallback implements ILuaCallback {
        static final String EVENT_ID = "portal_prepare";
        static final String FAILED_EVENT_ID = "portal_prepare_failed";
        final MethodResult pull = MethodResult.pullEvent(null, this);
        private final String id;

        PortalPrepareCallback(String id) {
            this.id = id;
        }

        @NotNull
        @Override
        public MethodResult resume(Object[] datas) {
            if (datas.length <= 0) {
                return pull;
            }
            if (FAILED_EVENT_ID.equals(datas[0])) {
                if (datas.length != 3 || !id.equals(datas[1])) {
                    return pull;
                }
                return MethodResult.of(null, datas[2]);
            }
            if (!EVENT_ID.equals(datas[0]) || datas.length != 2) {
                return pull;
            }
            if (!(datas[1] instanceof Map<?, ?> data) || !id.equals(data.get("shipId"))) {
                return pull;
            }
            return MethodResult.of(data);
        }
    }
}
