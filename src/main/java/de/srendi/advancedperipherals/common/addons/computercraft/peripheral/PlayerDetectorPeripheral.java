package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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
    public final List<String> getPlayersInRange(int range) {
        List<String> playersName = new ArrayList<>();
        for (ServerPlayerEntity player : getPlayers()) {
            if(isInRange(tileEntity.getPos(), player, range))
                playersName.add(player.getName().getString());
        }
        return playersName;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInRange(int range) {
        if(getPlayers().isEmpty())
            return false;
        for(ServerPlayerEntity player : getPlayers()) {
            if(isInRange(tileEntity.getPos(), player, range))
                return true;
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInRange(int range, String username) {
         List<String> playersName = new ArrayList<>();
        for (PlayerEntity player : getPlayers()) {
            if(isInRange(tileEntity.getPos(), player, range))
            playersName.add(player.getName().getString());
        }
        return playersName.contains(username);
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Double> getPlayerPos(String username) {
        ServerPlayerEntity existingPlayer = null;
        for(ServerPlayerEntity player : getPlayers()) {
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

    private List<ServerPlayerEntity> getPlayers() {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
    }

    private boolean isInRange(BlockPos pos, PlayerEntity player, int range) {
        BlockPos pos1 = new BlockPos(pos.getX()-range, pos.getY()+range, pos.getZ()+range);
        BlockPos pos2 = new BlockPos(pos.getX()+range, pos.getY()-range, pos.getZ()-range);

        int x1 = Math.min(pos1.getX(), pos2.getX());
        int y1 = Math.min(pos1.getY(), pos2.getY());
        int z1 = Math.min(pos1.getZ(), pos2.getZ());
        int x2 = Math.max(pos1.getX(), pos2.getX());
        int y2 = Math.max(pos1.getY(), pos2.getY());
        int z2 = Math.max(pos1.getZ(), pos2.getZ());

        int x = player.getPosition().getX();
        int z = player.getPosition().getZ();
        int y = player.getPosition().getY();

        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }
}
