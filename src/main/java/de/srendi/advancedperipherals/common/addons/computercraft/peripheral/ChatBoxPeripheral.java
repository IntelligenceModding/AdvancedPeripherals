package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.IArguments;
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
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;

public class ChatBoxPeripheral extends OperationPeripheral {

    private final static String SEND_MESSAGE_OPERATION = "sendMessage";
    private final static String PREFIX_FORMAT = "[%s] ";

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
    public final MethodResult sendFormattedMessage(@Nonnull IArguments arguments) throws LuaException {
        if (isOnCooldown(SEND_MESSAGE_OPERATION))
            throw new LuaException("You are sending messages too often. You can modify the cooldown in the config.");
        String message = arguments.getString(0);
        IFormattableTextComponent prefix = new StringTextComponent(String.format(PREFIX_FORMAT, arguments.optString(1, AdvancedPeripheralsConfig.defaultChatBoxPrefix)));
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            IFormattableTextComponent component = ITextComponent.Serializer.fromJson(message);
            if (component == null)
                return MethodResult.of(null, "incorrect json");
            player.sendMessage(prefix.append(component), Util.NIL_UUID);
        }
        trackOperation(SEND_MESSAGE_OPERATION);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final void sendMessage(@Nonnull IArguments arguments) throws LuaException {
        if (isOnCooldown(SEND_MESSAGE_OPERATION))
            throw new LuaException("You are sending messages too often. You can modify the cooldown in the config.");
        String message = arguments.getString(0);
        IFormattableTextComponent prefix = new StringTextComponent(String.format(PREFIX_FORMAT, arguments.optString(1, AdvancedPeripheralsConfig.defaultChatBoxPrefix)));
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            player.sendMessage(prefix.append(message), Util.NIL_UUID);
        }
        trackOperation(SEND_MESSAGE_OPERATION);
    }

    @LuaFunction(mainThread = true)
    public final void sendMessageToPlayer(@Nonnull IArguments arguments) throws LuaException {
        if (isOnCooldown(SEND_MESSAGE_OPERATION))
            throw new LuaException("You are sending messages too often. You can modify the cooldown in the config.");
        String message = arguments.getString(0);
        String playerName = arguments.getString(1);
        IFormattableTextComponent prefix = new StringTextComponent(String.format(PREFIX_FORMAT, arguments.optString(2, AdvancedPeripheralsConfig.defaultChatBoxPrefix)));
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (player.getName().getString().equals(playerName))
                player.sendMessage(prefix.append(message), Util.NIL_UUID);
        }
        trackOperation(SEND_MESSAGE_OPERATION);
    }

}
