package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.lib.peripherals.owner.PocketPeripheralOwner;
import de.srendi.advancedperipherals.lib.peripherals.owner.TileEntityPeripheralOwner;
import de.srendi.advancedperipherals.lib.peripherals.owner.TurtlePeripheralOwner;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerDetectorPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String TYPE = "playerDetector";

    public PlayerDetectorPeripheral(PeripheralTileEntity<?> tileEntity) {
        super(TYPE, new TileEntityPeripheralOwner<>(tileEntity));
    }

    public PlayerDetectorPeripheral(ITurtleAccess access, TurtleSide side) {
        super(TYPE, new TurtlePeripheralOwner(access, side));
    }

    public PlayerDetectorPeripheral(IPocketAccess pocket) {
        super(TYPE, new PocketPeripheralOwner(pocket));
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
        for (ServerPlayerEntity player : getPlayers()) {
            if (isInRange(player, firstPos, secondPos))
                playersName.add(player.getName().getString());
        }
        return playersName;
    }

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInCubic(int x, int y, int z) {
        List<String> playersName = new ArrayList<>();
        for (ServerPlayerEntity player : getPlayers()) {
            if (isInRange(getPos(), player, x, y, z))
                playersName.add(player.getName().getString());
        }
        return playersName;
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
    public final boolean isPlayersInCoords(Map<?, ?> firstCoord, Map<?, ?> secondCoord) throws LuaException {
        if (getPlayers().isEmpty())
            return false;
        BlockPos firstPos = LuaConverter.convertToBlockPos(firstCoord);
        BlockPos secondPos = LuaConverter.convertToBlockPos(secondCoord);
        for (ServerPlayerEntity player : getPlayers()) {
            if (isInRange(player, firstPos, secondPos))
                return true;
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInCubic(int x, int y, int z) {
        if (getPlayers().isEmpty())
            return false;
        for (ServerPlayerEntity player : getPlayers()) {
            if (isInRange(getPos(), player, x, y, z))
                return true;
        }
        return false;
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
    public final boolean isPlayerInCoords(Map<?, ?> firstCoord, Map<?, ?> secondCoord, String username) throws LuaException {
        List<String> playersName = new ArrayList<>();
        BlockPos firstPos = LuaConverter.convertToBlockPos(firstCoord);
        BlockPos secondPos = LuaConverter.convertToBlockPos(secondCoord);
        for (PlayerEntity player : getPlayers()) {
            if (isInRange(player, firstPos, secondPos)) {
                playersName.add(player.getName().getString());
            }
        }
        return playersName.contains(username);
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInCubic(int x, int y, int z, String username) {
        List<String> playersName = new ArrayList<>();
        for (PlayerEntity player : getPlayers()) {
            if (isInRange(getPos(), player, x, y, z)) {
                playersName.add(player.getName().getString());
            }
        }
        return playersName.contains(username);
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
    public final Map<String, Object> getPlayerPos(String username) throws LuaException {
        if(!AdvancedPeripheralsConfig.playerSpy)
            throw new LuaException("This function is disabled in the config. Activate it or ask an admin if he can activate it.");
        ServerPlayerEntity existingPlayer = null;
        for (ServerPlayerEntity player : getPlayers()) {
            if (player.getName().getString().equals(username)) {
                if (isInRange(getPos(), player, AdvancedPeripheralsConfig.playerDetMaxRange)) {
                    existingPlayer = player;
                    break;
                }
            }
        }
        if (existingPlayer == null)
            return null;

        Map<String, Object> info = new HashMap<>();
        info.put("x", Math.floor(existingPlayer.getX()));
        info.put("y", Math.floor(existingPlayer.getY()));
        info.put("z", Math.floor(existingPlayer.getZ()));
        if(AdvancedPeripheralsConfig.morePlayerInformation) {
            info.put("yaw", existingPlayer.yRot);
            info.put("pitch", existingPlayer.xRot);
            info.put("dimension", existingPlayer.getLevel().dimension().location().toString());
            info.put("eyeHeight", existingPlayer.getEyeHeight());
        }
        return info;
    }

    private List<ServerPlayerEntity> getPlayers() {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
    }

    private boolean isInRange(BlockPos pos, PlayerEntity player, int range) {
        World world = getWorld();
        return world.getNearbyPlayers(new EntityPredicate().allowInvulnerable().allowNonAttackable().allowUnseeable().allowSameTeam(),
                null, new AxisAlignedBB(pos.offset(range, range, range), pos.offset(-range, -range, -range))).contains(player);
    }

    private boolean isInRange(BlockPos pos, PlayerEntity player, int x, int y, int z) {
        World world = getWorld();
        return world.getNearbyPlayers(new EntityPredicate().allowInvulnerable().allowNonAttackable().allowUnseeable().allowSameTeam(),
                null, new AxisAlignedBB(pos.offset(x, y, z), pos.offset(-x, -y, -z))).contains(player);
    }

    private boolean isInRange(PlayerEntity player, BlockPos firstPos, BlockPos secondPos) {
        World world = getWorld();

        return world.getNearbyPlayers(new EntityPredicate().allowInvulnerable().allowNonAttackable().allowUnseeable().allowSameTeam(),
                null, new AxisAlignedBB(firstPos, secondPos)).contains(player);
    }
}
