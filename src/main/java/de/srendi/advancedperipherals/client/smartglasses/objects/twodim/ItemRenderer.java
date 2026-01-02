package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.ItemObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.util.RegistryUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemRenderer implements ITwoDObjectRenderer {

    @Override
    public void renderBatch(List<RenderableObject> objects, GuiGraphics gui, PoseStack poseStack, DeltaTracker partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();

        for (RenderableObject obj : objects) {
            Item renderItem = RegistryUtil.getRegistryEntry(((ItemObject) obj).item, BuiltInRegistries.ITEM);
            if (renderItem == null) {
                continue;
            }
            minecraft.getItemRenderer().renderGuiItem(new ItemStack(renderItem), (int) obj.x, (int) obj.y);
        }
    }
}
