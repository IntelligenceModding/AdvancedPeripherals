package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.TextObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;

import java.util.List;

public class TextRenderer implements ITwoDObjectRenderer<TextObject> {
    @Override
    public void renderBatch(List<TextObject> objects, GuiGraphics gui, DeltaTracker partialTick) {
        Font font = gui.minecraft.font;
        for (TextObject text : objects) {
            if (text.fontSize == 0) {
                continue;
            }

            float x = text.x;
            float width = font.width(text.content);
            if (text.center) {
                x -= width / 2;
            }

            gui.pose().pushPose();

            gui.pose().translate(x, text.y, text.z);
            gui.pose().scale(text.fontSize, text.fontSize, 1);
            gui.pose().rotateAround(text.getRotation(), width / 2, 9f / 2, 0);

            int color = (text.color & 0xffffff) | ((int) (Math.min(Math.max(text.opacity, 0), 1) * 0xff) << 24);

            font.drawInBatch(
                text.content,
                0,
                0,
                color,
                text.shadow,
                gui.pose().last().pose(),
                gui.bufferSource(),
                Font.DisplayMode.NORMAL,
                0,
                LightTexture.FULL_BRIGHT
            );
            gui.flush();

            gui.pose().popPose();
        }
    }
}
