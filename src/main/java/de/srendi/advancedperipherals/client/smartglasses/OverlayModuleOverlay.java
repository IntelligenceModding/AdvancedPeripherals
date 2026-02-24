package de.srendi.advancedperipherals.client.smartglasses;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.ITwoDObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OverlayModuleOverlay implements LayeredDraw.Layer {
    public static final ResourceLocation ID = AdvancedPeripherals.getRL("overlay_module_overlay");

    @Override
    public void render(GuiGraphics gui, DeltaTracker deltaTracker) {
        this.render(gui, gui.pose(), deltaTracker, gui.guiWidth(), gui.guiHeight());
    }

    public void render(GuiGraphics gui, PoseStack poseStack, DeltaTracker deltaTracker, int screenWidth, int screenHeight) {
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
                    ((ITwoDObjectRenderer) batch.get(0).getRenderObject()).renderBatch(batch, gui, poseStack, deltaTracker, screenWidth, screenHeight);
                }
            }
        }
        poseStack.popPose();

    }

}
