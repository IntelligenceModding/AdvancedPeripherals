package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.argoggles.ARRenderAction;
import de.srendi.advancedperipherals.common.argoggles.RenderActionType;
import de.srendi.advancedperipherals.common.blocks.blockentities.ARControllerEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;

import java.util.Optional;

public class ARControllerPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<ARControllerEntity>> {

    public static final String PERIPHERAL_TYPE = "arController";

    public ARControllerPeripheral(ARControllerEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableARGoggles.get();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult isRelativeMode() {
        int[] virtualScreenSize = owner.tileEntity.getVirtualScreenSize();
        if (virtualScreenSize != null) {
            return MethodResult.of(owner.tileEntity.isRelativeMode(), virtualScreenSize[0], virtualScreenSize[1]);
        } else {
            return MethodResult.of(owner.tileEntity.isRelativeMode());
        }
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
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DRAW_STRING, text, x, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawStringWithId(String id, String text, int x, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.DRAW_STRING, text, x, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawCenteredString(String text, int x, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DRAW_CENTERED_STRING, text, x, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawCenteredStringWithId(String id, String text, int x, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.DRAW_CENTERED_STRING, text, x, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawRightboundString(String text, int x, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DRAW_RIGHTBOUND_STRING, text, x, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawRightboundStringWithId(String id, String text, int x, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.DRAW_RIGHTBOUND_STRING, text, x, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void horizontalLine(int minX, int maxX, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.HORIZONTAL_LINE, minX, maxX, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void horizontalLineWithId(String id, int minX, int maxX, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.HORIZONTAL_LINE, minX, maxX, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void verticalLine(int x, int minY, int maxY, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.VERTICAL_LINE, x, minY, maxY, color));
    }

    @LuaFunction(mainThread = true)
    public final void verticalLineWithId(String id, int x, int minY, int maxY, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.VERTICAL_LINE, x, minY, maxY, color));
    }

    @LuaFunction(mainThread = true)
    public final void fill(int minX, int minY, int maxX, int maxY, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.FILL, minX, minY, maxX, maxY, color));
    }

    @LuaFunction(mainThread = true)
    public final void fillWithId(String id, int minX, int minY, int maxX, int maxY, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.FILL, minX, minY, maxX, maxY, color));
    }

    @LuaFunction(mainThread = true)
    public final void fillGradient(int minX, int minY, int maxX, int maxY, int colorFrom, int colorTo) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.FILL_GRADIENT, minX, minY, maxX, maxY, colorFrom, colorTo));
    }

    @LuaFunction(mainThread = true)
    public final void fillGradientWithId(String id, int minX, int minY, int maxX, int maxY, int colorFrom, int colorTo) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.FILL_GRADIENT, minX, minY, maxX, maxY, colorFrom, colorTo));
    }

    @LuaFunction(mainThread = true)
    public final void drawCircle(int x, int y, int radius, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DRAW_CIRCLE, x, y, radius, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawCircleWithId(String id, int x, int y, int radius, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.DRAW_CIRCLE, x, y, radius, color));
    }

    @LuaFunction(mainThread = true)
    public final void fillCircle(int x, int y, int radius, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.FILL_CIRCLE, x, y, radius, color));
    }

    @LuaFunction(mainThread = true)
    public final void fillCircleWithId(String id, int x, int y, int radius, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.FILL_CIRCLE, x, y, radius, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawItemIcon(String itemId, int x, int y) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DRAW_ITEM_ICON, itemId, x, y));
    }

    @LuaFunction(mainThread = true)
    public final void drawItemIconWithId(String id, String itemId, int x, int y) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.DRAW_ITEM_ICON, itemId, x, y));
    }

    @LuaFunction(mainThread = true)
    public final void clear() {
        owner.tileEntity.clearCanvas();
    }

    @LuaFunction(mainThread = true)
    public final void clearElement(String id) {
        owner.tileEntity.clearElement(id);
    }

    //TODO - 0.8r: These two functions do not work. This has several reasons. https://github.com/Seniorendi/AdvancedPeripherals/issues/307
    //Returning 0 instead of crashing
    @LuaFunction(mainThread = true)
    public final float getPlayerRotationY() {
        return 0;
    }

    @LuaFunction(mainThread = true)
    public final float getPlayerRotationZ() {
        return 0;
    }
}
