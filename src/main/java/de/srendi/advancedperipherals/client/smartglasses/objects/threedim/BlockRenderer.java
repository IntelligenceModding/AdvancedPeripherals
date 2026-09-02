package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BlockObject;
import de.srendi.advancedperipherals.common.util.fakelevel.FakeLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Quaternionf;

import java.util.List;
import java.util.WeakHashMap;

public class BlockRenderer implements IThreeDObjectRenderer<BlockObject> {
    private final WeakHashMap<ClientLevel, FakeLevel> fakeLevels = new WeakHashMap<>();

    @Override
    public void renderBatch(List<BlockObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 eyePos, Quaternionf eyeRotation) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
        BlockEntityRenderDispatcher blockEntityRenderer = minecraft.getBlockEntityRenderDispatcher();
        float partialTick = event.getPartialTick();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        RandomSource random = RandomSource.create();

        PoseStack.Pose lastPose = poseStack.last();
        poseStack.pushPose();

        for (BlockObject block : batch) {
            BlockState state = block.getBlockState();
            if (state == null) {
                continue;
            }

            VertexConsumer bufferBuilder = bufferSource.getBuffer(APRenderTypes.BLOCK_MAP.apply(block));

            poseStack.last().pose().set(lastPose.pose());
            poseStack.last().normal().set(lastPose.normal());

            if (block.relativePosition) {
                poseStack.translate(eyePos.x, eyePos.y, eyePos.z);
                if (block.relativeRotation) {
                    poseStack.mulPose(eyeRotation);
                }
            }
            poseStack.translate(block.x, block.y, block.z);
            poseStack.mulPose(block.getRotation());
            poseStack.scale(block.sizeX, block.sizeY, block.sizeZ);

            float alpha = block.opacity;

            poseStack.translate(-0.5f, -0.5f, -0.5f);
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
            if (!(state.getBlock() instanceof EntityBlock eb)) {
                continue;
            }
            BlockEntity blockEntity = eb.newBlockEntity(BlockPos.ZERO, state);
            if (blockEntity == null) {
                continue;
            }
            FakeLevel fakeLevel = this.fakeLevels.computeIfAbsent(minecraft.level, (level) -> new FakeLevel(minecraft.getConnection(), level));
            fakeLevel.setBlockAndUpdate(BlockPos.ZERO, state);
            fakeLevel.setBlockEntity(blockEntity);
            blockEntity.setLevel(fakeLevel);
            @SuppressWarnings("rawtypes")
            BlockEntityRenderer renderer = blockEntityRenderer.getRenderer(blockEntity);
            if (renderer == null) {
                continue;
            }
            poseStack.translate(0.5f, 0.5f, 0.5f);
            renderer.render(blockEntity, partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }
        poseStack.popPose();
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
