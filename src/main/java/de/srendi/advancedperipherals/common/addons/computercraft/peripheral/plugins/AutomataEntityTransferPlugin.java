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
import de.srendi.advancedperipherals.lib.peripherals.AbstractDataStorage;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.CAPTURE_ANIMAL;
import static de.srendi.advancedperipherals.common.setup.APDataComponents.ENTITY_TRANSFER;

public class AutomataEntityTransferPlugin extends AutomataCorePlugin {

    private final Predicate<Entity> suitableEntity;

    public AutomataEntityTransferPlugin(AutomataCorePeripheral automataCore, Predicate<Entity> suitableEntity) {
        super(automataCore);
        this.suitableEntity = suitableEntity;
    }

    @Override
    public IPeripheralOperation<?> @NotNull [] getOperations() {
        return new IPeripheralOperation[]{CAPTURE_ANIMAL};
    }

    protected boolean isEntityInside() {
        try (AbstractDataStorage.ReadView storage = automataCore.getPeripheralOwner().getDataStorage().allocRead()) {
            return storage.getPatched().has(ENTITY_TRANSFER.get());
        }
    }

    protected void saveEntity(CompoundTag data) {
        try (AbstractDataStorage.WriteView storage = automataCore.getPeripheralOwner().getDataStorage().allocWrite()) {
            PatchedDataComponentMap patch = storage.getPatched();
            patch.set(ENTITY_TRANSFER.get(), CustomData.of(data));
            storage.setPatch(patch.asPatch());
        }
    }

    protected CustomData getEntity() {
        try (AbstractDataStorage.ReadView storage = automataCore.getPeripheralOwner().getDataStorage().allocRead()) {
            return storage.getPatched().getOrDefault(ENTITY_TRANSFER.get(), CustomData.EMPTY);
        }
    }

    protected void removeEntity() {
        try (AbstractDataStorage.WriteView storage = automataCore.getPeripheralOwner().getDataStorage().allocWrite()) {
            PatchedDataComponentMap patch = storage.getPatched();
            patch.remove(ENTITY_TRANSFER.get());
            storage.setPatch(patch.asPatch());
        }
    }

    @Nullable
    protected Entity extractEntity() {
        CustomData data = getEntity();
        EntityType<?> type = EntityType.byString(data.getUnsafe().getString("entity")).orElse(null);
        if (type != null) {
            Entity entity = type.create(automataCore.getPeripheralOwner().getLevel());
            if (entity == null) {
                return null;
            }
            data.loadInto(entity);
            return entity;
        }
        return null;
    }


    @LuaFunction(mainThread = true)
    public final MethodResult captureAnimal(@NotNull IArguments arguments) throws LuaException {
        LuaTable<?, ?> options = EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null));

        float yaw = options.optDouble("yaw").orElse(0d).floatValue();
        float pitch = options.optDouble("pitch").orElse(0d).floatValue();

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
    public final MethodResult getCapturedAnimal(IArguments args) throws LuaException {
        boolean detailed = args.count() > 0 ? args.getBoolean(0) : false;
        Entity extractedEntity = extractEntity();
        if (extractedEntity == null) {
            return MethodResult.of(null, "No entity is stored");
        }
        return MethodResult.of(LuaConverter.entityToLua(extractedEntity, LuaConverter.entityContextBuilder().detailed(detailed).itemInHand(automataCore.getPeripheralOwner().getToolInMainHand()).build()));
    }
}
