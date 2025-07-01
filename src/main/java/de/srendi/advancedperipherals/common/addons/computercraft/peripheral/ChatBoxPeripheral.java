package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.google.gson.JsonSyntaxException;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
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
import de.srendi.advancedperipherals.common.events.Events;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import de.srendi.advancedperipherals.common.util.StringUtil;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralFunction;
import de.srendi.advancedperipherals.network.APNetworking;
import de.srendi.advancedperipherals.network.toclient.ToastToClientPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SimpleFreeOperation.CHAT_MESSAGE;
import static de.srendi.advancedperipherals.common.commands.APCommands.ROOT_SAFE_EXEC_LITERAL;

public class ChatBoxPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "chatBox";

    private long lastConsumedMessage;

    protected ChatBoxPeripheral(IPeripheralOwner owner) {
        super(PERIPHERAL_TYPE, owner);
        owner.attachOperation(CHAT_MESSAGE);
        lastConsumedMessage = Events.getLastChatMessageID() - 1;
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

    private static MutableComponent createFormattedError(String message) {
        return Component.literal("[AP] " + message).setStyle(Style.EMPTY.withColor(ChatFormatting.RED).withBold(true));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableChatBox.get();
    }

    protected MethodResult withChatOperation(IPeripheralFunction<Object, MethodResult> function) throws LuaException {
        return withOperation(CHAT_MESSAGE, null, null, function, null);
    }

    @Nullable
    protected ComponentContents filterComponentContents(@NotNull ComponentContents content) {
        return content;
    }

    private boolean isChatBoxPreventingRunCommand() {
        return APConfig.PERIPHERALS_CONFIG.chatBoxPreventRunCommand.get();
    }

    private List<Predicate<String>> getChatBoxCommandFilters() {
        return APConfig.PERIPHERALS_CONFIG.getChatBoxCommandFilters();
    }

    private boolean shouldWrapCommand(String command) {
        return APConfig.PERIPHERALS_CONFIG.chatBoxWrapCommand.get();
    }

    private boolean isCommandBanned(String command) {
        for (Predicate<String> pattern : getChatBoxCommandFilters()) {
            if (pattern.test(command)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    protected Style filterComponentStyle(@NotNull Style style) {
        ClickEvent click = style.getClickEvent();
        if (click != null) {
            if (isChatBoxPreventingRunCommand() && click.getAction() == ClickEvent.Action.RUN_COMMAND) {
                style = style
                    .withClickEvent(null)
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, createFormattedError("'run_command' action is banned")));
            } else if (click.getAction() == ClickEvent.Action.RUN_COMMAND || click.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                String command = click.getValue();
                if (command.length() > 0 && command.charAt(0) == '/') {
                    if (isCommandBanned(command)) {
                        style = style
                            .withClickEvent(null)
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, createFormattedError("Command `" + command + "` is banned")));
                    } else if (shouldWrapCommand(command)) {
                        style = style.withClickEvent(new ClickEvent(click.getAction(), "/" + ROOT_SAFE_EXEC_LITERAL + " " + command));
                    }
                }
            }
        }
        HoverEvent hover = style.getHoverEvent();
        if (hover != null) {
            HoverEvent.ItemStackInfo itemInfo = hover.getValue(HoverEvent.Action.SHOW_ITEM);
            if (itemInfo != null) {
                try {
                    itemInfo.getItemStack().getTooltipLines(null, TooltipFlag.Default.ADVANCED);
                } catch (RuntimeException e) {
                    style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, createFormattedError("Invalid item")));
                }
            }
        }
        return style;
    }

    @Nullable
    protected MutableComponent filterMessage(@NotNull Component message) {
        ComponentContents content = filterComponentContents(message.getContents());
        if (content == null) {
            return null;
        }
        MutableComponent out = MutableComponent.create(content);
        if (message instanceof MutableComponent mc) {
            Style style = filterComponentStyle(mc.getStyle());
            if (style == null) {
                return null;
            }
            out.setStyle(style);
        }
        for (Component comp : message.getSiblings()) {
            MutableComponent filtered = filterMessage(comp);
            if (filtered == null) {
                return null;
            }
            out.append(filtered);
        }
        return out;
    }

    @Nullable
    private MutableComponent appendPrefix(String prefix, String brackets, String color) {
        Component prefixComponent = Component.literal(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix.get());
        if (!prefix.isEmpty()) {
            MutableComponent formattablePrefix;
            try {
                formattablePrefix = Component.Serializer.fromJson(prefix);
                prefixComponent = formattablePrefix;
            } catch (JsonSyntaxException exception) {
                AdvancedPeripherals.debug("Non json prefix, using plain text instead.");
                prefixComponent = Component.literal(prefix);
            }
        }
        if (brackets.isEmpty()) brackets = "[]";

        return filterMessage(Component.literal(color + brackets.charAt(0) + "\u00a7r").append(prefixComponent).append(color + brackets.charAt(1) + "\u00a7r "));
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

    // 0 message, 1 prefix, 2 brackets, 3 color, 4 range, 5 utf8compatible
    @LuaFunction(mainThread = true)
    public final MethodResult sendFormattedMessage(@NotNull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            boolean useUTF8 = arguments.optBoolean(5, false);

            String message = arguments.getString(0);

            if (useUTF8) {
                message = StringUtil.byteStringToUTF8(message);
            }

            ResourceKey<Level> dimension = getLevel().dimension();
            MutableComponent component = Component.Serializer.fromJson(message);
            if (component == null) {
                return MethodResult.of(null, "incorrect json");
            }
            component = filterMessage(component);
            if (component == null) {
                return MethodResult.of(null, "illegal message");
            }

            Optional<String> brackets = arguments.optString(2);
            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }

            if (checkBrackets(brackets)) {
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");
            }

            Optional<String> prefix = arguments.optString(1);
            if (useUTF8) {
                prefix = prefix.map(StringUtil::byteStringToUTF8);
            }

            String bracketColor = arguments.optString(3, "");
            if (useUTF8) {
                bracketColor = StringUtil.byteStringToUTF8(bracketColor);
            }

            MutableComponent preparedMessage = appendPrefix(
                StringUtil.convertAndToSectionMark(prefix.orElseGet(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix)),
                brackets.orElse("[]"),
                StringUtil.convertAndToSectionMark(bracketColor)
            );
            if (preparedMessage == null) {
                return MethodResult.of(null, "illegal prefix");
            }
            preparedMessage.append(component);

            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = arguments.optInt(4, -1);
            if (
                APConfig.PERIPHERALS_CONFIG.chatBoxBroadcast.get()
                    && APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get()
                    && maxRange == -1
                    && range == -1
            ) {
                ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastSystemMessage(preparedMessage, false);
                return MethodResult.of(true);
            }

            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.getLevel().dimension() != dimension) {
                    continue;
                }
                if (CoordUtil.isInRange(getPos(), getLevel(), player, range, maxRange)) {
                    player.sendSystemMessage(preparedMessage);
                }
            }
            return MethodResult.of(true);
        });
    }

    // 0 message, 1 prefix, 2 brackets, 3 color, 4 range, 5 utf8compatible
    @LuaFunction(mainThread = true)
    public final MethodResult sendMessage(@NotNull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            boolean useUTF8 = arguments.optBoolean(5, false);

            String message = arguments.getString(0);

            if (useUTF8) {
                message = StringUtil.byteStringToUTF8(message);
            }

            Optional<String> brackets = arguments.optString(2);
            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }

            ResourceKey<Level> dimension = getLevel().dimension();
            if (checkBrackets(brackets)) {
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");
            }

            Optional<String> prefix = arguments.optString(1);
            if (useUTF8) {
                prefix = prefix.map(StringUtil::byteStringToUTF8);
            }

            String bracketColor = arguments.optString(3, "");
            if (useUTF8) {
                bracketColor = StringUtil.byteStringToUTF8(bracketColor);
            }

            MutableComponent preparedMessage = appendPrefix(
                StringUtil.convertAndToSectionMark(prefix.orElseGet(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix)),
                brackets.orElse("[]"),
                StringUtil.convertAndToSectionMark(bracketColor)
            );
            if (preparedMessage == null) {
                return MethodResult.of(null, "illegal prefix");
            }
            preparedMessage.append(message);

            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = arguments.optInt(4, -1);
            if (
                APConfig.PERIPHERALS_CONFIG.chatBoxBroadcast.get()
                    && APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get()
                    && maxRange == -1
                    && range == -1
            ) {
                ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastSystemMessage(preparedMessage, false);
                return MethodResult.of(true);
            }

            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.getLevel().dimension() != dimension) {
                    continue;
                }
                if (CoordUtil.isInRange(getPos(), getLevel(), player, range, maxRange)) {
                    player.sendSystemMessage(preparedMessage);
                }
            }
            return MethodResult.of(true);
        });
    }

    // 0 message, 1 playerName, 2 prefix, 3 brackets, 4 color, 5 range
    @LuaFunction(mainThread = true)
    public final MethodResult sendFormattedMessageToPlayer(@NotNull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            boolean useUTF8 = arguments.optBoolean(5, false);

            String message = arguments.getString(0);
            if (useUTF8) {
                message = StringUtil.byteStringToUTF8(message);
            }

            String playerName = arguments.getString(1);
            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = arguments.optInt(5, -1);
            ResourceKey<Level> dimension = getLevel().dimension();
            ServerPlayer player = getPlayer(playerName);
            if (player == null) {
                return MethodResult.of(null, "incorrect player name/uuid");
            }

            MutableComponent component = Component.Serializer.fromJson(message);
            if (component == null) {
                return MethodResult.of(null, "incorrect json");
            }
            component = filterMessage(component);
            if (component == null) {
                return MethodResult.of(null, "illegal message");
            }

            Optional<String> brackets = arguments.optString(3);
            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }

            if (checkBrackets(brackets)) {
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");
            }

            Optional<String> prefix = arguments.optString(2);
            if (useUTF8) {
                prefix = prefix.map(StringUtil::byteStringToUTF8);
            }

            String bracketColor = arguments.optString(4, "");
            if (useUTF8) {
                bracketColor = StringUtil.byteStringToUTF8(bracketColor);
            }


            MutableComponent preparedMessage = appendPrefix(
                StringUtil.convertAndToSectionMark(prefix.orElseGet(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix)),
                brackets.orElse("[]"),
                StringUtil.convertAndToSectionMark(bracketColor)
            );
            if (preparedMessage == null) {
                return MethodResult.of(null, "illegal prefix");
            }
            preparedMessage.append(component);
            if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.getLevel().dimension() != dimension) {
                return MethodResult.of(false, "NOT_SAME_DIMENSION");
            }

            if (CoordUtil.isInRange(getPos(), getLevel(), player, range, maxRange)) {
                player.sendSystemMessage(preparedMessage);
            }
            return MethodResult.of(true);
        });
    }


    // 0 message, 1 title, 2 playerName, 3 prefix, 4 brackets, 5 bracket color, 6 range, 7 utf8compatible
    @LuaFunction(mainThread = true)
    public final MethodResult sendFormattedToastToPlayer(@NotNull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            boolean useUTF8 = arguments.optBoolean(7, false);

            String message = arguments.getString(0);
            String title = arguments.getString(1);
            if (useUTF8) {
                message = StringUtil.byteStringToUTF8(message);
                title = StringUtil.byteStringToUTF8(title);
            }
            String playerName = arguments.getString(2);
            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = arguments.optInt(6, -1);
            ResourceKey<Level> dimension = getLevel().dimension();
            ServerPlayer player = getPlayer(playerName);
            if (player == null) {
                return MethodResult.of(null, "incorrect player name/uuid");
            }

            MutableComponent messageComponent = Component.Serializer.fromJson(message);
            if (messageComponent == null) {
                return MethodResult.of(null, "incorrect json for message");
            }
            messageComponent = filterMessage(messageComponent);
            if (messageComponent == null) {
                return MethodResult.of(null, "illegal message");
            }

            MutableComponent titleComponent = Component.Serializer.fromJson(title);
            if (titleComponent == null) {
                return MethodResult.of(null, "incorrect json for title");
            }
            titleComponent = filterMessage(titleComponent);
            if (titleComponent == null) {
                return MethodResult.of(null, "illegal title");
            }

            Optional<String> brackets = arguments.optString(4);

            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }

            if (checkBrackets(brackets)) {
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ,,,)");
            }


            Optional<String> prefix = arguments.optString(3);
            if (useUTF8) {
                prefix = prefix.map(StringUtil::byteStringToUTF8);
            }


            String bracketColor = arguments.optString(5, "");
            if (useUTF8) {
                bracketColor = StringUtil.byteStringToUTF8(bracketColor);
            }

            MutableComponent preparedMessage = appendPrefix(
                StringUtil.convertAndToSectionMark(prefix.orElseGet(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix)),
                brackets.orElse("[]"),
                StringUtil.convertAndToSectionMark(bracketColor)
            );
            if (preparedMessage == null) {
                return MethodResult.of(null, "illegal prefix");
            }
            preparedMessage.append(messageComponent);

            if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.getLevel().dimension() != dimension) {
                return MethodResult.of(false, "NOT_SAME_DIMENSION");
            }

            if (CoordUtil.isInRange(getPos(), getLevel(), player, range, maxRange)) {
                ToastToClientPacket packet = new ToastToClientPacket(titleComponent, preparedMessage);
                APNetworking.sendTo(packet, player);
            }

            return MethodResult.of(true);
        });
    }

    // 0 message, 1 playerName, 2 prefix, 3 brackets, 4 bracket color, 5 range, 6 utf8compatible
    @LuaFunction(mainThread = true)
    public final MethodResult sendMessageToPlayer(@NotNull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            boolean useUTF8 = arguments.optBoolean(6, false);

            String message = arguments.getString(0);
            if (useUTF8) {
                message = StringUtil.byteStringToUTF8(message);
            }
            String playerName = arguments.getString(1);
            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = arguments.optInt(5, -1);
            ResourceKey<Level> dimension = getLevel().dimension();
            ServerPlayer player = getPlayer(playerName);
            if (player == null) {
                return MethodResult.of(null, "incorrect player name/uuid");
            }

            Optional<String> brackets = arguments.optString(3);

            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }

            if (checkBrackets(brackets)) {
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");
            }

            Optional<String> prefix = arguments.optString(2);

            if (useUTF8) {
                prefix = prefix.map(StringUtil::byteStringToUTF8);
            }
            String bracketColor = arguments.optString(4, "");
            if (useUTF8) {
                bracketColor = StringUtil.byteStringToUTF8(bracketColor);
            }

            MutableComponent preparedMessage = appendPrefix(
                StringUtil.convertAndToSectionMark(prefix.orElseGet(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix)),
                brackets.orElse("[]"),
                StringUtil.convertAndToSectionMark(bracketColor)
            );
            if (preparedMessage == null) {
                return MethodResult.of(null, "illegal prefix");
            }
            preparedMessage.append(message);
            if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.getLevel().dimension() != dimension) {
                return MethodResult.of(false, "NOT_SAME_DIMENSION");
            }

            if (CoordUtil.isInRange(getPos(), getLevel(), player, range, maxRange)) {
                player.sendSystemMessage(preparedMessage, false);
            }
            return MethodResult.of(true);
        });
    }

    // 0 message, 1 title, 2 playerName, 3 prefix, 4 brackets, 5 bracket color, 6 range, 7 utf8compatible
    @LuaFunction(mainThread = true)
    public final MethodResult sendToastToPlayer(@NotNull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            boolean useUTF8 = arguments.optBoolean(7, false);

            String message = arguments.getString(0);
            String title = arguments.getString(1);

            if (useUTF8) {
                message = StringUtil.byteStringToUTF8(message);
                title = StringUtil.byteStringToUTF8(title);
            }

            String playerName = arguments.getString(2);
            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = arguments.optInt(6, -1);
            ResourceKey<Level> dimension = getLevel().dimension();
            ServerPlayer player = getPlayer(playerName);
            if (player == null) {
                return MethodResult.of(null, "incorrect player name/uuid");
            }

            Optional<String> brackets = arguments.optString(4);

            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }

            if (checkBrackets(brackets)) {
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");
            }

            Optional<String> prefix = arguments.optString(3);

            if (useUTF8) {
                prefix = prefix.map(StringUtil::byteStringToUTF8);
            }

            String bracketColor = arguments.optString(5, "");
            if (useUTF8) {
                bracketColor = StringUtil.byteStringToUTF8(bracketColor);
            }

            MutableComponent preparedMessage = appendPrefix(
                StringUtil.convertAndToSectionMark(prefix.orElseGet(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix)),
                brackets.orElse("[]"),
                StringUtil.convertAndToSectionMark(bracketColor)
            );
            if (preparedMessage == null) {
                return MethodResult.of(null, "illegal prefix");
            }
            preparedMessage.append(message);

            if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.getLevel().dimension() != dimension) {
                return MethodResult.of(false, "NOT_SAME_DIMENSION");
            }

            if (CoordUtil.isInRange(getPos(), getLevel(), player, range, maxRange)) {
                ToastToClientPacket packet = new ToastToClientPacket(Component.literal(title), preparedMessage);
                APNetworking.sendTo(packet, player);
            }
            return MethodResult.of(true);
        });
    }

    public void update() {
        lastConsumedMessage = Events.traverseChatMessages(lastConsumedMessage, message -> {
            String byteString = StringUtil.utf8ToByteString(message.message());
            for (IComputerAccess computer : getConnectedComputers()) {
                computer.queueEvent("chat", message.username(), message.message(), message.uuid(), message.isHidden(),
                    byteString);
            }
        });
    }
}
