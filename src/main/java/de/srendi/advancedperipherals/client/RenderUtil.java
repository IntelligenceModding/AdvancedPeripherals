package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class RenderUtil {
    private RenderUtil() {}

    public static void drawBox(PoseStack poseStack, VertexConsumer buffer, BoxLightMap lightMap, Vector4f rgba, Vector3f offset, Quaternionf rot, Vector3f size) {
        poseStack.pushPose();

        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(rot);

        drawPlane(poseStack, buffer, lightMap, rgba, Direction.UP, offset, size);
        drawPlane(poseStack, buffer, lightMap, rgba, Direction.DOWN, offset, size);
        drawPlane(poseStack, buffer, lightMap, rgba, Direction.EAST, offset, size);
        drawPlane(poseStack, buffer, lightMap, rgba, Direction.WEST, offset, size);
        drawPlane(poseStack, buffer, lightMap, rgba, Direction.NORTH, offset, size);
        drawPlane(poseStack, buffer, lightMap, rgba, Direction.SOUTH, offset, size);
        poseStack.popPose();
    }

    public static void drawPlane(PoseStack posestack, VertexConsumer buffer, BoxLightMap lightMap, Vector4f rgba, Direction perspective, Vector3f offset, Vector3f size) {
        posestack.pushPose();

        posestack.translate(offset.x, offset.y, offset.z);

        Matrix4f matrix4f = posestack.last().pose();

        float sX = size.x, sY = size.y, sZ = size.z;
        sX /= 2;
        sY /= 2;
        sZ /= 2;

        final float r = rgba.x, g = rgba.y, b = rgba.z, a = rgba.w;

        switch (perspective) {
            case UP -> {
                buffer.addVertex(matrix4f, -sX, sY, sZ).setColor(r, g, b, a).setLight(lightMap.usw).setNormal(0f, 1f, 0f);
                buffer.addVertex(matrix4f, sX, sY, sZ).setColor(r, g, b, a).setLight(lightMap.use).setNormal(0f, 1f, 0f);
                buffer.addVertex(matrix4f, sX, sY, -sZ).setColor(r, g, b, a).setLight(lightMap.une).setNormal(0f, 1f, 0f);
                buffer.addVertex(matrix4f, -sX, sY, -sZ).setColor(r, g, b, a).setLight(lightMap.unw).setNormal(0f, 1f, 0f);
            }
            case DOWN -> {
                buffer.addVertex(matrix4f, -sX, -sY, sZ).setColor(r, g, b, a).setLight(lightMap.dsw).setNormal(0f, -1f, 0f);
                buffer.addVertex(matrix4f, -sX, -sY, -sZ).setColor(r, g, b, a).setLight(lightMap.dnw).setNormal(0f, -1f, 0f);
                buffer.addVertex(matrix4f, sX, -sY, -sZ).setColor(r, g, b, a).setLight(lightMap.dne).setNormal(0f, -1f, 0f);
                buffer.addVertex(matrix4f, sX, -sY, sZ).setColor(r, g, b, a).setLight(lightMap.dse).setNormal(0f, -1f, 0f);
            }
            case SOUTH -> {
                buffer.addVertex(matrix4f, -sX, -sY, sZ).setColor(r, g, b, a).setLight(lightMap.sde).setNormal(0f, 0f, 1f);
                buffer.addVertex(matrix4f, sX, -sY, sZ).setColor(r, g, b, a).setLight(lightMap.sdw).setNormal(0f, 0f, 1f);
                buffer.addVertex(matrix4f, sX, sY, sZ).setColor(r, g, b, a).setLight(lightMap.sue).setNormal(0f, 0f, 1f);
                buffer.addVertex(matrix4f, -sX, sY, sZ).setColor(r, g, b, a).setLight(lightMap.suw).setNormal(0f, 0f, 1f);
            }
            case NORTH -> {
                buffer.addVertex(matrix4f, -sX, -sY, -sZ).setColor(r, g, b, a).setLight(lightMap.ndw).setNormal(0f, 0f, -1f);
                buffer.addVertex(matrix4f, -sX, sY, -sZ).setColor(r, g, b, a).setLight(lightMap.nuw).setNormal(0f, 0f, -1f);
                buffer.addVertex(matrix4f, sX, sY, -sZ).setColor(r, g, b, a).setLight(lightMap.nue).setNormal(0f, 0f, -1f);
                buffer.addVertex(matrix4f, sX, -sY, -sZ).setColor(r, g, b, a).setLight(lightMap.nde).setNormal(0f, 0f, -1f);
            }
            case EAST -> {
                buffer.addVertex(matrix4f, sX, -sY, -sZ).setColor(r, g, b, a).setLight(lightMap.edn).setNormal(1f, 0f, 0f);
                buffer.addVertex(matrix4f, sX, sY, -sZ).setColor(r, g, b, a).setLight(lightMap.eun).setNormal(1f, 0f, 0f);
                buffer.addVertex(matrix4f, sX, sY, sZ).setColor(r, g, b, a).setLight(lightMap.eus).setNormal(1f, 0f, 0f);
                buffer.addVertex(matrix4f, sX, -sY, sZ).setColor(r, g, b, a).setLight(lightMap.eds).setNormal(1f, 0f, 0f);
            }
            case WEST -> {
                buffer.addVertex(matrix4f, -sX, -sY, -sZ).setColor(r, g, b, a).setLight(lightMap.wdn).setNormal(-1f, 0f, 0f);
                buffer.addVertex(matrix4f, -sX, -sY, sZ).setColor(r, g, b, a).setLight(lightMap.wds).setNormal(-1f, 0f, 0f);
                buffer.addVertex(matrix4f, -sX, sY, sZ).setColor(r, g, b, a).setLight(lightMap.wus).setNormal(-1f, 0f, 0f);
                buffer.addVertex(matrix4f, -sX, sY, -sZ).setColor(r, g, b, a).setLight(lightMap.wun).setNormal(-1f, 0f, 0f);
            }
        }
        posestack.popPose();
    }

    public static void drawBoxWithTexture(PoseStack poseStack, VertexConsumer buffer, BoxLightMap lightMap, ModelTextures model, Vector3f rgb, Vector3f offset, Quaternionf rot, Vector3f size, float scale) {
        drawBoxWithTexture(poseStack, buffer, lightMap, model, new Vector4f(rgb, 1f), offset, rot, size, scale);
    }

    public static void drawBoxWithTexture(PoseStack poseStack, VertexConsumer buffer, BoxLightMap lightMap, ModelTextures model, Vector4f rgba, Vector3f offset, Quaternionf rot, Vector3f size, float scale) {
        poseStack.pushPose();

        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(rot);

        for (final Direction dir : Direction.values()) {
            drawPlaneWithTexture(poseStack, buffer, lightMap, model.getTexture(dir), rgba, dir, offset, size, scale);
        }
        poseStack.popPose();
    }

    public static void drawPlaneWithTexture(PoseStack poseStack, VertexConsumer buffer, BoxLightMap lightMap, TextureLocation texture, Vector3f rgb, Direction perspective, Vector3f offset, Vector3f size, float scale) {
        drawPlaneWithTexture(poseStack, buffer, lightMap, texture, new Vector4f(rgb, 1f), perspective, offset, size, scale);
    }

    public static void drawPlaneWithTexture(PoseStack poseStack, VertexConsumer buffer, BoxLightMap lightMap, TextureLocation texture, Vector4f rgba, Direction perspective, Vector3f offset, Vector3f size, float scale) {
        poseStack.pushPose();

        poseStack.translate(offset.x, offset.y, offset.z);

        Matrix4f matrix4f = poseStack.last().pose();

        float sX = size.x, sY = size.y, sZ = size.z;
        sX *= scale / 16f / 2;
        sY *= scale / 16f / 2;
        sZ *= scale / 16f / 2;

        final float r = rgba.x, g = rgba.y, b = rgba.z, a = rgba.w;

        final TextureAtlasSprite stillTexture = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture.location());
        final float textureScale = texture.scale();
        final float pUOffset = texture.offsetX() * textureScale, pVOffset = texture.offsetY() * textureScale;
        final float u1 = stillTexture.getU(pUOffset);
        final float v1 = stillTexture.getV(pVOffset);

        switch (perspective) {
            case UP -> {
                final float u2 = stillTexture.getU(pUOffset + size.z * textureScale);
                final float v2 = stillTexture.getV(pVOffset + size.x * textureScale);
                buffer.addVertex(matrix4f, -sX, sY, -sZ).setColor(r, g, b, a).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.unw).setNormal(0f, 1f, 0f);
                buffer.addVertex(matrix4f, -sX, sY, sZ).setColor(r, g, b, a).setUv(u1, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.usw).setNormal(0f, 1f, 0f);
                buffer.addVertex(matrix4f, sX, sY, sZ).setColor(r, g, b, a).setUv(u2, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.use).setNormal(0f, 1f, 0f);
                buffer.addVertex(matrix4f, sX, sY, -sZ).setColor(r, g, b, a).setUv(u2, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.une).setNormal(0f, 1f, 0f);
            }
            case DOWN -> {
                final float u2 = stillTexture.getU(pUOffset + size.z * textureScale);
                final float v2 = stillTexture.getV(pVOffset + size.x * textureScale);
                buffer.addVertex(matrix4f, -sX, -sY, -sZ).setColor(r, g, b, a).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.dnw).setNormal(0f, -1f, 0f);
                buffer.addVertex(matrix4f, sX, -sY, -sZ).setColor(r, g, b, a).setUv(u2, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.dne).setNormal(0f, -1f, 0f);
                buffer.addVertex(matrix4f, sX, -sY, sZ).setColor(r, g, b, a).setUv(u2, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.dse).setNormal(0f, -1f, 0f);
                buffer.addVertex(matrix4f, -sX, -sY, sZ).setColor(r, g, b, a).setUv(u1, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.dsw).setNormal(0f, -1f, 0f);
            }
            case SOUTH -> {
                final float u2 = stillTexture.getU(pUOffset + size.x * textureScale);
                final float v2 = stillTexture.getV(pVOffset + size.y * textureScale);
                buffer.addVertex(matrix4f, -sX, -sY, sZ).setColor(r, g, b, a).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.sde).setNormal(0f, 0f, 1f);
                buffer.addVertex(matrix4f, sX, -sY, sZ).setColor(r, g, b, a).setUv(u2, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.sdw).setNormal(0f, 0f, 1f);
                buffer.addVertex(matrix4f, sX, sY, sZ).setColor(r, g, b, a).setUv(u2, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.sue).setNormal(0f, 0f, 1f);
                buffer.addVertex(matrix4f, -sX, sY, sZ).setColor(r, g, b, a).setUv(u1, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.suw).setNormal(0f, 0f, 1f);
            }
            case NORTH -> {
                final float u2 = stillTexture.getU(pUOffset + size.x * textureScale);
                final float v2 = stillTexture.getV(pVOffset + size.y * textureScale);
                buffer.addVertex(matrix4f, -sX, -sY, -sZ).setColor(r, g, b, a).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.ndw).setNormal(0f, 0f, -1f);
                buffer.addVertex(matrix4f, -sX, sY, -sZ).setColor(r, g, b, a).setUv(u1, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.nuw).setNormal(0f, 0f, -1f);
                buffer.addVertex(matrix4f, sX, sY, -sZ).setColor(r, g, b, a).setUv(u2, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.nue).setNormal(0f, 0f, -1f);
                buffer.addVertex(matrix4f, sX, -sY, -sZ).setColor(r, g, b, a).setUv(u2, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.nde).setNormal(0f, 0f, -1f);
            }
            case EAST -> {
                final float u2 = stillTexture.getU(pUOffset + size.y * textureScale);
                final float v2 = stillTexture.getV(pVOffset + size.z * textureScale);
                buffer.addVertex(matrix4f, sX, -sY, -sZ).setColor(r, g, b, a).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.edn).setNormal(1f, 0f, 0f);
                buffer.addVertex(matrix4f, sX, sY, -sZ).setColor(r, g, b, a).setUv(u2, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.eun).setNormal(1f, 0f, 0f);
                buffer.addVertex(matrix4f, sX, sY, sZ).setColor(r, g, b, a).setUv(u2, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.eus).setNormal(1f, 0f, 0f);
                buffer.addVertex(matrix4f, sX, -sY, sZ).setColor(r, g, b, a).setUv(u1, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.eds).setNormal(1f, 0f, 0f);
            }
            case WEST -> {
                final float u2 = stillTexture.getU(pUOffset + size.y * textureScale);
                final float v2 = stillTexture.getV(pVOffset + size.z * textureScale);
                buffer.addVertex(matrix4f, -sX, -sY, -sZ).setColor(r, g, b, a).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.wdn).setNormal(-1f, 0f, 0f);
                buffer.addVertex(matrix4f, -sX, -sY, sZ).setColor(r, g, b, a).setUv(u1, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.wds).setNormal(-1f, 0f, 0f);
                buffer.addVertex(matrix4f, -sX, sY, sZ).setColor(r, g, b, a).setUv(u2, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.wus).setNormal(-1f, 0f, 0f);
                buffer.addVertex(matrix4f, -sX, sY, -sZ).setColor(r, g, b, a).setUv(u2, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMap.wun).setNormal(-1f, 0f, 0f);
            }
        }
        poseStack.popPose();
    }

    public static void drawSphere(PoseStack poseStack, VertexConsumer consumer, float radius, double pX, double pY, double pZ, float xRot, float yRot, float zRot, float r, float g, float b, float a, int sectors, int stacks) {
        poseStack.pushPose();
        poseStack.translate(pX, pY, pZ);
        poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(xRot), (float) Math.toRadians(yRot), (float) Math.toRadians(zRot)));

        Matrix4f matrix4f = poseStack.last().pose();
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ResourceLocation.withDefaultNamespace("block/dirt"));

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

                consumer.addVertex(matrix4f, x1, y1, z).setColor(r, g, b, a).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(nx1, ny1, nz1);
                consumer.addVertex(matrix4f, x2, y2, z).setColor(r, g, b, a).setUv(u2, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(nx2, ny2, nz2);
                consumer.addVertex(matrix4f, x3, y3, z3).setColor(r, g, b, a).setUv(u2, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(nx3, ny3, nz3);
                consumer.addVertex(matrix4f, x4, y4, z4).setColor(r, g, b, a).setUv(u1, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(nx4, ny4, nz4);

            }
        }
        poseStack.popPose();

    }

    public static void drawTorus(PoseStack poseStack, VertexConsumer consumer, float majorRadius, float minorRadius, double pX, double pY, double pZ, float xRot, float yRot, float zRot, float r, float g, float b, float a, int sides, int rings) {
        poseStack.pushPose();
        poseStack.translate(pX, pY, pZ);
        poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(xRot), (float) Math.toRadians(yRot), (float) Math.toRadians(zRot)));

        Matrix4f matrix4f = poseStack.last().pose();
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.withDefaultNamespace("block/crimson_stem"));

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
                consumer.addVertex(matrix4f, x, y, z).setColor(r, g, b, a).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(nx, ny, nz);

                // Calculate vertex positions
                x = centerX + minorRadius * cosSideAngle * cosRingAngle;
                y = centerY + minorRadius * cosSideAngle * sinRingAngle;
                z = minorRadius * sinSideAngle;

                // Calculate normal
                nx = x - centerX;
                ny = y - centerY;
                nz = z;

                consumer.addVertex(matrix4f, x, y, z).setColor(r, g, b, a).setUv(u1, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(nx, ny, nz);

                x = nextCenterX + minorRadius * cosSideAngle * nextCosRingAngle;
                y = nextCenterY + minorRadius * cosSideAngle * nextSinRingAngle;
                z = minorRadius * sinSideAngle;

                nx = x - nextCenterX;
                ny = y - nextCenterY;
                nz = z;
                consumer.addVertex(matrix4f, x, y, z).setColor(r, g, b, a).setUv(u2, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(nx, ny, nz);


                x = nextCenterX + minorRadius * nextCosSideAngle * nextCosRingAngle;
                y = nextCenterY + minorRadius * nextCosSideAngle * nextSinRingAngle;
                z = minorRadius * nextSinSideAngle;

                nx = x - nextCenterX;
                ny = y - nextCenterY;
                nz = z;
                consumer.addVertex(matrix4f, x, y, z).setColor(r, g, b, a).setUv(u2, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(nx, ny, nz);
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

    public static float getBlue(int hex) {
        return (hex & 0xff) / 255.0f;
    }

    public static float getGreen(int hex) {
        return (hex >> 8 & 0xff) / 255.0f;
    }

    public static float getRed(int hex) {
        return (hex >> 16 & 0xff) / 255.0f;
    }

    public record TextureLocation(ResourceLocation location, int offsetX, int offsetY, float scale) {
        public TextureLocation(ResourceLocation location, int offsetX, int offsetY) {
            this(location, offsetX, offsetY, 1f);
        }

        public static TextureLocation fromNonStandardSize(ResourceLocation location, int offsetX, int offsetY, int size) {
            return new TextureLocation(location, offsetX, offsetY, 16f / size);
        }
    }

    public record ModelTextures(
        TextureLocation down,
        TextureLocation up,
        TextureLocation north,
        TextureLocation south,
        TextureLocation west,
        TextureLocation east
    ) {
        public TextureLocation getTexture(final Direction dir) {
            return switch (dir) {
                case DOWN -> this.down;
                case UP -> this.up;
                case NORTH -> this.north;
                case SOUTH -> this.south;
                case WEST -> this.west;
                case EAST -> this.east;
            };
        }
    }

    public static final class BoxLightMap {
        public int use, usw, une, unw, dse, dsw, dne, dnw;
        public int sue, suw, nue, nuw, sde, sdw, nde, ndw;
        public int eus, wus, eun, wun, eds, wds, edn, wdn;

        public static BoxLightMap createFullBright() {
            return new BoxLightMap().setAll(LightTexture.FULL_BRIGHT);
        }

        @SuppressWarnings("checkstyle:Indentation")
        public BoxLightMap setAll(final int packedLight) {
            this.use = this.usw = this.une = this.unw = this.dse = this.dsw = this.dne = this.dnw =
            this.sue = this.suw = this.nue = this.nuw = this.sde = this.sdw = this.nde = this.ndw =
            this.eus = this.wus = this.eun = this.wun = this.eds = this.wds = this.edn = this.wdn =
                packedLight;
            return this;
        }

        public BoxLightMap setCorners(final int use, final int usw, final int une, final int unw, final int dse, final int dsw, final int dne, final int dnw) {
            this.use = this.sue = this.eus = use;
            this.usw = this.suw = this.wus = usw;
            this.une = this.nue = this.eun = une;
            this.unw = this.nuw = this.wun = unw;
            this.dse = this.sde = this.eds = dse;
            this.dsw = this.sdw = this.wds = dsw;
            this.dne = this.nde = this.edn = dne;
            this.dnw = this.ndw = this.wdn = dnw;
            return this;
        }

        public BoxLightMap setUSE(final int value) {
            this.use = this.sue = this.eus = value;
            return this;
        }

        public BoxLightMap setUSW(final int value) {
            this.usw = this.suw = this.wus = value;
            return this;
        }

        public BoxLightMap setUNE(final int value) {
            this.une = this.nue = this.eun = value;
            return this;
        }

        public BoxLightMap setUNW(final int value) {
            this.unw = this.nuw = this.wun = value;
            return this;
        }

        public BoxLightMap setDSE(final int value) {
            this.dse = this.sde = this.eds = value;
            return this;
        }

        public BoxLightMap setDSW(final int value) {
            this.dsw = this.sdw = this.wds = value;
            return this;
        }

        public BoxLightMap setDNE(final int value) {
            this.dne = this.nde = this.edn = value;
            return this;
        }

        public BoxLightMap setDNW(final int value) {
            this.dnw = this.ndw = this.wdn = value;
            return this;
        }

        public BoxLightMap setFaces(final int up, final int down, final int south, final int north, final int east, final int west) {
            this.use = this.usw = this.une = this.unw = up;
            this.dse = this.dsw = this.dne = this.dnw = down;
            this.sue = this.suw = this.sde = this.sdw = south;
            this.nue = this.nuw = this.nde = this.ndw = south;
            this.eus = this.eun = this.eds = this.edn = east;
            this.wus = this.wun = this.wds = this.wdn = west;
            return this;
        }

        public BoxLightMap setFace(final Direction face, final int light) {
            switch (face) {
                case UP -> this.use = this.usw = this.une = this.unw = light;
                case DOWN -> this.dse = this.dsw = this.dne = this.dnw = light;
                case SOUTH -> this.sue = this.suw = this.sde = this.sdw = light;
                case NORTH -> this.nue = this.nuw = this.nde = this.ndw = light;
                case EAST -> this.eus = this.eun = this.eds = this.edn = light;
                case WEST -> this.wus = this.wun = this.wds = this.wdn = light;
            }
            return this;
        }

        public BoxLightMap packLightMaps(final BoxLightMap block, final BoxLightMap sky) {
            this.use = LightTexture.pack(block.use, sky.use);
            this.usw = LightTexture.pack(block.usw, sky.usw);
            this.une = LightTexture.pack(block.une, sky.une);
            this.unw = LightTexture.pack(block.unw, sky.unw);
            this.dse = LightTexture.pack(block.dse, sky.dse);
            this.dsw = LightTexture.pack(block.dsw, sky.dsw);
            this.dne = LightTexture.pack(block.dne, sky.dne);
            this.dnw = LightTexture.pack(block.dnw, sky.dnw);
            this.sue = LightTexture.pack(block.sue, sky.sue);
            this.suw = LightTexture.pack(block.suw, sky.suw);
            this.nue = LightTexture.pack(block.nue, sky.nue);
            this.nuw = LightTexture.pack(block.nuw, sky.nuw);
            this.sde = LightTexture.pack(block.sde, sky.sde);
            this.sdw = LightTexture.pack(block.sdw, sky.sdw);
            this.nde = LightTexture.pack(block.nde, sky.nde);
            this.ndw = LightTexture.pack(block.ndw, sky.ndw);
            this.eus = LightTexture.pack(block.eus, sky.eus);
            this.wus = LightTexture.pack(block.wus, sky.wus);
            this.eun = LightTexture.pack(block.eun, sky.eun);
            this.wun = LightTexture.pack(block.wun, sky.wun);
            this.eds = LightTexture.pack(block.eds, sky.eds);
            this.wds = LightTexture.pack(block.wds, sky.wds);
            this.edn = LightTexture.pack(block.edn, sky.edn);
            this.wdn = LightTexture.pack(block.wdn, sky.wdn);
            return this;
        }

        public BoxLightMap getBlockLightMap() {
            final BoxLightMap block = new BoxLightMap();
            block.use = LightTexture.block(this.use);
            block.usw = LightTexture.block(this.usw);
            block.une = LightTexture.block(this.une);
            block.unw = LightTexture.block(this.unw);
            block.dse = LightTexture.block(this.dse);
            block.dsw = LightTexture.block(this.dsw);
            block.dne = LightTexture.block(this.dne);
            block.dnw = LightTexture.block(this.dnw);
            block.sue = LightTexture.block(this.sue);
            block.suw = LightTexture.block(this.suw);
            block.nue = LightTexture.block(this.nue);
            block.nuw = LightTexture.block(this.nuw);
            block.sde = LightTexture.block(this.sde);
            block.sdw = LightTexture.block(this.sdw);
            block.nde = LightTexture.block(this.nde);
            block.ndw = LightTexture.block(this.ndw);
            block.eus = LightTexture.block(this.eus);
            block.wus = LightTexture.block(this.wus);
            block.eun = LightTexture.block(this.eun);
            block.wun = LightTexture.block(this.wun);
            block.eds = LightTexture.block(this.eds);
            block.wds = LightTexture.block(this.wds);
            block.edn = LightTexture.block(this.edn);
            block.wdn = LightTexture.block(this.wdn);
            return block;
        }

        public BoxLightMap getSkyLightMap() {
            final BoxLightMap sky = new BoxLightMap();
            sky.use = LightTexture.sky(this.use);
            sky.usw = LightTexture.sky(this.usw);
            sky.une = LightTexture.sky(this.une);
            sky.unw = LightTexture.sky(this.unw);
            sky.dse = LightTexture.sky(this.dse);
            sky.dsw = LightTexture.sky(this.dsw);
            sky.dne = LightTexture.sky(this.dne);
            sky.dnw = LightTexture.sky(this.dnw);
            sky.sue = LightTexture.sky(this.sue);
            sky.suw = LightTexture.sky(this.suw);
            sky.nue = LightTexture.sky(this.nue);
            sky.nuw = LightTexture.sky(this.nuw);
            sky.sde = LightTexture.sky(this.sde);
            sky.sdw = LightTexture.sky(this.sdw);
            sky.nde = LightTexture.sky(this.nde);
            sky.ndw = LightTexture.sky(this.ndw);
            sky.eus = LightTexture.sky(this.eus);
            sky.wus = LightTexture.sky(this.wus);
            sky.eun = LightTexture.sky(this.eun);
            sky.wun = LightTexture.sky(this.wun);
            sky.eds = LightTexture.sky(this.eds);
            sky.wds = LightTexture.sky(this.wds);
            sky.edn = LightTexture.sky(this.edn);
            sky.wdn = LightTexture.sky(this.wdn);
            return sky;
        }
    }
}
