package de.srendi.advancedperipherals.client.smartglasses;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Quaternionf;

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
        Entity entity = camera.getEntity();
        if (entity == null) {
            return;
        }
        float partialTicks = camera.getPartialTickTime();
        Quaternionf eyeRotation = new Quaternionf();
        if (entity instanceof LivingEntity livingEntity) {
            eyeRotation
                .rotationY(Mth.DEG_TO_RAD * (180 - livingEntity.getViewYRot(partialTicks)))
                .rotateX(Mth.DEG_TO_RAD * -livingEntity.getViewXRot(partialTicks));
        }

        Vec3 view = camera.getPosition();
        Vec3 eyePos = entity.getEyePosition(camera.getPartialTickTime());
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
            entry.getKey().renderBatch(entry.getValue(), event, poseStack, eyePos, eyeRotation);
        }
        poseStack.popPose();
    }
}
