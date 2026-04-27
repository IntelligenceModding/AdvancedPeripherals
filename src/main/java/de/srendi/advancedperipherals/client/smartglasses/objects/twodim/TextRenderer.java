package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.TextObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class TextRenderer implements ITwoDObjectRenderer<TextObject> {

    @Override
    public void renderBatch(List<TextObject> objects, GuiGraphics gui, DeltaTracker partialTick) {
        for (TextObject text : objects) {
            if (text.fontSize == 0) {
                continue;
            }

            float x = text.x;
            float width = gui.minecraft.font.width(text.content);
            if (text.center) {
                x -= width / 2;
            }

            gui.pose().pushPose();

            gui.pose().translate(x, text.y, text.z);
            gui.pose().scale(text.fontSize, text.fontSize, 1);
            gui.pose().rotateAround(text.getRotation(), width / 2, 9f / 2, 0);

            int color = (text.color & 0xffffff) | ((int) (Math.min(Math.max(text.opacity, 0), 1) * 0xff) << 24);

            gui.drawString(gui.minecraft.font, text.content, 0, 0, color, text.shadow);

            gui.pose().popPose();
        }
    }

    @Override
    public int getWeight() {
        return 110;
    }
}
