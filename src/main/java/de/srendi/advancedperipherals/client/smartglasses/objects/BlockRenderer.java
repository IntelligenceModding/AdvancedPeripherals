package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.BlockObject;
import de.srendi.advancedperipherals.common.util.fakelevel.FakeLevel;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.gui.GuiGraphics;
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

import java.util.List;
import java.util.WeakHashMap;

public class BlockRenderer extends Simple2DObjectRenderer<BlockObject> {
    private final RandomSource random = RandomSource.create();
    private final WeakHashMap<ClientLevel, FakeLevel> fakeLevels = new WeakHashMap<>();

    @Override
    protected void render(BlockObject block, GuiGraphics gui, DeltaTracker partialTickTraker) {
        BlockState state = block.getBlockState();
        if (state == null) {
            return;
        }

        BlockRenderDispatcher blockRenderer = gui.minecraft.getBlockRenderer();
        BlockEntityRenderDispatcher blockEntityRenderer = gui.minecraft.getBlockEntityRenderDispatcher();
        float partialTick = partialTickTraker.getGameTimeDeltaPartialTick(true);
        MultiBufferSource.BufferSource bufferSource = gui.bufferSource();
        VertexConsumer bufferBuilder = bufferSource.getBuffer(APRenderTypes.BLOCK_MAP.apply(block));

        gui.pose().scale(block.sizeX, block.sizeY, block.sizeZ);

        float alpha = block.opacity;

        gui.pose().translate(-0.5f, -0.5f, -0.5f);
        renderBlockModel(
            gui.pose().last(),
            bufferBuilder,
            state,
            blockRenderer.blockColors,
            blockRenderer.getBlockModel(state),
            block.tintAll,
            block.color,
            alpha,
            LightTexture.FULL_BRIGHT,
            OverlayTexture.NO_OVERLAY,
            this.random
        );
        if (!(state.getBlock() instanceof EntityBlock eb)) {
            return;
        }
        BlockEntity blockEntity = eb.newBlockEntity(BlockPos.ZERO, state);
        if (blockEntity == null) {
            return;
        }
        FakeLevel fakeLevel = this.fakeLevels.computeIfAbsent(gui.minecraft.level, (level) -> new FakeLevel(gui.minecraft.getConnection(), level));
        fakeLevel.setBlockAndUpdate(BlockPos.ZERO, state);
        fakeLevel.setBlockEntity(blockEntity);
        blockEntity.setLevel(fakeLevel);
        @SuppressWarnings("rawtypes")
        BlockEntityRenderer renderer = blockEntityRenderer.getRenderer(blockEntity);
        if (renderer == null) {
            return;
        }
        gui.pose().translate(0.5f, 0.5f, 0.5f);
        renderer.render(blockEntity, partialTick, gui.pose(), bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
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
