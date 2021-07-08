package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.mechanic;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.RepresentationUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.FakePlayerProviderTurtle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class HusbandryMechanicSoulPeripheral extends WeakMechanicSoulPeripheral {
    private static final Predicate<Entity> isAnimal = entity1 -> entity1.getType().getCategory().isFriendly();
    private static final Predicate<Entity> isLivingEntity = entity1 -> entity1 instanceof LivingEntity;
    private static final Predicate<Entity> isNotPlayer = entity1 -> !(entity1 instanceof PlayerEntity);
    private static final Predicate<Entity> suitableEntity = isAnimal.and(isLivingEntity).and(isNotPlayer);

    public HusbandryMechanicSoulPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableHusbandryMechanicSoul;
    }

    public int getInteractionRadius() {
        return AdvancedPeripheralsConfig.husbandryMechanicSoulInteractionRadius;
    }

    @Override
    protected int getMaxFuelConsumptionRate() {
        return AdvancedPeripheralsConfig.husbandryMechanicSoulMaxFuelConsumptionLevel;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult useOnAnimal() {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        ActionResultType result = FakePlayerProviderTurtle.withPlayer(turtle, player -> player.useOnFilteredEntity(suitableEntity));
        return MethodResult.of(true, result.toString());
    }

    @LuaFunction
    public final MethodResult inspectAnimal() {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        RayTraceResult entityHit = FakePlayerProviderTurtle.withPlayer(turtle, player -> player.findHit(false, true, suitableEntity));
        if (entityHit.getType() == RayTraceResult.Type.MISS)
            return MethodResult.of(null, "Nothing found");
        Entity entity = ((EntityRayTraceResult)entityHit).getEntity();
        if (!(entity instanceof AnimalEntity))
            return MethodResult.of(null, "Well, entity is not animal entity, but how?");
        return MethodResult.of(RepresentationUtil.animalToLua((AnimalEntity) entity, turtle.getInventory().getItem(turtle.getSelectedSlot())));
    }

    @LuaFunction
    public final MethodResult searchAnimals() {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        AxisAlignedBB box = new AxisAlignedBB(getPos());
        List<Map<String, Object>> entities = new ArrayList<>();
        ItemStack itemInHand = turtle.getInventory().getItem(turtle.getSelectedSlot());
        getWorld().getEntities((Entity) null, box.inflate(getInteractionRadius()), suitableEntity).forEach(entity -> entities.add(RepresentationUtil.completeEntityToLua(entity, itemInHand)));
        return MethodResult.of(entities);
    }
}
