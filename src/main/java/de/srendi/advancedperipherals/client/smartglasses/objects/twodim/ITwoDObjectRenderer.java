package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public interface ITwoDObjectRenderer<O extends RenderableObject> extends IObjectRenderer {
    void renderBatch(List<O> object, GuiGraphics gui, PoseStack poseStack, DeltaTracker partialTick, int screenWidth, int screenHeight);
}
