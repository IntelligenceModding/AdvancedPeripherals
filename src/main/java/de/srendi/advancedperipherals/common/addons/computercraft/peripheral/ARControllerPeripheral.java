package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.argoggles.ARRenderAction;
import de.srendi.advancedperipherals.common.argoggles.RenderActionType;
import de.srendi.advancedperipherals.common.blocks.tileentity.ARControllerTile;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.owner.TileEntityPeripheralOwner;

import java.util.Optional;

public class ARControllerPeripheral extends BasePeripheral<TileEntityPeripheralOwner<ARControllerTile>> {
    public static final String TYPE = "arController";

    public ARControllerPeripheral(ARControllerTile tileEntity) {
        super(TYPE, new TileEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableARGoggles;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult isRelativeMode() {
        int[] virtualScreenSize = owner.tileEntity.getVirtualScreenSize();
        if (virtualScreenSize != null)
            return MethodResult.of(owner.tileEntity.isRelativeMode(), virtualScreenSize[0], virtualScreenSize[1]);
        else
            return MethodResult.of(owner.tileEntity.isRelativeMode());
    }

    @LuaFunction(mainThread = true)
    public final void setRelativeMode(boolean enabled, Optional<Integer> virtualScreenWidth, Optional<Integer> virtualScreenHeight) throws LuaException {
        if (enabled) {
            if (!virtualScreenWidth.isPresent() || !virtualScreenHeight.isPresent())
                throw new LuaException("You need to pass virtual screen width and height to enable relative mode.");
            owner.tileEntity.setRelativeMode(virtualScreenWidth.get(), virtualScreenHeight.get());
        } else {
            owner.tileEntity.disableRelativeMode();
        }
    }

    @LuaFunction(mainThread = true)
    public final void drawString(String text, int x, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DrawString, text, x, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawCenteredString(String text, int x, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DrawCenteredString, text, x, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawRightboundString(String text, int x, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DrawRightboundString, text, x, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void horizontalLine(int minX, int maxX, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.HorizontalLine, minX, maxX, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void verticalLine(int x, int minY, int maxY, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.VerticalLine, x, minY, maxY, color));
    }

    @LuaFunction(mainThread = true)
    public final void fill(int minX, int minY, int maxX, int maxY, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.Fill, minX, minY, maxX, maxY, color));
    }

    @LuaFunction(mainThread = true)
    public final void fillGradient(int minX, int minY, int maxX, int maxY, int colorFrom, int colorTo) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.FillGradient, minX, minY, maxX, maxY, colorFrom, colorTo));
    }

    @LuaFunction(mainThread = true)
    public final void drawCircle(int x, int y, int radius, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DrawCircle, x, y, radius, color));
    }

    @LuaFunction(mainThread = true)
    public final void fillCircle(int x, int y, int radius, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.FillCircle, x, y, radius, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawItemIcon(String itemId, int x, int y) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DrawItemIcon, itemId, x, y));
    }

    @LuaFunction(mainThread = true)
    public final void clear() {
        owner.tileEntity.clearCanvas();
    }
}
