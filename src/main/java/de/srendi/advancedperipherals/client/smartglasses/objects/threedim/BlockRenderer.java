package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BlockObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Quaternionf;

import java.util.List;

public class BlockRenderer implements IThreeDObjectRenderer<BlockObject> {

    @Override
    public void renderBatch(List<BlockObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 eyePos, Quaternionf eyeRotation) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        RandomSource random = RandomSource.create();

        for (BlockObject block : batch) {
            BlockState state = block.getBlockState();
            if (state == null) {
                continue;
            }

            VertexConsumer bufferBuilder = bufferSource.getBuffer(APRenderTypes.BLOCK_MAP.apply(block));

            poseStack.pushPose();

            if (block.relativePosition) {
                poseStack.translate(eyePos.x, eyePos.y, eyePos.z);
                if (block.relativeRotation) {
                    poseStack.mulPose(eyeRotation);
                }
            }
            poseStack.translate(block.x, block.y, block.z);
            poseStack.mulPose(block.getRotation());
            poseStack.scale(block.sizeX, block.sizeY, block.sizeZ);
            poseStack.translate(-0.5, -0.5, -0.5);

            float alpha = block.opacity;

            renderBlockModel(
                poseStack.last(),
                bufferBuilder,
                state,
                blockRenderer.blockColors,
                blockRenderer.getBlockModel(state),
                block.tintAll,
                block.color,
                alpha,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                random
            );

            poseStack.popPose();
        }
    }

    private void renderBlockModel(
        PoseStack.Pose pose,
        VertexConsumer bufferBuilder,
        BlockState state,
        BlockColors blockColors,
        BakedModel model,
        boolean tintAll,
        int color,
        float alpha,
        int packedLight,
        int packedOverlay,
        RandomSource random
    ) {
        long seed = 42;
        for (Direction d : Direction.values()) {
            random.setSeed(seed);
            List<BakedQuad> quads = model.getQuads(state, d, random);
            for (BakedQuad quad : quads) {
                renderBlockQuad(pose, bufferBuilder, state, blockColors, quad, tintAll, color, alpha, packedLight, packedOverlay);
            }
        }
        random.setSeed(seed);
        List<BakedQuad> quads = model.getQuads(state, null, random);
        for (BakedQuad quad : quads) {
            renderBlockQuad(pose, bufferBuilder, state, blockColors, quad, tintAll, color, alpha, packedLight, packedOverlay);
        }
    }

    private void renderBlockQuad(
        PoseStack.Pose pose,
        VertexConsumer bufferBuilder,
        BlockState state,
        BlockColors blockColors,
        BakedQuad quad,
        boolean tintAll,
        int color,
        float alpha,
        int packedLight,
        int packedOverlay
    ) {
        int clor = color;
        if (clor == -1) {
            if (quad.isTinted()) {
                clor = blockColors.getColor(state, null, null, quad.getTintIndex());
            }
        } else if (!tintAll && !quad.isTinted()) {
            clor = -1;
        }
        bufferBuilder.putBulkData(
            pose,
            quad,
            RenderUtil.getRed(clor),
            RenderUtil.getGreen(clor),
            RenderUtil.getBlue(clor),
            alpha,
            packedLight,
            packedOverlay,
            true
        );
    }
}
