package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.TextObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class TextRenderer implements ITwoDObjectRenderer {

    @Override
    public void renderBatch(List<RenderableObject> objects, GuiGraphics gui, PoseStack ignored, DeltaTracker partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        for (RenderableObject obj : objects) {
            TextObject text = (TextObject) obj;
            float rotX = text.rotX;
            float rotY = text.rotY;
            float rotZ = text.rotZ;

            float x = text.x;

            if (text.center) {
                x -= (minecraft.font.width(text.content) * text.fontSize) / 2f;
            }

            PoseStack poseStack = new PoseStack();

            poseStack.translate(x / text.fontSize, text.y / text.fontSize, text.z);

            poseStack.pushPose();

            poseStack.mulPose(Axis.XP.rotationDegrees(rotX));
            poseStack.mulPose(Axis.YP.rotationDegrees(rotY));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotZ));

            poseStack.scale(text.fontSize, text.fontSize, 1);

            if (!text.shadow) {
                minecraft.font.drawShadow(poseStack, text.content, 0, 0, text.color);
            } else {
                minecraft.font.draw(poseStack, text.content, 0, 0, text.color);
            }
            poseStack.popPose();
        }
    }

    @Override
    public int getWeight() {
        return 110;
    }
}
