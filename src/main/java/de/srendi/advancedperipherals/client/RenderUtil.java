package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RenderUtil {

    public static void drawBox(PoseStack stack, VertexConsumer buffer, float r, float g, float b, float a, float sX, float sY, float sZ) {
        drawBox(stack, buffer, r, g, b, a, 0, 0, 0, sX, sY, sZ);
    }

    public static void drawVoxelShape(PoseStack stack, VertexConsumer buffer, float r, float g, float b, float a, VoxelShape shape, float x, float y, float z) {
        renderVoxelShape(stack, buffer, r, g, b, a, shape);
    }

    public static void drawBox(PoseStack stack, VertexConsumer buffer, float r, float g, float b, float a, float pX, float pY, float pZ, float sX, float sY, float sZ) {
        stack.pushPose();
        sX = sX / 16; //Sizes in pixels please
        sY = sY / 16;
        sZ = sZ / 16;
        pX = pX / 16;
        pY = pY / 16;
        pZ = pZ / 16;

        drawPlane(stack, buffer, r, g, b, a, Perspective.UP, pX, pY, pZ, sX, sY, sZ);
        drawPlane(stack, buffer, r, g, b, a, Perspective.DOWN, pX, pY, pZ, sX, sY, sZ);
        drawPlane(stack, buffer, r, g, b, a, Perspective.RIGHT, pX, pY, pZ, sX, sY, sZ);
        drawPlane(stack, buffer, r, g, b, a, Perspective.LEFT, pX, pY, pZ, sX, sY, sZ);
        drawPlane(stack, buffer, r, g, b, a, Perspective.FRONT, pX, pY, pZ, sX, sY, sZ);
        drawPlane(stack, buffer, r, g, b, a, Perspective.BACK, pX, pY, pZ, sX, sY, sZ);
        stack.popPose();
    }

    public static void drawPlane(PoseStack stack, VertexConsumer buffer, float r, float g, float b, float a, Perspective perspective, float pX, float pY, float pZ, float sX, float sY, float sZ) {
        stack.pushPose();
        Matrix4f matrix4f = stack.last().pose();

        sX = sX / 2;
        sY = sY / 2;
        sZ = sZ / 2;

        pX = pX + 0.5f;
        pY = pY + 0.5f;
        pZ = pZ + 0.5f;


        if (perspective == Perspective.UP) {
            buffer.vertex(matrix4f, -sX + pX, sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        if (perspective == Perspective.DOWN) {
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
        }
        if (perspective == Perspective.BACK) {
            buffer.vertex(matrix4f, sX + pX, -sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
        }
        if (perspective == Perspective.FRONT) {
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
        }
        if (perspective == Perspective.RIGHT) {
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        if (perspective == Perspective.LEFT) {
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        stack.popPose();
    }

    public static void renderVoxelShape(PoseStack poseStack, VertexConsumer consumer, float r, float g, float b, float a, VoxelShape shape) {
        List<AABB> list = shape.toAabbs();

        for (AABB aabb : list) {
            renderShape(poseStack, consumer, r, g, b, a, Shapes.create(aabb));
        }
    }

    private static void renderShape(PoseStack poseStack, VertexConsumer consumer, float r, float g, float b, float a, VoxelShape shape) {
        AtomicInteger perspective = new AtomicInteger();
        shape.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> {

            minX = minX / 16;
            minY = minY / 16;
            minZ = minZ / 16;
            maxX = maxX / 16;
            maxY = maxY / 16;
            maxZ = maxZ / 16;

            if (perspective.get() > 5)
                perspective.set(0);
            drawPlane(poseStack, consumer, r, g, b, a, Perspective.values()[perspective.getAndIncrement()], (float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ);
        });
        perspective.set(0);
    }

    public static void drawBox(PoseStack stack, VertexConsumer buffer, ResourceLocation texture, float sX, float sY, float sZ, float pUOffset, float pVOffset, float pWidth, float pHeight) {
        drawBox(stack, buffer, texture, 0, 0, 0, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
    }

    public static void drawVoxelShape(PoseStack stack, VertexConsumer buffer, ResourceLocation texture, VoxelShape shape, float pUOffset, float pVOffset, float pWidth, float pHeight) {
        renderVoxelShape(stack, buffer, texture, shape, pUOffset, pVOffset, pWidth, pHeight);
    }

    public static void drawBox(PoseStack stack, VertexConsumer buffer, ResourceLocation texture, float pX, float pY, float pZ, float sX, float sY, float sZ, float pUOffset, float pVOffset, float pWidth, float pHeight) {
        stack.pushPose();
        sX = sX / 16; //Sizes in pixels please
        sY = sY / 16;
        sZ = sZ / 16;
        pX = pX / 16;
        pY = pY / 16;
        pZ = pZ / 16;

        drawPlane(stack, buffer, texture, Perspective.UP, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        drawPlane(stack, buffer, texture, Perspective.DOWN, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        drawPlane(stack, buffer, texture, Perspective.RIGHT, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        drawPlane(stack, buffer, texture, Perspective.LEFT, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        drawPlane(stack, buffer, texture, Perspective.FRONT, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        drawPlane(stack, buffer, texture, Perspective.BACK, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        stack.popPose();
    }

    public static void drawPlane(PoseStack stack, VertexConsumer buffer, ResourceLocation texture, Perspective perspective, float pX, float pY, float pZ, float sX, float sY, float sZ, float pUOffset, float pVOffset, float pWidth, float pHeight) {
        stack.pushPose();
        Matrix4f matrix4f = stack.last().pose();

        TextureAtlasSprite stillTexture = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);

        sX = sX / 2;
        sY = sY / 2;
        sZ = sZ / 2;

        pX = pX + 0.5f;
        pY = pY + 0.5f;
        pZ = pZ + 0.5f;

        float u1 = stillTexture.getU(pUOffset);
        float u2 = stillTexture.getU(pWidth);
        float v1 = stillTexture.getV(pVOffset);
        float v2 = stillTexture.getV(pHeight);

        if (perspective == Perspective.UP) {
            buffer.vertex(matrix4f, -sX + pX, sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        if (perspective == Perspective.DOWN) {
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
        }
        if (perspective == Perspective.BACK) {
            buffer.vertex(matrix4f, sX + pX, -sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
        }
        if (perspective == Perspective.FRONT) {
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
        }
        if (perspective == Perspective.RIGHT) {
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        if (perspective == Perspective.LEFT) {
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        stack.popPose();
    }

    public static void renderVoxelShape(PoseStack poseStack, VertexConsumer consumer, ResourceLocation texture, VoxelShape shape, float pUOffset, float pVOffset, float pWidth, float pHeight) {
        List<AABB> list = shape.toAabbs();

        for (AABB aabb : list) {
            renderShape(poseStack, consumer, texture, Shapes.create(aabb), pUOffset, pVOffset, pWidth, pHeight);
        }
    }

    private static void renderShape(PoseStack poseStack, VertexConsumer consumer, ResourceLocation texture, VoxelShape shape, float pUOffset, float pVOffset, float pWidth, float pHeight) {
        AtomicInteger perspective = new AtomicInteger();
        shape.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> {

            minX = minX / 16;
            minY = minY / 16;
            minZ = minZ / 16;
            maxX = maxX / 16;
            maxY = maxY / 16;
            maxZ = maxZ / 16;

            if (perspective.get() > 5)
                perspective.set(0);
            drawPlane(poseStack, consumer, texture, Perspective.values()[perspective.getAndIncrement()], (float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, pUOffset, pVOffset, pWidth, pHeight);
        });
        perspective.set(0);
    }

    public static float getBlue(int hex) {
        return (hex & 0xFF) / 255.0F;
    }

    public static float getGreen(int hex) {
        return (hex >> 8 & 0xFF) / 255.0F;
    }

    public static float getRed(int hex) {
        return (hex >> 16 & 0xFF) / 255.0F;
    }

    public static float getAlpha(int hex) {
        return (hex >> 24 & 0xFF) / 255.0F;
    }

    public enum Perspective {

        UP, DOWN, RIGHT, LEFT, FRONT, BACK
    }

}
