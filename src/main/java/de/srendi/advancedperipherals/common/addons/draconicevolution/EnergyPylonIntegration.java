package de.srendi.advancedperipherals.common.addons.draconicevolution;

import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

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
        return getCore().transferRate.get();
    }

    @LuaFunction(mainThread = true)
    public final int getTier() {
        return getCore().tier.get();
    }

    private TileEnergyCore getCore() {
        try {
            Field field = tileEntity.getClass().getDeclaredField("core");
            field.setAccessible(true);
            TileEnergyCore core = (TileEnergyCore) field.get(tileEntity);
            return core;
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            AdvancedPeripherals.debug("Could not access the core of the pylon! " + ex.getMessage(), Level.ERROR);
        }
        return null;
    }
}
