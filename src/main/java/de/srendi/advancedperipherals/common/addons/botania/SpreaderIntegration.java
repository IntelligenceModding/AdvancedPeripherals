package de.srendi.advancedperipherals.common.addons.botania;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import net.minecraft.tileentity.TileEntity;
import vazkii.botania.common.block.tile.mana.TileSpreader;

import java.util.HashMap;
import java.util.Map;

public class SpreaderIntegration extends TileEntityIntegrationPeripheral<TileSpreader> {

    public SpreaderIntegration(TileEntity entity) {
        super(entity);
    }

    @Override
    public String getType() {
        return "manaSpreader";
    }

    @LuaFunction(mainThread = true)
    public final int getMana() {
        return tileEntity.getCurrentMana();
    }

    @LuaFunction(mainThread = true)
    public final int getMaxMana() {
        return tileEntity.getMaxMana();
    }

    @LuaFunction(mainThread = true)
    public final Object getBounding() {
        if (tileEntity.getBinding() == null)
            return null;
        Map<String, Integer> coords = new HashMap<>(3);
        coords.put("x", tileEntity.getBinding().getX());
        coords.put("y", tileEntity.getBinding().getY());
        coords.put("z", tileEntity.getBinding().getZ());
        return coords;
    }

    @LuaFunction(mainThread = true)
    public final String getVariant() {
        return tileEntity.getVariant().toString();
    }

    @LuaFunction(mainThread = true)
    public final boolean isFull() {
        return tileEntity.isFull();
    }

    @LuaFunction(mainThread = true)
    public final boolean isEmpty() {
        return tileEntity.isEmpty();
    }
}
