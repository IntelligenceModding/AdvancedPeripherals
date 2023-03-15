package de.srendi.advancedperipherals.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.screens.SmartGlassesScreen;
import de.srendi.advancedperipherals.common.smartglasses.SlotType;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class SmartGlassesSettingsSwitch extends AbstractWidget {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(AdvancedPeripherals.MOD_ID, "textures/gui/smart_glasses_gui.png");

    private final SmartGlassesScreen screen;
    private final SlotType type;
    private final int leftPos;
    private final int topPos;

    public SmartGlassesSettingsSwitch(int x, int y, SlotType type, SmartGlassesScreen screen) {
        super(screen.getGuiLeft() + x, screen.getGuiTop() + y, 21, 22, type.getName());
        this.screen = screen;
        this.type = type;
        this.leftPos = screen.getGuiLeft();
        this.topPos = screen.getGuiTop();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBg(pPoseStack, Minecraft.getInstance(), pMouseX, pMouseY);
    }

    @Override
    public void renderButton(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
    }

    @Override
    protected void renderBg(@NotNull PoseStack pPoseStack, @NotNull Minecraft pMinecraft, int pMouseX, int pMouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        blit(pPoseStack, leftPos + this.getX(), topPos + this.getY(), 45, 217, 21, 22);
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        for(Slot slot : screen.getMenu().slots) {
            if(slot instanceof SmartGlassesSlot smartGlassesSlot) {
                if(smartGlassesSlot.slotType == this.type) {
                    smartGlassesSlot.setEnabled(true);
                }
                smartGlassesSlot.setEnabled(false);
            }
        }
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return this.active && this.visible && pMouseX >= (double)this.getX() && pMouseY >= (double)this.getY() && pMouseX < (double)(this.getX() + this.width) && pMouseY < (double)(this.getY() + this.height);
    }
}
