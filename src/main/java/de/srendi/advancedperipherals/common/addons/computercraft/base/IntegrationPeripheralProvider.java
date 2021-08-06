package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.BeaconIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IntegrationPeripheralProvider implements IPeripheralProvider {

    public final List<Integration<?>> integrations = new ArrayList<>();

    public void registerIntegration(Integration<?> integration) {
        integrations.add(integration);
    }

    public void register() {
        registerIntegration(new BeaconIntegration());
    }

    public LazyOptional<IPeripheral> getIntegration(BlockEntity tileEntity) {
        for (Integration<?> integration : integrations) {
            if (integration.isTileEntity(tileEntity)) {
                Integration<?> Integration = integration.getNewInstance();
                Integration.setTileEntity(tileEntity);
                return LazyOptional.of(() -> Integration);
            }
        }
        return LazyOptional.empty();
    }

    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        if (level.getBlockEntity(blockPos) == null)
            return LazyOptional.empty();
        BlockEntity tileEntity = level.getBlockEntity(blockPos);
        return getIntegration(tileEntity);
    }
}
