package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.api.metaphysics.IFeedableAutomataCore;
import de.srendi.advancedperipherals.api.peripherals.IBasePeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.WeakAutomataCorePeripheral;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.owner.TurtlePeripheralOwner;
import net.minecraft.util.ActionResultType;

public class AutomataSoulFeedingPlugin extends AutomataCorePlugin {

    public AutomataSoulFeedingPlugin(AutomataCorePeripheral automataCore) {
        super(automataCore);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult feedSoul() {
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        if (!(owner.getToolInMainHand().getItem() instanceof IFeedableAutomataCore)) {
            return MethodResult.of(null, "Well, you should feed correct mechanical soul!");
        }
        ActionResultType result = owner.withPlayer(APFakePlayer::useOnEntity);
        automataCore.addRotationCycle(3);
        return MethodResult.of(true, result.toString());
    }

    @Override
    public boolean isSuitable(IBasePeripheral<?> peripheral) {
        return peripheral.getClass() == WeakAutomataCorePeripheral.class;
    }
}
