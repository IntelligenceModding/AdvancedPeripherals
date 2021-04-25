package de.srendi.advancedperipherals.common.argoggles;

import de.srendi.advancedperipherals.AdvancedPeripherals;

public enum RenderActionType {

	DrawCenteredString(3), DrawString(3), Fill(5), HorizontalLine(4), VerticalLine(4), FillGradient(6),
	DrawRightboundString(3), DrawCircle(4), FillCircle(4), DrawItemIcon(2);

	int intArgCount;

	RenderActionType(int intArgCount) {
		this.intArgCount = intArgCount;
	}

	boolean ensureArgs(int[] args) {
		boolean correct = args.length >= intArgCount;
		if (!correct)
			AdvancedPeripherals.LOGGER.warn(
					"Got invalid number of arguments for AR render action {}: expected {}, got {}", this.toString(),
					intArgCount, args.length);
		return correct;
	}
}
