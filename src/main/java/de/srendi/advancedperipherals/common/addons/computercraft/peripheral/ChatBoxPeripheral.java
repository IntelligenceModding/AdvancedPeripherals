package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.IPeripheralOperation;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.OperationPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;

import java.util.Collections;
import java.util.List;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SimpleFreeOperation.CHAT_MESSAGE;

public class ChatBoxPeripheral extends OperationPeripheral {

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
    public List<IPeripheralOperation<?>> possibleOperations() {
        return Collections.singletonList(CHAT_MESSAGE);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableChatBox;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendFormattedMessage(@Nonnull IArguments arguments) throws LuaException {
        if (isOnCooldown(CHAT_MESSAGE))
            throw new LuaException("You are sending messages too often. You can modify the cooldown in the config.");
        String message = arguments.getString(0);
        IFormattableTextComponent component = ITextComponent.Serializer.fromJson(message);
        if (component == null)
            return MethodResult.of(null, "incorrect json");
        IFormattableTextComponent preparedMessage = new StringTextComponent(String.format(PREFIX_FORMAT, arguments.optString(1, AdvancedPeripheralsConfig.defaultChatBoxPrefix))).append(message);
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            player.sendMessage(preparedMessage, Util.NIL_UUID);
        }
        trackOperation(CHAT_MESSAGE, null);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final void sendMessage(@Nonnull IArguments arguments) throws LuaException {
        if (isOnCooldown(CHAT_MESSAGE))
            throw new LuaException("You are sending messages too often. You can modify the cooldown in the config.");
        String message = arguments.getString(0);
        IFormattableTextComponent preparedMessage = new StringTextComponent(String.format(PREFIX_FORMAT, arguments.optString(1, AdvancedPeripheralsConfig.defaultChatBoxPrefix))).append(message);
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            player.sendMessage(preparedMessage, Util.NIL_UUID);
        }
        trackOperation(CHAT_MESSAGE, null);
    }

    @LuaFunction(mainThread = true)
    public final void sendMessageToPlayer(@Nonnull IArguments arguments) throws LuaException {
        if (isOnCooldown(CHAT_MESSAGE))
            throw new LuaException("You are sending messages too often. You can modify the cooldown in the config.");
        String message = arguments.getString(0);
        String playerName = arguments.getString(1);
        IFormattableTextComponent preparedMessage = new StringTextComponent(String.format(PREFIX_FORMAT, arguments.optString(2, AdvancedPeripheralsConfig.defaultChatBoxPrefix))).append(message);
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (player.getName().getString().equals(playerName))
                player.sendMessage(preparedMessage, Util.NIL_UUID);
        }
        trackOperation(CHAT_MESSAGE, null);
    }

}
