package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.botania;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;

public class ManaFlowerIntegration extends ProxyIntegration<TileEntityGeneratingFlower> {
    @Override
    protected Class<TileEntityGeneratingFlower> getTargetClass() {
        return TileEntityGeneratingFlower.class;
    }

    @Override
    public ProxyIntegration<?> getNewInstance() {
        return new ManaFlowerIntegration();
    }

    @Override
    protected String getName() {
        return "manaFlower";
    }

    @LuaFunction
    public final boolean isPassiveFlower() {
        return getTileEntity().isPassiveFlower();
    }

    @LuaFunction
    public final int getMaxMana() {
        return getTileEntity().getMaxMana();
    }

    @LuaFunction
    public final int getGeneration() {
        return getTileEntity().getValueForPassiveGeneration();
    }

    @LuaFunction
    public final int getMana() {
        return getTileEntity().getMana();
    }

    @LuaFunction
    public final boolean isOnEnchantedSoil() {
        return getTileEntity().overgrowth;
    }
}
