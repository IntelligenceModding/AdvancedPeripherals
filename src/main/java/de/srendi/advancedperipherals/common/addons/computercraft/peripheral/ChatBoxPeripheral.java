package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.google.gson.JsonParseException;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.lua.ObjectLuaTable;
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
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.toclient.NarrateToClientPacket;
import de.srendi.advancedperipherals.common.network.toclient.ToastToClientPacket;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
import de.srendi.advancedperipherals.common.util.StringUtil;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SimpleFreeOperation.CHAT_MESSAGE;
import static de.srendi.advancedperipherals.common.commands.APCommands.ROOT_CHATBOX_LITERAL;
import static de.srendi.advancedperipherals.common.commands.APCommands.ROOT_SAFE_EXEC_LITERAL;

public class ChatBoxPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "chat_box";
    private static final List<String> SAFE_COMMANDS = List.of(
        ROOT_CHATBOX_LITERAL,
        ROOT_SAFE_EXEC_LITERAL
    );

    private long lastConsumedMessage = Events.getLastChatMessageID();

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
        this(PocketPeripheralOwner.of(pocket));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableChatBox.get();
    }

    protected MethodResult withChatOperation(IPeripheralFunction<Void, MethodResult> function) throws LuaException {
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

    private List<String> getSafeCommands() {
        return SAFE_COMMANDS;
    }

    protected boolean shouldWrapCommand(String command) {
        if (!APConfig.PERIPHERALS_CONFIG.chatBoxWrapCommand.get()) {
            return false;
        }

        for (String safe : this.getSafeCommands()) {
            if (command.equals(safe) || command.startsWith(safe + " ")) {
                return false;
            }
        }

        return true;
    }

    protected boolean isCommandBanned(String command) {
        for (Predicate<String> pattern : getChatBoxCommandFilters()) {
            if (pattern.test(command)) {
                return true;
            }
        }
        return false;
    }

    private static MutableComponent createFormattedError(String message) {
        return Component.literal("[AP] " + message).setStyle(Style.EMPTY.withColor(ChatFormatting.RED).withBold(true));
    }

    private MutableComponent preparePrefix(String prefix, String brackets, String color) {
        Component prefixComponent = Component.literal(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix.get());
        if (!prefix.isEmpty()) {
            MutableComponent formattablePrefix = null;
            try {
                formattablePrefix = this.parseFormattedMessage(prefix);
            } catch (JsonParseException e) {
                AdvancedPeripherals.debug("Not valid json prefix, using plain text instead.");
            }
            prefixComponent = formattablePrefix != null ? formattablePrefix : Component.literal(prefix);
        }

        return this.filterMessage(
            Component.literal(color + brackets.charAt(0) + "\u00a7r")
                .append(prefixComponent)
                .append(color + brackets.charAt(1) + "\u00a7r ")
        );
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
                if (command.startsWith("/")) {
                    command = command.substring(1);
                }
                if (isCommandBanned(command)) {
                    style = style
                        .withClickEvent(null)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, createFormattedError("Command `" + command + "` is banned")));
                } else if (shouldWrapCommand(command)) {
                    style = style.withClickEvent(new ClickEvent(click.getAction(), "/" + ROOT_SAFE_EXEC_LITERAL + " " + command));
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
    protected MutableComponent filterMessage(Component message) {
        if (message == null) {
            return null;
        }
        ComponentContents content = this.filterComponentContents(message.getContents());
        if (content == null) {
            return null;
        }
        MutableComponent out = MutableComponent.create(content);
        if (message instanceof MutableComponent mc) {
            Style style = this.filterComponentStyle(mc.getStyle());
            if (style == null) {
                return null;
            }
            out.setStyle(style);
        }
        for (Component comp : message.getSiblings()) {
            MutableComponent filtered = this.filterMessage(comp);
            if (filtered == null) {
                return null;
            }
            out.append(filtered);
        }
        return out;
    }

    @Nullable
    protected MutableComponent parseFormattedMessage(@NotNull String message) {
        return this.filterMessage(Component.Serializer.fromJson(message));
    }

    /**
     * @param argument uuid/name of a player
     * @return a player if the name/uuid belongs to a player
     */
    private ServerPlayer getPlayer(String argument) {
        MinecraftServer server = getLevel().getServer();
        UUID uuid;
        try {
            uuid = UUID.fromString(argument);
        } catch (IllegalArgumentException e) {
            uuid = null;
        }
        if (uuid != null) {
            return server.getPlayerList().getPlayer(uuid);
        }
        return server.getPlayerList().getPlayerByName(argument);
    }

    /**
     * Checks if brackets are in correct format if present
     *
     * @param brackets the brackets to check
     * @return true if brackets are not in the right format
     */
    private boolean isInvalidBrackets(Optional<String> brackets) {
        return brackets.isPresent() && brackets.get().length() != 2;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendMessage(String message, Optional<Map<?, ?>> options) throws LuaException {
        return withChatOperation(ignored -> {
            LuaTable<?, ?> optionsLua = EmptyLuaTable.orEmpty(options.orElse(null));
            boolean useUTF8 = optionsLua.optBoolean("utf8").orElse(false);

            String message1 = message;
            // check size while it represents bytes (in utf8 mode) as that is longer
            if (message1.length() > APConfig.PERIPHERALS_CONFIG.chatBoxMessageSize.get()) {
                return Errors.MESSAGE_TOO_LONG_RESULT;
            }
            if (useUTF8) {
                message1 = StringUtil.byteStringToUTF8(message1);
            }

            Optional<String> brackets = optionsLua.optString("brackets");
            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }
            if (isInvalidBrackets(brackets)) {
                return Errors.INCORRECT_BRACKETS_RESULT;
            }

            Optional<String> prefix = optionsLua.optString("prefix");
            if (useUTF8) {
                prefix = prefix.map(StringUtil::byteStringToUTF8);
            }

            String bracketsColor = optionsLua.optString("bracketsColor").orElse("");
            if (useUTF8) {
                bracketsColor = StringUtil.byteStringToUTF8(bracketsColor);
            }

            MutableComponent preparedMessage = this.preparePrefix(
                    StringUtil.convertAndToSectionMark(prefix.orElseGet(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix)),
                    brackets.orElse("[]"),
                    StringUtil.convertAndToSectionMark(bracketsColor)
            )
                .append(message1);

            for (ServerPlayer player : getPlayers(optionsLua, false)) {
                player.sendSystemMessage(preparedMessage, false);
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendFormattedMessage(String messageJson, Optional<Map<?, ?>> options) throws LuaException {
        return withChatOperation(ignored -> {
            LuaTable<?, ?> optionsLua = EmptyLuaTable.orEmpty(options.orElse(null));
            boolean useUTF8 = optionsLua.optBoolean("utf8").orElse(false);

            String message1 = messageJson;
            // check size while it represents bytes (in utf8 mode) as that is longer
            if (message1.length() > APConfig.PERIPHERALS_CONFIG.chatBoxMessageSize.get()) {
                return Errors.MESSAGE_TOO_LONG_RESULT;
            }
            if (useUTF8) {
                message1 = StringUtil.byteStringToUTF8(message1);
            }

            MutableComponent component = this.parseFormattedMessage(message1);
            if (component == null) {
                return Errors.INCORRECT_MESSAGE_JSON_RESULT;
            }

            Optional<String> brackets = optionsLua.optString("brackets");
            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }
            if (isInvalidBrackets(brackets)) {
                return Errors.INCORRECT_BRACKETS_RESULT;
            }

            Optional<String> prefix = optionsLua.optString("prefix");
            if (useUTF8) {
                prefix = prefix.map(StringUtil::byteStringToUTF8);
            }

            String bracketsColor = optionsLua.optString("bracketsColor").orElse("");
            if (useUTF8) {
                bracketsColor = StringUtil.byteStringToUTF8(bracketsColor);
            }

            MutableComponent preparedMessage = this.preparePrefix(
                    StringUtil.convertAndToSectionMark(prefix.orElseGet(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix)),
                    brackets.orElse("[]"),
                    StringUtil.convertAndToSectionMark(bracketsColor)
            )
                .append(component);

            for (ServerPlayer player : getPlayers(optionsLua, false)) {
                player.sendSystemMessage(preparedMessage);
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendToast(Map<?, ?> options) throws LuaException {
        return withChatOperation(ignored -> {
            LuaTable<?, ?> optionsLua = new ObjectLuaTable(options);
            boolean useUTF8 = optionsLua.optBoolean("utf8").orElse(false);

            String message = optionsLua.getString("message");
            // check size while it represents bytes (in utf8 mode) as that is longer
            if (message.length() > APConfig.PERIPHERALS_CONFIG.chatBoxMessageSize.get()) {
                return Errors.MESSAGE_TOO_LONG_RESULT;
            }
            if (useUTF8) {
                message = StringUtil.byteStringToUTF8(message);
            }

            String title = optionsLua.getString("title");
            // TODO: missing max length check?
            if (useUTF8) {
                title = StringUtil.byteStringToUTF8(title);
            }

            Optional<String> brackets = optionsLua.optString("brackets");
            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }
            if (isInvalidBrackets(brackets)) {
                return Errors.INCORRECT_BRACKETS_RESULT;
            }

            Optional<String> prefix = optionsLua.optString("prefix");
            if (useUTF8) {
                prefix = prefix.map(StringUtil::byteStringToUTF8);
            }

            String bracketsColor = optionsLua.optString("bracketsColor").orElse("");
            if (useUTF8) {
                bracketsColor = StringUtil.byteStringToUTF8(bracketsColor);
            }

            MutableComponent preparedMessage = this.preparePrefix(
                    StringUtil.convertAndToSectionMark(prefix.orElseGet(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix)),
                    brackets.orElse("[]"),
                    StringUtil.convertAndToSectionMark(bracketsColor)
            )
                .append(message);

            for (ServerPlayer player : getPlayers(optionsLua, true)) {
                ToastToClientPacket packet = new ToastToClientPacket(Component.literal(title), preparedMessage);
                APNetworking.sendToPlayer(player, packet);
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendFormattedToast(Map<?, ?> options) throws LuaException {
        return withChatOperation(ignored -> {
            LuaTable<?, ?> optionsLua = new ObjectLuaTable(options);
            boolean useUTF8 = optionsLua.optBoolean("utf8").orElse(false);

            String message = optionsLua.getString("message");
            // check size while it represents bytes (in utf8 mode) as that is longer
            if (message.length() > APConfig.PERIPHERALS_CONFIG.chatBoxMessageSize.get()) {
                return Errors.MESSAGE_TOO_LONG_RESULT;
            }
            if (useUTF8) {
                message = StringUtil.byteStringToUTF8(message);
            }

            String title = optionsLua.getString("title");
            // TODO: missing max length check?
            if (useUTF8) {
                title = StringUtil.byteStringToUTF8(title);
            }

            MutableComponent messageComponent = this.parseFormattedMessage(message);
            if (messageComponent == null) {
                return Errors.INCORRECT_MESSAGE_JSON_RESULT;
            }

            MutableComponent titleComponent = this.parseFormattedMessage(title);
            if (titleComponent == null) {
                return Errors.INCORRECT_TITLE_JSON_RESULT;
            }

            Optional<String> brackets = optionsLua.optString("brackets");
            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }
            if (isInvalidBrackets(brackets)) {
                return Errors.INCORRECT_BRACKETS_RESULT;
            }

            Optional<String> prefix = optionsLua.optString("prefix");
            if (useUTF8) {
                prefix = prefix.map(StringUtil::byteStringToUTF8);
            }

            String bracketsColor = optionsLua.optString("bracketsColor").orElse("");
            if (useUTF8) {
                bracketsColor = StringUtil.byteStringToUTF8(bracketsColor);
            }

            MutableComponent preparedMessage = this.preparePrefix(
                    StringUtil.convertAndToSectionMark(prefix.orElseGet(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix)),
                    brackets.orElse("[]"),
                    StringUtil.convertAndToSectionMark(bracketsColor)
            )
                .append(messageComponent);

            for (ServerPlayer player : getPlayers(optionsLua, true)) {
                ToastToClientPacket packet = new ToastToClientPacket(titleComponent, preparedMessage);
                APNetworking.sendToPlayer(player, packet);
            }

            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult narrateMessage(String message, Optional<Map<?, ?>> options) throws LuaException {
        return withChatOperation(ignored -> {
            LuaTable<?, ?> optionsLua = EmptyLuaTable.orEmpty(options.orElse(null));

            boolean useUTF8 = optionsLua.optBoolean("utf8").orElse(false);

            String message1 = message;
            // check size while it represents bytes (in utf8 mode) as that is longer
            if (message1.length() > APConfig.PERIPHERALS_CONFIG.chatBoxMessageSize.get()) {
                return Errors.MESSAGE_TOO_LONG_RESULT;
            }
            if (useUTF8) {
                message1 = StringUtil.byteStringToUTF8(message1);
            }

            boolean interrupt = !optionsLua.optBoolean("delay").orElse(false);

            for (ServerPlayer player : getPlayers(optionsLua, false)) {
                APNetworking.sendToPlayer(player, new NarrateToClientPacket(message1, interrupt, getPos()));
            }
            return MethodResult.of(true);
        });
    }

    private Iterable<ServerPlayer> getPlayers(LuaTable<?, ?> options, boolean mustHavePlayer) throws LuaException {
        int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
        ResourceKey<Level> dimension = getLevel().dimension();

        int range = options.optInt("range").orElse(-1);
        String playerId = mustHavePlayer ? options.getString("player") : options.optString("player").orElse(null);

        if (playerId != null) {
            ServerPlayer player = getPlayer(playerId);
            return (APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() || player.level().dimension() == dimension) &&
                CoordUtil.isInRange(getPhysicsPos(), getLevel(), player, range, maxRange)
                ? List.of(player)
                : List.of();
        }

        List<ServerPlayer> players = new ArrayList<>();
        for (ServerPlayer player : getLevel().getServer().getPlayerList().getPlayers()) {
            if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.level().dimension() != dimension) {
                continue;
            }
            if (CoordUtil.isInRange(getPhysicsPos(), getLevel(), player, range, maxRange)) {
                players.add(player);
            }
        }
        return players;
    }

    @Override
    public void update() {
        lastConsumedMessage = Events.traverseChatMessages(lastConsumedMessage, message -> {
            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            if (maxRange != -1) {
                if (message.level() != this.getLevel().dimension()) {
                    return;
                }
                Vec3 pos = this.getPhysicsPos();
                Vec3 mpos = message.position();
                if (Math.abs(pos.x - mpos.x) > maxRange || Math.abs(pos.y - mpos.y) > maxRange || Math.abs(pos.z - mpos.z) > maxRange) {
                    return;
                }
            }
            queueEvent(
                CCEvents.CHAT,
                message.senderId().toString(),
                message.senderName(),
                message.message(),
                message.isHidden(),
                StringUtil.utf8ToByteString(message.message())
            );
        });
    }

    private static final class Errors {
        static final String INCORRECT_BRACKETS = "INCORRECT_BRACKETS (e.g. [], {}, <>, ...)";
        static final String INCORRECT_MESSAGE_JSON = "INCORRECT_MESSAGE_JSON";
        static final String INCORRECT_TITLE_JSON = "INCORRECT_TITLE_JSON";
        static final String MESSAGE_TOO_LONG = "MESSAGE_TOO_LONG";

        static final MethodResult INCORRECT_BRACKETS_RESULT = MethodResult.of(null, INCORRECT_BRACKETS);
        static final MethodResult INCORRECT_MESSAGE_JSON_RESULT = MethodResult.of(null, INCORRECT_MESSAGE_JSON);
        static final MethodResult INCORRECT_TITLE_JSON_RESULT = MethodResult.of(null, INCORRECT_TITLE_JSON);
        static final MethodResult MESSAGE_TOO_LONG_RESULT = MethodResult.of(null, MESSAGE_TOO_LONG);
    }
}
