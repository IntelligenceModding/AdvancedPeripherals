package de.srendi.advancedperipherals.common.container;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.container.base.BaseScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class InventoryManagerScreen extends BaseScreen<InventoryManagerContainer> {

    public InventoryManagerScreen(InventoryManagerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
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
