package de.srendi.advancedperipherals.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dan200.computercraft.client.gui.AbstractComputerScreen;
import dan200.computercraft.client.gui.widgets.ComputerSidebar;
import dan200.computercraft.client.gui.widgets.TerminalWidget;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.widgets.SmartGlassesSettingsSwitch;
import de.srendi.advancedperipherals.common.container.SmartGlassesContainer;
import de.srendi.advancedperipherals.common.smartglasses.SlotType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import static dan200.computercraft.shared.turtle.inventory.TurtleMenu.BORDER;

public class SmartGlassesScreen extends AbstractComputerScreen<SmartGlassesContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(AdvancedPeripherals.MOD_ID, "textures/gui/smart_glasses_gui.png");
    public static final ResourceLocation SIDEBAR = new ResourceLocation(AdvancedPeripherals.MOD_ID, "textures/gui/corners_glasses.png");

    private static final int TEX_WIDTH = 254;
    private static final int TEX_HEIGHT = 217;
    public SlotType currentType = SlotType.defaultType();

    public SmartGlassesScreen(SmartGlassesContainer container, Inventory player, Component title) {
        super(container, player, title, BORDER);

        imageWidth = TEX_WIDTH + AbstractComputerMenu.SIDEBAR_WIDTH;
        imageHeight = TEX_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new SmartGlassesSettingsSwitch(254, 147, SlotType.PERIPHERALS, this));
        addRenderableWidget(new SmartGlassesSettingsSwitch(254, 170, SlotType.MODULES, this));
    }

    @Override
    protected TerminalWidget createTerminal() {
        return new TerminalWidget(terminalData, input, leftPos + BORDER + AbstractComputerMenu.SIDEBAR_WIDTH, topPos + BORDER);
    }

    @Override
    protected void renderBg(@NotNull PoseStack transform, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        blit(transform, leftPos + AbstractComputerMenu.SIDEBAR_WIDTH, topPos, 0, 0, TEX_WIDTH, TEX_HEIGHT);

        RenderSystem.setShaderTexture(0, SIDEBAR);
        ComputerSidebar.renderBackground(transform, leftPos, topPos + sidebarYOffset);
    }

    @Override
    protected void renderTooltip(@NotNull PoseStack poseStack, int x, int y) {
        super.renderTooltip(poseStack, x, y);
        renderables.forEach(renderable -> {
            if (renderable instanceof SmartGlassesSettingsSwitch smartGlassesSettingsSwitch) {
                smartGlassesSettingsSwitch.renderTooltip(poseStack, x, y);
            }
        });
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int x, int y) {
        FormattedCharSequence formattedcharsequence = currentType.getName().getVisualOrderText();
        this.font.draw(poseStack, formattedcharsequence, (float)(212 + AbstractComputerMenu.SIDEBAR_WIDTH - this.font.width(formattedcharsequence) / 2), (float)133, 4210752);
    }

    public void setCurrentType(SlotType currentType) {
        this.currentType = currentType;
    }
}
