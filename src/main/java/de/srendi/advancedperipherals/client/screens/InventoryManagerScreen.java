package de.srendi.advancedperipherals.client.screens;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.screens.base.BaseScreen;
import de.srendi.advancedperipherals.common.container.InventoryManagerContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InventoryManagerScreen extends BaseScreen<InventoryManagerContainer> {

    public InventoryManagerScreen(InventoryManagerContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public int getSizeX() {
        return 174;
    }

    @Override
    public int getSizeY() {
        return 165;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "textures/gui/inventory_manager_gui.png");
    }
}
