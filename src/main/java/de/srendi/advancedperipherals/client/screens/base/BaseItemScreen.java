/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.client.screens.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.common.container.base.BaseItemContainer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public abstract class BaseItemScreen<T extends BaseItemContainer> extends AbstractContainerScreen<T> {

    protected BaseItemScreen(T screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);

        imageWidth = getSizeX();
        imageHeight = getSizeY();
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int x, int y, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, x, y, partialTicks);
        renderTooltip(matrixStack, x, y);
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, getTexture());

        int xPos = (width - imageWidth) / 2;
        int yPos = (height - imageHeight) / 2;

        blit(matrixStack, xPos, yPos, 0, 0, imageWidth, imageHeight);
    }

    public abstract int getSizeX();

    public abstract int getSizeY();

    public abstract ResourceLocation getTexture();
}
