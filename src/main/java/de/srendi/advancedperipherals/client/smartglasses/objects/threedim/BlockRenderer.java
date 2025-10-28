package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BlockObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import de.srendi.advancedperipherals.common.util.RegistryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class BlockRenderer implements IThreeDObjectRenderer {

    @Override
    public void renderBatch(List<ThreeDimensionalObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 view, BufferBuilder bufferBuilder) {
        poseStack.pushPose();

        for (ThreeDimensionalObject obj : batch) {
            poseStack.pushPose();
            onPreRender(obj);

            bufferBuilder.begin(RenderType.solid().mode(), DefaultVertexFormat.BLOCK);

            BlockObject block = (BlockObject) obj;

            poseStack.translate(-view.x + block.getX(), -view.y + block.getY(), -view.z + block.getZ());
            poseStack.mulPose(new Quaternion(block.rotX, block.rotY, block.rotZ, true));
            poseStack.translate(-0.5f, -0.5f, -0.5f);
            float alpha = block.opacity;
            float red = RenderUtil.getRed(block.color);
            float green = RenderUtil.getGreen(block.color);
            float blue = RenderUtil.getBlue(block.color);

            RenderSystem.setShader(GameRenderer::getBlockShader);
            RenderSystem.setShaderColor(red, green, blue, alpha);

            Block blockToRender = RegistryUtil.getRegistryEntry(block.block, ForgeRegistries.BLOCKS);
            BlockPos blockPos = new BlockPos(obj.getX(), obj.getY(), obj.getZ());

            if (blockToRender != null)
                Minecraft.getInstance().getBlockRenderer().renderBatched(blockToRender.defaultBlockState(), blockPos, event.getCamera().getEntity().level, poseStack, bufferBuilder, false, event.getCamera().getEntity().level.random);

            poseStack.popPose();
            BufferUploader.drawWithShader(bufferBuilder.end());
            onPostRender(obj);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }


        poseStack.popPose();

    }
}
