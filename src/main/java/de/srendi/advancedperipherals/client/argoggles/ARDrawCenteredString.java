package de.srendi.advancedperipherals.client.argoggles;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public class ARDrawCenteredString extends ARRenderAction {

	private String text;
	private int x;
	private int y;
	private int color;
	
	public ARDrawCenteredString(String text, int x, int y, int color) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.color = color;
	}

	@Override
	public void draw(Minecraft mc, MatrixStack matrixStack) {
		AbstractGui.drawCenteredString(matrixStack, mc.fontRenderer, text, x, y, color);
	}
	
}
