package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.util.List;

public class TextRenderer implements IObjectRenderer {

    @Override
    public void renderBatch(List<RenderableObject> objects, ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        /*float alpha = object.opacity;
        float red = (float) (object.color >> 16 & 255) / 255.0F;
        float green = (float) (object.color >> 8 & 255) / 255.0F;
        float blue = (float) (object.color & 255) / 255.0F;
*/

    }
}
