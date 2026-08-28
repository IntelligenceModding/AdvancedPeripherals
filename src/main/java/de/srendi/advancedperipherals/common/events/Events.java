package de.srendi.advancedperipherals.common.events;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.lib.misc.DataPublisher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.UUID;
import java.util.function.Consumer;

@EventBusSubscriber
public class Events {

    private static final String PLAYED_BEFORE_TAG = "ap_played_before";
    private static final DataPublisher<ChatMessageRecord> messageQueue = new DataPublisher<>(64);
    private static final DataPublisher<IPlayerEvent> playerMessageQueue = new DataPublisher<>(64);

    @SubscribeEvent
    public static void onWorldJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        // We could switch to the advancement way to give new players the book. However, that would not allow us to create
        // a config option for that. So we will stick with the custom solution here.
        // See https://vazkiimods.github.io/Patchouli/docs/patchouli-basics/giving-new
        if (APConfig.WORLD_CONFIG.givePlayerBookOnJoin.get() && APAddon.PATCHOULI.isLoaded()) {
            if (getAndSetPlayedBefore(player)) {
                PatchouliAPI.IPatchouliAPI patchouli = PatchouliAPI.get();
                ItemStack book = patchouli.getBookStack(AdvancedPeripherals.getRL("manual"));
                player.addItem(book);
            }
        }

        putPlayerMessage(new PlayerDimensionEvent(CCEvents.PLAYER_JOIN, player.getUUID(), player.getGameProfile().getName(), player.level().dimension().location().toString(), null));
    }

    @SubscribeEvent
    public static void onWorldLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        putPlayerMessage(new PlayerDimensionEvent(CCEvents.PLAYER_LEAVE, player.getUUID(), player.getGameProfile().getName(), player.level().dimension().location().toString(), null));
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        String fromDim = event.getFrom().location().toString();
        String toDim = event.getTo().location().toString();

        putPlayerMessage(new PlayerDimensionEvent(CCEvents.PLAYER_CHANGED_DIMENSION, player.getUUID(), player.getGameProfile().getName(), fromDim, toDim));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        putPlayerMessage(new PlayerDeathEvent(player.getUUID(), player.getGameProfile().getName(), event.getSource()));
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
        UUID uuid = null;
        String username = "[say]";
        String message = MessageArgument.getMessage(context.build(""), "message").getString();
        boolean isHidden = false;
        CommandSourceStack source = context.getSource();
        Entity sourceEntity = source.getEntity();
        if (sourceEntity != null) {
            uuid = sourceEntity.getUUID();
            username = sourceEntity instanceof Player player
                ? player.getGameProfile().getName()
                : sourceEntity.getName().getString();
        }
        if (message.startsWith("$")) {
            event.setCanceled(true);
            message = message.substring(1);
            isHidden = true;
        }
        putChatMessage(
            new ChatMessageRecord(uuid, username, message, isHidden, source.getLevel().dimension(), source.getPosition())
        );
    }

    private static String getCommandName(CommandContextBuilder<?> context) {
        if (context != null && !context.getNodes().isEmpty()) {
            return context.getNodes().get(0).getNode().getName();
        }
        return "";
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onChat(ServerChatEvent event) {
        if (!APConfig.PERIPHERALS_CONFIG.enableChatBox.get()) {
            return;
        }
        ServerPlayer player = event.getPlayer();
        // TODO: investigate the use of event.getRawText
        String message = event.getMessage().getString();
        boolean isHidden = message.startsWith("$");
        if (isHidden) {
            message = message.substring(1);
            event.setCanceled(true);
        }
        putChatMessage(
            new ChatMessageRecord(player.getUUID(), event.getUsername(), message, isHidden, player.serverLevel().dimension(), player.position())
        );
    }

    public static void putChatMessage(ChatMessageRecord message) {
        messageQueue.add(message);
    }

    public static void putPlayerMessage(IPlayerEvent message) {
        if (!APConfig.PERIPHERALS_CONFIG.enablePlayerEvents.get()) {
            return;
        }
        playerMessageQueue.add(message);
    }

    public static long traverseChatMessages(long lastConsumedMessage, Consumer<ChatMessageRecord> consumer) {
        return messageQueue.traverse(lastConsumedMessage, consumer);
    }

    public static long traversePlayerMessages(long lastConsumedMessage, Consumer<IPlayerEvent> consumer) {
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
        if (tag.getBoolean(PLAYED_BEFORE_TAG)) {
            return false;
        }
        tag.putBoolean(PLAYED_BEFORE_TAG, true);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, tag);
        return true;
    }

    public record ChatMessageRecord(@Nullable UUID senderId, String senderName, String message, boolean isHidden, ResourceKey<Level> level, Vec3 position) {}

    public interface IPlayerEvent {
        String eventName();
        Object[] eventArgs();
        UUID playerId();
        boolean restrictedRange();
    }

    public record PlayerDimensionEvent(
        String eventName,
        UUID playerId,
        String playerName,
        String fromDimension,
        String toDimension
    ) implements IPlayerEvent {
        @Override
        public Object[] eventArgs() {
            return new Object[]{this.playerId.toString(), this.playerName, this.fromDimension, this.toDimension};
        }

        @Override
        public boolean restrictedRange() {
            return this.eventName.equals(CCEvents.PLAYER_CHANGED_DIMENSION);
        }
    }

    public record PlayerDeathEvent(UUID playerId, String playerName, DamageSource source) implements IPlayerEvent {
        @Override
        public String eventName() {
            return CCEvents.PLAYER_DEATH;
        }

        @Override
        public Object[] eventArgs() {
            return new Object[]{this.playerId.toString(), this.playerName, this.source.typeHolder().getRegisteredName()};
        }

        @Override
        public boolean restrictedRange() {
            return true;
        }
    }
}
