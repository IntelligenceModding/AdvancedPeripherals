package de.srendi.advancedperipherals.client.renderer;

import appeng.api.client.AEKeyRenderHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.common.addons.ae2.disk.AEDiskKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public class AEDiskKeyRenderer implements AEKeyRenderHandler<AEDiskKey> {

    @Override
    public void drawInGui(Minecraft minecraft, GuiGraphics guiGraphics, int x, int y, AEDiskKey stack) {
        //
    }

    @Override
    public void drawOnBlockFace(PoseStack poseStack, MultiBufferSource buffers, AEDiskKey what, float scale, int combinedLight, Level level) {
        //
    }

    @Override
    public Component getDisplayName(AEDiskKey stack) {
        return stack.getDisplayName();
    }
}
