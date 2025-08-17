package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.TextObject;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.util.List;

public class TextRenderer implements ITwoDObjectRenderer {

    @Override
    public void renderBatch(List<RenderableObject> objects, ForgeGui gui, PoseStack ignored, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        for (RenderableObject obj : objects) {
            TextObject text = (TextObject) obj;
            float rotX = obj.rotX;
            float rotY = obj.rotY;
            float rotZ = obj.rotZ;

            float x = text.x;

            if (text.center) {
                x -= (minecraft.font.width(text.content) * text.fontSize) / 2f;
            }

            PoseStack poseStack = new PoseStack();

            poseStack.translate(x / text.fontSize, text.y / text.fontSize, obj.z);

            poseStack.pushPose();

            poseStack.mulPose(Vector3f.XP.rotationDegrees(rotX));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(rotY));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotZ));

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
