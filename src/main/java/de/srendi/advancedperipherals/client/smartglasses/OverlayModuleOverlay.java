package de.srendi.advancedperipherals.client.smartglasses;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class OverlayModuleOverlay implements IGuiOverlay {
    public static final String ID = "overlay_module_overlay";

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        poseStack.pushPose();

        for (RenderableObject object : OverlayObjectHolder.getObjects()) {
            if (!object.isEnabled())
                continue;
            object.getRenderObject().render(object, gui, poseStack, partialTick, screenWidth, screenHeight);
        }
        poseStack.popPose();

    }

}
