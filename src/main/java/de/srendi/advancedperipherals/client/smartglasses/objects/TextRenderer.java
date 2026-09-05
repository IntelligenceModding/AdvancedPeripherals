package de.srendi.advancedperipherals.client.smartglasses.objects;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.TextObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;

public class TextRenderer extends Simple2DObjectRenderer<TextObject> {
    @Override
    protected void render(TextObject text, GuiGraphics gui, DeltaTracker partialTick) {
        if (text.fontSize == 0) {
            return;
        }
        Font font = gui.minecraft.font;

        float x = text.x;
        float width = font.width(text.content);
        if (text.center) {
            x -= width / 2;
        }

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
            text.depthTest ? Font.DisplayMode.NORMAL : Font.DisplayMode.SEE_THROUGH,
            0,
            LightTexture.FULL_BRIGHT
        );
        gui.flush();
    }

    @Override
    public int getWeight() {
        return 110;
    }
}
