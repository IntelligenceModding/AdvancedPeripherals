package de.srendi.advancedperipherals.common.addons.computercraft.integrations.botania;

import dan200.computercraft.api.lua.GenericSource;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;

public class ManaFlowerIntegration implements GenericSource {

    @NotNull
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "manaFlower");
    }

    @LuaFunction
    public static boolean isPassiveFlower(TileEntityGeneratingFlower tileEntity) {
        return tileEntity.isPassiveFlower();
    }

    @LuaFunction
    public static int getMaxMana(TileEntityGeneratingFlower tileEntity) {
        return tileEntity.getMaxMana();
    }

    @LuaFunction
    public static int getGeneration(TileEntityGeneratingFlower tileEntity) {
        return tileEntity.getValueForPassiveGeneration();
    }

    @LuaFunction
    public static int getMana(TileEntityGeneratingFlower tileEntity) {
        return tileEntity.getMana();
    }

    @LuaFunction
    public static boolean isOnEnchantedSoil(TileEntityGeneratingFlower tileEntity) {
        return tileEntity.overgrowth;
    }
}
