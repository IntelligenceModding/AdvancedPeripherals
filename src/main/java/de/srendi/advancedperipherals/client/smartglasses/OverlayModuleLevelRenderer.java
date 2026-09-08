package de.srendi.advancedperipherals.client.smartglasses;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
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
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        if (SmartGlassesItem.getEquipped(livingEntity).isEmpty()) {
            return;
        }

        float partialTicks = event.getPartialTick();
        Quaternionf eyeRotation = new Quaternionf()
            .rotationY(Mth.DEG_TO_RAD * (180 - livingEntity.getViewYRot(partialTicks)))
            .rotateX(Mth.DEG_TO_RAD * -livingEntity.getViewXRot(partialTicks));

        Vec3 view = camera.getPosition();
        Vec3 eyePos = entity.getEyePosition(partialTicks);
        poseStack.pushPose();
        poseStack.translate(-view.x, -view.y, -view.z);

        Map<IObjectRenderer, List<RenderableObject>> batches = new HashMap<>();

        for (OverlayObject object : OverlayObjectHolder.getObjects()) {
            if (!object.isEnabled()) {
                continue;
            }
            if (!(object instanceof RenderableObject renderableObject) || renderableObject.gui) {
                continue;
            }

            batches.computeIfAbsent(renderableObject.getType().getRenderer(), (r) -> new ArrayList<>()).add(renderableObject);
        }

        for (Map.Entry<IObjectRenderer, List<RenderableObject>> entry : batches.entrySet()) {
            entry.getKey().render3D(entry.getValue(), event, poseStack, eyePos, eyeRotation);
        }
        poseStack.popPose();
    }
}
