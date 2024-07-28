package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.toserver.SaddleTurtleControlPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.player.Input;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, value = Dist.CLIENT)
public class ClientEventSubscriber {
    public static final Minecraft MINECRAFT = Minecraft.getInstance();

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
        if (!MINECRAFT.options.keyShift.matches(event.getKey(), event.getScanCode())) {
            return;
        }
        switch (event.getAction()) {
            case InputConstants.PRESS:
                sneaking = true;
                if (ClientRegistry.SADDLE_TURTLE_OVERLAY.isPlayerMountedOnTurtle()) {
                    MINECRAFT.options.keyShift.setDown(false);
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
        if (event.isMounting() && event.getEntityMounting() == MINECRAFT.player && event.getEntityBeingMounted() instanceof TurtleSeatEntity) {
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
            APNetworking.sendToServer(new SaddleTurtleControlPacket(input.up, input.down, input.left, input.right, input.jumping, sneaking));
        }
    }

    private static final List<LazyRenderer> renderers = new ArrayList<>();

    public static void addRenderer(LazyRenderer r) {
        renderers.add(r);
    }

    // Reference https://github.com/mekanism/Mekanism/blob/1.19.x/src/main/java/mekanism/client/render/RenderTickHandler.java#L152
    @SubscribeEvent
    public static void renderWorld(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            if (renderers.isEmpty()) {
                return;
            }
            Camera camera = event.getCamera();
            PoseStack matrix = event.getPoseStack();
            MultiBufferSource buffer = MINECRAFT.renderBuffers().bufferSource();
            float partialTicks = event.getPartialTick();

            matrix.pushPose();
            // here we translate based on the inverse position of the client viewing camera to get back to 0, 0, 0
            Vec3 camVec = camera.getPosition();
            matrix.translate(-camVec.x, -camVec.y, -camVec.z);

            renderers.forEach((r) -> {
                r.render(partialTicks, matrix, buffer);
            });
            renderers.clear();

            matrix.popPose();
        }
    }

    public static interface LazyRenderer {
        void render(float partialTicks, PoseStack transform, MultiBufferSource bufferSource);
        Vec3 getCenterPos(float partialTicks);
    }
}
