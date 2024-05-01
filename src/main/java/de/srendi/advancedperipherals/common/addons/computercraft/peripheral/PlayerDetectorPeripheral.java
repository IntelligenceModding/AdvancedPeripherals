package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.IArguments;
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
import de.srendi.advancedperipherals.common.util.CoordUtil;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;

public class PlayerDetectorPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "player_detector";
    private static final int MAX_RANGE = APConfig.PERIPHERALS_CONFIG.playerDetMaxRange.get();

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
        // We also check if the max range is 100000000 since the old default was 100000000, so we don't break servers/worlds
        // with the old default value in the configuration
        return APConfig.PERIPHERALS_CONFIG.playerDetMultiDimensional.get() && (maxRange == -1 || maxRange >= 100000000);
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enablePlayerDetector.get();
    }

    @LuaFunction(mainThread = true)
    public final String[] getOnlinePlayers() {
        return ServerLifecycleHooks.getCurrentServer().getPlayerNames();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getPlayersInCoords(Map<?, ?> firstCoord, Map<?, ?> secondCoord) throws LuaException {
        List<String> playersName = new ArrayList<>();
        BlockPos firstPos = LuaConverter.convertToBlockPos(firstCoord);
        BlockPos secondPos = LuaConverter.convertToBlockPos(secondCoord);
        ResourceKey<Level> dimension = getLevel().dimension();

        for (ServerPlayer player : getPlayers()) {
            if (!isAllowedMultiDimensional() && player.getLevel().dimension() != dimension)
                continue;
            if (CoordUtil.isInRange(getPos(), player, getLevel(), firstPos, secondPos, MAX_RANGE))
                playersName.add(player.getName().getString());
        }
        return MethodResult.of(playersName);
    }

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInCubic(int x, int y, int z) {
        List<String> playersName = new ArrayList<>();
        ResourceKey<Level> dimension = getLevel().dimension();

        for (ServerPlayer player : getPlayers()) {
            if (!isAllowedMultiDimensional() && player.getLevel().dimension() != dimension)
                continue;
            if (CoordUtil.isInRange(getPos(), getLevel(), player, x, y, z, MAX_RANGE))
                playersName.add(player.getName().getString());
        }
        return playersName;
    }

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInRange(int range) {
        List<String> playersName = new ArrayList<>();
        ResourceKey<Level> dimension = getLevel().dimension();

        for (ServerPlayer player : getPlayers()) {
            if (!isAllowedMultiDimensional() && player.getLevel().dimension() != dimension)
                continue;
            if (range == -1 || CoordUtil.isInRange(getPos(), getLevel(), player, range, MAX_RANGE))
                playersName.add(player.getName().getString());
        }
        return playersName;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInCoords(Map<?, ?> firstCoord, Map<?, ?> secondCoord) throws LuaException {
        if (getPlayers().isEmpty())
            return false;
        BlockPos firstPos = LuaConverter.convertToBlockPos(firstCoord);
        BlockPos secondPos = LuaConverter.convertToBlockPos(secondCoord);
        ResourceKey<Level> dimension = getLevel().dimension();

        for (ServerPlayer player : getPlayers()) {
            if (!isAllowedMultiDimensional() && player.getLevel().dimension() != dimension)
                continue;
            if (CoordUtil.isInRange(getPos(), player, getLevel(), firstPos, secondPos, MAX_RANGE)) return true;
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInCubic(int x, int y, int z) {
        if (getPlayers().isEmpty())
            return false;
        ResourceKey<Level> dimension = getLevel().dimension();

        for (ServerPlayer player : getPlayers()) {
            if (!isAllowedMultiDimensional() && player.getLevel().dimension() != dimension)
                continue;
            if (CoordUtil.isInRange(getPos(), getLevel(), player, x, y, z, MAX_RANGE)) return true;
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInRange(int range) {
        if (getPlayers().isEmpty())
            return false;
        ResourceKey<Level> dimension = getLevel().dimension();

        for (ServerPlayer player : getPlayers()) {
            if (!isAllowedMultiDimensional() && player.getLevel().dimension() != dimension)
                continue;
            if (range == -1 || CoordUtil.isInRange(getPos(), getLevel(), player, range, MAX_RANGE)) return true;
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInCoords(Map<?, ?> firstCoord, Map<?, ?> secondCoord, String username) throws LuaException {
        BlockPos firstPos = LuaConverter.convertToBlockPos(firstCoord);
        BlockPos secondPos = LuaConverter.convertToBlockPos(secondCoord);
        ResourceKey<Level> dimension = getLevel().dimension();

        for (Player player : getPlayers()) {
            if (!isAllowedMultiDimensional() && player.getLevel().dimension() != dimension)
                continue;
            if (CoordUtil.isInRange(getPos(), player, getLevel(), firstPos, secondPos, MAX_RANGE))
                if(player.getName().getString().equals(username))
                    return true;
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInCubic(int x, int y, int z, String username) {
        ResourceKey<Level> dimension = getLevel().dimension();

        for (Player player : getPlayers()) {
            if (!isAllowedMultiDimensional() && player.getLevel().dimension() != dimension)
                continue;
            if (CoordUtil.isInRange(getPos(), getLevel(), player, x, y, z, MAX_RANGE)) {
                if(player.getName().getString().equals(username))
                    return true;
            }
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInRange(int range, String username) {
        ResourceKey<Level> dimension = getLevel().dimension();

        for (Player player : getPlayers()) {
            if (!isAllowedMultiDimensional() && player.getLevel().dimension() != dimension)
                continue;
            if (range == -1 || CoordUtil.isInRange(getPos(), getLevel(), player, range, MAX_RANGE)) {
                if(player.getName().getString().equals(username))
                    return true;
            }
        }
        return false;
    }

    @LuaFunction(value = {"getPlayerPos", "getPlayer"}, mainThread = true)
    public final Map<String, Object> getPlayerPos(IArguments arguments) throws LuaException {
        if (!APConfig.PERIPHERALS_CONFIG.playerSpy.get())
            throw new LuaException("This function is disabled in the config. Activate it or ask an admin if he can activate it.");
        ResourceKey<Level> dimension = getLevel().dimension();

        ServerPlayer existingPlayer = null;
        for (ServerPlayer player : getPlayers()) {
            if (!isAllowedMultiDimensional() && player.getLevel().dimension() != dimension)
                continue;
            if (player.getName().getString().equals(arguments.getString(0))) {
                if (MAX_RANGE == -1 || CoordUtil.isInRange(getPos(), getLevel(), player, MAX_RANGE, MAX_RANGE))
                    existingPlayer = player;
                break;
            }
        }
        if (existingPlayer == null)
            return Collections.emptyMap();

        Map<String, Object> info = new HashMap<>();

        double x = existingPlayer.getX(), y = existingPlayer.getY(), z = existingPlayer.getZ();

        if (APConfig.PERIPHERALS_CONFIG.playerSpyRandError.get()) {
            // We apply random error to the returned player position if so enabled in the configuration.

            // minDistance: Below this distance, the player's exact position is returned
            int minDistance = APConfig.PERIPHERALS_CONFIG.playerSpyPreciseMaxRange.get();
            // maxError: The maximum amount of blocks that the player's position can be off by (on each axis) at the max distance
            int maxError = APConfig.PERIPHERALS_CONFIG.playerSpyRandErrorAmount.get();
            // maxDistance: At this distance, maximum error is applied. Hard-coded to the minimum of MAX_RANGE and 10000, or just 10000 if MAX_RANGE is unlimited
            int maxDistance = MAX_RANGE == -1 ? 10000 : Math.min(MAX_RANGE, 10000);
            // sublinearFactor: We apply exponent to the calculations so that error increases quickly at first before leveling out
            // This is hard-coded so as not to overwhelm the player with configuration options, but this can probably be changed
            double sublinearFactor = 0.8;
            // yAxisWeight: Since the Y-axis obviously has a much smaller range than X- and Z- axes
            // (which can theoretically be infinite) in the Minecraft world, we should apply less error to it
            double yAxisWeight = (double) 1 / 4;

            maxDistance = Math.max(minDistance, maxDistance);

            // Calculate Euclidean distance between the player locator and the player in question
            double distanceFromPlayer = Math.sqrt(Math.pow(x - getPos().getX(), 2) + Math.pow(y - getPos().getY(), 2) + Math.pow(z - getPos().getZ(), 2));

            distanceFromPlayer -= minDistance;
            if (distanceFromPlayer > 0) {
                // We calculate error as the fraction of the player's distance and the max distance defined in the configuration
                // then we raise it to sublinearFactor to make it somewhat exponential
                double error = maxError * Math.min(Math.pow(distanceFromPlayer / maxDistance, sublinearFactor), 1);
                x += (Math.random() - 0.5) * 2 * error;
                y += (Math.random() - 0.5) * 2 * error * yAxisWeight;
                z += (Math.random() - 0.5) * 2 * error;
            }
        }

        int decimals = Math.min(arguments.optInt(1, 0), 4);

        final double unit = Math.pow(10, decimals);
        info.put("x", Math.floor(x * unit) / unit);
        info.put("y", Math.floor(y * unit) / unit);
        info.put("z", Math.floor(z * unit) / unit);
        if (APConfig.PERIPHERALS_CONFIG.morePlayerInformation.get()) {
            info.put("yaw", existingPlayer.yRotO);
            info.put("pitch", existingPlayer.xRotO);
            info.put("dimension", existingPlayer.getLevel().dimension().location().toString());
            info.put("eyeHeight", existingPlayer.getEyeHeight());
            info.put("health", existingPlayer.getHealth());
            // TODO: remove the next line in next major version
            info.put("maxHeatlh", existingPlayer.getMaxHealth()); // keep this for backward compatibility
            info.put("maxHealth", existingPlayer.getMaxHealth());
            info.put("airSupply", existingPlayer.getAirSupply());
            info.put("respawnPosition", LuaConverter.posToObject(existingPlayer.getRespawnPosition()));
            info.put("respawnDimension", existingPlayer.getRespawnDimension().location().toString());
            info.put("respawnAngle", existingPlayer.getRespawnAngle());
        }
        return info;
    }

    private List<ServerPlayer> getPlayers() {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
    }
}
