package de.srendi.advancedperipherals.client.screens;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.ClientUUIDCache;
import de.srendi.advancedperipherals.client.screens.base.BaseScreen;
import de.srendi.advancedperipherals.common.blocks.blockentities.InventoryManagerEntity;
import de.srendi.advancedperipherals.common.container.InventoryManagerContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class InventoryManagerScreen extends BaseScreen<InventoryManagerContainer> {
    private static final ResourceLocation BACKGROUND = AdvancedPeripherals.getRL("textures/gui/inventory_manager_gui.png");

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
    public ResourceLocation getBackgroundTexture() {
        return BACKGROUND;
    }

    @Override
    public void render(@NotNull GuiGraphics gui, int x, int y, float partialTicks) {
        super.render(gui, x, y, partialTicks);
        InventoryManagerEntity blockEntity = (InventoryManagerEntity) this.menu.getBlockEntity();
        Font font = Minecraft.getInstance().font;
        UUID owner = blockEntity.getOwnerUUID();

        String textToDraw = "No Owner";

        if (owner != null) {
            String username = ClientUUIDCache.getUsername(owner);
            if (username == null) {
                username = owner.toString();
            }
            textToDraw = "Owner: " + username;
        }
        gui.drawString(
            font,
            textToDraw,
            (getGuiLeft() + InventoryManagerContainer.SLOT_X + 8) - font.width(textToDraw) / 2,
            (getGuiTop() + InventoryManagerContainer.SLOT_Y) + 24,
            0x404040,
            false
        );
    }
}
