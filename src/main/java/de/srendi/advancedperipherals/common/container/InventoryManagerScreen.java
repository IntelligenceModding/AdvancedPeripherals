package de.srendi.advancedperipherals.common.container;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.ClientUUIDCache;
import de.srendi.advancedperipherals.common.blocks.blockentities.InventoryManagerEntity;
import de.srendi.advancedperipherals.common.container.base.BaseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class InventoryManagerScreen extends BaseScreen<InventoryManagerContainer> {

    private final InventoryManagerContainer container;

    public InventoryManagerScreen(InventoryManagerContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.container = screenContainer;
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

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int x, int y, float partialTicks) {
        super.render(guiGraphics, x, y, partialTicks);
        InventoryManagerEntity blockEntity = (InventoryManagerEntity) container.getTileEntity();
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        UUID owner = blockEntity.getOwner();

        String textToDraw = "No Owner";

        if (owner != null) {
            String username = ClientUUIDCache.getUsername(owner, minecraft.player.getUUID());
            if (username == null) {
                username = owner.toString();
            }
            textToDraw = "Current Owner: " + username;
        }
        guiGraphics.drawString(font, textToDraw, (getGuiLeft() + InventoryManagerContainer.SLOT_X + 8) - font.width(textToDraw) / 2, (getGuiTop() + InventoryManagerContainer.SLOT_Y) + 24, 4210752, false);
    }
}
