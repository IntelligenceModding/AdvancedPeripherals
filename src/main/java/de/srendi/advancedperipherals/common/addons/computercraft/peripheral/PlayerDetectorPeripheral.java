package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

// TODO: Improve player detect logic
// Check player's box in the area instead of check player in the players in the area
public class PlayerDetectorPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "player_detector";
    private static final int MAX_RANGE = APConfig.PERIPHERALS_CONFIG.playerDetMaxRange.get();
    private long lastConsumedMessage = Events.getLastPlayerMessageID() - 1;

    public PlayerDetectorPeripheral(PeripheralBlockEntity<?> tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    public PlayerDetectorPeripheral(ITurtleAccess access, TurtleSide side) {
        super(PERIPHERAL_TYPE, new TurtlePeripheralOwner(access, side));
    }

    public PlayerDetectorPeripheral(IPocketAccess pocket) {
        super(PERIPHERAL_TYPE, new PocketPeripheralOwner(pocket));
    }

    private boolean isAllowedMultiDimensional() {
        int maxRange = MAX_RANGE;
        return APConfig.PERIPHERALS_CONFIG.playerDetMultiDimensional.get() && maxRange == -1;
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enablePlayerDetector.get();
    }

    @LuaFunction(mainThread = true)
    public final String[] getOnlinePlayers() {
        return getPlayers()
            .map(player -> player.getName().getString())
            .toArray(String[]::new);
    }

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInCoords(Map<?, ?> firstCoord, Map<?, ?> secondCoord) throws LuaException {
        BlockPos firstPos = LuaConverter.convertToBlockPos(firstCoord);
        BlockPos secondPos = LuaConverter.convertToBlockPos(secondCoord);

        return getPlayers()
            .filter(player -> CoordUtil.isInRange(getCenterPos(), player, getLevel(), firstPos, secondPos, MAX_RANGE))
            .map(player -> player.getName().getString())
            .toList();
    }

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInCubic(int x, int y, int z) {
        return getPlayers()
            .filter(player -> CoordUtil.isInRange(getCenterPos(), getLevel(), player, x, y, z, MAX_RANGE))
            .map(player -> player.getName().getString())
            .toList();
    }

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInRange(int range) {
        return getPlayers()
            .filter(player -> CoordUtil.isInRange(getCenterPos(), getLevel(), player, range, MAX_RANGE))
            .map(player -> player.getName().getString())
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

    @LuaFunction(value = {"getPlayerPos", "getPlayer"}, mainThread = true)
    public final Map<String, Object> getPlayerPos(IArguments arguments) throws LuaException {
        if (!APConfig.PERIPHERALS_CONFIG.playerSpy.get()) {
            throw new LuaException("This function is disabled in the config. Activate it or ask an admin if he can activate it.");
        }
        ServerPlayer player = getPlayer(arguments.getString(0));
        if (player == null) {
            return null;
        }
        if (MAX_RANGE != -1 && !CoordUtil.isInRange(getCenterPos(), getLevel(), player, MAX_RANGE, MAX_RANGE)) {
            return null;
        }

        Map<String, Object> info = new HashMap<>();

        double x = player.getX(), y = player.getY(), z = player.getZ();

        if (APConfig.PERIPHERALS_CONFIG.playerSpyRandError.get()) {
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
            double distanceFromPlayer = Math.sqrt(getCenterPos().distanceToSqr(x, y, z));

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

        int decimals = Math.min(arguments.optInt(1, 0), 4);

        final double unit = Math.pow(10, decimals);
        info.put("x", Math.floor(x * unit) / unit);
        info.put("y", Math.floor(y * unit) / unit);
        info.put("z", Math.floor(z * unit) / unit);
        if (APConfig.PERIPHERALS_CONFIG.morePlayerInformation.get()) {
            info.put("uuid", player.getUUID().toString());
            info.put("name", player.getName().getString());
            info.put("yaw", player.getYRot());
            info.put("pitch", player.getXRot());
            info.put("dimension", player.level().dimension().location().toString());
            info.put("eyeHeight", player.getEyeHeight());
            info.put("health", player.getHealth());
            info.put("maxHealth", player.getMaxHealth());
            info.put("airSupply", player.getAirSupply());
            info.put("respawnPosition", LuaConverter.posToObject(player.getRespawnPosition()));
            info.put("respawnDimension", player.getRespawnDimension().location().toString());
            info.put("respawnAngle", player.getRespawnAngle());
        }
        return info;
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
        lastConsumedMessage = Events.traversePlayerMessages(
            lastConsumedMessage,
            message -> getConnectedComputers().forEach(computer ->
                computer.queueEvent(message.eventName(), message.playerName(), message.fromDimension(), message.toDimension())
            )
        );
    }
}
