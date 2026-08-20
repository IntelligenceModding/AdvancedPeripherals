package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperationContext;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.FuelAbility;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.PeripheralOwnerAbility;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.entity.TurtleEnderPearl;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.util.ChunkManager;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.ServerWorker;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.*;

public class AutomataWarpingPlugin extends AutomataCorePlugin {

    private final Map<String, TurtleEnderPearl> shipPearls = new HashMap<>();

    public AutomataWarpingPlugin(AutomataCorePeripheral automataCore) {
        super(automataCore);
    }

    @Override
    public IPeripheralOperation<?> @NotNull [] getOperations() {
        return new IPeripheralOperation[]{WARP, PREPARE_PORTAL, ACTIVE_PORTAL};
    }

    @NotNull
    @Unmodifiable
    protected Map<String, GlobalPos> getPointDatas() {
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        CompoundTag points = owner.getDataStorage().getCompound(APDataComponents.POINT_DATA_MARK);
        if (points.isEmpty()) {
            return Map.of();
        }
        Map<String, GlobalPos> data = new HashMap<>();
        for (String name : points.getAllKeys()) {
            GlobalPos pos = GlobalPos.CODEC.parse(NbtOps.INSTANCE, points.get(name)).result().orElse(null);
            if (pos != null) {
                data.put(name, pos);
            }
        }
        return data;
    }

    protected void setPointData(@NotNull Map<String, GlobalPos> data) {
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        CompoundTag settings = owner.getDataStorage();
        CompoundTag points = new CompoundTag();
        for (Map.Entry<String, GlobalPos> point : data.entrySet()) {
            points.put(point.getKey(), GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, point.getValue()).result().orElseThrow());
        }
        settings.put(APDataComponents.POINT_DATA_MARK, points);
        owner.putDataStorage(settings);
    }

    protected Pair<MethodResult, GlobalPos> getPoint(String name) {
        Map<String, GlobalPos> points = getPointDatas();
        if (!points.containsKey(name)) {
            return Pair.onlyLeft(MethodResult.of(null, "Warp point not exists"));
        }
        return Pair.onlyRight(points.get(name));
    }

    private SingleOperationContext getWarpContext(ResourceKey<Level> level, BlockPos pos) {
        return level == automataCore.getPeripheralOwner().getLevel().dimension()
            ? automataCore.toDistance(pos)
            : new SingleOperationContext(getCostsToLevel(level), 1);
    }

    private int getWarpCost(SingleOperationContext context) {
        FuelAbility<?> fuelAbility = automataCore.getPeripheralOwner().getAbility(PeripheralOwnerAbility.FUEL);
        Objects.requireNonNull(fuelAbility);
        return WARP.getCost(context) * fuelAbility.getFuelConsumptionMultiply();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult savePoint(String name) {
        automataCore.addRotationCycle();
        Map<String, GlobalPos> cdata = getPointDatas();
        if (cdata.size() >= APConfig.METAPHYSICS_CONFIG.endAutomataCoreWarpPointLimit.get()) {
            return MethodResult.of(null, "Cannot add new point, limit reached");
        }

        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        Level level = owner.getLevel();
        Map<String, GlobalPos> data = new HashMap<>(cdata);
        data.put(name, GlobalPos.of(level.dimension(), owner.getPos()));
        setPointData(data);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult deletePoint(String name) {
        automataCore.addRotationCycle();
        Map<String, GlobalPos> cdata = getPointDatas();
        if (!cdata.containsKey(name)) {
            return MethodResult.of(null, "Cannot find point to delete");
        }

        Map<String, GlobalPos> data = new HashMap<>(cdata);
        data.remove(name);
        setPointData(data);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final Collection<String> listPoints() {
        Map<String, GlobalPos> data = getPointDatas();
        return data.keySet();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult warpToPoint(String name) throws LuaException {
        Pair<MethodResult, GlobalPos> pairData = getPoint(name);
        if (pairData.leftPresent()) {
            return pairData.left();
        }

        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        GlobalPos globalPos = pairData.right();
        MinecraftServer server = owner.getLevel().getServer();
        ServerLevel newLevel = server.getLevel(globalPos.dimension());
        BlockPos newPosition = globalPos.pos();

        return automataCore.withOperation(WARP, getWarpContext(globalPos.dimension(), newPosition), context -> {
            ChunkManager.get(server).addForceChunk(newLevel, owner.getChunkLoadUUID(), new ChunkPos(newPosition));
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
        Pair<MethodResult, GlobalPos> pairData = getPoint(name);
        if (pairData.leftPresent()) {
            return pairData.left();
        }

        GlobalPos globalPos = pairData.right();
        return MethodResult.of(getWarpCost(getWarpContext(globalPos.dimension(), globalPos.pos())));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult distanceToPoint(String name) {
        Pair<MethodResult, GlobalPos> pairData = getPoint(name);
        if (pairData.leftPresent()) {
            return pairData.left();
        }

        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        GlobalPos globalPos = pairData.right();
        if (globalPos.dimension() != owner.getLevel().dimension()) {
            return MethodResult.of(-1);
        }
        return MethodResult.of(globalPos.pos().distManhattan(owner.getPos()));
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
        switch (arguments.optString(0).orElse("").toLowerCase(Locale.ROOT)) {
            case "up", "top" -> direction = Direction.UP;
            case "down", "bottom" -> direction = Direction.DOWN;
            case "front", "" -> direction = null;
            default -> {
                return MethodResult.of(null, "Direction can only be 'up', 'top', 'down', 'bottom', or 'front'");
            }
        };

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
                        Level level = pearl.level();
                        if (level == turtle.getLevel()) {
                            automataCore.queueEvent(PortalPrepareCallback.FAILED_EVENT_ID, shipId, "NO_PORTAL_FOUND");
                            pearl.discard();
                            return;
                        }
                        BlockPos pos = pearl.blockPosition();
                        Map<String, Object> data = Map.of(
                            "name", level.dimension().location().toString(),
                            "pos", LuaConverter.posToLua(pos),
                            "facing", pearl.getDirection().getName(),
                            "costs", getCostsToLevel(level.dimension()),
                            "canSpawn", owner.isMovementPossible(level, pos),
                            "shipId", shipId
                        );
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
        Level level = shipPearl.level();
        BlockPos newPosition = shipPearl.blockPosition();
        return automataCore.withOperation(ACTIVE_PORTAL, new SingleOperationContext(getCostsToLevel(level.dimension()), 1), context -> {
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

    private static int getCostsToLevel(ResourceKey<Level> level) {
        String dimension = level.location().toString();
        // TODO: load fuel costs from config / datapack
        return switch (dimension) {
            case "minecraft:overworld" -> 10000;
            case "minecraft:the_nether" -> 20000;
            case "minecraft:the_end" -> 50000;
            default -> 10000;
        };
    }

    private static final class PortalPrepareCallback implements ILuaCallback {
        static final String EVENT_ID = CCEvents.PORTAL_PREPARE;
        static final String FAILED_EVENT_ID = CCEvents.PORTAL_PREPARE_FAILED;
        final MethodResult pull = MethodResult.pullEvent(null, this);
        private final String id;

        PortalPrepareCallback(String id) {
            this.id = id;
        }

        @Override
        @NotNull
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
