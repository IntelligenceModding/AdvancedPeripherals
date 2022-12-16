package de.srendi.advancedperipherals.common.addons.mekanism;

import mekanism.api.Coord4D;
import mekanism.api.MekanismAPI;
import mekanism.common.util.UnitDisplayUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class Mekanism {

    private Mekanism() {
        throw new IllegalStateException("Utility class");
    }

    public static Object getRadiation(Level world, BlockPos pos) {
        if (!world.isClientSide) {
            Map<String, Object> map = new HashMap<>();
            String[] radiation = UnitDisplayUtils.getDisplayShort(MekanismAPI.getRadiationManager().getRadiationLevel(new Coord4D(pos, world)), UnitDisplayUtils.RadiationUnit.SV, 4).getString().split(" ");
            map.put("radiation", radiation[0]);
            map.put("unit", radiation[1]);
            return map;
        }
        return null;
    }

    public static double getRadiationRaw(Level world, BlockPos pos) {
        if (!world.isClientSide)
            return MekanismAPI.getRadiationManager().getRadiationLevel(new Coord4D(pos, world));
        return 0;
    }

}
