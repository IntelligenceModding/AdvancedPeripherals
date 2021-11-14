package de.srendi.advancedperipherals.common.addons.draconicevolution;

import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorStabilizer;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//TODO: A lot of methods and fields are private or protected. https://github.com/brandon3055/Draconic-Evolution/issues/1572
//Wait until this changed.
public class ReactorIntegration extends TileEntityIntegrationPeripheral<TileReactorStabilizer> {

    public ReactorIntegration(TileEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public String getType() {
        //Use the same name as in 1.12 draconic. So old programs could be used again
        return "draconic_reactor";
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getReactorInfo() {
        TileReactorCore reactor = getCore();
        if(reactor == null)
            return null;
        Map<String, Object> map = new HashMap<>();
        map.put("temperature", MathUtils.round(reactor.temperature.get(), 100.0D));
        map.put("fieldStrength", MathUtils.round(reactor.shieldCharge.get(), 100.0D));
        map.put("maxFieldStrength", MathUtils.round(reactor.maxShieldCharge.get(), 100.0D));
        map.put("energySaturation", reactor.saturation.get());
        map.put("maxEnergySaturation", reactor.maxSaturation.get());
        map.put("fuelConversion", MathUtils.round(reactor.convertedFuel.get(), 1000.0D));
        map.put("maxFuelConversion", reactor.reactableFuel.get() + reactor.convertedFuel.get());
        map.put("generationRate", (int) reactor.generationRate.get());
        map.put("fieldDrainRate", reactor.fieldDrain.get());
        map.put("fuelConversionRate", (int) Math.round(reactor.fuelUseRate.get() * 1000000.0D));
        map.put("status", reactor.reactorState.get().name().toLowerCase(Locale.ENGLISH));
        map.put("failSafe", reactor.failSafeMode.get());
        return map;
    }

    @LuaFunction(mainThread = true)
    public final boolean chargeReactor() {
        TileReactorCore reactor = getCore();

        if (reactor.canCharge()) {
            reactor.chargeReactor();
            return true;
        } else {
            return false;
        }
    }

    @LuaFunction(mainThread = true)
    public final boolean activateReactor() {
        TileReactorCore reactor = getCore();
        if (reactor.canActivate()) {
            reactor.activateReactor();
            return true;
        } else {
            return false;
        }
    }

    @LuaFunction(mainThread = true)
    public final boolean stopReactor() {
        TileReactorCore reactor = getCore();
        if (reactor.canStop()) {
            reactor.shutdownReactor();
            return true;
        } else {
            return false;
        }
    }

    @LuaFunction(mainThread = true)
    public final void setFailSafe(boolean failSafe) {
        TileReactorCore reactor = getCore();
        reactor.failSafeMode.set(failSafe);
    }

    private TileReactorCore getCore() {
        try {
            Method method = TileReactorComponent.class.getDeclaredMethod("getCachedCore", null);
            method.setAccessible(true);
            return (TileReactorCore) method.invoke(tileEntity, null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            AdvancedPeripherals.debug("Could not access the reactor of the stabilizer! " + ex, Level.ERROR);
        }
        return null;
    }
}
