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
import net.neoforged.client.event.RenderLevelStageEvent;
import net.neoforged.eventbus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE, modid = AdvancedPeripherals.MOD_ID)
public class OverlayModuleLevelRenderer {

    @SubscribeEvent
    public static void renderLevelState(RenderLevelStageEvent event) {
        PoseStack posestack = event.getPoseStack();
        Vec3 view = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition();

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            Map<Class<? extends RenderableObject>, List<ThreeDimensionalObject>> batches = new HashMap<>();

            for (RenderableObject object : OverlayObjectHolder.getObjects()) {
                if (!object.isEnabled() || !(object.getRenderObject() instanceof IThreeDObjectRenderer))
                    continue;

                ThreeDimensionalObject threeDimObject = (ThreeDimensionalObject) object;

                Class<? extends ThreeDimensionalObject> objectClass = threeDimObject.getClass();

                if (batches.containsKey(objectClass)) {
                    batches.get(objectClass).add(threeDimObject);
                    continue;
                }

                List<ThreeDimensionalObject> newBatchArray = new ArrayList<>();
                newBatchArray.add(threeDimObject);
                batches.put(objectClass, newBatchArray);
            }

            for (List<ThreeDimensionalObject> batch : batches.values()) {
                ((IThreeDObjectRenderer) batch.get(0).getRenderObject()).renderBatch(batch, event, posestack, view, bufferbuilder);
            }

            //TODO Everything below here is just for debugging and testing. Will be removed before we push to production
            BlockPos blockPos = new BlockPos(2, 100, 0);

            float[] colors = EnumColor.DARK_PURPLE.getRgb();

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferbuilder.begin(RenderType.translucent().mode(), DefaultVertexFormat.POSITION_COLOR_NORMAL);
            posestack.pushPose();

            posestack.translate(-view.x + blockPos.getX(), -view.y + blockPos.getY(), -view.z + blockPos.getZ());

            RenderUtil.drawPlane(posestack, bufferbuilder, colors[0], colors[1], colors[2], 0.8f, Direction.UP, 0f, 0.5f, 0f, 0.5f, 0f, 1f);

            BufferUploader.drawWithShader(bufferbuilder.end());
            posestack.popPose();

            VertexConsumer boxVertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
            //RenderSystem.setShader(GameRenderer::getPositionColorLightmapShader);

            //bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP);
            posestack.pushPose();
            colors = EnumColor.WHITE.getRgb();

            blockPos = new BlockPos(0, 100, 0);
            posestack.translate(-view.x + blockPos.getX(), -view.y + blockPos.getY(), -view.z + blockPos.getZ());

            RenderUtil.drawSphere(posestack, boxVertexConsumer, 2f, 0f, 0f, 0f, 270f, 0f, 0f, colors[0], colors[1], colors[2], 0.4f, 16, 128);

            //BufferUploader.drawWithShader(bufferbuilder.end());
            posestack.popPose();

            boxVertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));

            //bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_NORMAL);
            posestack.pushPose();

            colors = EnumColor.WHITE.getRgb();
            blockPos = new BlockPos(6, 100, 0);
            posestack.translate(-view.x + blockPos.getX(), -view.y + blockPos.getY(), -view.z + blockPos.getZ());

            RenderUtil.drawTorus(posestack, boxVertexConsumer, 1f, 0.4f, 0f, 0f, 0f, 0f, 0f, 0f, colors[0], colors[1], colors[2], 1f, 48, 48);

            //BufferUploader.drawWithShader(bufferbuilder.end());
            posestack.popPose();

        }
    }

}
