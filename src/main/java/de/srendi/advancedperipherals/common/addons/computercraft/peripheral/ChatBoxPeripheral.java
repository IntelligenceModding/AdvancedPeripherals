package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralFunction;
import de.srendi.advancedperipherals.lib.peripherals.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.lib.peripherals.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.lib.peripherals.owner.PocketPeripheralOwner;
import de.srendi.advancedperipherals.lib.peripherals.owner.TurtlePeripheralOwner;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SimpleFreeOperation.CHAT_MESSAGE;

public class ChatBoxPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String TYPE = "chatBox";

    private final static String PREFIX_FORMAT = "[%s] ";

    protected ChatBoxPeripheral(IPeripheralOwner owner) {
        super(TYPE, owner);
        owner.attachOperation(CHAT_MESSAGE);
    }

    public ChatBoxPeripheral(PeripheralTileEntity<?> tileEntity) {
        this(new BlockEntityPeripheralOwner<>(tileEntity));
    }

    public ChatBoxPeripheral(ITurtleAccess turtle, TurtleSide side) {
        this(new TurtlePeripheralOwner(turtle, side));
    }

    public ChatBoxPeripheral(IPocketAccess pocket) {
        this(new PocketPeripheralOwner(pocket));
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableChatBox;
    }

    protected MethodResult withChatOperation(IPeripheralFunction<Object, MethodResult> function) throws LuaException {
        return withOperation(CHAT_MESSAGE, null, null, function, null);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendFormattedMessage(@Nonnull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            String message = arguments.getString(0);
            MutableComponent component = Component.Serializer.fromJson(message);
            if (component == null)
                return MethodResult.of(null, "incorrect json");
            MutableComponent preparedMessage = new TextComponent(String.format(PREFIX_FORMAT, arguments.optString(1, AdvancedPeripheralsConfig.defaultChatBoxPrefix))).append(component);
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                player.sendMessage(preparedMessage, Util.NIL_UUID);
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendMessage(@Nonnull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            String message = arguments.getString(0);
            MutableComponent preparedMessage = new TextComponent(String.format(PREFIX_FORMAT, arguments.optString(1, AdvancedPeripheralsConfig.defaultChatBoxPrefix))).append(message);
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                player.sendMessage(preparedMessage, Util.NIL_UUID);
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendMessageToPlayer(@Nonnull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            String message = arguments.getString(0);
            String playerName = arguments.getString(1);
            MutableComponent preparedMessage = new TextComponent(String.format(PREFIX_FORMAT, arguments.optString(2, AdvancedPeripheralsConfig.defaultChatBoxPrefix))).append(message);
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (player.getName().getString().equals(playerName))
                    player.sendMessage(preparedMessage, Util.NIL_UUID);
            }
            return MethodResult.of(true);
        });
    }

}
