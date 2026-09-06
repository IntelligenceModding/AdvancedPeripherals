package de.srendi.advancedperipherals.client.smartglasses.objects;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.ItemObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemRenderer extends Simple2DObjectRenderer<ItemObject> {
    @Override
    protected void render(ItemObject object, GuiGraphics gui, DeltaTracker partialTick, boolean is3D) {
        if (object.item == null) {
            return;
        }
        Item renderItem = object.item.value();
        gui.pose().scale(1, 1, 1f / 256);
        gui.pose().translate(-8, -8, -150);
        gui.renderFakeItem(new ItemStack(renderItem), 0, 0);
    }
}
