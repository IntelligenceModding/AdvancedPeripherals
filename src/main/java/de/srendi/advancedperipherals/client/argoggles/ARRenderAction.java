package de.srendi.advancedperipherals.client.argoggles;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;

public abstract class ARRenderAction {
	public abstract void draw(Minecraft mc, MatrixStack matrixstack);
}
