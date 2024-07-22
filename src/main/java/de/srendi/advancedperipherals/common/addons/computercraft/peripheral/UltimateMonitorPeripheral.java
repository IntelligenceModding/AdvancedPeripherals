package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.core.apis.TableHelper;
import dan200.computercraft.shared.peripheral.monitor.MonitorPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.terminal.UltimateNetworkedTerminal;
import de.srendi.advancedperipherals.common.addons.computercraft.terminal.UltimateNetworkedTerminal.MonitorSide;
import de.srendi.advancedperipherals.common.addons.computercraft.terminal.UltimateServerMonitor;
import de.srendi.advancedperipherals.common.blocks.blockentities.UltimateMonitorEntity;

import java.util.Map;
import java.util.HashMap;

public class UltimateMonitorPeripheral extends MonitorPeripheral {
    private final UltimateMonitorEntity monitor;

    public UltimateMonitorPeripheral(UltimateMonitorEntity monitor) {
        super(monitor);
        this.monitor = monitor;
    }

    private UltimateServerMonitor getMonitor() throws LuaException {
        UltimateServerMonitor monitor = this.monitor.getCachedServerMonitor();
        if (monitor == null) {
            throw new LuaException("Monitor has been detached");
        }
        return monitor;
    }

    @Override
    public UltimateNetworkedTerminal getTerminal() throws LuaException {
        UltimateNetworkedTerminal term = getMonitor().getTerminal();
        if (term == null) {
            throw new LuaException("Monitor has been detached");
        }
        return term;
    }

    @LuaFunction
    public double getTransparency() {
        return (double)(this.getTerminal().getTransparency()) / 0xff;
    }

    @LuaFunction
    public void setTransparency(double transparency) {
        this.getTerminal().setTransparency((int)(transparency * 0xff));
    }

    @LuaFunction
    public Map<String, Object> getSideColor(IArguments args) {
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
    public void setSideColor(IArguments args) {
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
}
