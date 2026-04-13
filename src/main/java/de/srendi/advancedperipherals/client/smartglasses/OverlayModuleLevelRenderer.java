package de.srendi.advancedperipherals.client.smartglasses;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(Dist.CLIENT)
public class OverlayModuleLevelRenderer {

    @SuppressWarnings("rawtypes")
    @SubscribeEvent
    public static void renderLevelState(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        if (camera.getEntity() == null) {
            return;
        }

        Vec3 view = camera.getPosition();
        Vec3 eyePos = camera.getEntity().getEyePosition(camera.getPartialTickTime());
        poseStack.pushPose();
        poseStack.translate(-view.x, -view.y, -view.z);

        Map<IThreeDObjectRenderer, List<ThreeDimensionalObject>> batches = new HashMap<>();

        for (OverlayObject object : OverlayObjectHolder.getObjects()) {
            if (!object.isEnabled()) {
                continue;
            }
            if (!(object instanceof ThreeDimensionalObject threeDimObject)) {
                continue;
            }

            IThreeDObjectRenderer renderer = (IThreeDObjectRenderer) object.getType().getRenderer();
            batches.computeIfAbsent(renderer, (r) -> new ArrayList<>()).add(threeDimObject);
        }

        for (Map.Entry<IThreeDObjectRenderer, List<ThreeDimensionalObject>> entry : batches.entrySet()) {
            entry.getKey().renderBatch(entry.getValue(), event, poseStack, eyePos);
        }
        poseStack.popPose();
    }
}
