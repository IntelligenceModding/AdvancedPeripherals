package de.srendi.advancedperipherals.client.smartglasses;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.ITwoDObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RectangleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.TextObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OverlayModuleOverlay implements IGuiOverlay {
    public static final String ID = "overlay_module_overlay";

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        poseStack.pushPose();

        Map<Integer, Map<Class<? extends RenderableObject>, List<RenderableObject>>> prioritizedBatches = new TreeMap<>();

        for (RenderableObject object : OverlayObjectHolder.getObjects()) {
            if (!object.isEnabled() || !(object.getRenderObject() instanceof ITwoDObjectRenderer)) {
                continue;
            }

            // We need to sort the objects by their weight, some things can't be rendered before something else.
            // For example, when texts are rendered before our circles, rectangles, etc., the other objects can't be transparent anymore
            int weight = object.getRenderObject().getWeight();
            Class<? extends RenderableObject> objectClass = object.getClass();

            // Get or create the batch map for the current weight
            Map<Class<? extends RenderableObject>, List<RenderableObject>> batchesForWeight = prioritizedBatches.computeIfAbsent(weight, k -> new HashMap<>());

            List<RenderableObject> batch = batchesForWeight.computeIfAbsent(objectClass, k -> new ArrayList<>());

            batch.add(object);
        }

        for (Map<Class<? extends RenderableObject>, List<RenderableObject>> batchesForWeight : prioritizedBatches.values()) {
            for (List<RenderableObject> batch : batchesForWeight.values()) {

                if (!batch.isEmpty()) {
                    ((ITwoDObjectRenderer) batch.get(0).getRenderObject()).renderBatch(batch, gui, poseStack, partialTick, screenWidth, screenHeight);
                }
            }
        }
        poseStack.popPose();

    }

}
