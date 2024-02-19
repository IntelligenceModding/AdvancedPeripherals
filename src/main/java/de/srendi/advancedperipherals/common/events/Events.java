package de.srendi.advancedperipherals.common.events;

import com.google.common.collect.EvictingQueue;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Events {

    private static final String PLAYED_BEFORE = "ap_played_before";
    private static final int CHAT_QUEUE_MAX_SIZE = 50;
    private static final int PLAYER_QUEUE_MAX_SIZE = 50;
    public static final EvictingQueue<Pair<Long, ChatMessageObject>> messageQueue = EvictingQueue.create(CHAT_QUEUE_MAX_SIZE);
    public static final EvictingQueue<Pair<Long, PlayerMessageObject>> playerMessageQueue = EvictingQueue.create(PLAYER_QUEUE_MAX_SIZE);
    private static long lastChatMessageID = 0;
    private static long lastPlayerMessageID = 0;

    @SubscribeEvent
    public static void onWorldJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (APConfig.WORLD_CONFIG.givePlayerBookOnJoin.get()) {
            if (!hasPlayedBefore(player)) {
                ItemStack book = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("patchouli", "guide_book")));
                CompoundTag nbt = new CompoundTag();
                nbt.putString("patchouli:book", "advancedperipherals:manual");
                book.setTag(nbt);
                player.addItem(book);
            }
        }

        putPlayerMessage(Pair.of(getLastPlayerMessageID(), new PlayerMessageObject("playerJoin", player.getName().getString(), player.getLevel().dimension().location().toString(), "")));
    }

    @SubscribeEvent
    public static void onWorldLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        putPlayerMessage(Pair.of(getLastPlayerMessageID(), new PlayerMessageObject("playerLeave", player.getName().getString(), player.getLevel().dimension().location().toString(), "")));
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        String fromDim = event.getFrom().location().toString();
        String toDim = event.getTo().location().toString();

        putPlayerMessage(Pair.of(getLastPlayerMessageID(), new PlayerMessageObject("playerChangedDimension", player.getName().getString(), fromDim, toDim)));
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
            message = message.replace("$", "");
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
    public static void onChatBox(ServerChatEvent.Submitted event) {
        if (APConfig.PERIPHERALS_CONFIG.enableChatBox.get()) {
            String message = event.getMessage().getString();
            boolean isHidden = false;
            if (message.startsWith("$")) {
                event.setCanceled(true);
                message = message.replace("$", "");
                isHidden = true;
            }
            putChatMessage(Pair.of(getLastChatMessageID(), new ChatMessageObject(event.getUsername(), message, event.getPlayer().getUUID().toString(), isHidden)));
        }
    }

    public static synchronized void putChatMessage(Pair<Long, ChatMessageObject> message) {
        messageQueue.add(message);
        lastChatMessageID++;
    }

    public static synchronized void putPlayerMessage(Pair<Long, PlayerMessageObject> message) {
        playerMessageQueue.add(message);
        lastPlayerMessageID++;
    }

    public static synchronized long traverseChatMessages(long lastConsumedMessage, Consumer<ChatMessageObject> consumer) {
        for (Pair<Long, ChatMessageObject> message : messageQueue) {
            if (message.getLeft() <= lastConsumedMessage)
                continue;
            consumer.accept(message.getRight());
            lastConsumedMessage = message.getLeft();
        }
        return lastConsumedMessage;
    }

    public static synchronized long traversePlayerMessages(long lastConsumedMessage, Consumer<PlayerMessageObject> consumer) {
        for (Pair<Long, PlayerMessageObject> message : playerMessageQueue) {
            if (message.getLeft() <= lastConsumedMessage)
                continue;
            consumer.accept(message.getRight());
            lastConsumedMessage = message.getLeft();
        }
        return lastConsumedMessage;
    }

    public static synchronized long getLastChatMessageID() {
        return lastChatMessageID;
    }

    public static synchronized long getLastPlayerMessageID() {
        return lastPlayerMessageID;
    }

    private static boolean hasPlayedBefore(Player player) {
        CompoundTag tag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        if (tag.getBoolean(PLAYED_BEFORE)) {
            return true;
        } else {
            tag.putBoolean(PLAYED_BEFORE, true);
            player.getPersistentData().put(Player.PERSISTED_NBT_TAG, tag);
            return false;
        }
    }

    public record ChatMessageObject(String username, String message, String uuid, boolean isHidden) {}
    public record PlayerMessageObject(String eventName, String playerName, String fromDimension, String toDimension) {}
}
