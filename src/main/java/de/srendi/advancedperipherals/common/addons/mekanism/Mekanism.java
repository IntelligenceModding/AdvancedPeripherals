package de.srendi.advancedperipherals.common.addons.mekanism;

import mekanism.api.Coord4D;
import mekanism.common.util.UnitDisplayUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * We use this class to access things from the Mekanism API. So we can prevent game crashes when mekanism is not loaded
 */
public class Mekanism {

    public static Object getRadiation(World world, BlockPos pos) {
        if (!world.isClientSide) {
            Map<String, Object> map = new HashMap<>();
            String[] radiation = UnitDisplayUtils.getDisplayShort(mekanism.common.Mekanism.radiationManager.getRadiationLevel(new Coord4D(pos, world)), UnitDisplayUtils.RadiationUnit.SV, 4).getString().split(" ");
            map.put("radiation", radiation[0]);
            map.put("unit", radiation[1]);
            return map;

        }
        return null;
    }

    public static double getRadiationRaw(World world, BlockPos pos) {
        if (!world.isClientSide) {
            return mekanism.common.Mekanism.radiationManager.getRadiationLevel(new Coord4D(pos, world));
        }
        return 0;
    }

}
