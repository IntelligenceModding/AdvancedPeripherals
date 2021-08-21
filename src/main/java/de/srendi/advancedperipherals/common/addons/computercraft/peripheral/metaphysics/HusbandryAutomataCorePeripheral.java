package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.IAutomataCoreTier;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCoreTier;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.IPeripheralOperation;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.CAPTURE_ANIMAL;
import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.USE_ON_ANIMAL;

public class HusbandryAutomataCorePeripheral extends WeakAutomataCorePeripheral {
    public static final String TYPE = "husbandryAutomata";

    private static final Predicate<Entity> isAnimal = entity1 -> entity1.getType().getCategory().isFriendly();
    private static final Predicate<Entity> isLivingEntity = entity1 -> entity1 instanceof LivingEntity;
    private static final Predicate<Entity> isNotPlayer = entity1 -> !(entity1 instanceof Player);
    private static final Predicate<Entity> suitableEntity = isAnimal.and(isLivingEntity).and(isNotPlayer);
    private static final String ENTITY_NBT_KEY = "storedEntity";

    public HusbandryAutomataCorePeripheral(ITurtleAccess turtle, TurtleSide side) {
        super(TYPE, turtle, side);
    }

    protected HusbandryAutomataCorePeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableHusbandryAutomataCore;
    }

    @Override
    public IAutomataCoreTier getTier() {
        return AutomataCoreTier.TIER2;
    }
    protected boolean isEntityInside() {
        return !owner.getDataStorage().getCompound(ENTITY_NBT_KEY).isEmpty();
    }

    protected void saveEntity(CompoundTag data) {
        owner.getDataStorage().put(ENTITY_NBT_KEY, data);
    }

    protected CompoundTag getEntity() {
        return owner.getDataStorage().getCompound(ENTITY_NBT_KEY);
    }

    protected void removeEntity() {
        owner.getDataStorage().remove(ENTITY_NBT_KEY);
    }

    protected @Nullable
    Entity extractEntity() {
        CompoundTag data = getEntity();
        EntityType<?> type = EntityType.byString(data.getString("entity")).orElse(null);
        if (type != null) {
            Entity entity = type.create(getLevel());
            if (entity == null)
                return null;
            entity.load(data);
            return entity;
        }
        return null;
    }

    @Override
    public List<IPeripheralOperation<?>> possibleOperations() {
        List<IPeripheralOperation<?>> data = super.possibleOperations();
        data.add(USE_ON_ANIMAL);
        data.add(CAPTURE_ANIMAL);
        return data;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult useOnAnimal() {
        return withOperation(USE_ON_ANIMAL, context -> {
            ItemStack selectedTool = owner.getToolInMainHand();
            int previousDamageValue = selectedTool.getDamageValue();
            InteractionResult result = owner.withPlayer(player -> player.useOnFilteredEntity(suitableEntity));
            if (restoreToolDurability())
                selectedTool.setDamageValue(previousDamageValue);
            return MethodResult.of(true, result.toString());
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult inspectAnimal() {
        addRotationCycle();
        HitResult entityHit = owner.withPlayer(player -> player.findHit(false, true, suitableEntity));
        if (entityHit.getType() == HitResult.Type.MISS)
            return MethodResult.of(null, "Nothing found");
        Entity entity = ((EntityHitResult) entityHit).getEntity();
        if (!(entity instanceof Animal))
            return MethodResult.of(null, "Well, entity is not animal entity, but how?");
        return MethodResult.of(LuaConverter.animalToLua((Animal) entity, owner.getToolInMainHand()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult searchAnimals() {
        addRotationCycle();
        BlockPos currentPos = getPos();
        AABB box = new AABB(currentPos);
        List<Map<String, Object>> entities = new ArrayList<>();
        ItemStack itemInHand = owner.getToolInMainHand();
        getLevel().getEntities((Entity) null, box.inflate(getInteractionRadius()), suitableEntity).forEach(entity -> entities.add(LuaConverter.completeEntityWithPositionToLua(entity, itemInHand, currentPos)));
        return MethodResult.of(entities);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult captureAnimal() {
        HitResult entityHit = owner.withPlayer(player -> player.findHit(false, true, suitableEntity));
        if (entityHit.getType() == HitResult.Type.MISS)
            return MethodResult.of(null, "Nothing found");
        return withOperation(CAPTURE_ANIMAL, context -> {
            LivingEntity entity = (LivingEntity) ((EntityHitResult) entityHit).getEntity();
            if (entity instanceof Player || !entity.isAlive()) return MethodResult.of(null, "Unsuitable entity");
            CompoundTag nbt = new CompoundTag();
            nbt.putString("entity", EntityType.getKey(entity.getType()).toString());
            entity.saveWithoutId(nbt);
            entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
            saveEntity(nbt);
            return MethodResult.of(true);
        }, context -> {
            if (isEntityInside())
                return Optional.of(MethodResult.of(null, "Another entity already captured"));
            return Optional.empty();
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult releaseAnimal() {
        if (!isEntityInside())
            return MethodResult.of(null, "No entity is stored");
        addRotationCycle();
        Entity extractedEntity = extractEntity();
        if (extractedEntity == null)
            return MethodResult.of(null, "Problem with entity unpacking");
        BlockPos blockPos = getPos().offset(owner.getFacing().getNormal());
        extractedEntity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        removeEntity();
        getLevel().addFreshEntity(extractedEntity);
        return MethodResult.of(true);
    }

    @LuaFunction
    public final MethodResult getCapturedAnimal() {
        Entity extractedEntity = extractEntity();
        return MethodResult.of(LuaConverter.completeEntityToLua(extractedEntity, owner.getToolInMainHand()));
    }
}
