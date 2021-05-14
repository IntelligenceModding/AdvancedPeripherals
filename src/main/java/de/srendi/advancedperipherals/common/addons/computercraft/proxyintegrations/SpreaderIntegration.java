package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import net.minecraft.util.math.BlockPos;
import vazkii.botania.common.block.tile.mana.TileSpreader;

import java.util.HashMap;
import java.util.Map;

public class SpreaderIntegration extends ProxyIntegration<TileSpreader> {
    @Override
    protected Class<TileSpreader> getTargetClass() {
        return TileSpreader.class;
    }

    @Override
    public ProxyIntegration<?> getNewInstance() {
        return new SpreaderIntegration();
    }

    @Override
    protected String getName() {
        return "manaSpreader";
    }

    @LuaFunction
    public final int getMana() {
        return getTileEntity().getCurrentMana();
    }

    @LuaFunction
    public final int getMaxMana() {
        return getTileEntity().getMaxMana();
    }

    @LuaFunction
    public final Object getBounding() {
        if(getTileEntity().getBinding() == null)
            return null;
        Map<String, Integer> coords = new HashMap<>(3);
        coords.put("x", getTileEntity().getBinding().getX());
        coords.put("y", getTileEntity().getBinding().getY());
        coords.put("z", getTileEntity().getBinding().getZ());
        return coords;
    }

    @LuaFunction
    public final String getVariant() {
        return getTileEntity().getVariant().toString();
    }

    @LuaFunction
    public final void setCanShoot(boolean canShoot) {
        getTileEntity().setCanShoot(canShoot);
    }

    @LuaFunction
    public final boolean isFull() {
        return getTileEntity().isFull();
    }

    @LuaFunction
    public final boolean isEmpty() {
        return getTileEntity().isEmpty();
    }
}
