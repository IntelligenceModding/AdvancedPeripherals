package de.srendi.advancedperipherals.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.client.screens.base.BaseScreen;
import de.srendi.advancedperipherals.common.container.KeyboardContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * A simple screen but without any rendering calls. Used to unlock the mouse so we can freely write stuff
 */
public class KeyboardScreen extends BaseScreen<KeyboardContainer> {

    public KeyboardScreen(KeyboardContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int x, int y, float partialTicks) {
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int x, int y) {
    }

    @Override
    public void renderBackground(@NotNull PoseStack pPoseStack) {
    }

    @Override
    public int getSizeX() {
        return 256;
    }

    @Override
    public int getSizeY() {
        return 256;
    }

    @Override
    public ResourceLocation getTexture() {
        return null;
    }
}
