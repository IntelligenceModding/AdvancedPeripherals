package de.srendi.advancedperipherals.client.widgets;

import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.screens.SmartGlassesScreen;
import de.srendi.advancedperipherals.common.smartglasses.SlotType;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class SmartGlassesSettingsSwitch extends AbstractWidget {

    private static final ResourceLocation BACKGROUND = AdvancedPeripherals.getRL("textures/gui/smart_glasses_gui.png");

    private final SmartGlassesScreen screen;
    private final SlotType type;
    private boolean isEnabled;

    public SmartGlassesSettingsSwitch(int x, int y, SlotType type, SmartGlassesScreen screen) {
        super(screen.getGuiLeft() + x + AbstractComputerMenu.SIDEBAR_WIDTH, screen.getGuiTop() + y, 21, 22, type.getName());
        this.screen = screen;
        this.type = type;
        this.isEnabled = type == SlotType.defaultType();
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (isEnabled) {
            graphics.blit(BACKGROUND, this.getX() - 3, this.getY(), 45, 217, 24, 22);
        } else {
            graphics.blit(BACKGROUND, this.getX(), this.getY(), 23, 217, 21, 22);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (this.isEnabled) {
            return;
        }
        for (Slot slot : screen.getMenu().slots) {
            if (slot instanceof SmartGlassesSlot smartGlassesSlot) {
                smartGlassesSlot.setActiveSlotType(this.type);
            }
        }
        screen.renderables.forEach(renderable -> {
            if (renderable instanceof SmartGlassesSettingsSwitch smartGlassesSettingsSwitch) {
                smartGlassesSettingsSwitch.isEnabled = false;
            }
        });
        screen.setCurrentType(this.type);
        this.isEnabled = true;
    }

    public void renderTooltip(GuiGraphics gui, int x, int y) {
        if (screen != null && isMouseOver(x, y)) {
            gui.renderTooltip(gui.minecraft.font, type.getName(), x, y);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
    }
}
