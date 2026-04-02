package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.ItemObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemRenderer implements ITwoDObjectRenderer<ItemObject> {
    @Override
    public void renderBatch(List<ItemObject> objects, GuiGraphics gui, PoseStack poseStack, DeltaTracker partialTick, int screenWidth, int screenHeight) {
        for (ItemObject obj : objects) {
            if (obj.item == null) {
                continue;
            }
            Item renderItem = BuiltInRegistries.ITEM.get(obj.item);
            if (renderItem == null) {
                continue;
            }
            gui.renderItem(new ItemStack(renderItem), (int) obj.x, (int) obj.y);
        }
    }
}
