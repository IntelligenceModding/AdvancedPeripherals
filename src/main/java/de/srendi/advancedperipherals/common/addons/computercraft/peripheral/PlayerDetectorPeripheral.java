package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;

public class PlayerDetectorPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "playerDetector";

    public PlayerDetectorPeripheral(PeripheralBlockEntity<?> tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    public PlayerDetectorPeripheral(ITurtleAccess access, TurtleSide side) {
        super(PERIPHERAL_TYPE, new TurtlePeripheralOwner(access, side));
    }

    public PlayerDetectorPeripheral(IPocketAccess pocket) {
        super(PERIPHERAL_TYPE, new PocketPeripheralOwner(pocket));
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
    public final List<String> getPlayersInCoords(Map<?, ?> firstCoord, Map<?, ?> secondCoord) throws LuaException {
        List<String> playersName = new ArrayList<>();
        BlockPos firstPos = LuaConverter.convertToBlockPos(firstCoord);
        BlockPos secondPos = LuaConverter.convertToBlockPos(secondCoord);
        for (ServerPlayer player : getPlayers()) {
            if (CoordUtil.isInRange(getPos(), player, getLevel(), firstPos, secondPos))
                playersName.add(player.getName().getString());
        }
        return playersName;
    }

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInCubic(int x, int y, int z) {
        List<String> playersName = new ArrayList<>();
        for (ServerPlayer player : getPlayers()) {
            if (CoordUtil.isInRange(getPos(), getLevel(), player, x, y, z))
                playersName.add(player.getName().getString());
        }
        return playersName;
    }

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInRange(int range) {
        List<String> playersName = new ArrayList<>();
        for (ServerPlayer player : getPlayers()) {
            if (CoordUtil.isInRange(getPos(), getLevel(), player, range))
                playersName.add(player.getName().getString());
        }
        return playersName;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInCoords(Map<?, ?> firstCoord, Map<?, ?> secondCoord) throws LuaException {
        if (getPlayers().isEmpty()) return false;
        BlockPos firstPos = LuaConverter.convertToBlockPos(firstCoord);
        BlockPos secondPos = LuaConverter.convertToBlockPos(secondCoord);
        for (ServerPlayer player : getPlayers()) {
            if (CoordUtil.isInRange(getPos(), player, getLevel(), firstPos, secondPos)) return true;
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInCubic(int x, int y, int z) {
        if (getPlayers().isEmpty()) return false;
        for (ServerPlayer player : getPlayers()) {
            if (CoordUtil.isInRange(getPos(), getLevel(), player, x, y, z)) return true;
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInRange(int range) {
        if (getPlayers().isEmpty()) return false;
        for (ServerPlayer player : getPlayers()) {
            if (CoordUtil.isInRange(getPos(), getLevel(), player, range)) return true;
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInCoords(Map<?, ?> firstCoord, Map<?, ?> secondCoord, String username) throws LuaException {
        BlockPos firstPos = LuaConverter.convertToBlockPos(firstCoord);
        BlockPos secondPos = LuaConverter.convertToBlockPos(secondCoord);
        for (Player player : getPlayers()) {
            if (CoordUtil.isInRange(getPos(), player, getLevel(), firstPos, secondPos))
                if(player.getName().getString().equals(username))
                    return true;
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInCubic(int x, int y, int z, String username) {
        for (Player player : getPlayers()) {
            if (CoordUtil.isInRange(getPos(), getLevel(), player, x, y, z)) {
                if(player.getName().getString().equals(username))
                    return true;
            }
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInRange(int range, String username) {
        for (Player player : getPlayers()) {
            if (CoordUtil.isInRange(getPos(), getLevel(), player, range)) {
                if(player.getName().getString().equals(username))
                    return true;
            }
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getPlayerPos(String username) throws LuaException {
        if (!APConfig.PERIPHERALS_CONFIG.playerSpy.get())
            throw new LuaException("This function is disabled in the config. Activate it or ask an admin if he can activate it.");
        ServerPlayer existingPlayer = null;
        for (ServerPlayer player : getPlayers()) {
            if (player.getName().getString().equals(username)) {
                if (CoordUtil.isInRange(getPos(), getLevel(), player, APConfig.PERIPHERALS_CONFIG.playerDetMaxRange.get()))
                    existingPlayer = player;
                break;
            }
        }
        if (existingPlayer == null)
            return Collections.emptyMap();

        Map<String, Object> info = new HashMap<>();
        info.put("x", Math.floor(existingPlayer.getX()));
        info.put("y", Math.floor(existingPlayer.getY()));
        info.put("z", Math.floor(existingPlayer.getZ()));
        if (APConfig.PERIPHERALS_CONFIG.morePlayerInformation.get()) {
            info.put("yaw", existingPlayer.yRotO);
            info.put("pitch", existingPlayer.xRotO);
            info.put("dimension", existingPlayer.getLevel().dimension().location().toString());
            info.put("eyeHeight", existingPlayer.getEyeHeight());
        }
        return info;
    }

    private List<ServerPlayer> getPlayers() {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
    }

}
