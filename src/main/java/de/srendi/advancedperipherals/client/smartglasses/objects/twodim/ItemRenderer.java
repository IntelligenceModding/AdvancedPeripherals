package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.ItemObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemRenderer implements ITwoDObjectRenderer<ItemObject> {
    @Override
    public void renderBatch(List<ItemObject> objects, GuiGraphics gui, DeltaTracker partialTick) {
        for (ItemObject obj : objects) {
            if (obj.item == null) {
                continue;
            }
            Item renderItem = BuiltInRegistries.ITEM.get(obj.item);
            if (renderItem == null) {
                continue;
            }
            int x = (int) obj.x, y = (int) obj.y;
            gui.pose().pushPose();
            gui.pose().rotateAround(obj.getRotation(), x, y, 0);
            gui.renderFakeItem(new ItemStack(renderItem), x, y);
            gui.pose().popPose();
        }
    }
}
