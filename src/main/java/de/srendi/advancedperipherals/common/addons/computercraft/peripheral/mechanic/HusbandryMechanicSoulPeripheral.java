package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.mechanic;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import de.srendi.advancedperipherals.common.util.fakeplayer.FakePlayerProviderTurtle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.IForgeShearable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HusbandryMechanicSoulPeripheral extends WeakMechanicSoulPeripheral {
    public HusbandryMechanicSoulPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult useOnEntity() {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        ActionResultType result = FakePlayerProviderTurtle.withPlayer(turtle, player -> player.useOnFilteredEntity(entity -> entity.getType().getCategory().isFriendly()));
        return MethodResult.of(true, result.toString());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult inspectEntity() {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        RayTraceResult entityHit = FakePlayerProviderTurtle.withPlayer(turtle, player -> player.findHit(false, true, entity -> entity.getType().getCategory().isFriendly()));
        if (entityHit.getType() == RayTraceResult.Type.MISS)
            return MethodResult.of(null, "Nothing found");
        Entity entity = ((EntityRayTraceResult)entityHit).getEntity();
        if (!(entity instanceof AnimalEntity))
            return MethodResult.of(null, "Well, entity is not animal entity, but how?");
        Map<String, Object> data = new HashMap<>();
        AnimalEntity animal = (AnimalEntity) entity;
        data.put("entity_id", animal.getId());
        data.put("name", animal.getName().getString());
        data.put("tags", animal.getTags());
        data.put("baby", animal.isBaby());
        data.put("inLove", animal.isInLove());
        data.put("aggressive", animal.isAggressive());
        if (animal instanceof IForgeShearable) {
            IForgeShearable shareable = (IForgeShearable) animal;
            data.put("shareable", shareable.isShearable(turtle.getInventory().getItem(turtle.getSelectedSlot()), animal.level, animal.blockPosition()));
        }
        return MethodResult.of(data);
    }
}
