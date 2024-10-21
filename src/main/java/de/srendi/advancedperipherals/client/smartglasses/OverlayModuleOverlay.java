package de.srendi.advancedperipherals.client.smartglasses;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.ITwoDObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RenderableObject;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverlayModuleOverlay implements IGuiOverlay {
    public static final String ID = "overlay_module_overlay";

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        poseStack.pushPose();

        Map<Class<? extends RenderableObject>, List<RenderableObject>> batches = new HashMap<>();

        for (RenderableObject object : OverlayObjectHolder.getObjects()) {
            if (!object.isEnabled() || !(object.getRenderObject() instanceof ITwoDObjectRenderer))
                continue;

            Class<? extends RenderableObject> objectClass = object.getClass();

            if (batches.containsKey(objectClass)) {
                batches.get(objectClass).add(object);
                continue;
            }

            List<RenderableObject> newBatchArray = new ArrayList<>();
            newBatchArray.add(object);
            batches.put(objectClass, newBatchArray);
        }

        for (List<RenderableObject> batch : batches.values()) {
            ((ITwoDObjectRenderer) batch.get(0).getRenderObject()).renderBatch(batch, gui, poseStack, partialTick, screenWidth, screenHeight);
        }

        poseStack.popPose();

    }

}
