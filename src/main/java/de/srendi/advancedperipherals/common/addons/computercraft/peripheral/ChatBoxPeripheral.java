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
import de.srendi.advancedperipherals.common.network.toclient.ToastToClientPacket;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
import de.srendi.advancedperipherals.common.util.StringUtil;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralFunction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SimpleFreeOperation.CHAT_MESSAGE;

public class ChatBoxPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "chat_box";
    private static final Pattern UUID_PATTERN = Pattern.compile("^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$");

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

    private MutableComponent appendPrefix(String prefix, String brackets, String color) {
        Component prefixComponent = Component.literal(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix.get());
        if (!prefix.isEmpty()) {
            MutableComponent formattablePrefix;
            try {
                formattablePrefix = MutableComponent.Serializer.fromJson(prefix, RegistryAccess.EMPTY);
                prefixComponent = formattablePrefix;
            } catch (JsonParseException e) {
                AdvancedPeripherals.debug("Not vaild json prefix, using plain text instead.");
                prefixComponent = Component.literal(prefix);
            }
        }

        return Component.literal(color + brackets.charAt(0) + "\u00a7r").append(prefixComponent).append(color + brackets.charAt(1) + "\u00a7r ");
    }

    /**
     * @param argument uuid/name of a player
     * @return a player if the name/uuid belongs to a player
     */
    private ServerPlayer getPlayer(String argument) {
        MinecraftServer server = getLevel().getServer();
        if (UUID_PATTERN.matcher(argument).matches()) {
            return server.getPlayerList().getPlayer(UUID.fromString(argument));
        }
        return server.getPlayerList().getPlayerByName(argument);
    }

    /**
     * Checks if brackets are in correct format if present
     *
     * @param brackets the brackets to check
     * @return true if brackets are not in the right format
     */
    private boolean isInvaildBrackets(Optional<String> brackets) {
        return brackets.isPresent() && brackets.get().length() != 2;
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

            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = optionsLua.optInt("range").orElse(-1);
            ResourceKey<Level> dimension = getLevel().dimension();
            MutableComponent component = Component.Serializer.fromJson(message1, RegistryAccess.EMPTY);
            if (component == null) {
                return Errors.INCORRECT_MESSAGE_JSON_RESULT;
            }

            Optional<String> brackets = optionsLua.optString("brackets");
            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }
            if (isInvaildBrackets(brackets)) {
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

            MutableComponent preparedMessage = appendPrefix(
                    StringUtil.convertAndToSectionMark(prefix.orElseGet(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix)),
                    brackets.orElse("[]"),
                    StringUtil.convertAndToSectionMark(bracketsColor)
            );
            preparedMessage.append(component);

            for (ServerPlayer player : getLevel().getServer().getPlayerList().getPlayers()) {
                if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.level().dimension() != dimension) {
                    continue;
                }
                if (CoordUtil.isInRange(getPhysicsPos(), getLevel(), player, range, maxRange)) {
                    player.sendSystemMessage(preparedMessage);
                }
            }
            return MethodResult.of(true);
        });
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

            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = optionsLua.optInt("range").orElse(-1);
            ResourceKey<Level> dimension = getLevel().dimension();

            Optional<String> brackets = optionsLua.optString("brackets");
            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }
            if (isInvaildBrackets(brackets)) {
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

            MutableComponent preparedMessage = appendPrefix(
                    StringUtil.convertAndToSectionMark(prefix.orElseGet(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix)),
                    brackets.orElse("[]"),
                    StringUtil.convertAndToSectionMark(bracketsColor)
            );
            preparedMessage.append(message1);

            for (ServerPlayer player : getLevel().getServer().getPlayerList().getPlayers()) {
                if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.level().dimension() != dimension) {
                    continue;
                }
                if (CoordUtil.isInRange(getPhysicsPos(), getLevel(), player, range, maxRange)) {
                    player.sendSystemMessage(preparedMessage);
                }
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendFormattedMessageToPlayer(String messageJson, String playerId, Optional<Map<?, ?>> options) throws LuaException {
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

            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = optionsLua.optInt("range").orElse(-1);
            ResourceKey<Level> dimension = getLevel().dimension();
            ServerPlayer player = getPlayer(playerId);
            if (player == null) {
                return Errors.INCORRECT_PLAYER_ID_RESULT;
            }

            MutableComponent component = Component.Serializer.fromJson(message1, RegistryAccess.EMPTY);
            if (component == null) {
                return Errors.INCORRECT_MESSAGE_JSON_RESULT;
            }

            Optional<String> brackets = optionsLua.optString("brackets");
            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }
            if (isInvaildBrackets(brackets)) {
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

            MutableComponent preparedMessage = appendPrefix(
                    StringUtil.convertAndToSectionMark(prefix.orElseGet(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix)),
                    brackets.orElse("[]"),
                    StringUtil.convertAndToSectionMark(bracketsColor)
            );
            preparedMessage.append(component);

            if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.level().dimension() != dimension) {
                return Errors.NOT_SAME_DIMENSION_RESULT;
            }

            if (CoordUtil.isInRange(getPhysicsPos(), getLevel(), player, range, maxRange)) {
                player.sendSystemMessage(preparedMessage);
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendMessageToPlayer(String message, String playerId, Optional<Map<?, ?>> options) throws LuaException {
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

            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = optionsLua.optInt(5).orElse(-1);
            ResourceKey<Level> dimension = getLevel().dimension();
            ServerPlayer player = getPlayer(playerId);
            if (player == null) {
                return Errors.INCORRECT_PLAYER_ID_RESULT;
            }

            Optional<String> brackets = optionsLua.optString("brackets");
            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }
            if (isInvaildBrackets(brackets)) {
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

            MutableComponent preparedMessage = appendPrefix(
                    StringUtil.convertAndToSectionMark(prefix.orElseGet(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix)),
                    brackets.orElse("[]"),
                    StringUtil.convertAndToSectionMark(bracketsColor)
            );
            preparedMessage.append(message1);
            if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.level().dimension() != dimension) {
                return Errors.NOT_SAME_DIMENSION_RESULT;
            }

            if (CoordUtil.isInRange(getPhysicsPos(), getLevel(), player, range, maxRange)) {
                player.sendSystemMessage(preparedMessage, false);
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendFormattedToastToPlayer(Map<?, ?> options) throws LuaException {
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

            String playerId = optionsLua.getString("player");
            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = optionsLua.optInt("range").orElse(-1);
            ResourceKey<Level> dimension = getLevel().dimension();
            ServerPlayer player = getPlayer(playerId);
            if (player == null) {
                return Errors.INCORRECT_PLAYER_ID_RESULT;
            }

            MutableComponent messageComponent = Component.Serializer.fromJson(message, RegistryAccess.EMPTY);
            if (messageComponent == null) {
                return Errors.INCORRECT_MESSAGE_JSON_RESULT;
            }

            MutableComponent titleComponent = Component.Serializer.fromJson(title, RegistryAccess.EMPTY);
            if (titleComponent == null) {
                return Errors.INCORRECT_TITLE_JSON_RESULT;
            }

            Optional<String> brackets = optionsLua.optString(4);
            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }
            if (isInvaildBrackets(brackets)) {
                return Errors.INCORRECT_BRACKETS_RESULT;
            }

            Optional<String> prefix = optionsLua.optString(3);
            if (useUTF8) {
                prefix = prefix.map(StringUtil::byteStringToUTF8);
            }

            String bracketsColor = optionsLua.optString("bracketsColor").orElse("");
            if (useUTF8) {
                bracketsColor = StringUtil.byteStringToUTF8(bracketsColor);
            }

            MutableComponent preparedMessage = appendPrefix(
                    StringUtil.convertAndToSectionMark(prefix.orElseGet(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix)),
                    brackets.orElse("[]"),
                    StringUtil.convertAndToSectionMark(bracketsColor)
            );
            preparedMessage.append(messageComponent);

            if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.level().dimension() != dimension) {
                return Errors.NOT_SAME_DIMENSION_RESULT;
            }

            if (CoordUtil.isInRange(getPhysicsPos(), getLevel(), player, range, maxRange)) {
                ToastToClientPacket packet = new ToastToClientPacket(titleComponent, preparedMessage);
                PacketDistributor.sendToPlayer(player, packet);
            }

            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendToastToPlayer(Map<?, ?> options) throws LuaException {
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

            String playerId = optionsLua.getString("player");
            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = optionsLua.optInt(6).orElse(-1);
            ResourceKey<Level> dimension = getLevel().dimension();
            ServerPlayer player = getPlayer(playerId);
            if (player == null) {
                return Errors.INCORRECT_PLAYER_ID_RESULT;
            }

            Optional<String> brackets = optionsLua.optString("brackets");
            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }
            if (isInvaildBrackets(brackets)) {
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

            MutableComponent preparedMessage = appendPrefix(
                    StringUtil.convertAndToSectionMark(prefix.orElseGet(APConfig.PERIPHERALS_CONFIG.defaultChatBoxPrefix)),
                    brackets.orElse("[]"),
                    StringUtil.convertAndToSectionMark(bracketsColor)
            );
            preparedMessage.append(message);

            if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.level().dimension() != dimension) {
                return Errors.NOT_SAME_DIMENSION_RESULT;
            }

            if (CoordUtil.isInRange(getPhysicsPos(), getLevel(), player, range, maxRange)) {
                ToastToClientPacket packet = new ToastToClientPacket(Component.literal(title), preparedMessage);
                PacketDistributor.sendToPlayer(player, packet);
            }
            return MethodResult.of(true);
        });
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
        static final String INCORRECT_PLAYER_ID = "INCORRECT_PLAYER_NAME_OR_UUID";
        static final String INCORRECT_TITLE_JSON = "INCORRECT_TITLE_JSON";
        static final String MESSAGE_TOO_LONG = "MESSAGE_TOO_LONG";
        static final String NOT_SAME_DIMENSION = "NOT_SAME_DIMENSION";

        static final MethodResult INCORRECT_BRACKETS_RESULT = MethodResult.of(null, INCORRECT_BRACKETS);
        static final MethodResult INCORRECT_MESSAGE_JSON_RESULT = MethodResult.of(null, INCORRECT_MESSAGE_JSON);
        static final MethodResult INCORRECT_PLAYER_ID_RESULT = MethodResult.of(null, INCORRECT_PLAYER_ID);
        static final MethodResult INCORRECT_TITLE_JSON_RESULT = MethodResult.of(null, INCORRECT_TITLE_JSON);
        static final MethodResult MESSAGE_TOO_LONG_RESULT = MethodResult.of(null, MESSAGE_TOO_LONG);
        static final MethodResult NOT_SAME_DIMENSION_RESULT = MethodResult.of(null, NOT_SAME_DIMENSION);
    }
}
