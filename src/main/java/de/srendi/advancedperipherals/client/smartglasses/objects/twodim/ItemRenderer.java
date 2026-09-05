package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.ItemObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
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
            Item renderItem = obj.item.value();
            gui.pose().pushPose();
            gui.pose().translate(obj.x, obj.y, obj.z);
            gui.pose().scale(1, 1, 1f / 256);
            gui.pose().mulPose(obj.getRotation());
            gui.pose().translate(-8, -8, -150);
            gui.renderFakeItem(new ItemStack(renderItem), 0, 0);
            gui.pose().popPose();
        }
    }
}
