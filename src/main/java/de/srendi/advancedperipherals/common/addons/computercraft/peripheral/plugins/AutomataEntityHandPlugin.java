package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import de.srendi.advancedperipherals.lib.peripherals.owner.TurtlePeripheralOwner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.USE_ON_ANIMAL;

public class AutomataEntityHandPlugin extends AutomataCorePlugin {

    private final Predicate<Entity> suitableEntity;

    public AutomataEntityHandPlugin(AutomataCorePeripheral automataCore, Predicate<Entity> suitableEntity) {
        super(automataCore);
        this.suitableEntity = suitableEntity;
    }

    @Override
    public @Nullable IPeripheralOperation<?>[] getOperations() {
        return new IPeripheralOperation[]{USE_ON_ANIMAL};
    }

    @LuaFunction(mainThread = true)
    public final MethodResult useOnAnimal() throws LuaException {
        return automataCore.withOperation(USE_ON_ANIMAL, context -> {
            TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
            ItemStack selectedTool = owner.getToolInMainHand();
            int previousDamageValue = selectedTool.getDamageValue();
            ActionResultType result = owner.withPlayer(player -> player.useOnFilteredEntity(suitableEntity));
            if (automataCore.hasAttribute(AutomataCorePeripheral.ATTR_STORING_TOOL_DURABILITY))
                selectedTool.setDamageValue(previousDamageValue);
            return MethodResult.of(true, result.toString());
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult inspectAnimal() {
        automataCore.addRotationCycle();
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        RayTraceResult entityHit = owner.withPlayer(player -> player.findHit(false, true, suitableEntity));
        if (entityHit.getType() == RayTraceResult.Type.MISS)
            return MethodResult.of(null, "Nothing found");
        Entity entity = ((EntityRayTraceResult) entityHit).getEntity();
        if (!(entity instanceof AnimalEntity))
            return MethodResult.of(null, "Well, entity is not animal entity, but how?");
        return MethodResult.of(LuaConverter.animalToLua((AnimalEntity) entity, owner.getToolInMainHand()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult searchAnimals() {
        automataCore.addRotationCycle();
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        BlockPos currentPos = owner.getPos();
        AxisAlignedBB box = new AxisAlignedBB(currentPos);
        List<Map<String, Object>> entities = new ArrayList<>();
        ItemStack itemInHand = owner.getToolInMainHand();
        owner.getWorld().getEntities((Entity) null, box.inflate(automataCore.getInteractionRadius()), suitableEntity).forEach(entity -> entities.add(LuaConverter.completeEntityWithPositionToLua(entity, itemInHand, currentPos)));
        return MethodResult.of(entities);
    }
}
