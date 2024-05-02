package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.core.apis.TableHelper;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collections;
import java.util.Map;
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

    protected void saveEntity(CompoundTag data) {
        automataCore.getPeripheralOwner().getDataStorage().put(ENTITY_NBT_KEY, data);
    }

    protected CompoundTag getEntity() {
        return automataCore.getPeripheralOwner().getDataStorage().getCompound(ENTITY_NBT_KEY);
    }

    protected void removeEntity() {
        automataCore.getPeripheralOwner().getDataStorage().remove(ENTITY_NBT_KEY);
    }

    @Nullable
    protected Entity extractEntity() {
        CompoundTag data = getEntity();
        EntityType<?> type = EntityType.byString(data.getString("entity")).orElse(null);
        if (type != null) {
            Entity entity = type.create(automataCore.getPeripheralOwner().getLevel());
            if (entity == null)
                return null;
            entity.load(data);
            return entity;
        }
        return null;
    }


    @LuaFunction(mainThread = true)
    public final MethodResult captureAnimal(@NotNull IArguments arguments) throws LuaException {
        Map<?, ?> opts = arguments.count() > 0 ? arguments.getTable(0) : Collections.emptyMap();
        float yaw = opts != null ? (float) TableHelper.optNumberField(opts, "yaw", 0) : 0;
        float pitch = opts != null ? (float) TableHelper.optNumberField(opts, "pitch", 0) : 0;

        HitResult entityHit = automataCore.getPeripheralOwner().withPlayer(APFakePlayer.wrapActionWithRot(yaw, pitch, p -> p.findHit(false, true, suitableEntity)));
        if (entityHit.getType() == HitResult.Type.MISS)
            return MethodResult.of(null, "Nothing found");
        return automataCore.withOperation(CAPTURE_ANIMAL, context -> {
            LivingEntity entity = (LivingEntity) ((EntityHitResult) entityHit).getEntity();
            if (entity instanceof Player || !entity.isAlive())
                return MethodResult.of(null, "Unsuitable entity");

            CompoundTag nbt = new CompoundTag();
            nbt.putString("entity", EntityType.getKey(entity.getType()).toString());
            entity.saveWithoutId(nbt);
            entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
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
        owner.getLevel().addFreshEntity(extractedEntity);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getCapturedAnimal() {
        Entity extractedEntity = extractEntity();
        return MethodResult.of(LuaConverter.completeEntityToLua(extractedEntity, automataCore.getPeripheralOwner().getToolInMainHand()));
    }
}
