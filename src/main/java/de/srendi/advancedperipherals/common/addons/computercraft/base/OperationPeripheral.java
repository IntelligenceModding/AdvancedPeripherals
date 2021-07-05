package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class OperationPeripheral extends FuelConsumingPeripheral{

    private int fuelConsumptionRate = 1;
    private final Map<String, Timestamp> lastOperationTimestamps = new HashMap<>();

    public OperationPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    public OperationPeripheral(String type, TileEntity tileEntity) {
        super(type, tileEntity);
    }

    public OperationPeripheral(String type, Entity entity) {
        super(type, entity);
    }

    public OperationPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
    }

    @Override
    public boolean consumeFuel(int count) {
        return super.consumeFuel(count * fuelConsumptionRate);
    }

    @Override
    public boolean consumeFuel(int count, boolean simulate) {
        return super.consumeFuel(count * fuelConsumptionRate, simulate);
    }

    protected abstract int getRawCooldown(String name);

    protected abstract int getMaxFuelConsumptionRate();

    public int getCooldown(String name) {
        return getRawCooldown(name) / fuelConsumptionRate;
    }

    public void trackOperation(String name) {
        lastOperationTimestamps.put(name, Timestamp.valueOf(LocalDateTime.now()));
    }

    public Optional<MethodResult> cooldownCheck(String name){
        if (isOnCooldown(name))
            return Optional.of(MethodResult.of(null, String.format("%s is on cooldown", name)));
        return Optional.empty();
    }

    public boolean isOnCooldown(String name) {
        Timestamp lastScanTimestamp = lastOperationTimestamps.get(name);
        if (lastScanTimestamp == null) {
            return false;
        }
        return Timestamp.valueOf(LocalDateTime.now()).getTime() - lastScanTimestamp.getTime() < getCooldown(name);
    }

    public int getCurrentCooldown(String name) {
        Timestamp lastScanTimestamp = lastOperationTimestamps.get(name);
        if (lastScanTimestamp == null) {
            return 0;
        }
        return (int) Math.max(0, getCooldown(name) - Timestamp.valueOf(LocalDateTime.now()).getTime() + lastScanTimestamp.getTime());
    }

    @LuaFunction
    public final int getFuelConsumptionRate() {
        return fuelConsumptionRate;
    }

    @LuaFunction
    public final MethodResult setFuelConsumptionRate(int rate) {
        int maxConsumptionRate = getMaxFuelConsumptionRate();
        if (rate > maxConsumptionRate)
            return MethodResult.of(null, String.format("Rate is bigger than max allowed %d", maxConsumptionRate));
        if (rate < 1)
            return MethodResult.of(null, "Consumption rate cannot be lower than 1");
        fuelConsumptionRate = rate;
        return MethodResult.of(true);
    }
}
