package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.RepresentationUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.FakePlayerProviderTurtle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

import javax.annotation.Nonnull;
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
    private static final String ENTITY_NBT_KEY = "storedEntity";
    private final static String USE_ON_ANIMAL_OPERATION = "use_on_animal";
    private final static String CAPTURE_ANIMAL_OPERATION = "capture_animal";

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
    protected int getRawCooldown(String name) {
        return switch (name) {
            case USE_ON_ANIMAL_OPERATION -> AdvancedPeripheralsConfig.useOnAnimalCooldown;
            case CAPTURE_ANIMAL_OPERATION -> AdvancedPeripheralsConfig.captureAnimalCooldown;
            default -> super.getRawCooldown(name);
        };
    }

    @Override
    protected int getMaxFuelConsumptionRate() {
        return AdvancedPeripheralsConfig.husbandryMechanicSoulMaxFuelConsumptionLevel;
    }

    protected boolean isEntityInside(@Nonnull IComputerAccess access) {
        Pair<MethodResult, CompoundNBT> result = getCompoundSetting(access, ENTITY_NBT_KEY);
        if (result.leftPresent())
            return false;
        return !result.getRight().isEmpty();
    }

    protected Pair<MethodResult, Boolean> saveEntity(@Nonnull IComputerAccess access, CompoundNBT data) {
        return setCompoundSetting(access, ENTITY_NBT_KEY, data);
    }

    protected Pair<MethodResult, CompoundNBT> getEntity(@Nonnull IComputerAccess access) {
        return getCompoundSetting(access, ENTITY_NBT_KEY);
    }

    protected Pair<MethodResult, Boolean> removeEntity(@Nonnull IComputerAccess access) {
        return removeSetting(access, ENTITY_NBT_KEY);
    }

    protected Pair<MethodResult, Entity> extractEntity(@Nonnull IComputerAccess access) {
        return getEntity(access).mapRight(data -> {
            EntityType<?> type = EntityType.byString(data.getString("entity")).orElse(null);
            if (type != null) {
                Entity entity = type.create(getWorld());
                if (entity == null)
                    return null;
                entity.load(data);
                return entity;
            }
            return null;
        });
    }

    protected ItemStack getItemInHand() {
        return turtle.getInventory().getItem(turtle.getSelectedSlot());
    }

    @LuaFunction
    public Map<String, Object> getConfiguration() {
        Map<String, Object> result = super.getConfiguration();
        result.put("useOnAnimalCost", AdvancedPeripheralsConfig.useOnAnimalCost);
        result.put("useOnAnimalCooldown", AdvancedPeripheralsConfig.useOnAnimalCooldown);
        result.put("captureAnimalCost", AdvancedPeripheralsConfig.captureAnimalCost);
        result.put("captureAnimalCooldown", AdvancedPeripheralsConfig.captureAnimalCooldown);
        return result;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult useOnAnimal(@Nonnull IComputerAccess access) {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = cooldownCheck(USE_ON_ANIMAL_OPERATION);
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = consumeFuelOp(access, AdvancedPeripheralsConfig.useOnAnimalCost);
        if (checkResults.isPresent()) return checkResults.map(result -> fuelErrorCallback(access, result)).get();
        ItemStack selectedTool = turtle.getInventory().getItem(turtle.getSelectedSlot());
        int previousDamageValue = selectedTool.getDamageValue();
        ActionResultType result = FakePlayerProviderTurtle.withPlayer(turtle, player -> player.useOnFilteredEntity(suitableEntity));
        if (restoreToolDurability())
            selectedTool.setDamageValue(previousDamageValue);
        trackOperation(access, USE_ON_ANIMAL_OPERATION);
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
        return MethodResult.of(RepresentationUtil.animalToLua((AnimalEntity) entity, getItemInHand()));
    }

    @LuaFunction
    public final MethodResult searchAnimals() {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        AxisAlignedBB box = new AxisAlignedBB(getPos());
        List<Map<String, Object>> entities = new ArrayList<>();
        ItemStack itemInHand = getItemInHand();
        BlockPos currentPos = getPos();
        getWorld().getEntities((Entity) null, box.inflate(getInteractionRadius()), suitableEntity).forEach(entity -> entities.add(RepresentationUtil.completeEntityWithPositionToLua(entity, itemInHand, currentPos)));
        return MethodResult.of(entities);
    }

    @LuaFunction
    public final MethodResult captureAnimal(@Nonnull IComputerAccess access) {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = cooldownCheck(CAPTURE_ANIMAL_OPERATION);
        if (checkResults.isPresent()) return checkResults.get();
        if (isEntityInside(access))
            return MethodResult.of(null, "Another entity already captured");
        RayTraceResult entityHit = FakePlayerProviderTurtle.withPlayer(turtle, player -> player.findHit(false, true, suitableEntity));
        if (entityHit.getType() == RayTraceResult.Type.MISS)
            return MethodResult.of(null, "Nothing found");
        checkResults = consumeFuelOp(access, AdvancedPeripheralsConfig.captureAnimalCost);
        if (checkResults.isPresent()) return checkResults.map(result -> fuelErrorCallback(access, result)).get();
        LivingEntity entity = (LivingEntity) ((EntityRayTraceResult)entityHit).getEntity();
        if (entity instanceof PlayerEntity || !entity.isAlive()) return MethodResult.of(null, "Unsuitable entity");
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("entity", EntityType.getKey(entity.getType()).toString());
        entity.saveWithoutId(nbt);
        entity.remove();
        trackOperation(access, CAPTURE_ANIMAL_OPERATION);
        return saveEntity(access, nbt).reduce(((methodResult, aBoolean) -> {
            if (methodResult != null)
                return methodResult;
            return MethodResult.of(aBoolean);
        }));
    }

    @LuaFunction
    public final MethodResult releaseAnimal(@Nonnull IComputerAccess access) {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        if (!isEntityInside(access))
            return MethodResult.of(null, "No entity is stored");
        Pair<MethodResult, Entity> extractedEntity = extractEntity(access);
        if (extractedEntity.leftPresent())
            return extractedEntity.getLeft();
        Entity entity = extractedEntity.getRight();
        BlockPos blockPos = getPos().offset(turtle.getDirection().getNormal());
        entity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        return removeEntity(access).reduce((mResult, bool) -> {
            if (mResult != null)
                return mResult;
            getWorld().addFreshEntity(entity);
            return MethodResult.of(bool);
        });
    }

    @LuaFunction
    public final MethodResult getCapturedAnimal(@Nonnull IComputerAccess access) {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        Pair<MethodResult, Entity> extractedEntity = extractEntity(access);
        if (extractedEntity.leftPresent())
            return extractedEntity.getLeft();
        return MethodResult.of(RepresentationUtil.completeEntityToLua(extractedEntity.getRight(), getItemInHand()));
    }
}
