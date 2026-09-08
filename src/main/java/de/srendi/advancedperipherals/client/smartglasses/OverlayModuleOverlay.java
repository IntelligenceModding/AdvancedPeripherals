package de.srendi.advancedperipherals.client.smartglasses;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import net.minecraft.client.Minecraft;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class OverlayModuleOverlay implements IGuiOverlay {
    public static final ResourceLocation ID = AdvancedPeripherals.getRL("overlay_module_overlay");

    @SuppressWarnings("rawtypes")
    @Override
    public void render(ForgeGui fgui, GuiGraphics gui, float partialTick, int screenWidth, int screenHeight) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (SmartGlassesItem.getEquipped(player).isEmpty()) {
            return;
        }

        NavigableMap<Integer, Map<IObjectRenderer, List<RenderableObject>>> prioritizedBatches = new TreeMap<>();

        for (OverlayObject object : OverlayObjectHolder.getObjects()) {
            if (!object.isEnabled()) {
                continue;
            }
            if (!(object instanceof RenderableObject renderableObject) || !renderableObject.gui) {
                continue;
            }

            IObjectRenderer renderer = renderableObject.getType().getRenderer();
            // We need to sort the objects by their weight, some things can't be rendered before something else.
            // For example, when texts are rendered before our circles, rectangles, etc., the other objects can't be transparent anymore
            int weight = renderer.getWeight();

            // Get or create the batch map for the current weight
            Map<IObjectRenderer, List<RenderableObject>> batches = prioritizedBatches.computeIfAbsent(weight, k -> new HashMap<>());
            batches.computeIfAbsent(renderer, k -> new ArrayList<>()).add(renderableObject);
        }

        for (Map<IObjectRenderer, List<RenderableObject>> batches : prioritizedBatches.values()) {
            for (Map.Entry<IObjectRenderer, List<RenderableObject>> entry : batches.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    continue;
                }
                entry.getKey().render2D(entry.getValue(), gui, partialTick);
            }
        }
    }
}
