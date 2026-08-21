package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.core.computer.ComputerSide;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SmartGlassesSideAccess implements IPocketAccess {

    private final ComputerSide side;
    private final SmartGlassesComputer computer;

    public SmartGlassesSideAccess(ComputerSide side, SmartGlassesComputer computer) {
        this.side = side;
        this.computer = computer;
    }

    public ComputerSide getSide() {
        return this.side;
    }

    public SmartGlassesComputer getComputer() {
        return this.computer;
    }

    @Override
    public ServerLevel getLevel() {
        return this.computer.getLevel();
    }

    @Override
    public Vec3 getPosition() {
        final Entity entity = this.getEntity();
        return entity != null ? entity.position() : this.computer.getPosition().getCenter();
    }

    @Override
    @Nullable
    public Entity getEntity() {
        return this.computer.getEntity();
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
    public UpgradeData<IPocketUpgrade> getUpgrade() {
        return this.computer.getUpgrade(this.side);
    }

    @Override
    public void setUpgrade(@Nullable UpgradeData<IPocketUpgrade> upgrade) {
        this.computer.setUpgrade(this.side, upgrade);
    }

    @Override
    public CompoundTag getUpgradeNBTData() {
        return this.computer.getUpgradeData(this.side);
    }

    @Override
    public void updateUpgradeNBTData() {
        this.computer.updateUpgradeData(this.side);
    }

    @Override
    public void invalidatePeripheral() {
        this.computer.invalidatePeripheral(this.side);
    }

    @SuppressWarnings("removal")
    @Override
    public Map<ResourceLocation, IPeripheral> getUpgrades() {
        UpgradeData<IPocketUpgrade> upgrade = this.getUpgrade();
        if (upgrade == null) {
            return Map.of();
        }
        return Map.of(upgrade.upgrade().getUpgradeID(), this.computer.getPeripheral(this.side));
    }
}
