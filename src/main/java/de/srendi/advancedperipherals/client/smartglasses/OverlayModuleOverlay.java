package de.srendi.advancedperipherals.client.smartglasses;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.ITwoDObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class OverlayModuleOverlay implements LayeredDraw.Layer {
    public static final ResourceLocation ID = AdvancedPeripherals.getRL("overlay_module_overlay");

    @SuppressWarnings("rawtypes")
    @Override
    public void render(GuiGraphics gui, DeltaTracker deltaTracker) {
        NavigableMap<Integer, Map<ITwoDObjectRenderer, List<OverlayObject>>> prioritizedBatches = new TreeMap<>();

        for (OverlayObject object : OverlayObjectHolder.getObjects()) {
            if (!object.isEnabled()) {
                continue;
            }
            if (!(object.getType().getRenderer() instanceof ITwoDObjectRenderer renderer)) {
                continue;
            }

            // We need to sort the objects by their weight, some things can't be rendered before something else.
            // For example, when texts are rendered before our circles, rectangles, etc., the other objects can't be transparent anymore
            int weight = renderer.getWeight();

            // Get or create the batch map for the current weight
            Map<ITwoDObjectRenderer, List<OverlayObject>> batches = prioritizedBatches.computeIfAbsent(weight, k -> new HashMap<>());
            batches.computeIfAbsent(renderer, k -> new ArrayList<>()).add(object);
        }

        for (Map<ITwoDObjectRenderer, List<OverlayObject>> batches : prioritizedBatches.values()) {
            for (Map.Entry<ITwoDObjectRenderer, List<OverlayObject>> entry : batches.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    continue;
                }
                entry.getKey().renderBatch(entry.getValue(), gui, deltaTracker);
            }
        }
    }
}
