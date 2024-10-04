package de.srendi.advancedperipherals.client.smartglasses;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE, modid = AdvancedPeripherals.MOD_ID)
public class OverlayModuleLevelRenderer {

    @SubscribeEvent
    public static void renderLevelState(RenderLevelStageEvent event) {
        PoseStack posestack = event.getPoseStack();
        Vec3 view = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition();

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            bufferbuilder.begin(RenderType.solid().mode(), DefaultVertexFormat.BLOCK);
            BlockPos blockPos = new BlockPos(0, 190, 0);

            posestack.pushPose();
            posestack.translate(-view.x + blockPos.getX(), -view.y + blockPos.getY(), -view.z + blockPos.getZ());
            float[] colors = EnumColor.GREEN.getRgb();
            RenderSystem.setShaderColor(colors[0], colors[1], colors[2], 0.8f);

            Minecraft.getInstance().getBlockRenderer().renderBatched(Blocks.STONE.defaultBlockState(), blockPos, event.getCamera().getEntity().level, posestack, bufferbuilder, false, event.getCamera().getEntity().level.random);
            BufferUploader.drawWithShader(bufferbuilder.end());
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            posestack.popPose();

            blockPos = new BlockPos(2, 190, 0);

            colors = EnumColor.DARK_PURPLE.getRgb();

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferbuilder.begin(RenderType.translucent().mode(), DefaultVertexFormat.POSITION_COLOR_NORMAL);
            posestack.pushPose();

            posestack.translate(-view.x + blockPos.getX(), -view.y + blockPos.getY(), -view.z + blockPos.getZ());

            RenderUtil.drawPlane(posestack, bufferbuilder, colors[0], colors[1], colors[2], 0.8f, RenderUtil.Perspective.UP, 0f, 0.5f, 0f, 0.5f, 0f, 1f);

            BufferUploader.drawWithShader(bufferbuilder.end());
            posestack.popPose();
            posestack.pushPose();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferbuilder.begin(RenderType.translucent().mode(), DefaultVertexFormat.POSITION_COLOR_NORMAL);

            blockPos = new BlockPos(5, 190, 0);
            posestack.translate(-view.x + blockPos.getX(), -view.y + blockPos.getY(), -view.z + blockPos.getZ());

            RenderUtil.drawBox(posestack, bufferbuilder, colors[0], colors[1], colors[2], 0.8f, 16f, 16f, 12f);
            BufferUploader.drawWithShader(bufferbuilder.end());
            posestack.popPose();

        }
    }

}
