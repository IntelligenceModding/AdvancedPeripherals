package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.ItemObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RenderableObject;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class ItemRenderer implements ITwoDObjectRenderer {

    @Override
    public void renderBatch(List<RenderableObject> objects, ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();

        for (RenderableObject object : objects) {
            Item renderItem = ItemUtil.getRegistryEntry(((ItemObject) object).item, ForgeRegistries.ITEMS);
            if (renderItem == null)
                continue;
            minecraft.getItemRenderer().renderGuiItem(new ItemStack(renderItem), object.x, object.y);
        }

    }
}
