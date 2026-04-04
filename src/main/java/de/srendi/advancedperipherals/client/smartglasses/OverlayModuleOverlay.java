package de.srendi.advancedperipherals.client.smartglasses;

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
import java.util.NavigableMap;
import java.util.TreeMap;

public class OverlayModuleOverlay implements LayeredDraw.Layer {
    public static final ResourceLocation ID = AdvancedPeripherals.getRL("overlay_module_overlay");

    @Override
    public void render(GuiGraphics gui, DeltaTracker deltaTracker) {
        NavigableMap<Integer, Map<Class<?>, List<RenderableObject>>> prioritizedBatches = new TreeMap<>();

        for (RenderableObject object : OverlayObjectHolder.getObjects()) {
            if (!object.isEnabled() || !(object.getObjectRenderer() instanceof ITwoDObjectRenderer)) {
                continue;
            }

            // We need to sort the objects by their weight, some things can't be rendered before something else.
            // For example, when texts are rendered before our circles, rectangles, etc., the other objects can't be transparent anymore
            int weight = object.getObjectRenderer().getWeight();
            Class<?> objectClass = object.getClass();

            // Get or create the batch map for the current weight
            Map<Class<?>, List<RenderableObject>> batchesForWeight = prioritizedBatches.computeIfAbsent(weight, k -> new HashMap<>());
            List<RenderableObject> batch = batchesForWeight.computeIfAbsent(objectClass, k -> new ArrayList<>());
            batch.add(object);
        }

        for (Map<Class<?>, List<RenderableObject>> batchesForWeight : prioritizedBatches.values()) {
            for (List<RenderableObject> batch : batchesForWeight.values()) {
                if (batch.isEmpty()) {
                    continue;
                }
                ((ITwoDObjectRenderer) batch.get(0).getObjectRenderer()).renderBatch(batch, gui, deltaTracker);
            }
        }
    }

}
