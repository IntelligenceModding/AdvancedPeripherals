package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public final MethodResult useOnAnimal(@NotNull IArguments arguments) throws LuaException {
        LuaTable<?, ?> options = EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null));

        boolean sneak = options.optBoolean("sneak").orElse(false);
        float yaw = options.optDouble("yaw").orElse(0d).floatValue();
        float pitch = options.optDouble( "pitch").orElse(0d).floatValue();

        return automataCore.withOperation(USE_ON_ANIMAL, context -> {
            TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
            ItemStack selectedTool = owner.getToolInMainHand();
            int previousDamageValue = selectedTool.getDamageValue();
            InteractionResult result = owner.withPlayer(APFakePlayer.wrapActionWithShiftKey(sneak, APFakePlayer.wrapActionWithRot(yaw, pitch, p -> p.useOnFilteredEntity(suitableEntity))));
            if (automataCore.hasAttribute(AutomataCorePeripheral.ATTR_STORING_TOOL_DURABILITY))
                selectedTool.setDamageValue(previousDamageValue);

            return MethodResult.of(result.consumesAction(), result.toString());
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult inspectAnimal(@NotNull IArguments arguments) throws LuaException {
        LuaTable<?, ?> options = EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null));

        boolean sneak = options.optBoolean("sneak").orElse(false);
        float yaw = options.optDouble("yaw").orElse(0d).floatValue();
        float pitch = options.optDouble( "pitch").orElse(0d).floatValue();

        automataCore.addRotationCycle();
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        HitResult entityHit = owner.withPlayer(APFakePlayer.wrapActionWithRot(yaw, pitch, p -> p.findHit(false, true, suitableEntity)));
        if (entityHit.getType() == HitResult.Type.MISS)
            return MethodResult.of(null, "Nothing found");

        Entity entity = ((EntityHitResult) entityHit).getEntity();
        if (!(entity instanceof Animal animal))
            return MethodResult.of(null, "Well, entity is not animal entity, but how?");

        return MethodResult.of(LuaConverter.animalToLua(animal, owner.getToolInMainHand()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult searchAnimals() {
        automataCore.addRotationCycle();
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        BlockPos currentPos = owner.getPos();
        AABB box = new AABB(currentPos);
        ItemStack itemInHand = owner.getToolInMainHand();
        List<Map<String, Object>> entities = owner.getLevel().getEntities((Entity) null, box.inflate(automataCore.getInteractionRadius()), suitableEntity).stream().map(entity -> LuaConverter.completeEntityWithPositionToLua(entity, itemInHand, currentPos)).toList();
        return MethodResult.of(entities);
    }
}
