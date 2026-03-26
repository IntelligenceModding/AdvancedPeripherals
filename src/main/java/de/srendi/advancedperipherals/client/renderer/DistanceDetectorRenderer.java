package de.srendi.advancedperipherals.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.blocks.blockentities.DistanceDetectorEntity;
import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class DistanceDetectorRenderer implements BlockEntityRenderer<DistanceDetectorEntity> {

    public DistanceDetectorRenderer(BlockEntityRendererProvider.Context pContext) {
        super();
    }

    @Override
    public void render(@NotNull DistanceDetectorEntity be, float partialTick, @NotNull PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!be.getShowLaser()) {
            return;
        }
        float distance = Mth.lerp(partialTick, be.currentDistanceO, be.currentDistanceLerped);
        float[] color = (be.detected() ? EnumColor.RED : EnumColor.DARK_RED).getRgb();
        renderBeaconBeam(be, poseStack, bufferSource, BeaconRenderer.BEAM_LOCATION, partialTick, 1, 0, distance + 0.5f, color, 0.05f, 0.09f);
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull DistanceDetectorEntity be) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public AABB getRenderBoundingBox(DistanceDetectorEntity be) {
        float currentDistance = be.getCurrentDistance();
        if (currentDistance == -1) {
            currentDistance = be.getMaxRange();
        }
        currentDistance += 1.5f;
        Direction direction = be.getBlockState().getValue(BaseBlock.ORIENTATION).front();
        Vec3 blockPos = Vec3.atCenterOf(be.getBlockPos());
        return new AABB(blockPos, blockPos.add(direction.getStepX() * currentDistance, direction.getStepY() * currentDistance, direction.getStepZ() * currentDistance));
    }

    @Override
    public boolean shouldRender(@NotNull DistanceDetectorEntity pBlockEntity, @NotNull Vec3 pCameraPos) {
        return true;
    }

    public static void renderBeaconBeam(DistanceDetectorEntity detectorEntity, PoseStack pPoseStack, MultiBufferSource pBufferSource, ResourceLocation pBeamLocation, float pPartialTick, float pTextureScale, int pYOffset, float pHeight, float[] pColors, float pBeamRadius, float pGlowRadius) {
        long pGameTime = detectorEntity.getLevel().getGameTime();
        float maxX = pYOffset + pHeight;
        Direction direction = detectorEntity.getBlockState().getValue(BaseBlock.ORIENTATION).front();
        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, 0.5D, 0.5D);
        float degrees = Math.floorMod(pGameTime, 360) + pPartialTick;
        float reversedDegrees = pHeight < 0 ? degrees : -degrees;
        float time = Mth.frac(reversedDegrees * 0.2F - Mth.floor(reversedDegrees * 0.1F));
        float r = pColors[0];
        float g = pColors[1];
        float b = pColors[2];
        pPoseStack.pushPose();
        pPoseStack.mulPose(direction.getRotation());
        pPoseStack.mulPose(Axis.YP.rotationDegrees(degrees * 2.25F - 45.0F));
        float f15 = -1.0F + time;
        float f16 = pHeight * pTextureScale * (0.5F / pBeamRadius) + f15;
        renderPart(pPoseStack, pBufferSource.getBuffer(RenderType.beaconBeam(pBeamLocation, false)), r, g, b, 1.0F, pYOffset, maxX, 0.0F, pBeamRadius, pBeamRadius, 0.0F, -pBeamRadius, 0.0F, 0.0F, -pBeamRadius, 0.0F, 1.0F, f16, f15);
        pPoseStack.popPose();
        pPoseStack.mulPose(direction.getRotation());
        f15 = -1.0F + time;
        f16 = pHeight * pTextureScale + f15;
        renderPart(pPoseStack, pBufferSource.getBuffer(RenderType.beaconBeam(pBeamLocation, true)), r, g, b, 0.225F, pYOffset, maxX, -pGlowRadius, -pGlowRadius, pGlowRadius, -pGlowRadius, -pBeamRadius, pGlowRadius, pGlowRadius, pGlowRadius, 0.0F, 1.0F, f16, f15);
        pPoseStack.popPose();
    }

    private static void renderPart(PoseStack pPoseStack, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, float pAlpha, int pMinY, float pMaxY, float pX0, float pZ0, float pX1, float pZ1, float pX2, float pZ2, float pX3, float pZ3, float pMinU, float pMaxU, float pMinV, float pMaxV) {
        PoseStack.Pose posestackPose = pPoseStack.last();
        Matrix4f matrix4f = posestackPose.pose();
        renderQuad(matrix4f, posestackPose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX0, pZ0, pX1, pZ1, pMinU, pMaxU, pMinV, pMaxV);
        renderQuad(matrix4f, posestackPose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX3, pZ3, pX2, pZ2, pMinU, pMaxU, pMinV, pMaxV);
        renderQuad(matrix4f, posestackPose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX1, pZ1, pX3, pZ3, pMinU, pMaxU, pMinV, pMaxV);
        renderQuad(matrix4f, posestackPose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX2, pZ2, pX0, pZ0, pMinU, pMaxU, pMinV, pMaxV);
    }

    private static void renderQuad(Matrix4f pPose, PoseStack.Pose pNormal, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, float pAlpha, int pMinY, float pMaxY, float pMinX, float pMinZ, float pMaxX, float pMaxZ, float pMinU, float pMaxU, float pMinV, float pMaxV) {
        addVertex(pPose, pNormal, pConsumer, pRed, pGreen, pBlue, pAlpha, pMaxY, pMinX, pMinZ, pMaxU, pMinV);
        addVertex(pPose, pNormal, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMinX, pMinZ, pMaxU, pMaxV);
        addVertex(pPose, pNormal, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxX, pMaxZ, pMinU, pMaxV);
        addVertex(pPose, pNormal, pConsumer, pRed, pGreen, pBlue, pAlpha, pMaxY, pMaxX, pMaxZ, pMinU, pMinV);
    }

    private static void addVertex(Matrix4f pPose, PoseStack.Pose pNormal, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, float pAlpha, float pY, float pX, float pZ, float pU, float pV) {
        pConsumer.addVertex(pPose, pX, pY, pZ).setColor(pRed, pGreen, pBlue, pAlpha).setUv(pU, pV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pNormal, 0.0F, 1.0F, 0.0F);
    }

}
