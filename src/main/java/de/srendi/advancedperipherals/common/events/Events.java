package de.srendi.advancedperipherals.common.events;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.misc.DataPublisher;
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

@EventBusSubscriber
public class Events {

    private static final String PLAYED_BEFORE = "ap_played_before";
    private static final DataPublisher<ChatMessageObject> messageQueue = new DataPublisher<>(64);
    private static final DataPublisher<PlayerMessageObject> playerMessageQueue = new DataPublisher<>(64);

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

        putPlayerMessage(new PlayerMessageObject("player_join", player.getName().getString(), player.level().dimension().location().toString(), null));
    }

    @SubscribeEvent
    public static void onWorldLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        putPlayerMessage(new PlayerMessageObject("player_leave", player.getName().getString(), player.level().dimension().location().toString(), null));
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        String fromDim = event.getFrom().location().toString();
        String toDim = event.getTo().location().toString();

        putPlayerMessage(new PlayerMessageObject("player_changed_dimension", player.getName().getString(), fromDim, toDim));
    }

    @SubscribeEvent
    public static void onCommand(CommandEvent event) throws CommandSyntaxException {
        if (event.getException() != null) {
            return;
        }
        CommandContextBuilder<CommandSourceStack> context = event.getParseResults().getContext();
        if (context.getCommand() == null || !getCommandName(context).equals("say")) {
            return;
        }
        String username = "sayCommand";
        String uuid = null;
        String message = MessageArgument.getMessage(context.build(""), "message").getString();
        boolean isHidden = false;
        CommandSourceStack source = context.getSource();
        if (source.getEntity() != null) {
            username = source.getEntity().getDisplayName().getString();
            uuid = source.getEntity().getUUID().toString();
        }
        if (message.startsWith("$")) {
            event.setCanceled(true);
            message = message.substring(1);
            isHidden = true;
        }
        putChatMessage(new ChatMessageObject(username, message, uuid, isHidden));
    }

    private static String getCommandName(CommandContextBuilder<?> context) {
        if (context != null && !context.getNodes().isEmpty()) {
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
            putChatMessage(new ChatMessageObject(event.getUsername(), message, event.getPlayer().getUUID().toString(), isHidden));
        }
    }

    public static void putChatMessage(ChatMessageObject message) {
        messageQueue.add(message);
    }

    public static void putPlayerMessage(PlayerMessageObject message) {
        playerMessageQueue.add(message);
    }

    public static long traverseChatMessages(long lastConsumedMessage, Consumer<ChatMessageObject> consumer) {
        return messageQueue.traverse(lastConsumedMessage, consumer);
    }

    public static long traversePlayerMessages(long lastConsumedMessage, Consumer<PlayerMessageObject> consumer) {
        return playerMessageQueue.traverse(lastConsumedMessage, consumer);
    }

    public static long getLastChatMessageID() {
        return messageQueue.getLastID();
    }

    public static long getLastPlayerMessageID() {
        return playerMessageQueue.getLastID();
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
