package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;

public interface IThreeDObjectRenderer<O extends ThreeDimensionalObject> extends IObjectRenderer {

    void renderBatch(List<O> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 view);

    default void onPostRender(O object) {
        if (object.disableCulling) {
            RenderSystem.enableCull();
        }
        if (object.disableDepthTest) {
            RenderSystem.enableDepthTest();
        }
    }

    default void onPreRender(O object) {
        if (object.disableCulling) {
            RenderSystem.disableCull();
        }
        if (object.disableDepthTest) {
            RenderSystem.disableDepthTest();
        }
    }
}
