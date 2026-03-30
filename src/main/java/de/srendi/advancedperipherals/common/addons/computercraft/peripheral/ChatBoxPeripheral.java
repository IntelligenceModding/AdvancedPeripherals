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
import de.srendi.advancedperipherals.common.events.Events;
import de.srendi.advancedperipherals.common.network.toclient.ToastToClientPacket;
import de.srendi.advancedperipherals.common.util.CoordUtil;
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
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

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
            } catch (JsonSyntaxException exception) {
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

            // check size while it represents bytes (in utf8 mode) as that is longer
            if (message.length() > APConfig.PERIPHERALS_CONFIG.chatBoxMessageSize.get())
                return MethodResult.of(null, "Message is too long");

            if (useUTF8) {
                message = StringUtil.byteStringToUTF8(message);
            }

            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = arguments.optInt(4, -1);
            ResourceKey<Level> dimension = getLevel().dimension();
            MutableComponent component = Component.Serializer.fromJson(message, RegistryAccess.EMPTY);
            if (component == null)
                return MethodResult.of(null, "incorrect json");

            Optional<String> brackets = arguments.optString(2);
            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }

            if (checkBrackets(brackets))
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");

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

            for (ServerPlayer player : getLevel().getServer().getPlayerList().getPlayers()) {
                if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.level().dimension() != dimension) {
                    continue;
                }
                if (CoordUtil.isInRange(getCenterPos(), getLevel(), player, range, maxRange)) {
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

            // check size while it represents bytes (in utf8 mode) as that is longer
            if (message.length() > APConfig.PERIPHERALS_CONFIG.chatBoxMessageSize.get()) {
                return MethodResult.of(null, "Message is too long");
            }

            if (useUTF8) {
                message = StringUtil.byteStringToUTF8(message);
            }

            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = arguments.optInt(4, -1);
            ResourceKey<Level> dimension = getLevel().dimension();

            Optional<String> brackets = arguments.optString(2);
            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }

            if (checkBrackets(brackets))
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");

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

            for (ServerPlayer player : getLevel().getServer().getPlayerList().getPlayers()) {
                if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.level().dimension() != dimension) {
                    continue;
                }
                if (CoordUtil.isInRange(getCenterPos(), getLevel(), player, range, maxRange)) {
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

            // check size while it represents bytes (in utf8 mode) as that is longer
            if (message.length() > APConfig.PERIPHERALS_CONFIG.chatBoxMessageSize.get())
                return MethodResult.of(null, "Message is too long");

            if (useUTF8) {
                message = StringUtil.byteStringToUTF8(message);
            }

            String playerName = arguments.getString(1);
            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = arguments.optInt(5, -1);
            ResourceKey<Level> dimension = getLevel().dimension();
            ServerPlayer player = getPlayer(playerName);
            if (player == null)
                return MethodResult.of(null, "incorrect player name/uuid");

            MutableComponent component = Component.Serializer.fromJson(message, RegistryAccess.EMPTY);
            if (component == null)
                return MethodResult.of(null, "incorrect json");

            Optional<String> brackets = arguments.optString(3);
            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }

            if (checkBrackets(brackets))
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");

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

            if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.level().dimension() != dimension) {
                return MethodResult.of(false, "NOT_SAME_DIMENSION");
            }

            if (CoordUtil.isInRange(getCenterPos(), getLevel(), player, range, maxRange)) {
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
            // check size while it represents bytes (in utf8 mode) as that is longer
            if (message.length() > APConfig.PERIPHERALS_CONFIG.chatBoxMessageSize.get()) {
                return MethodResult.of(null, "Message is too long");
            }

            if (useUTF8) {
                message = StringUtil.byteStringToUTF8(message);
            }

            String title = arguments.getString(1);

            // TODO: missing max length check?

            if (useUTF8) {
                title = StringUtil.byteStringToUTF8(title);
            }

            String playerName = arguments.getString(2);
            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = arguments.optInt(6, -1);
            ResourceKey<Level> dimension = getLevel().dimension();
            ServerPlayer player = getPlayer(playerName);
            if (player == null)
                return MethodResult.of(null, "incorrect player name/uuid");

            MutableComponent messageComponent = Component.Serializer.fromJson(message, RegistryAccess.EMPTY);
            if (messageComponent == null)
                return MethodResult.of(null, "incorrect json for message");

            MutableComponent titleComponent = Component.Serializer.fromJson(title, RegistryAccess.EMPTY);
            if (titleComponent == null)
                return MethodResult.of(null, "incorrect json for title");

            Optional<String> brackets = arguments.optString(4);

            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }

            if (checkBrackets(brackets))
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ,,,)");


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

            if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.level().dimension() != dimension)
                return MethodResult.of(false, "NOT_SAME_DIMENSION");

            if (CoordUtil.isInRange(getPhysicsPos(), getLevel(), player, range, maxRange)) {
                ToastToClientPacket packet = new ToastToClientPacket(titleComponent, preparedMessage);
                PacketDistributor.sendToPlayer(player, packet);
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

            // check size while it represents bytes (in utf8 mode) as that is longer
            if (message.length() > APConfig.PERIPHERALS_CONFIG.chatBoxMessageSize.get())
                return MethodResult.of(null, "Message is too long");

            if (useUTF8) {
                message = StringUtil.byteStringToUTF8(message);
            }

            String playerName = arguments.getString(1);
            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = arguments.optInt(5, -1);
            ResourceKey<Level> dimension = getLevel().dimension();
            ServerPlayer player = getPlayer(playerName);
            if (player == null)
                return MethodResult.of(null, "incorrect player name/uuid");

            Optional<String> brackets = arguments.optString(3);

            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }

            if (checkBrackets(brackets))
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");

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
            if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.level().dimension() != dimension)
                return MethodResult.of(false, "NOT_SAME_DIMENSION");

            if (CoordUtil.isInRange(getCenterPos(), getLevel(), player, range, maxRange)) {
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

            if (message.length() > APConfig.PERIPHERALS_CONFIG.chatBoxMessageSize.get())
                return MethodResult.of(null, "Message is too long");

            // check size while it represents bytes (in utf8 mode) as that is longer
            if (useUTF8) {
                message = StringUtil.byteStringToUTF8(message);
            }

            String title = arguments.getString(1);

            // TODO: missing max length check?

            if (useUTF8) {
                title = StringUtil.byteStringToUTF8(title);
            }

            String playerName = arguments.getString(2);
            int maxRange = APConfig.PERIPHERALS_CONFIG.chatBoxMaxRange.get();
            int range = arguments.optInt(6, -1);
            ResourceKey<Level> dimension = getLevel().dimension();
            ServerPlayer player = getPlayer(playerName);
            if (player == null)
                return MethodResult.of(null, "incorrect player name/uuid");

            Optional<String> brackets = arguments.optString(4);

            if (useUTF8) {
                brackets = brackets.map(StringUtil::byteStringToUTF8);
            }

            if (checkBrackets(brackets))
                return MethodResult.of(null, "incorrect bracket string (e.g. [], {}, <>, ...)");

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

            if (!APConfig.PERIPHERALS_CONFIG.chatBoxMultiDimensional.get() && player.level().dimension() != dimension)
                return MethodResult.of(false, "NOT_SAME_DIMENSION");

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
            String byteString = StringUtil.utf8ToByteString(message.message());
            queueEvent("chat", message.username(), message.message(), message.uuid(), message.isHidden(), byteString);
        });
    }
}
