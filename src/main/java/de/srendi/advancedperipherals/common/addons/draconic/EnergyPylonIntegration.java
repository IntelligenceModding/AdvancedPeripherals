package de.srendi.advancedperipherals.common.addons.draconic;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorInjector;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorStabilizer;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFluidGate;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

//TODO: A lot of methods and fields are private or protected. https://github.com/brandon3055/Draconic-Evolution/issues/1572
//Wait until this changed.
public class EnergyPylonIntegration extends TileEntityIntegrationPeripheral<TileEnergyPylon> {

    public EnergyPylonIntegration(TileEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public String getType() {
        //Use the same name as in 1.12 draconic. So old programs could be used again
        return "draconic_rf_storage";
    }

    @LuaFunction(mainThread = true)
    public final long getEnergyStored() {
        return tileEntity.getExtendedStorage();
    }

    @LuaFunction(mainThread = true)
    public final long getMaxEnergyStored() {
        return tileEntity.getExtendedCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getTransferPerTick() {
        return 0;
    }
}
