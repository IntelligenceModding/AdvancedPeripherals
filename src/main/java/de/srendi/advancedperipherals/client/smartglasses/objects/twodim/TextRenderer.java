package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.TextObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class TextRenderer implements ITwoDObjectRenderer<TextObject> {

    @Override
    public void renderBatch(List<TextObject> objects, GuiGraphics gui, PoseStack ignored, DeltaTracker partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        for (TextObject text : objects) {
            float rotX = text.rotX;
            float rotY = text.rotY;
            float rotZ = text.rotZ;

            float x = text.x;

            if (text.center) {
                x -= minecraft.font.width(text.content) / 2;
            }

            gui.pose().pushPose();

            gui.pose().translate(x, text.y, text.z);
            gui.pose().scale(text.fontSize, text.fontSize, 1);
            gui.pose().mulPose(Axis.XP.rotationDegrees(rotX));
            gui.pose().mulPose(Axis.YP.rotationDegrees(rotY));
            gui.pose().mulPose(Axis.ZP.rotationDegrees(rotZ));

            int color = (text.color & 0xffffff) | ((int) (Math.min(Math.max(text.opacity, 0), 1) * 0xff) << 24);

            gui.drawString(minecraft.font, text.content, 0, 0, color, text.shadow);

            gui.pose().popPose();
        }
    }

    @Override
    public int getWeight() {
        return 110;
    }
}
