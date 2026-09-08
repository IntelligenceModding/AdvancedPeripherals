package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.api.lua.IComputerSystem;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.setup.APComputerComponents;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.world.entity.Entity;

import java.util.Map;
import java.util.function.Supplier;

public final class SmartGlassesAPI implements ILuaAPI {
    private final Supplier<Entity> equipped;

    private SmartGlassesAPI(Supplier<Entity> equipped) {
        this.equipped = equipped;
    }

    public static ILuaAPI create(IComputerSystem system) {
        final Supplier<Entity> smartGlassesEquipped = system.getComponent(APComputerComponents.SMARTGLASSES_EQUIPPED);
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
        return this.equipped.get() != null;
    }

    @LuaFunction(mainThread = true)
    public Map<String, Object> getOwner() {
        Entity entity = this.equipped.get();
        if (entity == null) {
            return null;
        }

        Map<String, Object> data = LuaConverter.entityToLua(entity, LuaConverter.entityContextBuilder().detailed().build());
        CoordUtil.putXYZCoords(data, entity.getX(), entity.getY(), entity.getZ());
        return data;
    }
}
