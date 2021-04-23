package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import java.util.Optional;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.argoggles.ARRenderAction;
import de.srendi.advancedperipherals.common.argoggles.RenderActionType;
import de.srendi.advancedperipherals.common.blocks.tileentity.ARControllerTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class ARControllerPeripheral extends BasePeripheral{
	private ARControllerTileEntity tileEntity;
	
	public ARControllerPeripheral(String type, ARControllerTileEntity tileEntity) {
        super(type, tileEntity);
        this.tileEntity = tileEntity;
    }

    public ARControllerPeripheral(String type, TileEntity tileEntity) {
        super(type, tileEntity);
    }

    public ARControllerPeripheral(String type, Entity tileEntity) {
        super(type, tileEntity);
    }
    
    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableARGoggles;
    }
    
    @LuaFunction
    public final MethodResult isRelativeMode() {
    	int[] virtualScreenSize = tileEntity.getVirtualScreenSize();
    	if (virtualScreenSize != null)
    		return MethodResult.of(tileEntity.isRelativeMode(), virtualScreenSize[0], virtualScreenSize[1]);
    	else
    		return MethodResult.of(tileEntity.isRelativeMode());
    }
    
    @LuaFunction
    public final void setRelativeMode(boolean enabled, Optional<Integer> virtualScreenWidth, Optional<Integer> virtualScreenHeight) throws LuaException {
    	if (enabled) {
    		if (!virtualScreenWidth.isPresent() || !virtualScreenHeight.isPresent())
    			throw new LuaException("You need to pass virtual screen width and height to enable relative mode.");
    		tileEntity.setRelativeMode(virtualScreenWidth.get(), virtualScreenHeight.get());
    	}
    	else {
    		tileEntity.disableRelativeMode();
    	}
    }
    
    @LuaFunction
    public final void drawString(String text, int x, int y, int color) {
    	tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DrawString, text, x, y, color));
    }
    
    @LuaFunction
    public final void drawCenteredString(String text, int x, int y, int color) {
    	tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DrawCenteredString, text, x, y, color));
    }
    
    @LuaFunction
    public final void drawRightboundString(String text, int x, int y, int color) {
    	tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DrawRightboundString, text, x, y, color));
    }
    
    @LuaFunction
    public final void horizontalLine(int minX, int maxX, int y, int color) {
    	tileEntity.addToCanvas(new ARRenderAction(RenderActionType.HorizontalLine, minX, maxX, y, color));
    }
    
    @LuaFunction
    public final void verticalLine(int x, int minY, int maxY, int color) {
    	tileEntity.addToCanvas(new ARRenderAction(RenderActionType.VerticalLine, x, minY, maxY, color));
    }
    
    @LuaFunction
    public final void fill(int minX, int minY, int maxX, int maxY, int color) {
    	tileEntity.addToCanvas(new ARRenderAction(RenderActionType.Fill, minX, minY, maxX, maxY, color));
    }
    
    @LuaFunction
    public final void fillGradient(int minX, int minY, int maxX, int maxY, int colorFrom, int colorTo) {
    	tileEntity.addToCanvas(new ARRenderAction(RenderActionType.FillGradient, minX, minY, maxX, maxY, colorFrom, colorTo));
    }
    
    @LuaFunction
    public final void clear() {
    	tileEntity.clearCanvas();
    }
}
