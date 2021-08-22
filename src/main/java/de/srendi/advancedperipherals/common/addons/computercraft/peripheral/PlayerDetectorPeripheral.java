package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerDetectorPeripheral extends BasePeripheral {

    public static final String TYPE = "playerDetector";

    public PlayerDetectorPeripheral(PeripheralTileEntity<?> tileEntity) {
        super(TYPE, tileEntity);
    }

    public PlayerDetectorPeripheral(ITurtleAccess access, TurtleSide side) {
        super(TYPE, access, side);
    }

    public PlayerDetectorPeripheral(IPocketAccess pocket) {
        super(TYPE, pocket);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enablePlayerDetector;
    }

    @LuaFunction(mainThread = true)
    public final String[] getOnlinePlayers() {
        return ServerLifecycleHooks.getCurrentServer().getPlayerNames();
    }

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInCoords(Map<?, ?> firstCoord, Map<?, ?> secondCoord) throws LuaException {
        List<String> playersName = new ArrayList<>();
        BlockPos firstPos = LuaConverter.convertToBlockPos(firstCoord);
        BlockPos secondPos = LuaConverter.convertToBlockPos(secondCoord);
        for (ServerPlayer player : getPlayers()) {
            if (isInRange(player, firstPos, secondPos))
                playersName.add(player.getName().getString());
        }
        return playersName;
    }

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInCubic(int x, int y, int z) {
        List<String> playersName = new ArrayList<>();
        for (ServerPlayer player : getPlayers()) {
            if (isInRange(getPos(), player, x, y, z))
                playersName.add(player.getName().getString());
        }
        return playersName;
    }

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInRange(int range) {
        List<String> playersName = new ArrayList<>();
        for (ServerPlayer player : getPlayers()) {
            if (isInRange(getPos(), player, range))
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
        for (ServerPlayer player : getPlayers()) {
            if (isInRange(player, firstPos, secondPos))
                return true;
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInCubic(int x, int y, int z) {
        if (getPlayers().isEmpty())
            return false;
        for (ServerPlayer player : getPlayers()) {
            if (isInRange(getPos(), player, x, y, z))
                return true;
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInRange(int range) {
        if (getPlayers().isEmpty())
            return false;
        for (ServerPlayer player : getPlayers()) {
            if (isInRange(getPos(), player, range))
                return true;
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInCoords(Map<?, ?> firstCoord, Map<?, ?> secondCoord, String username) throws LuaException {
        List<String> playersName = new ArrayList<>();
        BlockPos firstPos = LuaConverter.convertToBlockPos(firstCoord);
        BlockPos secondPos = LuaConverter.convertToBlockPos(secondCoord);
        for (Player player : getPlayers()) {
            if (isInRange(player, firstPos, secondPos)) {
                playersName.add(player.getName().getString());
            }
        }
        return playersName.contains(username);
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInCubic(int x, int y, int z, String username) {
        List<String> playersName = new ArrayList<>();
        for (Player player : getPlayers()) {
            if (isInRange(getPos(), player, x, y, z)) {
                playersName.add(player.getName().getString());
            }
        }
        return playersName.contains(username);
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInRange(int range, String username) {
        List<String> playersName = new ArrayList<>();
        for (Player player : getPlayers()) {
            if (isInRange(getPos(), player, range)) {
                playersName.add(player.getName().getString());
            }
        }
        return playersName.contains(username);
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Double> getPlayerPos(String username) {
        ServerPlayer existingPlayer = null;
        for (ServerPlayer player : getPlayers()) {
            if (player.getName().getString().equals(username)) {
                if (isInRange(getPos(), player, AdvancedPeripheralsConfig.playerDetMaxRange)) {
                    existingPlayer = player;
                    break;
                }
            }
        }
        if (existingPlayer == null)
            return null;

        Map<String, Double> coordinates = new HashMap<>();
        coordinates.put("x", Math.floor(existingPlayer.getX()));
        coordinates.put("y", Math.floor(existingPlayer.getY()));
        coordinates.put("z", Math.floor(existingPlayer.getZ()));
        return coordinates;
    }

    private List<ServerPlayer> getPlayers() {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
    }

    private boolean isInRange(BlockPos pos, Player player, int range) {
        Level world = getLevel();
        return world.getNearbyPlayers(TargetingConditions.forNonCombat().ignoreInvisibilityTesting().ignoreLineOfSight(),
                null, new AABB(pos.offset(range, range, range), pos.offset(-range, -range, -range))).contains(player);
    }

    private boolean isInRange(BlockPos pos, Player player, int x, int y, int z) {
        Level world = getLevel();
        return world.getNearbyPlayers(TargetingConditions.forNonCombat().ignoreInvisibilityTesting().ignoreLineOfSight(),
                null, new AABB(pos.offset(x, y, z), pos.offset(-x, -y, -z))).contains(player);
    }

    private boolean isInRange(Player player, BlockPos firstPos, BlockPos secondPos) {
        Level world = getLevel();

        return world.getNearbyPlayers(TargetingConditions.forNonCombat().ignoreInvisibilityTesting().ignoreLineOfSight(),
                null, new AABB(firstPos, secondPos)).contains(player);
    }
}
