package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.google.gson.JsonSyntaxException;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.PocketPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralFunction;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SimpleFreeOperation.CHAT_MESSAGE;

public class ChatBoxPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String TYPE = "chatBox";

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
        return APConfig.PERIPHERALS_CONFIG.ENABLE_CHAT_BOX.get();
    }

    protected MethodResult withChatOperation(IPeripheralFunction<Object, MethodResult> function) throws LuaException {
        return withOperation(CHAT_MESSAGE, null, null, function, null);
    }

    private MutableComponent appendPrefix(String prefix, String brackets, String color) {
        TextComponent prefixComponent = new TextComponent(APConfig.PERIPHERALS_CONFIG.DEFAULT_CHAT_BOX_PREFIX.get());
        if (!prefix.isEmpty()) {
            MutableComponent formattablePrefix;
            try {
                formattablePrefix = MutableComponent.Serializer.fromJson(prefix);
                prefixComponent = (TextComponent) formattablePrefix;
            } catch (JsonSyntaxException exception) {
                AdvancedPeripherals.debug("Non json prefix, using plain text instead.");
                prefixComponent = new TextComponent(prefix);
            }
        }
        if (brackets.isEmpty())
            brackets = "[]";

        return new TextComponent(color + brackets.charAt(0) + "\u00a7r").append(prefixComponent).append(color + brackets.charAt(1) + "\u00a7r ");
    }

    /**
     * @param argument uuid/name of a player
     * @return true if the name/uuid belongs to a player
     */
    private ServerPlayer getPlayer(String argument) {
        if (argument.matches("\\b[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}\\b"))
            return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(UUID.fromString(argument));
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(argument);
    }

    /**
     * Checks if brackets are in correct format if present
     *
     * @param brackets {@link IArguments#optString(int) IArguments.optString()} to check
     * @return true if brackets are in the right format
     */
    private boolean checkBrackets(Optional<String> brackets) {
        return brackets.isPresent() && brackets.get().length() != 2;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendFormattedMessage(@Nonnull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            String message = arguments.getString(0);
            int range = arguments.optInt(4, -1);
            MutableComponent component = Component.Serializer.fromJson(message);
            if (component == null)
                return MethodResult.of(null, "incorrect json");
            if (checkBrackets(arguments.optString(2)))
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");
            MutableComponent preparedMessage = appendPrefix(
                    arguments.optString(1, APConfig.PERIPHERALS_CONFIG.DEFAULT_CHAT_BOX_PREFIX.get()),
                    arguments.optString(2, "[]"),
                    arguments.optString(3, "")
            ).append(component);
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (range != -1) {
                    if (CoordUtil.isInRange(getPos(), getLevel(), player, range))
                        player.sendMessage(preparedMessage, Util.NIL_UUID);
                } else {
                    player.sendMessage(preparedMessage, Util.NIL_UUID);
                }
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendMessage(@Nonnull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            String message = arguments.getString(0);
            int range = arguments.optInt(4, -1);
            if (checkBrackets(arguments.optString(2)))
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");
            MutableComponent preparedMessage = appendPrefix(
                    arguments.optString(1, APConfig.PERIPHERALS_CONFIG.DEFAULT_CHAT_BOX_PREFIX.get()),
                    arguments.optString(2, "[]"),
                    arguments.optString(3, "")
            ).append(message);
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (range != -1) {
                    if (CoordUtil.isInRange(getPos(), getLevel(), player, range))
                        player.sendMessage(preparedMessage, Util.NIL_UUID);
                } else
                    player.sendMessage(preparedMessage, Util.NIL_UUID);
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendFormattedMessageToPlayer(@Nonnull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            String message = arguments.getString(0);
            String playerName = arguments.getString(1);
            ServerPlayer player = getPlayer(playerName);
            if (player == null)
                return MethodResult.of(null, "incorrect player name/uuid");
            MutableComponent component = Component.Serializer.fromJson(message);
            if (component == null)
                return MethodResult.of(null, "incorrect json");
            if (checkBrackets(arguments.optString(3)))
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");
            MutableComponent preparedMessage = appendPrefix(
                    arguments.optString(2, APConfig.PERIPHERALS_CONFIG.DEFAULT_CHAT_BOX_PREFIX.get()),
                    arguments.optString(3, "[]"),
                    arguments.optString(4, "")
            ).append(component);
            player.sendMessage(preparedMessage, Util.NIL_UUID);
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendMessageToPlayer(@Nonnull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            String message = arguments.getString(0);
            String playerName = arguments.getString(1);
            ServerPlayer player = getPlayer(playerName);
            if (player == null)
                return MethodResult.of(null, "incorrect player name/uuid");
            if (checkBrackets(arguments.optString(3)))
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");
            MutableComponent preparedMessage = appendPrefix(
                    arguments.optString(2, APConfig.PERIPHERALS_CONFIG.DEFAULT_CHAT_BOX_PREFIX.get()),
                    arguments.optString(3, "[]"),
                    arguments.optString(4, "")
            ).append(message);
            player.sendMessage(preparedMessage, Util.NIL_UUID);
            return MethodResult.of(true);
        });
    }
}
