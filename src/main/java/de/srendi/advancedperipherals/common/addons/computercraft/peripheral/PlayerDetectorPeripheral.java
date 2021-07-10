package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerDetectorPeripheral extends BasePeripheral {

    public PlayerDetectorPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    public PlayerDetectorPeripheral(String type, ITurtleAccess access, TurtleSide side) {
        super(type, access, side);
    }

    public PlayerDetectorPeripheral(String type, IPocketAccess pocket) {
        super(type, pocket);
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
    public final List<String> getPlayersInRange(int range) {
        List<String> playersName = new ArrayList<>();
        for (ServerPlayerEntity player : getPlayers()) {
            if (isInRange(getPos(), player, range))
                playersName.add(player.getName().getString());
        }
        return playersName;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInRange(int range) {
        if (getPlayers().isEmpty())
            return false;
        for (ServerPlayerEntity player : getPlayers()) {
            if (isInRange(getPos(), player, range))
                return true;
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInRange(int range, String username) {
        List<String> playersName = new ArrayList<>();
        for (PlayerEntity player : getPlayers()) {
            if (isInRange(getPos(), player, range)) {
                playersName.add(player.getName().getString());
            }
        }
        return playersName.contains(username);
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Double> getPlayerPos(String username) {
        ServerPlayerEntity existingPlayer = null;
        for (ServerPlayerEntity player : getPlayers()) {
            if (player.getName().getString().equals(username)) {
                if (isInRange(getPos(), player, AdvancedPeripheralsConfig.playerDetMaxRange)) {
                    existingPlayer = player;
                    break;
                }
            }
        }
        if (existingPlayer == null) {
            return null;
        }
        Map<String, Double> coordinates = new HashMap<>();
        coordinates.put("x", Math.floor(existingPlayer.getX()));
        coordinates.put("y", Math.floor(existingPlayer.getY()));
        coordinates.put("z", Math.floor(existingPlayer.getZ()));
        return coordinates;
    }

    private List<ServerPlayerEntity> getPlayers() {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
    }

    private boolean isInRange(BlockPos pos, PlayerEntity player, int range) {
        World world = getWorld();
        return world.getNearbyPlayers(new EntityPredicate().allowInvulnerable().allowNonAttackable().allowUnseeable().allowSameTeam(),
                null, new AxisAlignedBB(pos.offset(range, range, range), pos.offset(-range, -range, -range))).contains(player);
    }
}
