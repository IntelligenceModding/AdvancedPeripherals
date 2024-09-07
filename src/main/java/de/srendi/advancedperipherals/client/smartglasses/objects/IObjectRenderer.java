package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.util.List;

public interface IObjectRenderer {

    void renderBatch(List<RenderableObject> object, ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight);

}
