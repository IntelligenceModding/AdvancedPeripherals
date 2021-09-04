package de.srendi.advancedperipherals.common.addons.botania;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import net.minecraft.tileentity.TileEntity;
import vazkii.botania.common.block.tile.mana.TilePool;

public class ManaPoolIntegration extends TileEntityIntegrationPeripheral<TilePool> {

    public ManaPoolIntegration(TileEntity entity) {
        super(entity);
    }

    @Override
    public String getType() {
        return "manaPool";
    }

    @LuaFunction(mainThread = true)
    public final int getMana() {
        return tileEntity.getCurrentMana();
    }

    @LuaFunction(mainThread = true)
    public final int getMaxMana() {
        return tileEntity.getAvailableSpaceForMana();
    }

    @LuaFunction(mainThread = true)
    public final boolean isFull() {
        return tileEntity.isFull();
    }

    @LuaFunction(mainThread = true)
    public final boolean isEmpty() {
        return getMana() == 0;
    }

}
