package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.OperationPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ChatBoxPeripheral extends OperationPeripheral {

    private final static String SEND_MESSAGE_OPERATION = "sendMessage";

    public ChatBoxPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    public ChatBoxPeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    public ChatBoxPeripheral(String type, IPocketAccess pocket) {
        super(type, pocket);
    }

    @Override
    protected int getRawCooldown(String name) {
        if (name.equals(SEND_MESSAGE_OPERATION))
            // Some legacy logic, cooldown defined in seconds
            // and we need miliseconds here
            return AdvancedPeripheralsConfig.chatBoxCooldown * 1000;
        return 0;
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableChatBox;
    }

    @LuaFunction
    public final int getSendCooldown() {
        return getCurrentCooldown(SEND_MESSAGE_OPERATION);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendFormattedMessage(String message) throws LuaException {
        if (isOnCooldown(SEND_MESSAGE_OPERATION))
            throw new LuaException("You are sending messages too often. You can modify the cooldown in the config.");
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            ITextComponent component = ITextComponent.Serializer.fromJson(message);
            if (component == null)
                return MethodResult.of(null, "incorrect json");
            player.sendMessage(component, Util.NIL_UUID);
        }
        trackOperation(SEND_MESSAGE_OPERATION);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final void sendMessage(Object message) throws LuaException {
        if (isOnCooldown(SEND_MESSAGE_OPERATION))
            throw new LuaException("You are sending messages too often. You can modify the cooldown in the config.");
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            player.sendMessage(new StringTextComponent(message.toString()), Util.NIL_UUID);
        }
        trackOperation(SEND_MESSAGE_OPERATION);
    }

    @LuaFunction(mainThread = true)
    public final void sendMessageToPlayer(Object message, String playerName) throws LuaException {
        if (isOnCooldown(SEND_MESSAGE_OPERATION))
            throw new LuaException("You are sending messages too often. You can modify the cooldown in the config.");
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (player.getName().getString().equals(playerName))
                player.sendMessage(new StringTextComponent(message.toString()), Util.NIL_UUID);
        }
        trackOperation(SEND_MESSAGE_OPERATION);
    }

}
