package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Quaternionf;

import java.util.List;

public abstract class Simple2DObjectRenderer<O extends RenderableObject> implements IObjectRenderer<O> {
    protected abstract void render(O object, GuiGraphics gui, DeltaTracker partialTick);

    @Override
    public void render2D(List<O> batch, GuiGraphics gui, DeltaTracker partialTick) {
        PoseStack.Pose last = gui.pose().last();
        gui.pose().pushPose();
        for (O obj : batch) {
            gui.pose().last().pose().set(last.pose());
            gui.pose().last().normal().set(last.normal());
            gui.pose().translate(obj.x, obj.y, obj.z);
            gui.pose().mulPose(obj.getRotation());
            this.render(obj, gui, partialTick);
        }
        gui.pose().popPose();
    }

    @Override
    public void render3D(List<O> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 eyePos, Quaternionf eyeRotation) {
        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        GuiGraphics gui = new GuiGraphics(minecraft, bufferSource);
        DeltaTracker partialTick = event.getPartialTick();

        for (O obj : batch) {
            gui.pose().last().pose().set(poseStack.last().pose());
            IObjectRenderer.apply3DTransforms(gui.pose(), obj, eyePos, eyeRotation);
            this.render(obj, gui, partialTick);
        }
    }
}
