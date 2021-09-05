package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralPlugin;
import de.srendi.advancedperipherals.lib.peripherals.owner.IPeripheralOwner;
import mekanism.api.Coord4D;
import mekanism.common.Mekanism;
import mekanism.common.util.UnitDisplayUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EnvironmentDetectorPlugin implements IPeripheralPlugin {
    private final IPeripheralOwner owner;

    public EnvironmentDetectorPlugin(IPeripheralOwner owner) {
        this.owner = owner;
    }

    @LuaFunction(mainThread = true)
    public final Object getRadiation() {
        Map<String, Object> map = new HashMap<>();
        String[] radiation = UnitDisplayUtils.getDisplayShort(
                Mekanism.radiationManager.getRadiationLevel(new Coord4D(owner.getPos(), Objects.requireNonNull(owner.getWorld()))),
                UnitDisplayUtils.RadiationUnit.SV, 4
        ).getString().split(" ");
        map.put("radiation", radiation[0]);
        map.put("unit", radiation[1]);
        return map;
    }

    @LuaFunction(mainThread = true)
    public final double getRadiationRaw() {
        return Mekanism.radiationManager.getRadiationLevel(new Coord4D(owner.getPos(), Objects.requireNonNull(owner.getWorld())));
    }
}
