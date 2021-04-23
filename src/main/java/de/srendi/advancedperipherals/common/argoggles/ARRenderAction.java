package de.srendi.advancedperipherals.common.argoggles;

import java.util.Arrays;
import java.util.Optional;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public final class ARRenderAction implements INBTSerializable<CompoundNBT> {

	private static final String TYPE = "type";
	private static final String STRING_ARG = "string_arg";
	private static final String INT_ARGS = "int_args";

	private RenderActionType type;
	private String stringArg = null;
	private int[] intArgs = new int[0];
	private Optional<int[]> virtualScreenSize = Optional.empty();

	public ARRenderAction() {

	}

	public ARRenderAction(RenderActionType type, int... intArgs) {
		this();
		this.type = type;
		this.intArgs = intArgs;
	}

	public ARRenderAction(RenderActionType type, String stringArg, int... intArgs) {
		this(type, intArgs);
		this.stringArg = stringArg;
	}

	public void draw(Minecraft mc, MatrixStack matrixStack, int w, int h) {
		if (!type.ensureArgs(intArgs))
			return;
		int[] i = intArgs;
		switch (type) {
			case DrawCenteredString:
				AbstractGui.drawCenteredString(matrixStack, mc.fontRenderer, stringArg, relativeX(i[0], w),
						relativeY(i[1], h), i[2]);
				break;
			case DrawString:
				AbstractGui.drawString(matrixStack, mc.fontRenderer, stringArg, relativeX(i[0], w), relativeY(i[1], h),
						i[2]);
				break;
			case Fill:
				AbstractGui.fill(matrixStack, relativeX(i[0], w), relativeY(i[1], h), relativeX(i[2], w),
						relativeY(i[3], h), i[4]);
				break;
			default:
				AdvancedPeripherals.LOGGER.warn("Failed to execute AR render action of unimplemented type " + type);
				break;
		}
	}

	private int relativeX(int x, int windowWidth) {
		if (virtualScreenSize.isPresent()) {
			return (int) Math.round((double) x / virtualScreenSize.get()[0] * windowWidth);
		} else
			return x;
	}

	private int relativeY(int y, int windowHeight) {
		if (virtualScreenSize.isPresent()) {
			return (int) Math.round((double) y / virtualScreenSize.get()[1] * windowHeight);
		} else
			return y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ARRenderAction) {
			ARRenderAction a = (ARRenderAction) obj;
			return type.equals(a.type) && stringArg.equals(a.stringArg) && Arrays.equals(intArgs, a.intArgs);
		}
		return super.equals(obj);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		type = RenderActionType.valueOf(nbt.getString(TYPE));
		stringArg = nbt.getString(STRING_ARG);
		intArgs = nbt.getIntArray(INT_ARGS);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString(TYPE, type.toString());
		nbt.putString(STRING_ARG, stringArg);
		nbt.putIntArray(INT_ARGS, intArgs);
		return nbt;
	}

	public int[] getVirtualScreenSize() {
		if (virtualScreenSize.isPresent())
			return virtualScreenSize.get();
		else
			return null;
	}

	public void setRelativeMode(int virtualScreenWidth, int virtualScreenHeight) {
		virtualScreenSize = Optional.of(new int[] { virtualScreenWidth, virtualScreenHeight });
	}

	public void disableRelativeMode() {
		virtualScreenSize = Optional.empty();
	}

	public ARRenderAction copyWithVirtualScreenSize(Optional<int[]> virtualScreenSize2) {
		ARRenderAction action = new ARRenderAction(type, stringArg, intArgs);
		if (virtualScreenSize2.isPresent())
			action.setRelativeMode(virtualScreenSize2.get()[0], virtualScreenSize2.get()[1]);
		return action;
	}
}
