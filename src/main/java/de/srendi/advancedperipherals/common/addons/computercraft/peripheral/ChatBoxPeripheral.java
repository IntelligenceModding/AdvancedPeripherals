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
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SimpleFreeOperation.CHAT_MESSAGE;

public class ChatBoxPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "chatBox";

    protected ChatBoxPeripheral(IPeripheralOwner owner) {
        super(PERIPHERAL_TYPE, owner);
        owner.attachOperation(CHAT_MESSAGE);
    }

    public ChatBoxPeripheral(PeripheralBlockEntity<?> tileEntity) {
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
        return APConfig.PERIPHERALS_CONFIG.enableChatBox.get();
    }

    protected MethodResult withChatOperation(IPeripheralFunction<Object, MethodResult> function) throws LuaException {
        return withOperation(CHAT_MESSAGE, null, null, function, null);
    }

    private MutableComponent appendPrefix(String prefix, String brackets, String color) {
        Component prefixComponent = Component.literal(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix.get());
        if (!prefix.isEmpty()) {
            MutableComponent formattablePrefix;
            try {
                formattablePrefix = MutableComponent.Serializer.fromJson(prefix);
                prefixComponent = formattablePrefix;
            } catch (JsonSyntaxException exception) {
                AdvancedPeripherals.debug("Non json prefix, using plain text instead.");
                prefixComponent = Component.literal(prefix);
            }
        }
        if (brackets.isEmpty()) brackets = "[]";

        return Component.literal(color + brackets.charAt(0) + "\u00a7r").append(prefixComponent).append(color + brackets.charAt(1) + "\u00a7r ");
    }

    /**
     * @param argument uuid/name of a player
     * @return a player if the name/uuid belongs to a player
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
    public final MethodResult sendFormattedMessage(@NotNull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            String message = arguments.getString(0);
            int range = arguments.optInt(4, -1);
            MutableComponent component = Component.Serializer.fromJson(message);
            if (component == null) return MethodResult.of(null, "incorrect json");
            if (checkBrackets(arguments.optString(2)))
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");
            MutableComponent preparedMessage = appendPrefix(arguments.optString(1, APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix.get()), arguments.optString(2, "[]"), arguments.optString(3, "")).append(component);
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (range != -1) {
                    if (CoordUtil.isInRange(getPos(), getLevel(), player, range))
                        player.sendSystemMessage(preparedMessage);
                } else {
                    player.sendSystemMessage(preparedMessage);
                }
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendMessage(@NotNull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            String message = arguments.getString(0);
            int range = arguments.optInt(4, -1);
            if (checkBrackets(arguments.optString(2)))
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");
            MutableComponent preparedMessage = appendPrefix(arguments.optString(1, APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix.get()), arguments.optString(2, "[]"), arguments.optString(3, "")).append(message);
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (range != -1) {
                    if (CoordUtil.isInRange(getPos(), getLevel(), player, range))
                        player.sendSystemMessage(preparedMessage);
                } else {
                    player.sendSystemMessage(preparedMessage);
                }
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendFormattedMessageToPlayer(@NotNull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            String message = arguments.getString(0);
            String playerName = arguments.getString(1);
            ServerPlayer player = getPlayer(playerName);
            if (player == null) return MethodResult.of(null, "incorrect player name/uuid");
            MutableComponent component = Component.Serializer.fromJson(message);
            if (component == null) return MethodResult.of(null, "incorrect json");
            if (checkBrackets(arguments.optString(3)))
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");
            MutableComponent preparedMessage = appendPrefix(arguments.optString(2, APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix.get()), arguments.optString(3, "[]"), arguments.optString(4, "")).append(component);
            player.sendSystemMessage(preparedMessage);
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendMessageToPlayer(@NotNull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            String message = arguments.getString(0);
            String playerName = arguments.getString(1);
            ServerPlayer player = getPlayer(playerName);
            if (player == null) return MethodResult.of(null, "incorrect player name/uuid");
            if (checkBrackets(arguments.optString(3)))
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");
            MutableComponent preparedMessage = appendPrefix(arguments.optString(2, APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix.get()), arguments.optString(3, "[]"), arguments.optString(4, "")).append(message);
            player.sendSystemMessage(preparedMessage);
            return MethodResult.of(true);
        });
    }
}
