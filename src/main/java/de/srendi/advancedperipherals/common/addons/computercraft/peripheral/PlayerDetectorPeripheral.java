package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

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

    public static final String PERIPHERAL_TYPE = "playerDetector";
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
    public final Map<String, Object> getPlayerPos(String username) throws LuaException {
        if (!APConfig.PERIPHERALS_CONFIG.playerSpy.get())
            throw new LuaException("This function is disabled in the config. Activate it or ask an admin if he can activate it.");
        ResourceKey<Level> dimension = getLevel().dimension();

        ServerPlayer existingPlayer = null;
        for (ServerPlayer player : getPlayers()) {
            if (!isAllowedMultiDimensional() && player.getLevel().dimension() != dimension)
                continue;
            if (player.getName().getString().equals(username)) {
                if (MAX_RANGE == -1 || CoordUtil.isInRange(getPos(), getLevel(), player, MAX_RANGE, MAX_RANGE))
                    existingPlayer = player;
                break;
            }
        }
        if (existingPlayer == null)
            return Collections.emptyMap();

        Map<String, Object> info = new HashMap<>();

        double x = existingPlayer.getX();
        double y = existingPlayer.getY();
        double z = existingPlayer.getZ();

        if (APConfig.PERIPHERALS_CONFIG.playerSpyRandError.get()) {
            double distance = Math.sqrt(Math.pow(x - getPos().getX(), 2) + Math.pow(y - getPos().getY(), 2) + Math.pow(z - getPos().getZ(), 2));

            final int minDistance = 50;
            final int maxDistance = 10000;
            final int maxError = 2500;

            distance -= minDistance;
            if (distance > 0) {
                double error = maxError * Math.min(Math.pow(distance / maxDistance, 0.8), 1);
                x += (Math.random()-0.5)*2 * error;
                y += (Math.random()-0.5)*2 * (error / 4);
                z += (Math.random()-0.5)*2 * error;
            }
        }

        info.put("x", Math.floor(x));
        info.put("y", Math.floor(y));
        info.put("z", Math.floor(z));
        if (APConfig.PERIPHERALS_CONFIG.morePlayerInformation.get()) {
            info.put("yaw", existingPlayer.yRotO);
            info.put("pitch", existingPlayer.xRotO);
            info.put("dimension", existingPlayer.getLevel().dimension().location().toString());
            info.put("eyeHeight", existingPlayer.getEyeHeight());
            info.put("health", existingPlayer.getHealth());
            info.put("maxHeatlh", existingPlayer.getMaxHealth());
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
