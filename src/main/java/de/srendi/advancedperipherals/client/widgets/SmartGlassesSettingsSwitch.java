package de.srendi.advancedperipherals.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.screens.SmartGlassesScreen;
import de.srendi.advancedperipherals.common.smartglasses.SlotType;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class SmartGlassesSettingsSwitch extends AbstractWidget {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(AdvancedPeripherals.MOD_ID, "textures/gui/smart_glasses_gui.png");

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
    protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, type.getName());
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBg(pPoseStack, Minecraft.getInstance(), pMouseX, pMouseY);
    }

    @Override
    public void renderButton(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        // Disable rendering of default buttons
    }

    @Override
    protected void renderBg(@NotNull PoseStack pPoseStack, @NotNull Minecraft pMinecraft, int pMouseX, int pMouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        if (isEnabled) {
            blit(pPoseStack, this.getX() - 3, this.getY(), 45, 217, 23, 22);
        } else {
            blit(pPoseStack, this.getX(), this.getY(), 23, 217, 21, 22);
        }
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        if (this.isEnabled)
            return;
        for (Slot slot : screen.getMenu().slots) {
            if (slot instanceof SmartGlassesSlot smartGlassesSlot) {
                if (smartGlassesSlot.slotType == this.type) {
                    smartGlassesSlot.setEnabled(true);
                    continue;
                }
                smartGlassesSlot.setEnabled(false);
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

    public void renderTooltip(PoseStack poseStack, int x, int y) {
        if (screen != null && isMouseOver(x, y))
            screen.renderComponentTooltip(poseStack, Collections.singletonList(type.getName()), x, y);
    }

}
