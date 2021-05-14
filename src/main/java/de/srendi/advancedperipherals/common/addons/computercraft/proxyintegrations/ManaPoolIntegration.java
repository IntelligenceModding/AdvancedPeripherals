package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import vazkii.botania.common.block.tile.mana.TilePool;

public class ManaPoolIntegration extends ProxyIntegration<TilePool> {

    @Override
    protected Class<TilePool> getTargetClass() {
        return TilePool.class;
    }

    @Override
    public ProxyIntegration<?> getNewInstance() {
        return new ManaPoolIntegration();
    }

    @Override
    protected String getName() {
        return "manaPool";
    }

    @LuaFunction
    public final int getMana() {
        return getTileEntity().getCurrentMana();
    }

    @LuaFunction
    public final int getMaxMana() {
        return getTileEntity().getAvailableSpaceForMana();
    }

    @LuaFunction
    public final boolean isFull() {
        return getTileEntity().isFull();
    }

    @LuaFunction
    public final boolean isEmpty() {
        return getMana() == 0;
    }
}
