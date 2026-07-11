package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import org.jetbrains.annotations.NotNull;

/**
 * Integration for kinetic tile entities with scroll value behaviours like the speed controller or the creative motor
 */
public class ScrollValueBehaviourIntegration implements APGenericPeripheral {
    @Override
    @NotNull
    public String getPeripheralType() {
        return "scroll_behaviour";
    }

    @LuaFunction(mainThread = true)
    public final int getScrollValue(ScrollValueBehaviour scrollBehaviour) {
        return scrollBehaviour.getValue();
    }

    @LuaFunction(mainThread = true)
    public final void setScrollValue(ScrollValueBehaviour scrollBehaviour, int speed) {
        scrollBehaviour.setValue(speed);
    }
}
