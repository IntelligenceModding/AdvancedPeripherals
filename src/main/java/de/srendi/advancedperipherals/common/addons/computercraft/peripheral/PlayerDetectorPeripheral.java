package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.PocketPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.events.Events;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatType;
import net.minecraft.world.phys.Vec3;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PlayerDetectorPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "player_detector";
    private static final int MAX_RANGE = APConfig.PERIPHERALS_CONFIG.playerDetMaxRange.get();
    private long lastConsumedMessage = Events.getLastPlayerMessageID();

    public PlayerDetectorPeripheral(PeripheralBlockEntity<?> tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    public PlayerDetectorPeripheral(ITurtleAccess access, TurtleSide side) {
        super(PERIPHERAL_TYPE, new TurtlePeripheralOwner(access, side));
    }

    public PlayerDetectorPeripheral(IPocketAccess pocket) {
        super(PERIPHERAL_TYPE, PocketPeripheralOwner.of(pocket));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enablePlayerDetector.get();
    }

    @Override
    protected Map<String, Object> getPeripheralConfiguration() {
        Map<String, Object> configs = super.getPeripheralConfiguration();
        configs.put("playerSpyEnabled", APConfig.PERIPHERALS_CONFIG.playerSpy.get());
        return configs;
    }

    private boolean isAllowedMultiDimensional() {
        int maxRange = MAX_RANGE;
        return APConfig.PERIPHERALS_CONFIG.playerDetMultiDimensional.get() && maxRange == -1;
    }

    @LuaFunction(mainThread = true)
    public final String[] getOnlinePlayers() {
        return getPlayers()
            .map(player -> player.getGameProfile().getName())
            .toArray(String[]::new);
    }

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInCoords(Map<?, ?> firstCoord, Map<?, ?> secondCoord) throws LuaException {
        BlockPos firstPos = LuaConverter.convertToBlockPos(firstCoord);
        BlockPos secondPos = LuaConverter.convertToBlockPos(secondCoord);

        return getPlayers()
            .filter(player -> CoordUtil.isInRange(getCenterPos(), player, getLevel(), firstPos, secondPos, MAX_RANGE))
            .map(player -> player.getGameProfile().getName())
            .toList();
    }

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInCubic(int x, int y, int z) {
        return getPlayers()
            .filter(player -> CoordUtil.isInRange(getCenterPos(), getLevel(), player, x, y, z, MAX_RANGE))
            .map(player -> player.getGameProfile().getName())
            .toList();
    }

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInRange(int range) {
        return getPlayers()
            .filter(player -> CoordUtil.isInRange(getCenterPos(), getLevel(), player, range, MAX_RANGE))
            .map(player -> player.getGameProfile().getName())
            .toList();
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInCoords(Map<?, ?> firstCoord, Map<?, ?> secondCoord) throws LuaException {
        BlockPos firstPos = LuaConverter.convertToBlockPos(firstCoord);
        BlockPos secondPos = LuaConverter.convertToBlockPos(secondCoord);

        return getPlayers()
            .anyMatch(player -> CoordUtil.isInRange(getCenterPos(), player, getLevel(), firstPos, secondPos, MAX_RANGE));
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInCubic(int x, int y, int z) {
        return getPlayers()
            .anyMatch(player -> CoordUtil.isInRange(getCenterPos(), getLevel(), player, x, y, z, MAX_RANGE));
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInRange(int range) {
        return getPlayers()
            .anyMatch(player -> CoordUtil.isInRange(getCenterPos(), getLevel(), player, range, MAX_RANGE));
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInCoords(Map<?, ?> firstCoord, Map<?, ?> secondCoord, String username) throws LuaException {
        BlockPos firstPos = LuaConverter.convertToBlockPos(firstCoord);
        BlockPos secondPos = LuaConverter.convertToBlockPos(secondCoord);

        ServerPlayer player = getPlayer(username);
        return player != null && CoordUtil.isInRange(getCenterPos(), player, getLevel(), firstPos, secondPos, MAX_RANGE);
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInCubic(int x, int y, int z, String username) {
        ServerPlayer player = getPlayer(username);
        return player != null && CoordUtil.isInRange(getCenterPos(), getLevel(), player, x, y, z, MAX_RANGE);
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInRange(int range, String username) {
        ServerPlayer player = getPlayer(username);
        return player != null && CoordUtil.isInRange(getCenterPos(), getLevel(), player, range, MAX_RANGE);
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getPlayer(IArguments arguments) throws LuaException {
        if (!APConfig.PERIPHERALS_CONFIG.playerSpy.get()) {
            throw new LuaException("This function is disabled in the config [Player_Detector.playerSpy]. Activate it or ask admins if they can activate it.");
        }
        ServerPlayer player = getPlayer(arguments.getString(0));
        if (player == null) {
            return null;
        }
        if (MAX_RANGE != -1 && !CoordUtil.isInRange(getCenterPos(), getLevel(), player, MAX_RANGE, MAX_RANGE)) {
            return null;
        }
        return getPlayerInfo(player, player == owner.getHoldingEntity());
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getOwner() throws LuaException {
        if (!(owner.getHoldingEntity() instanceof ServerPlayer player)) {
            return null;
        }
        return getPlayerInfo(player, true);
    }

    private Map<String, Object> getPlayerInfo(ServerPlayer player, boolean isOwner) {
        boolean showAbsCoords = true;

        Map<String, Object> info = APConfig.PERIPHERALS_CONFIG.morePlayerInformation.get()
            ? LuaConverter.entityToLua(player)
            : new HashMap<>();

        Vec3 selfPos = this.getPhysicsPos();
        Vec3 playerPos = player.position();
        double x = playerPos.x, y = playerPos.y, z = playerPos.z;

        if (!isOwner && APConfig.PERIPHERALS_CONFIG.playerSpyRandError.get()) {
            // We apply random error to the returned player position if so enabled in the configuration.
            final int maxErrorRange = 10000;

            // minDistance: Below this distance, the player's exact position is returned
            int minDistance = APConfig.PERIPHERALS_CONFIG.playerSpyPreciseMaxRange.get();
            // maxError: The maximum amount of blocks that the player's position can be off by (on each axis) at the max distance
            int maxError = APConfig.PERIPHERALS_CONFIG.playerSpyRandErrorAmount.get();
            // maxDistance: At this distance, maximum error is applied.
            int maxDistance = MAX_RANGE == -1 ? maxErrorRange : Math.min(MAX_RANGE, maxErrorRange);
            // sublinearFactor: We apply exponent to the calculations so that error increases quickly at first before leveling out
            // This is hard-coded so as not to overwhelm the player with configuration options, but this can probably be changed
            double sublinearFactor = 0.8;
            // yAxisWeight: Since the Y-axis obviously has a much smaller range than X and Z axises
            // (which can theoretically be infinite) in the Minecraft world, we should apply less error to it
            double yAxisWeight = 1.0 / 4;

            maxDistance = Math.max(minDistance, maxDistance);

            // Calculate Euclidean distance between the player locator and the player in question
            double distanceFromPlayer = selfPos.distanceTo(playerPos);

            distanceFromPlayer -= minDistance;
            if (distanceFromPlayer > 0) {
                // We calculate error as the fraction of the player's distance and the max distance defined in the configuration
                // then we raise it to sublinearFactor to make it somewhat exponential
                double error = maxError * Math.min(Math.pow(distanceFromPlayer / maxDistance, sublinearFactor), 1);
                x += (Math.random() * 2 - 1) * error;
                y += (Math.random() * 2 - 1) * error * yAxisWeight;
                z += (Math.random() * 2 - 1) * error;
            }
        }
        if (showAbsCoords) {
            CoordUtil.putXYZCoords(info, x, y, z);
        }
        CoordUtil.putFRUCoords(info, x - selfPos.x, y - selfPos.y, z - selfPos.z, owner.getOrientation());

        if (APConfig.PERIPHERALS_CONFIG.morePlayerInformation.get()) {
            // TODO: should we put those into lua converter as well?
            info.put("respawnPosition", LuaConverter.posToLua(player.getRespawnPosition()));
            info.put("respawnDimension", player.getRespawnDimension().location().toString());
            info.put("respawnAngle", player.getRespawnAngle());
        }

        WeakReference<ServerPlayer> playerRef = new WeakReference<>(player);
        if (APConfig.PERIPHERALS_CONFIG.playerSpyStatistics.get()) {
            info.put("getStat", (ILuaFunction) (args) -> this.getPlayerStat(playerRef, args.getString(0)));
        }
        return info;
    }

    private MethodResult getPlayerStat(WeakReference<ServerPlayer> playerRef, String statName) {
        ServerPlayer player = playerRef.get();
        if (player == null || player.isRemoved()) {
            return Errors.PLAYER_NOT_EXISTS_RESULT;
        }
        ResourceLocation statId = ResourceLocation.tryParse(statName);
        if (statId == null) {
            return Errors.INVALID_STAT_ID_RESULT;
        }
        ResourceLocation statTypeId = ResourceLocation.tryParse(statId.getNamespace().replace('.', ':'));
        if (statTypeId == null) {
            return Errors.INVALID_STAT_ID_RESULT;
        }
        ResourceLocation statValueId = ResourceLocation.tryParse(statId.getPath().replace('.', ':'));
        if (statValueId == null) {
            return Errors.INVALID_STAT_ID_RESULT;
        }

        @SuppressWarnings("rawtypes")
        StatType statType = BuiltInRegistries.STAT_TYPE.get(statTypeId);
        if (statType == null) {
            return Errors.UNKNOWN_STAT_TYPE_RESULT;
        }
        Object statValue = statType.getRegistry().get(statValueId);
        if (statValue == null) {
            return Errors.UNKNOWN_STAT_VALUE_RESULT;
        }
        return MethodResult.of(player.getStats().getValue(statType.get(statValue)));
    }

    private Stream<ServerPlayer> getPlayers() {
        ServerLevel level = getLevel();
        if (level == null) {
            return Stream.of();
        }
        Stream<ServerPlayer> players = isAllowedMultiDimensional()
            ? level.getServer().getPlayerList().getPlayers().stream()
            : level.players().stream();
        if (!APConfig.PERIPHERALS_CONFIG.showSpectators.get()) {
            players = players.filter(player -> !player.isSpectator());
        }
        return players;
    }

    private ServerPlayer getPlayer(String username) {
        ServerLevel level = getLevel();
        if (level == null) {
            return null;
        }
        // Note: getPlayerByName still has O(N) time complexity but doesn't matter
        ServerPlayer player = level.getServer().getPlayerList().getPlayerByName(username);
        if (player == null) {
            return null;
        }
        if (!isAllowedMultiDimensional() && player.level() != level) {
            return null;
        }
        if (!APConfig.PERIPHERALS_CONFIG.showSpectators.get() && player.isSpectator()) {
            return null;
        }
        return player;
    }

    @Override
    public void update() {
        lastConsumedMessage = Events.traversePlayerMessages(lastConsumedMessage, message -> {
            // TODO: distance check?
            queueEvent(message.eventName(), message.eventArgs());
        });
    }

    private static final class Errors {
        static final String INVALID_STAT_ID = "INVALID_STAT_ID";
        static final String PLAYER_NOT_EXISTS = "PLAYER_NOT_EXISTS";
        static final String UNKNOWN_STAT_TYPE = "UNKNOWN_STAT_TYPE";
        static final String UNKNOWN_STAT_VALUE = "UNKNOWN_STAT_VALUE";

        static final MethodResult INVALID_STAT_ID_RESULT = MethodResult.of(null, INVALID_STAT_ID);
        static final MethodResult PLAYER_NOT_EXISTS_RESULT = MethodResult.of(null, PLAYER_NOT_EXISTS);
        static final MethodResult UNKNOWN_STAT_TYPE_RESULT = MethodResult.of(null, UNKNOWN_STAT_TYPE);
        static final MethodResult UNKNOWN_STAT_VALUE_RESULT = MethodResult.of(null, UNKNOWN_STAT_VALUE);
    }
}
