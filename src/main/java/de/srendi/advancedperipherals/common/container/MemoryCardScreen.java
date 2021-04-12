package de.srendi.advancedperipherals.common.container;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.container.base.BaseItemScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class MemoryCardScreen extends BaseItemScreen<MemoryCardContainer> {

    public MemoryCardScreen(MemoryCardContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
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
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "textures/gui/memory_card_gui.png");
    }
}
