package de.srendi.advancedperipherals.common.addons.computercraft.integrations.botania;

import dan200.computercraft.api.lua.GenericSource;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.common.block.tile.mana.TileSpreader;

import java.util.HashMap;
import java.util.Map;

public class SpreaderIntegration implements GenericSource {

    @NotNull
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "manaSpreader");
    }

    @LuaFunction
    public static int getMana(TileSpreader tileEntity) {
        return tileEntity.getCurrentMana();
    }

    @LuaFunction
    public static int getMaxMana(TileSpreader tileEntity) {
        return tileEntity.getMaxMana();
    }

    @LuaFunction
    public static Object getBounding(TileSpreader tileEntity) {
        if (tileEntity.getBinding() == null)
            return null;
        Map<String, Integer> coords = new HashMap<>(3);
        coords.put("x", tileEntity.getBinding().getX());
        coords.put("y", tileEntity.getBinding().getY());
        coords.put("z", tileEntity.getBinding().getZ());
        return coords;
    }

    @LuaFunction
    public static String getVariant(TileSpreader tileEntity) {
        return tileEntity.getVariant().toString();
    }

    @LuaFunction
    public static boolean isFull(TileSpreader tileEntity) {
        return tileEntity.isFull();
    }

    @LuaFunction
    public static boolean isEmpty(TileSpreader tileEntity) {
        return tileEntity.isEmpty();
    }
}
