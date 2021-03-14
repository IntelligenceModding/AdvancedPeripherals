package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerDetectorPeripheral extends BasePeripheral {

    public PlayerDetectorPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    public PlayerDetectorPeripheral(String type, TileEntity tileEntity) {
        super(type, tileEntity);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enablePlayerDetector;
    }

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInRange(int range) throws LuaException {
        double rangeNegative = -(double) range;
        List<PlayerEntity> players = tileEntity.getWorld().getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(tileEntity.getPos().add(rangeNegative, rangeNegative, rangeNegative), tileEntity.getPos().add((double) range + 1, (double) range + 1, (double) range + 1)));
        List<String> playersName = new ArrayList<>();
        for (PlayerEntity name : players) {
            playersName.add(name.getName().getString());
        }
        return playersName;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInRange(int range) throws LuaException {
        double rangeNegative = -(double) range;
        List<PlayerEntity> players = tileEntity.getWorld().getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(tileEntity.getPos().add(rangeNegative, rangeNegative, rangeNegative), tileEntity.getPos().add((double) range + 1, (double) range + 1, (double) range + 1)));
        return !players.isEmpty();
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInRange(int range, String username) throws LuaException {
        double rangeNegative = -(double) range;
        List<PlayerEntity> players = tileEntity.getWorld().getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(tileEntity.getPos().add(rangeNegative, rangeNegative, rangeNegative), tileEntity.getPos().add((double) range + 1, (double) range + 1, (double) range + 1)));
        List<String> playersName = new ArrayList<>();
        for (PlayerEntity name : players) {
            playersName.add(name.getName().getString());
        }
        return playersName.contains(username);
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Double> getPlayerPos(String username) {
        List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        ServerPlayerEntity existingPlayer = null;
        for(ServerPlayerEntity player : players) {
           if(player.getName().getString().equals(username)) {
               existingPlayer = player;
               break;
           }
        }
        if(existingPlayer == null)
            return null;
        Map<String, Double> coordinates = new HashMap<>();
        coordinates.put("x", Math.floor(existingPlayer.getPosX()));
        coordinates.put("y", Math.floor(existingPlayer.getPosY()));
        coordinates.put("z", Math.floor(existingPlayer.getPosZ()));
        return coordinates;
    }
}
