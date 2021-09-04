package de.srendi.advancedperipherals.common.addons.botania;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import net.minecraft.tileentity.TileEntity;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;

public class ManaFlowerIntegration extends TileEntityIntegrationPeripheral<TileEntityGeneratingFlower> {

    public ManaFlowerIntegration(TileEntity entity) {
        super(entity);
    }

    @Override
    public String getType() {
        return "manaFlower";
    }

    @LuaFunction(mainThread = true)
    public final boolean isPassiveFlower() {
        return tileEntity.isPassiveFlower();
    }

    @LuaFunction(mainThread = true)
    public final int getMaxMana() {
        return tileEntity.getMaxMana();
    }

    @LuaFunction(mainThread = true)
    public final int getGeneration() {
        return tileEntity.getValueForPassiveGeneration();
    }

    @LuaFunction(mainThread = true)
    public final int getMana() {
        return tileEntity.getMana();
    }

    @LuaFunction(mainThread = true)
    public final boolean isOnEnchantedSoil() {
        return tileEntity.overgrowth;
    }
}
