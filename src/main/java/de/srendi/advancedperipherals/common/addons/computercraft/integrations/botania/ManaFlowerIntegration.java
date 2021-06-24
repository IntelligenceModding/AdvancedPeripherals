package de.srendi.advancedperipherals.common.addons.computercraft.integrations.botania;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.Integration;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;

public class ManaFlowerIntegration extends Integration<TileEntityGeneratingFlower> {
    @Override
    protected Class<TileEntityGeneratingFlower> getTargetClass() {
        return TileEntityGeneratingFlower.class;
    }

    @Override
    public Integration<?> getNewInstance() {
        return new ManaFlowerIntegration();
    }

    @Override
    public String getType() {
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
