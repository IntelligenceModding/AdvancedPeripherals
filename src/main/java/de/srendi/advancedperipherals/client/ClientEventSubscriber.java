package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import dan200.computercraft.shared.ModRegistry;
import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.network.toserver.OverlayModuleClientInfoPacket;
import de.srendi.advancedperipherals.common.network.toserver.PlayerInteractionPacket;
import de.srendi.advancedperipherals.common.network.toserver.SaddleTurtleControlPacket;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.smartglasses.modules.keyboard.KeyboardModule;
import de.srendi.advancedperipherals.common.util.HitResultUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEventSubscriber {
    private static int lastWidth = 0;
    private static int lastHeight = 0;
    private static double lastScale = 0;
    private static int lastGlassesId = -1;

    @SubscribeEvent
    public static void onClientLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientUUIDCache.reset();
        lastGlassesId = -1;
    }

    @SubscribeEvent
    public static void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientUUIDCache.reset();
        OverlayObjectHolder.clear();
    }

    @SubscribeEvent
    public static void preClientTick(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        ItemStack smartGlasses = SmartGlassesItem.getEquipped(player);
        if (smartGlasses.isEmpty()) {
            return;
        }
        int glassesId = SmartGlassesItem.getComputerID(smartGlasses);
        Window window = minecraft.getWindow();

        int sizeX = window.getWidth(), sizeY = window.getHeight();
        double guiScale = window.getGuiScale();

        if (sizeX != lastWidth || sizeY != lastHeight || guiScale != lastScale || glassesId != lastGlassesId) {
            lastWidth = sizeX;
            lastHeight = sizeY;
            lastScale = guiScale;
            lastGlassesId = glassesId;
            OverlayModuleClientInfoPacket.sendCurrentInformation();
        }
    }

    @SubscribeEvent
    public static void renderingHuds(RenderGuiLayerEvent.Pre event) {
        if (ClientRegistry.SADDLE_TURTLE_OVERLAY.shouldRenderFuelBar() && event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR)) {
            event.setCanceled(true);
        }
    }

    private static boolean sneaking = false;

    @SubscribeEvent
    public static void playerTryDismount(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean isShift = minecraft.options.keyShift.matches(event.getKey(), event.getScanCode());
        if (!isShift) {
            return;
        }
        switch (event.getAction()) {
            case InputConstants.PRESS -> {
                sneaking = true;
                if (ClientRegistry.SADDLE_TURTLE_OVERLAY.isPlayerControllingTurtle()) {
                    minecraft.options.keyShift.setDown(false);
                }
            }
            case InputConstants.RELEASE -> {
                sneaking = false;
            }
        }
    }

    private static Input lastInput = new Input();
    private static boolean lastSneak = false;

    @SubscribeEvent
    public static void playerMounting(EntityMountEvent event) {
        if (event.isMounting() && event.getEntityMounting() == Minecraft.getInstance().player && event.getEntityBeingMounted() instanceof TurtleSeatEntity) {
            // clear last key records
            lastInput.up = false;
            lastInput.down = false;
            lastInput.left = false;
            lastInput.right = false;
            lastInput.jumping = false;
            lastSneak = false;
        }
    }

    @SubscribeEvent
    public static void playerMove(MovementInputUpdateEvent event) {
        if (ClientRegistry.SADDLE_TURTLE_OVERLAY.isPlayerControllingTurtle()) {
            Input input = event.getInput();
            if (sneaking == lastSneak && lastInput != null) {
                if (lastInput.up == input.up && lastInput.down == input.down && lastInput.left == input.left && lastInput.right == input.right && lastInput.jumping == input.jumping) {
                    return;
                }
            }
            lastInput.up = input.up;
            lastInput.down = input.down;
            lastInput.left = input.left;
            lastInput.right = input.right;
            lastInput.jumping = input.jumping;
            lastSneak = sneaking;
            PacketDistributor.sendToServer(new SaddleTurtleControlPacket(input.up, input.down, input.left, input.right, input.jumping, sneaking));
        }
    }

    @SubscribeEvent
    public static void playerInteraction(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        int button = event.isAttack() ? 0 : event.isUseItem() ? 1 : event.isPickBlock() ? 2 : -1;
        if (button == -1) {
            return;
        }

        ItemStack glasses = SmartGlassesItem.getEquipped(player);
        if (glasses.isEmpty() || !glasses.getOrDefault(ModRegistry.DataComponents.ON.get(), false)) {
            return;
        }

        int buttons = SmartGlassesItem.getModuleData(glasses, KeyboardModule.ID, APDataComponents.HANDLING_INTERACTION_BUTTONS.get(), (byte) 0);

        if (((1 << button) & buttons) == 0) {
            return;
        }
        event.setSwingHand(true);
        event.setCanceled(true);

        BlockState hitBlock = null;
        UUID hitEntity = null;

        float partialTicks = minecraft.getTimer().getGameTimeDeltaTicks();
        Vec3 playerEyes = player.getEyePosition(partialTicks);
        double reachRange = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);

        HitResult hitResultBlock = player.pick(reachRange, partialTicks, false);
        if (hitResultBlock instanceof BlockHitResult result && result.getType() == HitResult.Type.BLOCK) {
            hitBlock = player.level().getBlockState(result.getBlockPos());
        }
        EntityHitResult hitResultEntity = HitResultUtil.getEntityHitResult(playerEyes, playerEyes.add(player.getViewVector(partialTicks).scale(reachRange)), player.level(), player);
        if (hitResultEntity.getType() == HitResult.Type.ENTITY) {
            hitEntity = hitResultEntity.getEntity().getUUID();
        }

        PacketDistributor.sendToServer(new PlayerInteractionPacket(button + 1, hitBlock, hitEntity));
    }
}
