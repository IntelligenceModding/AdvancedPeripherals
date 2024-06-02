package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.platform.InputConstants;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import de.srendi.advancedperipherals.common.network.PacketHandler;
import de.srendi.advancedperipherals.common.network.toserver.SaddleTurtleControlPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, value = Dist.CLIENT)
public class ClientEventSubscriber {
    @SubscribeEvent
    public static void renderingHuds(RenderGuiOverlayEvent.Pre event) {
        if (ClientRegistry.SADDLE_TURTLE_OVERLAY.shouldRenderFuelBar() && event.getOverlay().id().equals(VanillaGuiOverlay.EXPERIENCE_BAR.id())) {
            event.setCanceled(true);
            return;
        }
    }

    private static boolean sneaking = false;

    @SubscribeEvent
    public static void playerTryDismount(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.keyShift.matches(event.getKey(), event.getScanCode())) {
            return;
        }
        switch (event.getAction()) {
            case InputConstants.PRESS:
                sneaking = true;
                if (ClientRegistry.SADDLE_TURTLE_OVERLAY.isPlayerMountedOnTurtle()) {
                    minecraft.options.keyShift.setDown(false);
                }
                break;
            case InputConstants.RELEASE:
                sneaking = false;
                break;
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
        if (ClientRegistry.SADDLE_TURTLE_OVERLAY.isPlayerMountedOnTurtle()) {
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
            PacketHandler.sendToServer(new SaddleTurtleControlPacket(input.up, input.down, input.left, input.right, input.jumping, sneaking));
        }
    }
}
