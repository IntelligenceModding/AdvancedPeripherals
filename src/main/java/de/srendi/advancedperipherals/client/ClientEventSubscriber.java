package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import de.srendi.advancedperipherals.common.network.toserver.OverlayModuleClientInfoPacket;
import de.srendi.advancedperipherals.common.network.toserver.SaddleTurtleControlPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEventSubscriber {
    private static int lastWidth = 0;
    private static int lastHeight = 0;
    private static double lastScale = 0;

    @SubscribeEvent
    public static void preClientTick(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        Window window = minecraft.getWindow();

        int sizeX = window.getWidth(), sizeY = window.getHeight();
        double guiScale = window.getGuiScale();

        if (sizeX != lastWidth || sizeY != lastHeight || guiScale != lastScale) {
            lastWidth = sizeX;
            lastHeight = sizeY;
            lastScale = guiScale;
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
}
