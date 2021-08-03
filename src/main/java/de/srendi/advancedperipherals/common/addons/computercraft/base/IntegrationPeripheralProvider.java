package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.BeaconIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.botania.ManaFlowerIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.botania.ManaPoolIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.botania.SpreaderIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.immersiveengineering.CapacitorIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.immersiveengineering.RedstoneConnectorIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.immersiveengineering.RedstoneProbeIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.integrateddynamics.VariableStoreIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.storagedrawers.DrawerIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
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
        //Mekanism does have cc integration in V10.1.
        /*if (ModList.get().isLoaded("mekanismgenerators")) {
            registerIntegration(new FissionIntegration());
            registerIntegration(new FusionIntegration());
            registerIntegration(new TurbineIntegration());
        }
        if (ModList.get().isLoaded("mekanism")) {
            registerIntegration(new InductionPortIntegration());
            registerIntegration(new BoilerIntegration());
            registerIntegration(new DigitalMinerIntegration());
            registerIntegration(new ChemicalTankIntegration());
            registerIntegration(new GenericMekanismIntegration());
        }*/
        if (ModList.get().isLoaded("botania")) {
            registerIntegration(new ManaPoolIntegration());
            registerIntegration(new SpreaderIntegration());
            registerIntegration(new ManaFlowerIntegration());
        }
        if (ModList.get().isLoaded("immersiveengineering")) {
            registerIntegration(new RedstoneProbeIntegration());
            registerIntegration(new RedstoneConnectorIntegration());
            registerIntegration(new CapacitorIntegration());
        }
        if (ModList.get().isLoaded("integrateddynamics")) {
            registerIntegration(new VariableStoreIntegration());
        }
        if (ModList.get().isLoaded("storagedrawers")) {
            registerIntegration(new DrawerIntegration());
        }
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
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level world, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        if (world.getBlockEntity(blockPos) == null)
            return LazyOptional.empty();
        BlockEntity tileEntity = world.getBlockEntity(blockPos);
        return getIntegration(tileEntity);
    }
}
