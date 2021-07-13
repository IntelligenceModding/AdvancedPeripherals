package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.RepresentationUtil;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class HusbandryAutomataCorePeripheral extends WeakAutomataCorePeripheral {
    private static final Predicate<Entity> isAnimal = entity1 -> entity1.getType().getCategory().isFriendly();
    private static final Predicate<Entity> isLivingEntity = entity1 -> entity1 instanceof LivingEntity;
    private static final Predicate<Entity> isNotPlayer = entity1 -> !(entity1 instanceof PlayerEntity);
    private static final Predicate<Entity> suitableEntity = isAnimal.and(isLivingEntity).and(isNotPlayer);
    private static final String ENTITY_NBT_KEY = "storedEntity";
    private final static String USE_ON_ANIMAL_OPERATION = "use_on_animal";
    private final static String CAPTURE_ANIMAL_OPERATION = "capture_animal";

    public HusbandryAutomataCorePeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableHusbandryAutomataCore;
    }

    @Override
    public int getInteractionRadius() {
        return AdvancedPeripheralsConfig.husbandryAutomataCoreInteractionRadius;
    }

    @Override
    protected int getRawCooldown(String name) {
        switch (name) {
            case USE_ON_ANIMAL_OPERATION:
                return AdvancedPeripheralsConfig.useOnAnimalCooldown;
            case CAPTURE_ANIMAL_OPERATION:
                return AdvancedPeripheralsConfig.captureAnimalCooldown;
            default:
                return super.getRawCooldown(name);
        }
    }

    @Override
    protected int getMaxFuelConsumptionRate() {
        return AdvancedPeripheralsConfig.husbandryAutomataCoreMaxFuelConsumptionLevel;
    }

    protected boolean isEntityInside() {
        return !owner.getDataStorage().getCompound(ENTITY_NBT_KEY).isEmpty();
    }

    protected void saveEntity(CompoundNBT data) {
        owner.getDataStorage().put(ENTITY_NBT_KEY, data);
    }

    protected CompoundNBT getEntity() {
        return owner.getDataStorage().getCompound(ENTITY_NBT_KEY);
    }

    protected void removeEntity() {
        owner.getDataStorage().remove(ENTITY_NBT_KEY);
    }

    protected @Nullable
    Entity extractEntity() {
        CompoundNBT data = getEntity();
        EntityType<?> type = EntityType.byString(data.getString("entity")).orElse(null);
        if (type != null) {
            Entity entity = type.create(getWorld());
            if (entity == null)
                return null;
            entity.load(data);
            return entity;
        }
        return null;
    }

    @Override
    public Map<String, Object> getPeripheralConfiguration() {
        Map<String, Object> result = super.getPeripheralConfiguration();
        result.put("useOnAnimalCost", AdvancedPeripheralsConfig.useOnAnimalCost);
        result.put("useOnAnimalCooldown", AdvancedPeripheralsConfig.useOnAnimalCooldown);
        result.put("captureAnimalCost", AdvancedPeripheralsConfig.captureAnimalCost);
        result.put("captureAnimalCooldown", AdvancedPeripheralsConfig.captureAnimalCooldown);
        return result;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult useOnAnimal() {
        Optional<MethodResult> checkResults = cooldownCheck(USE_ON_ANIMAL_OPERATION);
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = consumeFuelOp(AdvancedPeripheralsConfig.useOnAnimalCost);
        if (checkResults.isPresent()) return checkResults.map(this::fuelErrorCallback).get();
        addRotationCycle();
        ItemStack selectedTool = owner.getToolInMainHand();
        int previousDamageValue = selectedTool.getDamageValue();
        ActionResultType result = owner.withPlayer(player -> player.useOnFilteredEntity(suitableEntity));
        if (restoreToolDurability())
            selectedTool.setDamageValue(previousDamageValue);
        trackOperation(USE_ON_ANIMAL_OPERATION);
        return MethodResult.of(true, result.toString());
    }

    @LuaFunction
    public final MethodResult inspectAnimal() {
        addRotationCycle();
        RayTraceResult entityHit = owner.withPlayer(player -> player.findHit(false, true, suitableEntity));
        if (entityHit.getType() == RayTraceResult.Type.MISS)
            return MethodResult.of(null, "Nothing found");
        Entity entity = ((EntityRayTraceResult) entityHit).getEntity();
        if (!(entity instanceof AnimalEntity))
            return MethodResult.of(null, "Well, entity is not animal entity, but how?");
        return MethodResult.of(RepresentationUtil.animalToLua((AnimalEntity) entity, owner.getToolInMainHand()));
    }

    @LuaFunction
    public final MethodResult searchAnimals() {
        addRotationCycle();
        BlockPos currentPos = getPos();
        AxisAlignedBB box = new AxisAlignedBB(currentPos);
        List<Map<String, Object>> entities = new ArrayList<>();
        ItemStack itemInHand = owner.getToolInMainHand();
        getWorld().getEntities((Entity) null, box.inflate(getInteractionRadius()), suitableEntity).forEach(entity -> entities.add(RepresentationUtil.completeEntityWithPositionToLua(entity, itemInHand, currentPos)));
        return MethodResult.of(entities);
    }

    @LuaFunction
    public final MethodResult captureAnimal() {
        Optional<MethodResult> checkResults = cooldownCheck(CAPTURE_ANIMAL_OPERATION);
        if (checkResults.isPresent()) return checkResults.get();
        if (isEntityInside())
            return MethodResult.of(null, "Another entity already captured");
        addRotationCycle();
        RayTraceResult entityHit = owner.withPlayer(player -> player.findHit(false, true, suitableEntity));
        if (entityHit.getType() == RayTraceResult.Type.MISS)
            return MethodResult.of(null, "Nothing found");
        checkResults = consumeFuelOp(AdvancedPeripheralsConfig.captureAnimalCost);
        if (checkResults.isPresent()) return checkResults.map(this::fuelErrorCallback).get();
        LivingEntity entity = (LivingEntity) ((EntityRayTraceResult) entityHit).getEntity();
        if (entity instanceof PlayerEntity || !entity.isAlive()) return MethodResult.of(null, "Unsuitable entity");
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("entity", EntityType.getKey(entity.getType()).toString());
        entity.saveWithoutId(nbt);
        entity.remove();
        trackOperation(CAPTURE_ANIMAL_OPERATION);
        saveEntity(nbt);
        return MethodResult.of(true);
    }

    @LuaFunction
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
        getWorld().addFreshEntity(extractedEntity);
        return MethodResult.of(true);
    }

    @LuaFunction
    public final MethodResult getCapturedAnimal() {
        Entity extractedEntity = extractEntity();
        return MethodResult.of(RepresentationUtil.completeEntityToLua(extractedEntity, owner.getToolInMainHand()));
    }
}
