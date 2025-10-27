package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import mekanism.common.lib.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Quaternionf;

public class RenderUtil {

    public static void drawBox(PoseStack poseStack, VertexConsumer buffer, float r, float g, float b, float a, float pX, float pY, float pZ, float xRot, float yRot, float zRot, float sX, float sY, float sZ) {
        poseStack.pushPose();
        sX = sX / 16; //Sizes in pixels please
        sY = sY / 16;
        sZ = sZ / 16;
        pX = pX / 16;
        pY = pY / 16;
        pZ = pZ / 16;

        poseStack.mulPose(new Quaternionf(xRot, yRot, zRot, true));

        drawPlane(poseStack, buffer, r, g, b, a, Direction.UP, pX, pY, pZ, sX, sY, sZ);
        drawPlane(poseStack, buffer, r, g, b, a, Direction.DOWN, pX, pY, pZ, sX, sY, sZ);
        drawPlane(poseStack, buffer, r, g, b, a, Direction.EAST, pX, pY, pZ, sX, sY, sZ);
        drawPlane(poseStack, buffer, r, g, b, a, Direction.WEST, pX, pY, pZ, sX, sY, sZ);
        drawPlane(poseStack, buffer, r, g, b, a, Direction.NORTH, pX, pY, pZ, sX, sY, sZ);
        drawPlane(poseStack, buffer, r, g, b, a, Direction.SOUTH, pX, pY, pZ, sX, sY, sZ);
        poseStack.popPose();
    }

