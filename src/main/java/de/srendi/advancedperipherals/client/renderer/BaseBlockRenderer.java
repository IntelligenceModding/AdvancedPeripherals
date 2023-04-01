package de.srendi.advancedperipherals.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.common.blocks.blockentities.RedstoneIntegratorEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.RandomSource;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

public class BaseBlockRenderer implements BlockEntityRenderer<RedstoneIntegratorEntity> {

    private final BlockEntityRendererProvider.Context context;

    public BaseBlockRenderer(BlockEntityRendererProvider.Context context) {
        super();
        this.context = context;
    }

    @Override
    public void render(@NotNull RedstoneIntegratorEntity blockEntity, float partialTime, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        RandomSource randomsource = RandomSource.create();

        RenderType glowingBlock = APRenderTypes.GLOWING_BLOCK;
        poseStack.pushPose();
        context.getBlockRenderDispatcher().renderBatched(blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity.getLevel(), poseStack, multiBufferSource.getBuffer(glowingBlock), true, randomsource, ModelData.EMPTY, glowingBlock);
        poseStack.popPose();

    }
}
