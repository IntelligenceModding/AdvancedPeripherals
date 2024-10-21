package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RenderableObject;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.util.List;

public interface ITwoDObjectRenderer extends IObjectRenderer {

    void renderBatch(List<RenderableObject> object, ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight);

}
