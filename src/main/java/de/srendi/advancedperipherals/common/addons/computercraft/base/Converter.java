package de.srendi.advancedperipherals.common.addons.computercraft.base;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class Converter {

    public static Object posToObject(BlockPos pos) {
        Map<String, Object> map = new HashMap<>();
        map.put("x", pos.getX());
        map.put("y", pos.getY());
        map.put("z", pos.getZ());
        return map;
    }

}
