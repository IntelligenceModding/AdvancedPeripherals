package de.srendi.advancedperipherals.common.addons.valkyrienskies;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperationContext;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheralPlugin;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperation.SCAN_SHIPS;

public class ShipScannerPlugin extends BasePeripheralPlugin {
    public ShipScannerPlugin(IPeripheralOwner owner) {
        super(owner);
    }

    @Override
    public IPeripheralOperation<?>[] getOperations() {
        return new IPeripheralOperation[]{SCAN_SHIPS};
    }

    @LuaFunction(mainThread = true)
    public final MethodResult scanShips(int radius) throws LuaException {
        return withOperation(SCAN_SHIPS, new SphereOperationContext(radius), context -> {
            return context.getRadius() > SCAN_SHIPS.getMaxCostRadius() ? MethodResult.of(null, "Radius exceeds max value") : null;
        }, context -> {
            Vec3 pos = this.owner.getCenterPos();
            List<ServerShip> ships = ValkyrienSkies.getNearbyShips((ServerLevel) this.owner.getLevel(), pos, context.getRadius());
            List<Map<String, Object>> shipDatas = ships.stream().map(s -> LuaConverter.shipToObject(s, pos)).collect(Collectors.toList());
            return MethodResult.of(shipDatas);
        }, null);
    }

    @LuaFunction
    public final MethodResult scanShipCost(int radius) {
        int estimatedCost = estimateShipCost(radius);
        if (estimatedCost < 0) {
            return MethodResult.of(null, "Radius exceeds max value");
        }
        return MethodResult.of(estimatedCost);
    }

    private static int estimateShipCost(int radius) {
        if (radius <= SCAN_SHIPS.getMaxFreeRadius()) {
            return 0;
        }
        if (radius > SCAN_SHIPS.getMaxCostRadius()) {
            return -1;
        }
        return SCAN_SHIPS.getCost(SphereOperationContext.of(radius));
    }
}
