package de.srendi.advancedperipherals.common.addons.mekanism;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.radiation.IRadiationManager;
import mekanism.common.util.UnitDisplayUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.GlobalPos;
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
            String[] radiation = UnitDisplayUtils.getDisplayShort(IRadiationManager.INSTANCE.getRadiationLevel(GlobalPos.of(world.dimension(), pos)), UnitDisplayUtils.RadiationUnit.SV, 4).getString().split(" ");
            map.put("radiation", radiation[0]);
            map.put("unit", radiation[1]);
            return map;
        }
        return null;
    }

    public static double getRadiationRaw(Level world, BlockPos pos) {
        if (!world.isClientSide)
            return IRadiationManager.INSTANCE.getRadiationLevel(GlobalPos.of(world.dimension(), pos));
        return 0;
    }

    public static DefaultedRegistry<Chemical> getChemicalRegistry() {
        return MekanismAPI.CHEMICAL_REGISTRY;
    }

}
