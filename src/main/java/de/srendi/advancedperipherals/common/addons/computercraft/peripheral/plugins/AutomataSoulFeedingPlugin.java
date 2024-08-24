package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.WeakAutomataCorePeripheral;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.metaphysics.IFeedableAutomataCore;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import net.minecraft.world.InteractionResult;

public class AutomataSoulFeedingPlugin extends AutomataCorePlugin {

    public AutomataSoulFeedingPlugin(AutomataCorePeripheral automataCore) {
        super(automataCore);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult feedSoul() {
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        if (!(owner.getToolInMainHand().getItem() instanceof IFeedableAutomataCore))
            return MethodResult.of(null, "Well, you should feed correct mechanical soul!");

        InteractionResult result = owner.withPlayer(APFakePlayer::useOnEntity);
        automataCore.addRotationCycle(3);
        return MethodResult.of(result.consumesAction(), result.toString());
    }

    @Override
    public boolean isSuitable(IPeripheral peripheral) {
        return peripheral.getClass() == WeakAutomataCorePeripheral.class;
    }
}
