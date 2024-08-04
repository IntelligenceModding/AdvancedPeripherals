package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public interface IObjectRenderer {

    void render(RenderableObject object, ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight);

}
