package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.api.lua.IComputerSystem;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.setup.APComputerComponents;

import java.util.function.BooleanSupplier;

public class SmartGlassesAPI implements ILuaAPI {
    private final BooleanSupplier equipped;

    private SmartGlassesAPI(BooleanSupplier equipped) {
        this.equipped = equipped;
    }

    public static ILuaAPI create(IComputerSystem system) {
        final BooleanSupplier smartGlassesEquipped = system.getComponent(APComputerComponents.SMARTGLASSES_EQUIPPED);
        if (smartGlassesEquipped == null) {
            return null;
        }
        return new SmartGlassesAPI(smartGlassesEquipped);
    }

    @Override
    public String[] getNames() {
        return new String[]{"smartglasses"};
    }

    /**
     * isEquipped check if the smart glasses is equipped.
     * Only equipped smart glasses tick its modules.
     *
     * @return if the smart glasses is equipped
     */
    @LuaFunction(mainThread = true)
    public boolean isEquipped() {
        return this.equipped.getAsBoolean();
    }
}
