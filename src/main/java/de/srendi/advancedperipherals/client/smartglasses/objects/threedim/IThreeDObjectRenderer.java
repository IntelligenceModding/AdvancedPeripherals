package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.List;

public interface IThreeDObjectRenderer<O extends ThreeDimensionalObject> extends IObjectRenderer {

    void renderBatch(List<O> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 eyePos, Quaternionf eyeRotation);

    default void onPreRender(@NotNull O object) {
        if (!object.culling) {
            RenderSystem.disableCull();
        }
        if (!object.depthTest) {
            RenderSystem.disableDepthTest();
        }
    }

    default void onPostRender(@NotNull O object) {
        if (!object.culling) {
            RenderSystem.enableCull();
        }
        if (!object.depthTest) {
            RenderSystem.enableDepthTest();
        }
    }
}
