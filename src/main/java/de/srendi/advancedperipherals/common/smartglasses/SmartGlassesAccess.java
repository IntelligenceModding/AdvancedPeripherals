package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SmartGlassesAccess implements IPocketAccess {

    private final SmartGlassesComputer computer;

    public SmartGlassesAccess(SmartGlassesComputer computer) {
        this.computer = computer;
    }

    @Nullable
    @Override
    public Entity getEntity() {
        return computer.getEntity();
    }

    @Override
    public int getColour() {
        return 0;
    }

    @Override
    public void setColour(int colour) {
    }

    @Override
    public int getLight() {
        return 0;
    }

    @Override
    public void setLight(int colour) {
    }

    @Override
    public CompoundTag getUpgradeNBTData() {
        return new CompoundTag();
    }

    @Override
    public void updateUpgradeNBTData() {

    }

    @Override
    public void invalidatePeripheral() {

    }

    @Override
    public Map<ResourceLocation, IPeripheral> getUpgrades() {
        return computer.getUpgrades();
    }

    public SmartGlassesComputer getComputer() {
        return computer;
    }
}
