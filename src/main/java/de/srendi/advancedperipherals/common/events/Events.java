package de.srendi.advancedperipherals.common.events;

import com.google.common.collect.EvictingQueue;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.function.Consumer;

@EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID)
public class Events {

    private static final String PLAYED_BEFORE = "ap_played_before";
    private static final int CHAT_QUEUE_MAX_SIZE = 50;
    private static final int PLAYER_QUEUE_MAX_SIZE = 50;
    private static final EvictingQueue<Pair<Long, ChatMessageObject>> messageQueue = EvictingQueue.create(CHAT_QUEUE_MAX_SIZE);
    private static final EvictingQueue<Pair<Long, PlayerMessageObject>> playerMessageQueue = EvictingQueue.create(PLAYER_QUEUE_MAX_SIZE);
    private static long lastChatMessageID = 0;
    private static long lastPlayerMessageID = 0;

    @SubscribeEvent
    public static void onWorldJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        // We could switch to the advancement way to give new players the book. However, that would not allow us to create
        // a config option for that. So we will stick with the custom solution here.
        // See https://vazkiimods.github.io/Patchouli/docs/patchouli-basics/giving-new
        if (APConfig.WORLD_CONFIG.givePlayerBookOnJoin.get() && APAddon.PATCHOULI.isLoaded()) {
            if (!getAndSetPlayedBefore(player)) {
                vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI patchouli = vazkii.patchouli.api.PatchouliAPI.get();
                ItemStack book = patchouli.getBookStack(AdvancedPeripherals.getRL("manual"));
                player.addItem(book);
            }
        }

        putPlayerMessage(Pair.of(getLastPlayerMessageID(), new PlayerMessageObject("player_join", player.getName().getString(), player.level().dimension().location().toString(), null)));
    }

    @SubscribeEvent
    public static void onWorldLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        putPlayerMessage(Pair.of(getLastPlayerMessageID(), new PlayerMessageObject("player_leave", player.getName().getString(), player.level().dimension().location().toString(), null)));
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        String fromDim = event.getFrom().location().toString();
        String toDim = event.getTo().location().toString();

        putPlayerMessage(Pair.of(getLastPlayerMessageID(), new PlayerMessageObject("player_changed_dimension", player.getName().getString(), fromDim, toDim)));
    }

    @SubscribeEvent
    public static void onCommand(CommandEvent event) throws CommandSyntaxException {
        if (!getCommandName(event.getParseResults().getContext()).equals("say"))
            return;
        String username = "sayCommand";
        String uuid = null;
        String message = MessageArgument.getMessage(event.getParseResults().getContext().build("apChatEvent"), "message").getString();
        boolean isHidden = false;
        CommandSourceStack source = event.getParseResults().getContext().getSource();
        if (source.getEntity() != null) {
            username = source.getEntity().getDisplayName().getString();
            uuid = source.getEntity().getUUID().toString();
        }
        if (message.startsWith("$")) {
            event.setCanceled(true);
            message = message.substring(1);
            isHidden = true;
        }
        putChatMessage(Pair.of(getLastChatMessageID(), new ChatMessageObject(username, message, uuid, isHidden)));
    }

    private static String getCommandName(CommandContextBuilder<?> context) {
        if (context != null && context.getNodes() != null && !context.getNodes().isEmpty()) {
            return context.getNodes().get(0).getNode().getName();
        }
        return "";
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onChatBox(ServerChatEvent event) {
        if (APConfig.PERIPHERALS_CONFIG.enableChatBox.get()) {
            String message = event.getMessage().getString();
            boolean isHidden = false;
            if (message.startsWith("$")) {
                event.setCanceled(true);
                message = message.substring(1);
                isHidden = true;
            }
            putChatMessage(Pair.of(getLastChatMessageID(), new ChatMessageObject(event.getUsername(), message, event.getPlayer().getUUID().toString(), isHidden)));
        }
    }

    public static void putChatMessage(Pair<Long, ChatMessageObject> message) {
        synchronized (messageQueue) {
            messageQueue.add(message);
            lastChatMessageID++;
        }
    }

    public static void putPlayerMessage(Pair<Long, PlayerMessageObject> message) {
        synchronized (playerMessageQueue) {
            playerMessageQueue.add(message);
            lastPlayerMessageID++;
        }
    }

    public static long traverseChatMessages(long lastConsumedMessage, Consumer<ChatMessageObject> consumer) {
        synchronized (messageQueue) {
            for (Pair<Long, ChatMessageObject> message : messageQueue) {
                if (message.getLeft() <= lastConsumedMessage)
                    continue;
                consumer.accept(message.getRight());
                lastConsumedMessage = message.getLeft();
            }
        }
        return lastConsumedMessage;
    }

    public static long traversePlayerMessages(long lastConsumedMessage, Consumer<PlayerMessageObject> consumer) {
        synchronized (playerMessageQueue) {
            for (Pair<Long, PlayerMessageObject> message : playerMessageQueue) {
                if (message.getLeft() <= lastConsumedMessage)
                    continue;
                consumer.accept(message.getRight());
                lastConsumedMessage = message.getLeft();
            }
        }
        return lastConsumedMessage;
    }

    public static long getLastChatMessageID() {
        synchronized (messageQueue) {
            return lastChatMessageID;
        }
    }

    public static long getLastPlayerMessageID() {
        synchronized (playerMessageQueue) {
            return lastPlayerMessageID;
        }
    }

    private static boolean getAndSetPlayedBefore(Player player) {
        CompoundTag tag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        if (tag.getBoolean(PLAYED_BEFORE)) {
            return true;
        }
        tag.putBoolean(PLAYED_BEFORE, true);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, tag);
        return false;
    }

    public record ChatMessageObject(String username, String message, String uuid, boolean isHidden) {}
    public record PlayerMessageObject(String eventName, String playerName, String fromDimension, String toDimension) {}
}
