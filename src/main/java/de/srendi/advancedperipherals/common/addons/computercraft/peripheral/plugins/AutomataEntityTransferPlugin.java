package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.owner.TurtlePeripheralOwner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

import javax.annotation.Nullable;
import java.util.function.Predicate;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.CAPTURE_ANIMAL;

public class AutomataEntityTransferPlugin extends AutomataCorePlugin {

    private static final String ENTITY_NBT_KEY = "storedEntity";

    private final Predicate<Entity> suitableEntity;

    public AutomataEntityTransferPlugin(AutomataCorePeripheral automataCore, Predicate<Entity> suitableEntity) {
        super(automataCore);
        this.suitableEntity = suitableEntity;
    }

    @Override
    public @org.jetbrains.annotations.Nullable IPeripheralOperation<?>[] getOperations() {
        return new IPeripheralOperation[]{CAPTURE_ANIMAL};
    }

    protected boolean isEntityInside() {
        return !automataCore.getPeripheralOwner().getDataStorage().getCompound(ENTITY_NBT_KEY).isEmpty();
    }

    protected void saveEntity(CompoundNBT data) {
        automataCore.getPeripheralOwner().getDataStorage().put(ENTITY_NBT_KEY, data);
    }

    protected CompoundNBT getEntity() {
        return automataCore.getPeripheralOwner().getDataStorage().getCompound(ENTITY_NBT_KEY);
    }

    protected void removeEntity() {
        automataCore.getPeripheralOwner().getDataStorage().remove(ENTITY_NBT_KEY);
    }

    protected @Nullable
    Entity extractEntity() {
        CompoundNBT data = getEntity();
        EntityType<?> type = EntityType.byString(data.getString("entity")).orElse(null);
        if (type != null) {
            Entity entity = type.create(automataCore.getPeripheralOwner().getWorld());
            if (entity == null)
                return null;
            entity.load(data);
            return entity;
        }
        return null;
    }


    @LuaFunction(mainThread = true)
    public final MethodResult captureAnimal() throws LuaException {
        RayTraceResult entityHit = automataCore.getPeripheralOwner().withPlayer(player -> player.findHit(false, true, suitableEntity));
        if (entityHit.getType() == RayTraceResult.Type.MISS)
            return MethodResult.of(null, "Nothing found");
        return automataCore.withOperation(CAPTURE_ANIMAL, context -> {
            LivingEntity entity = (LivingEntity) ((EntityRayTraceResult) entityHit).getEntity();
            if (entity instanceof PlayerEntity || !entity.isAlive()) return MethodResult.of(null, "Unsuitable entity");
            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("entity", EntityType.getKey(entity.getType()).toString());
            entity.saveWithoutId(nbt);
            entity.remove();
            saveEntity(nbt);
            return MethodResult.of(true);
        }, context -> {
            if (isEntityInside())
                return MethodResult.of(null, "Another entity already captured");
            return null;
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult releaseAnimal() {
        if (!isEntityInside())
            return MethodResult.of(null, "No entity is stored");
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        automataCore.addRotationCycle();
        Entity extractedEntity = extractEntity();
        if (extractedEntity == null)
            return MethodResult.of(null, "Problem with entity unpacking");
        BlockPos blockPos = owner.getPos().offset(owner.getFacing().getNormal());
        extractedEntity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        removeEntity();
        owner.getWorld().addFreshEntity(extractedEntity);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getCapturedAnimal() {
        Entity extractedEntity = extractEntity();
        return MethodResult.of(LuaConverter.completeEntityToLua(extractedEntity, automataCore.getPeripheralOwner().getToolInMainHand()));
    }
}
