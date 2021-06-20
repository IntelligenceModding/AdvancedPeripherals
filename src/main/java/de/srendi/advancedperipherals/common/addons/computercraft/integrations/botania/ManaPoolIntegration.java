package de.srendi.advancedperipherals.common.addons.computercraft.integrations.botania;

import dan200.computercraft.api.lua.GenericSource;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.common.block.tile.mana.TilePool;

public class ManaPoolIntegration implements GenericSource {

    @NotNull
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "manaPool");
    }

    @LuaFunction
    public static int getMana(TilePool tileEntity) {
        return tileEntity.getCurrentMana();
    }

    @LuaFunction
    public static int getMaxMana(TilePool tileEntity) {
        return tileEntity.getAvailableSpaceForMana();
    }

    @LuaFunction
    public static boolean isFull(TilePool tileEntity) {
        return tileEntity.isFull();
    }

    @LuaFunction
    public static boolean isEmpty(TilePool tileEntity) {
        return getMana(tileEntity) == 0;
    }

}
