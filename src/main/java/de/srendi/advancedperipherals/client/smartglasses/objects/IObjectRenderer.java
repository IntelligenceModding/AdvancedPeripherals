package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;

public interface IObjectRenderer<O extends OverlayObject> {
    void render2D(List<O> batch, GuiGraphics gui, DeltaTracker partialTick);

    void render3D(List<O> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 eyePos, Quaternionf eyeRotation);

    /**
     * Get the weight of the renderer. Lower weight means higher priority and it will render first.
     * Some things need to be rendered before others to prevent color and opacity issues.
     * @return the weight of the renderer.
     */
    default int getWeight() {
        return 100;
    }

    static void apply3DTransforms(PoseStack poseStack, RenderableObject obj, Vec3 eyePos, Quaternionf eyeRotation) {
        if (obj.relativePosition) {
            poseStack.translate(eyePos.x, eyePos.y, eyePos.z);
            if (obj.relativeRotation) {
                poseStack.mulPose(eyeRotation);
            }
        }
        poseStack.translate(obj.x, obj.y, obj.z);
        poseStack.mulPose(obj.getRotation());
    }

    static void apply3DTransforms(Matrix4f mat, RenderableObject obj, Vec3 eyePos, Quaternionf eyeRotation) {
        if (obj.relativePosition) {
            mat.translate((float) eyePos.x, (float) eyePos.y, (float) eyePos.z);
            if (obj.relativeRotation) {
                mat.rotate(eyeRotation);
            }
        }
        mat.translate(obj.x, obj.y, obj.z);
        mat.rotate(obj.getRotation());
    }
}
