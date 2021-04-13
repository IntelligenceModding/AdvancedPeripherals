package de.srendi.advancedperipherals.common.container;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.container.base.BaseItemScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class MemoryCardScreen extends BaseItemScreen<MemoryCardContainer> {

    public boolean boxOne;
    public boolean boxTwo;
    public MemoryCardScreen(MemoryCardContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        if(!item.getOrCreateTag().isEmpty()) {
            boxOne = item.getOrCreateTag().getBoolean("canExtract");
            boxTwo = item.getOrCreateTag().getBoolean("canInject");
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isPointInRegion(58, 33, 4, 4, mouseX, mouseY)) {
            if(boxOne)
                item.getTag().putBoolean("canExtract", false);
            if(!boxOne)
                item.getTag().putBoolean("canExtract", true);
        }
        if(isPointInRegion(58, 51, 4, 4, mouseX, mouseY)) {
            if(boxTwo)
                item.getTag().putBoolean("canInject", false);
            if(!boxTwo)
                item.getTag().putBoolean("canInject", true);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void blit(MatrixStack matrixStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        super.blit(matrixStack, x, y, uOffset, vOffset, uWidth, vHeight);
        int xPos = (width - xSize) / 2;
        int yPos = (height - ySize) / 2;
        if(boxOne)
            blit(matrixStack,  xPos + 58, yPos + 33, 175, 0, 4, 4);
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