    public static void drawPlane(PoseStack posestack, VertexConsumer buffer, float r, float g, float b, float a, Direction perspective, float pX, float pY, float pZ, float sX, float sY, float sZ) {
        posestack.pushPose();

        pX = pX + 0.5f;
        pY = pY + 0.5f;
        pZ = pZ + 0.5f;

        posestack.translate(pX, pY, pZ);

        Matrix4f matrix4f = posestack.last().pose();

        sX = sX / 2;
        sY = sY / 2;
        sZ = sZ / 2;


        if (perspective == Direction.UP) {
            buffer.vertex(matrix4f, -sX, sY, sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, sY, sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, sY, -sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX, sY, -sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        if (perspective == Direction.DOWN) {
            buffer.vertex(matrix4f, -sX, -sY, sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX, -sY, -sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, -sY, -sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, -sY, sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
        }
        if (perspective == Direction.SOUTH) {
            buffer.vertex(matrix4f, sX, -sY, sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, -sY, -sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, sY, -sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, sY, sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
        }
        if (perspective == Direction.NORTH) {
            buffer.vertex(matrix4f, -sX, -sY, sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX, sY, sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX, sY, -sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX, -sY, -sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
        }
        if (perspective == Direction.EAST) {
            buffer.vertex(matrix4f, -sX, -sY, -sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX, sY, -sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, sY, -sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, -sY, -sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        if (perspective == Direction.WEST) {
            buffer.vertex(matrix4f, -sX, -sY, sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, -sY, sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, sY, sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX, sY, sZ).color(r, g, b, a).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        posestack.popPose();
    }

    public static void drawTorus(PoseStack poseStack, VertexConsumer consumer, float majorRadius, float minorRadius, double pX, double pY, double pZ, float xRot, float yRot, float zRot, float r, float g, float b, float a, int sides, int rings) {
        poseStack.pushPose();
        poseStack.translate(pX, pY, pZ);
        poseStack.mulPose(new Quaternion(xRot, yRot, zRot, true));

        Matrix4f matrix4f = poseStack.last().pose();
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation("block/crimson_stem"));

        float x, y, z;
        float nx, ny, nz;

        float ringStep = (float) (2 * Math.PI / rings);
        float sideStep = (float) (2 * Math.PI / sides);
        float ringAngle, sideAngle;

        for (int i = 0; i < rings; ++i) {
            ringAngle = i * ringStep;
            float cosRingAngle = (float) Math.cos(ringAngle);
            float sinRingAngle = (float) Math.sin(ringAngle);

            float nextRingAngle = (i + 1) * ringStep;
            float nextCosRingAngle = (float) Math.cos(nextRingAngle);
            float nextSinRingAngle = (float) Math.sin(nextRingAngle);

            // Calculate the center point of the minor circles
            float centerX = majorRadius * cosRingAngle;
            float centerY = majorRadius * sinRingAngle;
            float nextCenterX = majorRadius * nextCosRingAngle;
            float nextCenterY = majorRadius * nextSinRingAngle;

            for (int j = 0; j < sides; ++j) {
                sideAngle = j * sideStep;
                float cosSideAngle = (float) Math.cos(sideAngle);
                float sinSideAngle = (float) Math.sin(sideAngle);

                float nextSideAngle = (j + 1) * sideStep;
                float nextCosSideAngle = (float) Math.cos(nextSideAngle);
                float nextSinSideAngle = (float) Math.sin(nextSideAngle);

                float s = j / sides;
                float t = i / rings;

                float u1 = getU(s * sides, texture.getU1(), texture.getU0(), sides);
                float u2 = getU((s + 1.0f / sides) * sides, texture.getU1(), texture.getU0(), sides);
                float v1 = getV(t * rings, texture.getV1(), texture.getV0(), rings);
                float v2 = getV((t + 1.0f / rings) * rings, texture.getV1(), texture.getV0(), rings);

                x = centerX + minorRadius * nextCosSideAngle * cosRingAngle;
                y = centerY + minorRadius * nextCosSideAngle * sinRingAngle;
                z = minorRadius * nextSinSideAngle;

                nx = x - centerX;
                ny = y - centerY;
                nz = z;
                consumer.vertex(matrix4f, x, y, z).color(r, g, b, a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(nx, ny, nz).endVertex();

                // Calculate vertex positions
                x = centerX + minorRadius * cosSideAngle * cosRingAngle;
                y = centerY + minorRadius * cosSideAngle * sinRingAngle;
                z = minorRadius * sinSideAngle;

                // Calculate normal
                nx = x - centerX;
                ny = y - centerY;
                nz = z;

                consumer.vertex(matrix4f, x, y, z).color(r, g, b, a).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(nx, ny, nz).endVertex();

                x = nextCenterX + minorRadius * cosSideAngle * nextCosRingAngle;
                y = nextCenterY + minorRadius * cosSideAngle * nextSinRingAngle;
                z = minorRadius * sinSideAngle;

                nx = x - nextCenterX;
                ny = y - nextCenterY;
                nz = z;
                consumer.vertex(matrix4f, x, y, z).color(r, g, b, a).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(nx, ny, nz).endVertex();


                x = nextCenterX + minorRadius * nextCosSideAngle * nextCosRingAngle;
                y = nextCenterY + minorRadius * nextCosSideAngle * nextSinRingAngle;
                z = minorRadius * nextSinSideAngle;

                nx = x - nextCenterX;
                ny = y - nextCenterY;
                nz = z;
                consumer.vertex(matrix4f, x, y, z).color(r, g, b, a).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(nx, ny, nz).endVertex();
            }
        }

        poseStack.popPose();
    }

    public static void drawSphere(PoseStack poseStack, VertexConsumer consumer, float radius, double pX, double pY, double pZ, float xRot, float yRot, float zRot, float r, float g, float b, float a, int sectors, int stacks) {
        poseStack.pushPose();
        poseStack.translate(pX, pY, pZ);
        poseStack.mulPose(new Quaternion(xRot, yRot, zRot, true));

        Matrix4f matrix4f = poseStack.last().pose();
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(new ResourceLocation("block/dirt"));

        float z, xy;
        float nx1, ny1, nz1, nx2, ny2, nz2, nx3, ny3, nz3, nx4, ny4, nz4, lengthInv = (1.0f / radius); // vertex normal
        float s, t;

        float sectorStep = (float) (2 * Math.PI / sectors);
        float stackStep = (float) (Math.PI / stacks);
        float sectorAngle, stackAngle;

        for (int i = 1; i <= stacks; ++i) {
            stackAngle = (float) (Math.PI / 2 - i * stackStep);

            xy = (float) (radius * Math.cos(stackAngle));
            z = (float) (radius * Math.sin(stackAngle));

            for (int j = 0; j < sectors; ++j) {

                sectorAngle = j * sectorStep;

                float x1 = (float) (xy * Math.cos(sectorAngle));
                float y1 = (float) (xy * Math.sin(sectorAngle));

                float x2 = (float) (xy * Math.cos(sectorAngle + sectorStep));
                float y2 = (float) (xy * Math.sin(sectorAngle + sectorStep));

                float x3 = (float) (radius * Math.cos(stackAngle + stackStep) * Math.cos(sectorAngle + sectorStep));
                float y3 = (float) (radius * Math.cos(stackAngle + stackStep) * Math.sin(sectorAngle + sectorStep));
                float z3 = (float) (radius * Math.sin(stackAngle + stackStep));

                float x4 = (float) (radius * Math.cos(stackAngle + stackStep) * Math.cos(sectorAngle));
                float y4 = (float) (radius * Math.cos(stackAngle + stackStep) * Math.sin(sectorAngle));
                float z4 = (float) (radius * Math.sin(stackAngle + stackStep));

                nx1 = x1 * lengthInv;
                ny1 = y1 * lengthInv;
                nz1 = z * lengthInv;

                nx2 = x2 * lengthInv;
                ny2 = y2 * lengthInv;
                nz2 = z * lengthInv;

                nx3 = x3 * lengthInv;
                ny3 = y3 * lengthInv;
                nz3 = z3 * lengthInv;

                nx4 = x4 * lengthInv;
                ny4 = y4 * lengthInv;
                nz4 = z4 * lengthInv;

                s = j / sectors;
                t = i / stacks;

                float u1 = getU(s * sectors, texture.getU1(), texture.getU0(), sectors);
                float u2 = getU((s + 1.0d / sectors) * sectors, texture.getU1(), texture.getU0(), sectors);
                float v1 = getV(t * stacks, texture.getV1(), texture.getV0(), stacks);
                float v2 = getV((t + 1.0d / stacks) * stacks, texture.getV1(), texture.getV0(), stacks);

                // For a reason which I am too dumb to understand, the uv coords have a one pixel offset
                // So... I just reverse it and it works
                v1 -= (texture.getV1() - texture.getV0()) / stacks;
                v2 -= (texture.getV1() - texture.getV0()) / stacks;

                consumer.vertex(matrix4f, x1, y1, z).color(r, g, b, a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(nx1, ny1, nz1).endVertex();
                consumer.vertex(matrix4f, x2, y2, z).color(r, g, b, a).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(nx2, ny2, nz2).endVertex();
                consumer.vertex(matrix4f, x3, y3, z3).color(r, g, b, a).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(nx3, ny3, nz3).endVertex();
                consumer.vertex(matrix4f, x4, y4, z4).color(r, g, b, a).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(nx4, ny4, nz4).endVertex();

            }
        }
        poseStack.popPose();

    }

    private static float getU(double pU, float u1, float u0, float resolution) {
        float f = u1 - u0;
        return u0 + f * (float) pU / resolution;
    }

    private static float getV(double pV, float v1, float v0, float resolution) {
        float f = v1 - v0;
        return v0 + f * (float) pV / resolution;
    }

    public static void drawBox(PoseStack poseStack, VertexConsumer buffer, ResourceLocation texture, float pX, float pY, float pZ, float xRot, float yRot, float zRot, float sX, float sY, float sZ, float pUOffset, float pVOffset, float pWidth, float pHeight) {
        poseStack.pushPose();
        sX = sX / 16; //Sizes in pixels please
        sY = sY / 16;
        sZ = sZ / 16;
        pX = pX / 16;
        pY = pY / 16;
        pZ = pZ / 16;

        poseStack.mulPose(new Quaternion(xRot, yRot, zRot, true));

        drawPlane(poseStack, buffer, texture, Direction.UP, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        drawPlane(poseStack, buffer, texture, Direction.DOWN, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        drawPlane(poseStack, buffer, texture, Direction.EAST, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        drawPlane(poseStack, buffer, texture, Direction.WEST, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        drawPlane(poseStack, buffer, texture, Direction.NORTH, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        drawPlane(poseStack, buffer, texture, Direction.SOUTH, pX, pY, pZ, sX, sY, sZ, pUOffset, pVOffset, pWidth, pHeight);
        poseStack.popPose();
    }

    public static void drawPlane(PoseStack poseStack, VertexConsumer buffer, ResourceLocation texture, Direction perspective, float pX, float pY, float pZ, float sX, float sY, float sZ, float pUOffset, float pVOffset, float pWidth, float pHeight) {
        poseStack.pushPose();

        pX = pX + 0.5f;
        pY = pY + 0.5f;
        pZ = pZ + 0.5f;

        poseStack.translate(pX, pY, pZ);

        Matrix4f matrix4f = poseStack.last().pose();

        TextureAtlasSprite stillTexture = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);

        sX = sX / 2;
        sY = sY / 2;
        sZ = sZ / 2;

        float u1 = stillTexture.getU(pUOffset);
        float u2 = stillTexture.getU(pWidth);
        float v1 = stillTexture.getV(pVOffset);
        float v2 = stillTexture.getV(pHeight);

        if (perspective == Direction.UP) {
            buffer.vertex(matrix4f, -sX, sY, sZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, sY, sZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, sY, -sZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX, sY, -sZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        if (perspective == Direction.DOWN) {
            buffer.vertex(matrix4f, -sX, -sY, sZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX, -sY, -sZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, -sY, -sZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, -sY, sZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, -1f, 0f).endVertex();
        }
        if (perspective == Direction.SOUTH) {
            buffer.vertex(matrix4f, sX, -sY, sZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, -sY, -sZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, sY, -sZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, sY, sZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
        }
        if (perspective == Direction.NORTH) {
            buffer.vertex(matrix4f, -sX, -sY, sZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX, sY, sZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX, sY, -sZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX, -sY, -sZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(1f, 0f, 0f).endVertex();
        }
        if (perspective == Direction.EAST) {
            buffer.vertex(matrix4f, -sX, -sY, -sZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX, sY, -sZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, sY, -sZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, -sY, -sZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        if (perspective == Direction.WEST) {
            buffer.vertex(matrix4f, -sX, -sY, sZ).color(1, 1, 1, 1f).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, -sY, sZ).color(1, 1, 1, 1f).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, sX, sY, sZ).color(1, 1, 1, 1f).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
            buffer.vertex(matrix4f, -sX, sY, sZ).color(1, 1, 1, 1f).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(0f, 1f, 0f).endVertex();
        }
        poseStack.popPose();
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

}
