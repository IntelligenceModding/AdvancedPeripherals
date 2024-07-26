/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.monitor;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaValues;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.apis.TableHelper;
import dan200.computercraft.core.apis.TermMethods;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.terminal.UltimateNetworkedTerminal;
import de.srendi.advancedperipherals.common.addons.computercraft.terminal.UltimateNetworkedTerminal.MonitorSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.HashMap;

public class UltimateMonitorPeripheral extends TermMethods implements IPeripheral {
    private final UltimateMonitorEntity monitor;

    public UltimateMonitorPeripheral(UltimateMonitorEntity monitor) {
        this.monitor = monitor;
    }

    @Override
    public String getType() {
        return "monitor";
    }

    @LuaFunction
    public boolean isUltimate() {
        return true;
    }

    @LuaFunction
    public double getPanelDepth() throws LuaException {
        return (double)(this.getTerminal().getPanelDepth());
    }

    @LuaFunction
    public void setPanelDepth(double panelDepth) throws LuaException {
        this.getTerminal().setPanelDepth((float)(panelDepth));
    }

    @LuaFunction
    public double getTextTransparency() throws LuaException {
        return (double)(this.getTerminal().getTextTransparency()) / 0xff;
    }

    @LuaFunction
    public void setTextTransparency(double transparency) throws LuaException {
        this.getTerminal().setTextTransparency((int)(transparency * 0xff));
    }

    @LuaFunction
    public double getBackgroundTransparency() throws LuaException {
        return (double)(this.getTerminal().getBackgroundTransparency()) / 0xff;
    }

    @LuaFunction
    public void setBackgroundTransparency(double transparency) throws LuaException {
        this.getTerminal().setBackgroundTransparency((int)(transparency * 0xff));
    }

    @LuaFunction
    public Map<String, Object> getSideColor(IArguments args) throws LuaException {
        if (args.count() < 1) {
            throw new LuaException("getSideColor need one argument");
        }
        MonitorSide side = MonitorSide.fromString(args.getString(0));
        if (side == null) {
            throw new LuaException(String.format("Invalid monitor side %s", side));
        }
        int[] color = this.getTerminal().getSideColor(side);
        int a = this.getTerminal().getSideTransparency(side);
        Map<String, Object> obj = new HashMap<>();
        obj.put("r", color[0]);
        obj.put("g", color[1]);
        obj.put("b", color[2]);
        obj.put("a", a);
        return obj;
    }

    @LuaFunction
    public void setSideColor(IArguments args) throws LuaException {
        if (args.count() < 2) {
            throw new LuaException("setSideColor need two arguments");
        }
        MonitorSide side = MonitorSide.fromString(args.getString(0));
        if (side == null) {
            throw new LuaException(String.format("Invalid monitor side %s", side));
        }
        if (!(args.get(1) instanceof Map<?, ?> obj)) {
            throw new LuaException("The second argument should be an rgba table");
        }
        int r = (int) TableHelper.getNumberField(obj, "r");
        int g = (int) TableHelper.getNumberField(obj, "g");
        int b = (int) TableHelper.getNumberField(obj, "b");
        int a = (int) TableHelper.getNumberField(obj, "a");
        this.getTerminal().setSideColor(side, new int[]{r, g, b});
        this.getTerminal().setSideTransparency(side, a);
    }

    /**
     * Set the scale of this monitor. A larger scale will result in the monitor having a lower resolution, but display
     * text much larger.
     *
     * @param scaleArg The monitor's scale. This must be a multiple of 0.5 between 0.5 and 5.
     * @throws LuaException If the scale is out of range.
     * @see #getTextScale()
     */
    @LuaFunction
    public final void setTextScale( double scaleArg ) throws LuaException
    {
        int scale = (int) (LuaValues.checkFinite( 0, scaleArg ) * 2.0);
        if( scale < 1 || scale > 10 ) throw new LuaException( "Expected number in range 0.5-5" );
        getMonitor().setTextScale( scale );
    }

    /**
     * Get the monitor's current text scale.
     *
     * @return The monitor's current scale.
     * @throws LuaException If the monitor cannot be found.
     * @cc.since 1.81.0
     */
    @LuaFunction
    public final double getTextScale() throws LuaException
    {
        return getMonitor().getTextScale() / 2.0;
    }

    @Override
    public void attach( @Nonnull IComputerAccess computer )
    {
        monitor.addComputer( computer );
    }

    @Override
    public void detach( @Nonnull IComputerAccess computer )
    {
        monitor.removeComputer( computer );
    }

    @Override
    public boolean equals( IPeripheral other )
    {
        return other instanceof UltimateMonitorPeripheral && monitor == ((UltimateMonitorPeripheral) other).monitor;
    }

    @Nonnull
    private UltimateServerMonitor getMonitor() throws LuaException
    {
        UltimateServerMonitor monitor = this.monitor.getCachedServerMonitor();
        if( monitor == null ) throw new LuaException( "Monitor has been detached" );
        return monitor;
    }

    @Nonnull
    @Override
    public UltimateNetworkedTerminal getTerminal() throws LuaException
    {
        UltimateNetworkedTerminal terminal = getMonitor().getTerminal();
        if( terminal == null ) throw new LuaException( "Monitor has been detached" );
        return terminal;
    }

    @Nullable
    @Override
    public Object getTarget()
    {
        return monitor;
    }
}
