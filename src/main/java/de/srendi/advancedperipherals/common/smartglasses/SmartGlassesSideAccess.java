package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.core.computer.ComputerSide;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    @Override
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
    public DataComponentPatch getUpgradeData() {
        UpgradeData<IPocketUpgrade> upgradeData = this.getUpgrade();
        return upgradeData == null ? DataComponentPatch.EMPTY : upgradeData.data();
    }

    @Override
    public void setUpgradeData(DataComponentPatch data) {
        UpgradeData<IPocketUpgrade> upgradeData = this.getUpgrade();
        if (upgradeData == null) {
            return;
        }
        this.setUpgrade(UpgradeData.of(upgradeData.holder(), data));
    }

    @Override
    public void invalidatePeripheral() {
        this.computer.invalidatePeripheral();
    }
}
