package de.srendi.advancedperipherals.client.screens;

import dan200.computercraft.client.gui.AbstractComputerScreen;
// import dan200.computercraft.client.gui.widgets.ComputerSidebar;
import dan200.computercraft.client.gui.widgets.TerminalWidget;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import dan200.computercraft.shared.turtle.inventory.TurtleMenu;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.widgets.SmartGlassesSettingsSwitch;
import de.srendi.advancedperipherals.common.container.SmartGlassesContainer;
import de.srendi.advancedperipherals.common.smartglasses.SlotType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class SmartGlassesScreen extends AbstractComputerScreen<SmartGlassesContainer> {

    private static final ResourceLocation BACKGROUND = AdvancedPeripherals.getRL("textures/gui/smart_glasses_gui.png");
    public static final ResourceLocation SIDEBAR = AdvancedPeripherals.getRL("textures/gui/corners_glasses.png");

    private static final int TEX_WIDTH = 254;
    private static final int TEX_HEIGHT = 217;
    private SlotType currentType = SlotType.defaultType();

    public SmartGlassesScreen(SmartGlassesContainer container, Inventory player, Component title) {
        super(container, player, title, TurtleMenu.BORDER);

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
        return new TerminalWidget(
            terminalData,
            computerInput,
            computerActions,
            leftPos + TurtleMenu.BORDER + AbstractComputerMenu.SIDEBAR_WIDTH,
            topPos + TurtleMenu.BORDER
        );
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, leftPos + AbstractComputerMenu.SIDEBAR_WIDTH, topPos, 0, 0, TEX_WIDTH, TEX_HEIGHT);

        if (currentType == SlotType.PERIPHERALS) {
            graphics.blit(BACKGROUND, leftPos + AbstractComputerMenu.SIDEBAR_WIDTH + 222, topPos + 183, 186, 183, 18, 18);
        }

        // TODO: render sidebar on the top-left corner
        // graphics.blit(SIDEBAR, leftPos, topPos + sidebarYOffset, AbstractComputerMenu.SIDEBAR_WIDTH, ComputerSidebar.HEIGHT);
    }

    // TODO:
    // @Override
    // protected void renderTooltip(@NotNull PoseStack poseStack, int x, int y) {
    //     super.renderTooltip(poseStack, x, y);
    //     renderables.forEach(renderable -> {
    //         if (renderable instanceof SmartGlassesSettingsSwitch smartGlassesSettingsSwitch) {
    //             smartGlassesSettingsSwitch.renderTooltip(poseStack, x, y);
    //         }
    //     });
    // }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int x, int y) {
        FormattedCharSequence invName = currentType.getName().getVisualOrderText();
        graphics.drawString(this.font, invName, 212 + AbstractComputerMenu.SIDEBAR_WIDTH - this.font.width(invName) / 2, 133, 0x404040, false);
    }

    public void setCurrentType(SlotType currentType) {
        this.currentType = currentType;
    }
}
