package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import net.minecraft.world.item.ItemStack;
import owmii.powah.block.reactor.ReactorPartTile;
import owmii.powah.block.reactor.ReactorTile;
import owmii.powah.lib.logistics.energy.Energy;

import java.util.Optional;

public class ReactorIntegration implements APGenericPeripheral {
    private static final int URANINITE_SLOT = 1;
    private static final int REDSTONE_SLOT = 3;
    private static final int CARBON_SLOT = 2;

    @Override
    public String getPeripheralType() {
        return "uraninite_reactor";
    }

    // Note core method will invoke getBlockEntity, which is not thread safe
    @LuaFunction(mainThread = true)
    public final boolean isRunning(ReactorPartTile blockEntity) {
        return blockEntity.core().map(ReactorTile::isRunning).orElse(false);
    }

    @LuaFunction(mainThread = true)
    public final double getFuel(ReactorPartTile blockEntity) {
        return blockEntity.core().map(t -> t.fuel.perCent()).orElse(0D);
    }

    @LuaFunction(mainThread = true)
    public final double getCarbon(ReactorPartTile blockEntity) {
        return blockEntity.core().map(t -> t.carbon.perCent()).orElse(0D);
    }

    @LuaFunction(mainThread = true)
    public final double getRedstone(ReactorPartTile blockEntity) {
        return blockEntity.core().map(t -> t.redstone.perCent()).orElse(0D);
    }

    @LuaFunction(mainThread = true)
    public final double getStoredEnergy(ReactorPartTile blockEntity) {
        return blockEntity.core().map(ReactorTile::getEnergy).map(Energy::getEnergyStored).orElse(0L);
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergy(ReactorPartTile blockEntity) {
        return blockEntity.core().map(ReactorTile::getEnergy).map(Energy::getMaxEnergyStored).orElse(0L);
    }

    @LuaFunction(mainThread = true)
    public final double getTemperature(ReactorPartTile blockEntity) {
        return blockEntity.core().map(t -> t.temp.perCent()).orElse(0D);
    }

    private static Object getInventoryAt(ReactorPartTile blockEntity, int slot) {
        return blockEntity.core()
            .map(t -> t.getStack(slot))
            .map(LuaConverter::itemStackToObject)
            .orElse(null);
    }

    @LuaFunction(mainThread = true)
    public final Object getUraniniteSlot(ReactorPartTile blockEntity) {
        return getInventoryAt(blockEntity, URANINITE_SLOT);
    }

    @LuaFunction(mainThread = true)
    public final Object getRedstoneSlot(ReactorPartTile blockEntity) {
        return getInventoryAt(blockEntity, REDSTONE_SLOT);
    }

    @LuaFunction(mainThread = true)
    public final Object getCarbonSlot(ReactorPartTile blockEntity) {
        return getInventoryAt(blockEntity, CARBON_SLOT);
    }
}
