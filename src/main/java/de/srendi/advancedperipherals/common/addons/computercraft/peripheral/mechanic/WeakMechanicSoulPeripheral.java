package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.mechanic;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.util.InventoryUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.base.OperationPeripheral;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.items.WeakMechanicSoul;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.fakeplayer.FakePlayerProviderTurtle;
import de.srendi.advancedperipherals.common.util.fakeplayer.TurtleFakePlayer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WeakMechanicSoulPeripheral extends OperationPeripheral {

    protected static final String DIG_OPERATION = "dig";
    protected static final String CLICK_OPERATION = "click";
    protected static final String SUCK_OPERATION = "suck";

    public WeakMechanicSoulPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
    }

    @Override
    protected int getRawCooldown(String name) {
        switch (name) {
            case DIG_OPERATION: return AdvancedPeripheralsConfig.digBlockCooldown;
            case CLICK_OPERATION: return AdvancedPeripheralsConfig.clickBlockCooldown;
            case SUCK_OPERATION: return AdvancedPeripheralsConfig.suckItemCooldown;
        }
        throw new IllegalArgumentException(String.format("Cannot find cooldown for op %s", name));
    }

    @Override
    protected int getMaxFuelConsumptionRate() {
        return AdvancedPeripheralsConfig.weakMechanicSoulMaxFuelConsumptionLevel;
    }

    public int getItemSuckRadius() {
        return AdvancedPeripheralsConfig.weakMechanicSoulSuckRange;
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableWeakMechanicSoul;
    }

    protected @Nonnull
    MethodResult fuelErrorCallback(@Nonnull IComputerAccess access, MethodResult fuelErrorResult) {
        return fuelErrorResult;
    }

    protected Optional<MethodResult> turtleChecks() {
        if (turtle == null) {
            return Optional.of(MethodResult.of(null, "Well, you can use it only from turtle now!"));
        }
        if (turtle.getOwningPlayer() == null) {
            return Optional.of(MethodResult.of(null, "Well, turtle should have owned player!"));
        }
        MinecraftServer server = getWorld().getServer();
        if (server == null) {
            return Optional.of(MethodResult.of(null, "Problem with server finding ..."));
        }
        return Optional.empty();
    }

    protected Pair<MethodResult, TurtleSide> getTurtleSide(@Nonnull IComputerAccess access) {
        TurtleSide side;
        try {
            side = TurtleSide.valueOf(access.getAttachmentName().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Pair.onlyLeft(MethodResult.of(null, e.getMessage()));
        }
        return Pair.onlyRight(side);
    }

    protected AxisAlignedBB getBox(BlockPos pos, int radius) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        return new AxisAlignedBB(
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius
        );
    }

    protected List<ItemEntity> getItems() {
        return getWorld().getEntitiesOfClass(ItemEntity.class, getBox(getPos(), getItemSuckRadius()));
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

        ItemStack remainder = InventoryUtil.storeItems(storeStack, turtle.getItemHandler(), turtle.getSelectedSlot());

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

    @LuaFunction
    public Map<String, Object> getConfiguration() {
        Map<String, Object> result = super.getConfiguration();
        result.put("digCost", AdvancedPeripheralsConfig.digBlockCost);
        result.put("digCooldown", AdvancedPeripheralsConfig.digBlockCooldown);
        result.put("clickCost", AdvancedPeripheralsConfig.clickBlockCost);
        result.put("clickCooldown", AdvancedPeripheralsConfig.clickBlockCooldown);
        result.put("suckCost", AdvancedPeripheralsConfig.suckItemCost);
        result.put("suckCooldown", AdvancedPeripheralsConfig.suckItemCooldown);
        result.put("fuelConsumptionRate", getFuelConsumptionRate());
        result.put("maxFuelConsumptionRate", getMaxFuelConsumptionRate());
        result.put("suckRadius", getItemSuckRadius());
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
    public int getClickCooldown() {
        return getCurrentCooldown(CLICK_OPERATION);
    }

    @LuaFunction
    public final MethodResult lookAtBlock() {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();

        RayTraceResult result = FakePlayerProviderTurtle.withPlayer(turtle, turtleFakePlayer -> turtleFakePlayer.findHit(true, false));
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
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();

        RayTraceResult result = FakePlayerProviderTurtle.withPlayer(turtle, turtleFakePlayer -> turtleFakePlayer.findHit(false, true));
        if (result.getType() == RayTraceResult.Type.MISS) {
            return MethodResult.of(null, "No entity find");
        }
        EntityRayTraceResult entityHit = (EntityRayTraceResult) result;
        Map<String, Object> data = new HashMap<>();
        Entity entity = entityHit.getEntity();
        data.put("entity_id", entity.getId());
        data.put("name", entity.getName().getString());
        data.put("tags", entity.getTags());
        return MethodResult.of(data);
    }

    @LuaFunction(mainThread = true)
    public MethodResult digBlock(@Nonnull IComputerAccess access) {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = cooldownCheck(DIG_OPERATION);
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = consumeFuelOp(AdvancedPeripheralsConfig.digBlockCost);
        if (checkResults.isPresent()) return checkResults.map(result -> fuelErrorCallback(access, result)).get();

        Pair<Boolean, String> result = FakePlayerProviderTurtle.withPlayer(turtle, turtleFakePlayer -> turtleFakePlayer.digBlock(turtle.getDirection().getOpposite()));
        if (!result.getLeft()) {
            return MethodResult.of(null, result.getRight());
        }

        trackOperation(DIG_OPERATION);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult clickBlock(@Nonnull IComputerAccess access) {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = cooldownCheck(CLICK_OPERATION);
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = consumeFuelOp(AdvancedPeripheralsConfig.clickBlockCost);
        if (checkResults.isPresent()) return checkResults.map(result -> fuelErrorCallback(access, result)).get();

        ActionResultType result = FakePlayerProviderTurtle.withPlayer(turtle, TurtleFakePlayer::useOnBlock);
        trackOperation(CLICK_OPERATION);
        return MethodResult.of(true, result.toString());
    }

    @LuaFunction
    public final MethodResult scanItems() {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
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
    public MethodResult collectSpecificItem(@Nonnull IComputerAccess access, @Nonnull IArguments arguments) throws LuaException {
        String technicalName = arguments.getString(0);
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = cooldownCheck(SUCK_OPERATION);
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = consumeFuelOp(AdvancedPeripheralsConfig.suckItemCost);
        if (checkResults.isPresent()) return checkResults.map(result -> fuelErrorCallback(access, result)).get();

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
    public MethodResult collectItems(@Nonnull IComputerAccess access, @Nonnull IArguments arguments) throws LuaException {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = cooldownCheck(SUCK_OPERATION);
        if (checkResults.isPresent()) return checkResults.get();
        int requiredQuantity = arguments.optInt(0, Integer.MAX_VALUE);

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
                return fuelErrorCallback(access, MethodResult.of(null, "Not enough fuel to continue sucking"));
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
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        if (!(turtle.getInventory().getItem(turtle.getSelectedSlot()).getItem() instanceof WeakMechanicSoul)) {
            return MethodResult.of(null, "Well, you should feed weak mechanical soul!");
        }
        ActionResultType result = FakePlayerProviderTurtle.withPlayer(turtle, TurtleFakePlayer::useOnEntity);
        return MethodResult.of(true, result.toString());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult chargeTurtle(@Nonnull IArguments arguments) throws LuaException {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        ItemStack stack = turtle.getInventory().getItem(turtle.getSelectedSlot());
        int fuel = arguments.optInt(0, -1);
        return stack.getCapability(CapabilityEnergy.ENERGY).map(storage -> {
            int availableFuelSpace = turtle.getFuelLimit() - turtle.getFuelLevel();
            int requestedRF;
            if (fuel != -1) {
                requestedRF = fuel * AdvancedPeripheralsConfig.energyToFuelRate;
            } else {
                requestedRF = storage.getEnergyStored();
            }
            int realConsumedRF = storage.extractEnergy(Math.min(requestedRF, availableFuelSpace * AdvancedPeripheralsConfig.energyToFuelRate), false);
            int receivedFuel = realConsumedRF / AdvancedPeripheralsConfig.energyToFuelRate;
            turtle.addFuel(receivedFuel);
            return MethodResult.of(true, receivedFuel);
        }).orElse(MethodResult.of(null, "Item should provide energy ..."));
    }

}