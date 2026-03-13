package de.srendi.advancedperipherals.client.smartglasses;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
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

    @SubscribeEvent
    public static void renderLevelState(RenderLevelStageEvent event) {
        PoseStack poseStack = event.getPoseStack();
        Vec3 view = event.getCamera().getPosition();

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            Map<Class<? extends RenderableObject>, List<ThreeDimensionalObject>> batches = new HashMap<>();

            for (RenderableObject object : OverlayObjectHolder.getObjects()) {
                if (!object.isEnabled()) {
                    continue;
                }
                if (!(object instanceof ThreeDimensionalObject threeDimObject)) {
                    continue;
                }

                Class<? extends ThreeDimensionalObject> objectClass = threeDimObject.getClass();

                List<ThreeDimensionalObject> batchList = batches.get(objectClass);
                if (batchList == null) {
                    batchList = new ArrayList<>();
                    batches.put(objectClass, batchList);
                }
                batchList.add(threeDimObject);
            }

            for (List<ThreeDimensionalObject> batch : batches.values()) {
                batch.get(0).getObjectRenderer().renderBatch(batch, event, poseStack, view);
            }

            // TODO: Everything below here is just for debugging and testing. Will be removed before we push to production
            // {
            //     BlockPos blockPos = new BlockPos(2, 100, 0);

            //     float[] colors = EnumColor.DARK_PURPLE.getRgb();

            //     RenderSystem.setShader(GameRenderer::getPositionColorShader);
            //     BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder(RenderType.translucent().mode(), DefaultVertexFormat.POSITION_COLOR_NORMAL);
            //     poseStack.pushPose();

            //     poseStack.translate(-view.x + blockPos.getX(), -view.y + blockPos.getY(), -view.z + blockPos.getZ());

            //     RenderUtil.drawPlane(poseStack, bufferbuilder, colors[0], colors[1], colors[2], 0.8f, Direction.UP, 0f, 0.5f, 0f, 0.5f, 0f, 1f);

            //     BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
            //     poseStack.popPose();

            //     VertexConsumer boxVertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
            //     //RenderSystem.setShader(GameRenderer::getPositionColorLightmapShader);

            //     //bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP);
            //     poseStack.pushPose();
            //     colors = EnumColor.WHITE.getRgb();

            //     blockPos = new BlockPos(0, 100, 0);
            //     poseStack.translate(-view.x + blockPos.getX(), -view.y + blockPos.getY(), -view.z + blockPos.getZ());

            //     RenderUtil.drawSphere(poseStack, boxVertexConsumer, 2f, 0f, 0f, 0f, 270f, 0f, 0f, colors[0], colors[1], colors[2], 0.4f, 16, 128);

            //     //BufferUploader.drawWithShader(bufferbuilder.end());
            //     poseStack.popPose();

            //     boxVertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));

            //     //bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_NORMAL);
            //     poseStack.pushPose();

            //     colors = EnumColor.WHITE.getRgb();
            //     blockPos = new BlockPos(6, 100, 0);
            //     poseStack.translate(-view.x + blockPos.getX(), -view.y + blockPos.getY(), -view.z + blockPos.getZ());

            //     RenderUtil.drawTorus(poseStack, boxVertexConsumer, 1f, 0.4f, 0f, 0f, 0f, 0f, 0f, 0f, colors[0], colors[1], colors[2], 1f, 48, 48);

            //     //BufferUploader.drawWithShader(bufferbuilder.end());
            //     poseStack.popPose();
            // }
        }
    }
}
