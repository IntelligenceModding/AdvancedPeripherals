package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.Text;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.util.List;

public class TextRenderer implements IObjectRenderer {

    @Override
    public void renderBatch(List<RenderableObject> objects, ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {

        Minecraft minecraft = Minecraft.getInstance();
        for (RenderableObject object : objects) {
            Text text = (Text) object;
            poseStack.scale(text.fontSize, text.fontSize, 1);
            if (text.shadow) {
                minecraft.font.drawShadow(poseStack, text.content, text.x / text.fontSize, text.y / text.fontSize, text.color);
            } else {
                minecraft.font.draw(poseStack, text.content, text.x / text.fontSize, text.y / text.fontSize, text.color);
            }
        }

    }
}
