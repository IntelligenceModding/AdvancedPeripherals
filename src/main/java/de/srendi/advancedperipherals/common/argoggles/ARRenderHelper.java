package de.srendi.advancedperipherals.common.argoggles;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;

public class ARRenderHelper extends AbstractGui {
	private static ARRenderHelper instance = new ARRenderHelper();
	
	public static void drawRightboundString(MatrixStack matrixStack, FontRenderer fontRenderer, String text, int x, int y, int color) {
		drawString(matrixStack, fontRenderer, text, x - fontRenderer.getStringWidth(text), y, color);
	}
	
	@Override
	public void hLine(MatrixStack matrixStack, int minX, int maxX, int y, int color) {
		super.hLine(matrixStack, minX, maxX, y, color);
	}
	
	@Override
	public void vLine(MatrixStack matrixStack, int x, int minY, int maxY, int color) {
		super.vLine(matrixStack, x, minY, maxY, color);
	}
	
	@Override
	protected void fillGradient(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {
		super.fillGradient(matrixStack, x1, y1, x2, y2, colorFrom, colorTo);
	}

	public static ARRenderHelper getInstance() {
		return instance;
	}
}
