package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.MechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.items.WeakMechanicSoul;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.RepresentationUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WeakMechanicSoulPeripheral extends MechanicSoulPeripheral {

    protected static final String DIG_OPERATION = "dig";
    protected static final String USE_ON_BLOCK_OPERATION = "useOnBlock";
    protected static final String SUCK_OPERATION = "suck";
    protected static final String FUEL_CONSUMING_RATE_SETTING = "FUEL_CONSUMING_RATE";
    protected static final int DEFAULT_FUEL_CONSUMING_RATE = 1;

    public WeakMechanicSoulPeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    @Override
    protected int getRawCooldown(String name) {
        switch (name) {
            case DIG_OPERATION: return AdvancedPeripheralsConfig.digBlockCooldown;
            case USE_ON_BLOCK_OPERATION: return AdvancedPeripheralsConfig.useOnBlockCooldown;
            case SUCK_OPERATION: return AdvancedPeripheralsConfig.suckItemCooldown;
        }
        throw new IllegalArgumentException(String.format("Cannot find cooldown for op %s", name));
    }

    @Override
    protected int getMaxFuelConsumptionRate() {
        return AdvancedPeripheralsConfig.weakMechanicSoulMaxFuelConsumptionLevel;
    }

    protected boolean restoreToolDurability() {
        return false;
    }

    @Override
    protected int _getFuelConsumptionRate() {
        CompoundNBT settings = owner.getSettings();
        int rate = settings.getInt(FUEL_CONSUMING_RATE_SETTING);
        if (rate == 0) {
            _setFuelConsumptionRate(DEFAULT_FUEL_CONSUMING_RATE);
            return DEFAULT_FUEL_CONSUMING_RATE;
        }
        return rate;
    }

    @Override
    protected void _setFuelConsumptionRate(int rate) {
        owner.getSettings().putInt(FUEL_CONSUMING_RATE_SETTING, rate);
    }

    public int getInteractionRadius() {
        return AdvancedPeripheralsConfig.weakMechanicSoulInteractionRadius;
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableWeakMechanicSoul;
    }

    protected @Nonnull MethodResult fuelErrorCallback(MethodResult fuelErrorResult) {
        return fuelErrorResult;
    }

    protected List<ItemEntity> getItems() {
        return getWorld().getEntitiesOfClass(ItemEntity.class, getBox(getPos(), getInteractionRadius()));
    }

    protected int suckItem(ItemEntity entity, int requiredQuantity) {
        ItemStack stack = entity.getItem().copy();

        ItemStack storeStack;
        ItemStack leaveStack;
        if (stack.getCount() > requiredQuantity) {
            storeStack = stack.split(requiredQuantity);
            leaveStack = stack;
        } else {
            storeStack = stack;
            leaveStack = ItemStack.EMPTY;
        }

        ItemStack remainder = owner.storeItem(storeStack);

        if (remainder != storeStack) {
            if (remainder.isEmpty() && leaveStack.isEmpty()) {
                entity.remove();
            } else if (remainder.isEmpty()) {
                entity.setItem(leaveStack);
            } else if (leaveStack.isEmpty()) {
                entity.setItem(remainder);
            } else {
                leaveStack.grow(remainder.getCount());
                entity.setItem(leaveStack);
            }
        }
        requiredQuantity -= storeStack.getCount();
        return requiredQuantity;
    }

    @Override
    public Map<String, Object> getPeripheralConfiguration() {
        Map<String, Object> result = super.getPeripheralConfiguration();
        result.put("digCost", AdvancedPeripheralsConfig.digBlockCost);
        result.put("digCooldown", AdvancedPeripheralsConfig.digBlockCooldown);
        result.put("useOnBlockCost", AdvancedPeripheralsConfig.clickBlockCost);
        result.put("useOnBlockCooldown", AdvancedPeripheralsConfig.useOnBlockCooldown);
        result.put("suckCost", AdvancedPeripheralsConfig.suckItemCost);
        result.put("suckCooldown", AdvancedPeripheralsConfig.suckItemCooldown);
        result.put("suckRadius", getInteractionRadius());
        return result;
    }

    @LuaFunction
    public int getSuckCooldown() {
        return getCurrentCooldown(SUCK_OPERATION);
    }

    @LuaFunction
    public int getDigCooldown() {
        return getCurrentCooldown(DIG_OPERATION);
    }

    @LuaFunction
    public int getUseOnBlockCooldown() {
        return getCurrentCooldown(USE_ON_BLOCK_OPERATION);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult lookAtBlock() {
        addRotationCycle();
        RayTraceResult result = owner.withPlayer(APFakePlayer -> APFakePlayer.findHit(true, false));
        if (result.getType() == RayTraceResult.Type.MISS) {
            return MethodResult.of(null, "No block find");
        }
        BlockRayTraceResult blockHit = (BlockRayTraceResult) result;
        BlockState state = getWorld().getBlockState(blockHit.getBlockPos());
        Map<String, Object> data = new HashMap<>();
        ResourceLocation blockName = state.getBlock().getRegistryName();
        if (blockName != null)
            data.put("name", blockName.toString());
        data.put("tags", state.getBlock().getTags());
        return MethodResult.of(data);
    }

    @LuaFunction
    public final MethodResult lookAtEntity() {
        addRotationCycle();
        RayTraceResult result = owner.withPlayer(APFakePlayer -> APFakePlayer.findHit(false, true));
        if (result.getType() == RayTraceResult.Type.MISS) {
            return MethodResult.of(null, "No entity find");
        }
        EntityRayTraceResult entityHit = (EntityRayTraceResult) result;
        return MethodResult.of(RepresentationUtil.entityToLua(entityHit.getEntity()));
    }

    @LuaFunction(mainThread = true)
    public MethodResult digBlock() {
        Optional<MethodResult>  checkResults = cooldownCheck(DIG_OPERATION);
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = consumeFuelOp(AdvancedPeripheralsConfig.digBlockCost);
        if (checkResults.isPresent()) return checkResults.map(this::fuelErrorCallback).get();
        addRotationCycle();
        ItemStack selectedTool = owner.getToolInMainHand();
        int previousDamageValue = selectedTool.getDamageValue();
        Pair<Boolean, String> result = owner.withPlayer(APFakePlayer -> APFakePlayer.digBlock(owner.getFacing().getOpposite()));
        if (!result.getLeft()) {
            return MethodResult.of(null, result.getRight());
        }
        if (restoreToolDurability())
            selectedTool.setDamageValue(previousDamageValue);
        trackOperation(DIG_OPERATION);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult useOnBlock() {
        Optional<MethodResult> checkResults = cooldownCheck(USE_ON_BLOCK_OPERATION);
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = consumeFuelOp(AdvancedPeripheralsConfig.clickBlockCost);
        if (checkResults.isPresent()) return checkResults.map(this::fuelErrorCallback).get();
        addRotationCycle();
        ItemStack selectedTool = owner.getToolInMainHand();
        int previousDamageValue = selectedTool.getDamageValue();
        ActionResultType result = owner.withPlayer(APFakePlayer::useOnBlock);
        if (restoreToolDurability())
            selectedTool.setDamageValue(previousDamageValue);
        trackOperation(USE_ON_BLOCK_OPERATION);
        return MethodResult.of(true, result.toString());
    }

    @LuaFunction
    public final MethodResult scanItems() {
        addRotationCycle();
        List<ItemEntity> items = getItems();
        Map<Integer, Map<String, Object>> data = new HashMap<>();
        int index = 1;
        for (ItemEntity item : items) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("entity_id", item.getId());
            itemData.put("name", item.getItem().getDisplayName().getString());
            ResourceLocation itemName = item.getItem().getItem().getRegistryName();
            if (itemName != null)
                itemData.put("technicalName", itemName.toString());
            itemData.put("count", item.getItem().getCount());
            itemData.put("tags", item.getItem().getItem().getTags().stream().map(ResourceLocation::toString).collect(Collectors.toList()));
            data.put(index, itemData);
            index++;
        }
        return MethodResult.of(data);
    }

    @LuaFunction(mainThread = true)
    public MethodResult collectSpecificItem(@Nonnull IArguments arguments) throws LuaException {
        String technicalName = arguments.getString(0);
        Optional<MethodResult> checkResults = cooldownCheck(SUCK_OPERATION);
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = consumeFuelOp(AdvancedPeripheralsConfig.suckItemCost);
        if (checkResults.isPresent()) return checkResults.map(this::fuelErrorCallback).get();
        addRotationCycle();
        int requiredQuantity = arguments.optInt(1, Integer.MAX_VALUE);

        List<ItemEntity> items = getItems();

        for (ItemEntity item : items) {
            ResourceLocation itemName = item.getItem().getItem().getRegistryName();
            if (itemName == null) continue;
            if (itemName.toString().equals(technicalName)) {
                requiredQuantity -= suckItem(item, requiredQuantity);
            }
        }
        trackOperation(SUCK_OPERATION);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public MethodResult collectItems(@Nonnull IArguments arguments) throws LuaException {
        Optional<MethodResult> checkResults = cooldownCheck(SUCK_OPERATION);
        if (checkResults.isPresent()) return checkResults.get();
        int requiredQuantity = arguments.optInt(0, Integer.MAX_VALUE);
        addRotationCycle();
        if (requiredQuantity == 0) {
            return MethodResult.of(true);
        }

        List<ItemEntity> items = getItems();
        if (items.isEmpty()) {
            return MethodResult.of(null, "Nothing to take");
        }

        for (ItemEntity entity : items) {
            int consumedCount = Math.min(entity.getItem().getCount(), requiredQuantity);
            if (!consumeFuel(consumedCount * AdvancedPeripheralsConfig.suckItemCost))
                return fuelErrorCallback(MethodResult.of(null, "Not enough fuel to continue sucking"));
            requiredQuantity -= suckItem(entity, requiredQuantity);
            if (requiredQuantity <= 0) {
                break;
            }
        }
        trackOperation(SUCK_OPERATION);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult feedSoul() {
        if (!(owner.getToolInMainHand().getItem() instanceof WeakMechanicSoul)) {
            return MethodResult.of(null, "Well, you should feed weak mechanical soul!");
        }
        ActionResultType result = owner.withPlayer(APFakePlayer::useOnEntity);
        addRotationCycle(3);
        return MethodResult.of(true, result.toString());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult chargeTurtle(@Nonnull IArguments arguments) throws LuaException {
        if (fuelConsumptionDisabled())
            return MethodResult.of(null, "Fuel consumption is disabled, why do you even need this?");
        ItemStack stack = owner.getToolInMainHand();
        int fuel = arguments.optInt(0, -1);
        return stack.getCapability(CapabilityEnergy.ENERGY).map(storage -> {
            int availableFuelSpace = owner.getFuelMaxCount() - owner.getFuelCount();
            int requestedRF;
            if (fuel != -1) {
                requestedRF = fuel * AdvancedPeripheralsConfig.energyToFuelRate;
            } else {
                requestedRF = storage.getEnergyStored();
            }
            int realConsumedRF = storage.extractEnergy(Math.min(requestedRF, availableFuelSpace * AdvancedPeripheralsConfig.energyToFuelRate), false);
            int receivedFuel = realConsumedRF / AdvancedPeripheralsConfig.energyToFuelRate;
            owner.addFuel(receivedFuel);
            addRotationCycle();
            return MethodResult.of(true, receivedFuel);
        }).orElse(MethodResult.of(null, "Item should provide energy ..."));
    }

}
