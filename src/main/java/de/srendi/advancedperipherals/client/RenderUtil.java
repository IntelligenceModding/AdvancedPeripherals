package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RenderUtil {

    public static void drawBox(PoseStack stack, VertexConsumer buffer, float r, float g, float b, float a, float sX, float sY, float sZ) {
        drawBox(stack, buffer, r, g, b, a, 0, 0, 0, sX, sY, sZ);
    }

    public static void drawVoxelShape(PoseStack stack, VertexConsumer buffer, float r, float g, float b, float a, VoxelShape shape) {
        drawVoxelShape(stack, buffer, shape, 0f, 0f, 0f, r, g, b, a);
    }

    public static void drawBox(PoseStack stack, VertexConsumer buffer, float r, float g, float b, float a, float pX, float pY, float pZ, float sX, float sY, float sZ) {
        stack.pushPose();
        sX = sX / 16; //Sizes in pixels please
        sY = sY / 16;
        sZ = sZ / 16;
        pX = pX / 16;
        pY = pY / 16;
        pZ = pZ / 16;

        drawPlane(stack, buffer, r, g, b, a, Direction.UP, pX, pY, pZ, sX, sY, sZ);
        drawPlane(stack, buffer, r, g, b, a, Direction.DOWN, pX, pY, pZ, sX, sY, sZ);
        drawPlane(stack, buffer, r, g, b, a, Direction.EAST, pX, pY, pZ, sX, sY, sZ);
        drawPlane(stack, buffer, r, g, b, a, Direction.WEST, pX, pY, pZ, sX, sY, sZ);
        drawPlane(stack, buffer, r, g, b, a, Direction.NORTH, pX, pY, pZ, sX, sY, sZ);
        drawPlane(stack, buffer, r, g, b, a, Direction.SOUTH, pX, pY, pZ, sX, sY, sZ);
        stack.popPose();
    }

    public static void drawPlane(PoseStack stack, VertexConsumer buffer, float r, float g, float b, float a, Direction perspective, float pX, float pY, float pZ, float sX, float sY, float sZ) {
        stack.pushPose();
        Matrix4f matrix4f = stack.last().pose();

        sX = sX / 2;
        sY = sY / 2;
        sZ = sZ / 2;

        pX = pX + 0.5f;
        pY = pY + 0.5f;
        pZ = pZ + 0.5f;


        if (perspective == Direction.UP) {
            buffer.vertex(matrix4f, -sX + pX, sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        if (perspective == Direction.DOWN) {
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
        }
        if (perspective == Direction.SOUTH) {
            buffer.vertex(matrix4f, sX + pX, -sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
        }
        if (perspective == Direction.NORTH) {
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
        }
        if (perspective == Direction.EAST) {
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, -sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        if (perspective == Direction.WEST) {
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, sZ + pZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        stack.popPose();
    }

    public static void drawVoxelShape(PoseStack pPoseStack, VertexConsumer pConsumer, VoxelShape pShape, double pX, double pY, double pZ, float r, float g, float b, float a) {
        for (Direction direction : Direction.values()) {
            pShape.calculateFace(direction).forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                // Get the vertices for the face
                float x1 = (float) (minX + pX);
                float y1 = (float) (minY + pY);
                float z1 = (float) (minZ + pZ);
                float x2 = (float) (maxX + pX);
                float y2 = (float) (maxY + pY);
                float z2 = (float) (maxZ + pZ);

                // Calculate the normal for the face
                Vec3i normalVec = direction.getNormal();
                float nx = normalVec.getX();
                float ny = normalVec.getY();
                float nz = normalVec.getZ();

                PoseStack.Pose pose = pPoseStack.last();

                //drawPlane(pPoseStack, pConsumer, r,g,b,a, direction, x1, y1, z1, x2, y2, z2);
                // Draw the quad (two triangles)
                pConsumer.vertex(pose.pose(), x1, y1, z1).color(r, g, b, a).normal(pose.normal(), nx, ny, nz).endVertex();
                pConsumer.vertex(pose.pose(), x2, y1, z1).color(r, g, b, a).normal(pose.normal(), nx, ny, nz).endVertex();
                pConsumer.vertex(pose.pose(), x2, y2, z2).color(r, g, b, a).normal(pose.normal(), nx, ny, nz).endVertex();
                pConsumer.vertex(pose.pose(), x1, y2, z2).color(r, g, b, a).normal(pose.normal(), nx, ny, nz).endVertex();
            });
        }

    }

    public static void drawShape(PoseStack pPoseStack, VertexConsumer pConsumer, VoxelShape pShape, double pX, double pY, double pZ, float r, float g, float b, float a) {
        PoseStack.Pose posestack$pose = pPoseStack.last();
        pShape.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> {
            float f = (float) (maxX - minX);
            float f1 = (float) (maxY - minY);
            float f2 = (float) (maxZ - minZ);
            float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
            f /= f3;
            f1 /= f3;
            f2 /= f3;
            pConsumer.vertex(posestack$pose.pose(), (float) (minX + pX), (float) (minY + pY), (float) (minZ + pZ)).color(r, g, b, a).normal(posestack$pose.normal(), f, f1, f2).endVertex();
            pConsumer.vertex(posestack$pose.pose(), (float) (maxX + pX), (float) (maxY + pY), (float) (maxZ + pZ)).color(r, g, b, a).normal(posestack$pose.normal(), f, f1, f2).endVertex();
        });
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

        drawPlane(stack, buffer, texture, Direction.UP, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        drawPlane(stack, buffer, texture, Direction.DOWN, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        drawPlane(stack, buffer, texture, Direction.EAST, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        drawPlane(stack, buffer, texture, Direction.WEST, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        drawPlane(stack, buffer, texture, Direction.NORTH, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        drawPlane(stack, buffer, texture, Direction.SOUTH, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        stack.popPose();
    }

    public static void drawPlane(PoseStack stack, VertexConsumer buffer, ResourceLocation texture, Direction perspective, float pX, float pY, float pZ, float sX, float sY, float sZ, float pUOffset, float pVOffset, float pWidth, float pHeight) {
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

        if (perspective == Direction.UP) {
            buffer.vertex(matrix4f, -sX + pX, sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        if (perspective == Direction.DOWN) {
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
        }
        if (perspective == Direction.SOUTH) {
            buffer.vertex(matrix4f, sX + pX, -sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
        }
        if (perspective == Direction.NORTH) {
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, sZ + pZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
        }
        if (perspective == Direction.EAST) {
            buffer.vertex(matrix4f, -sX + pX, -sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX + pX, sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX + pX, -sY + pY, -sZ + pZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        if (perspective == Direction.WEST) {
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
            drawShape(poseStack, consumer, texture, Shapes.create(aabb), pUOffset, pVOffset, pWidth, pHeight);
        }
    }

    private static void drawShape(PoseStack poseStack, VertexConsumer consumer, ResourceLocation texture, VoxelShape shape, float pUOffset, float pVOffset, float pWidth, float pHeight) {
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
            drawPlane(poseStack, consumer, texture, Direction.values()[perspective.getAndIncrement()], (float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, pUOffset, pVOffset, pWidth, pHeight);
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

}
