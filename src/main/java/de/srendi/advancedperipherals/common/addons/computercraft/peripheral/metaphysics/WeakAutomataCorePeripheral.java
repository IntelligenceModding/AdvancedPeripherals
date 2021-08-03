package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.IAutomataCoreTier;
import de.srendi.advancedperipherals.common.addons.computercraft.base.IFeedableAutomataCore;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCorePeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCoreTier;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.IPeripheralOperation;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.*;

public class WeakAutomataCorePeripheral extends AutomataCorePeripheral {

    public WeakAutomataCorePeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    @Override
    public IAutomataCoreTier getTier() {
        return AutomataCoreTier.TIER1;
    }

    protected boolean restoreToolDurability() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableWeakAutomataCore;
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
                entity.remove(false);
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
    public List<IPeripheralOperation<?>> possibleOperations() {
        return new ArrayList<IPeripheralOperation<?>>() {{
            add(DIG);
            add(SUCK);
            add(USE_ON_BLOCK);
        }};
    }

    @LuaFunction(mainThread = true)
    public final MethodResult lookAtBlock() {
        addRotationCycle();
        HitResult result = owner.withPlayer(APFakePlayer -> APFakePlayer.findHit(true, false));
        if (result.getType() == HitResult.Type.MISS) {
            return MethodResult.of(null, "No block find");
        }
        BlockHitResult blockHit = (BlockHitResult) result;
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
        HitResult result = owner.withPlayer(APFakePlayer -> APFakePlayer.findHit(false, true));
        if (result.getType() == HitResult.Type.MISS) {
            return MethodResult.of(null, "No entity find");
        }
        EntityHitResult entityHit = (EntityHitResult) result;
        return MethodResult.of(LuaConverter.entityToLua(entityHit.getEntity()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult digBlock() {
        return withOperation(DIG, context -> {
            ItemStack selectedTool = owner.getToolInMainHand();
            int previousDamageValue = selectedTool.getDamageValue();
            Pair<Boolean, String> result = owner.withPlayer(APFakePlayer -> APFakePlayer.digBlock(owner.getFacing().getOpposite()));
            if (!result.getLeft()) {
                return MethodResult.of(null, result.getRight());
            }
            if (restoreToolDurability())
                selectedTool.setDamageValue(previousDamageValue);
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult useOnBlock() {
        return withOperation(USE_ON_BLOCK, context -> {
            ItemStack selectedTool = owner.getToolInMainHand();
            int previousDamageValue = selectedTool.getDamageValue();
            InteractionResult result = owner.withPlayer(APFakePlayer::useOnBlock);
            if (restoreToolDurability())
                selectedTool.setDamageValue(previousDamageValue);
            return MethodResult.of(true, result.toString());
        });
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
    public final MethodResult collectSpecificItem(@Nonnull IArguments arguments) throws LuaException {
        String technicalName = arguments.getString(0);
        int requiredQuantityArg = arguments.optInt(1, Integer.MAX_VALUE);
        return withOperation(SUCK, context -> {
            List<ItemEntity> items = getItems();
            int requiredQuantity = requiredQuantityArg;
            for (ItemEntity item : items) {
                ResourceLocation itemName = item.getItem().getItem().getRegistryName();
                if (itemName == null) continue;
                if (itemName.toString().equals(technicalName)) {
                    requiredQuantity -= suckItem(item, requiredQuantity);
                }
                if (requiredQuantity <= 0)
                    break;
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult collectItems(@Nonnull IArguments arguments) throws LuaException {
        int requiredQuantityArg = arguments.optInt(0, Integer.MAX_VALUE);
        return withOperation(SUCK, context -> {
            if (requiredQuantityArg == 0) {
                return MethodResult.of(true);
            }

            List<ItemEntity> items = getItems();
            if (items.isEmpty()) {
                return MethodResult.of(null, "Nothing to take");
            }
            int requiredQuantity = requiredQuantityArg;
            for (ItemEntity entity : items) {
                int consumedCount = Math.min(entity.getItem().getCount(), requiredQuantity);
                if (!consumeFuel(SUCK.getCost(context.extraCount(consumedCount))))
                    return fuelErrorCallback(MethodResult.of(null, "Not enough fuel to continue sucking"));
                requiredQuantity -= suckItem(entity, requiredQuantity);
                if (requiredQuantity <= 0) {
                    break;
                }
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult feedSoul() {
        if (!(owner.getToolInMainHand().getItem() instanceof IFeedableAutomataCore)) {
            return MethodResult.of(null, "Well, you should feed correct mechanical soul!");
        }
        InteractionResult result = owner.withPlayer(APFakePlayer::useOnEntity);
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
