package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.mechanic;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.util.InventoryUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.OperationPeripheral;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.items.WeakMechanicSoul;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.RepresentationUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.FakePlayerProviderTurtle;
import de.srendi.advancedperipherals.common.util.fakeplayer.TurtleFakePlayer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraftforge.energy.CapabilityEnergy;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WeakMechanicSoulPeripheral extends OperationPeripheral {

    protected static final String DIG_OPERATION = "dig";
    protected static final String USE_ON_BLOCK_OPERATION = "useOnBlock";
    protected static final String SUCK_OPERATION = "suck";
    protected static final String FUEL_CONSUMING_RATE_SETTING = "FUEL_CONSUMING_RATE";
    protected static final int DEFAULT_FUEL_CONSUMING_RATE = 1;

    public WeakMechanicSoulPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
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

    @Override
    protected int _getFuelConsumptionRate(@NotNull IComputerAccess access) {
        Pair<MethodResult, Integer> searchResult = getIntSetting(access, FUEL_CONSUMING_RATE_SETTING);
        if (searchResult.rightPresent()) {
            int rate = searchResult.getRight();
            if (rate == 0) {
                _setFuelConsumptionRate(access, DEFAULT_FUEL_CONSUMING_RATE);
                return DEFAULT_FUEL_CONSUMING_RATE;
            }
            return searchResult.getRight();
        }
        AdvancedPeripherals.LOGGER.error("Lost error: " + searchResult.getLeft().toString());
        return DEFAULT_FUEL_CONSUMING_RATE;
    }

    @Override
    protected void _setFuelConsumptionRate(@NotNull IComputerAccess access, int rate) {
        Pair<MethodResult, Boolean> setResult = setIntSetting(access, FUEL_CONSUMING_RATE_SETTING, rate);
        if (setResult.leftPresent())
            AdvancedPeripherals.LOGGER.error("Lost error: " + setResult.getLeft().toString());
    }

    public int getInteractionRadius() {
        return AdvancedPeripheralsConfig.weakMechanicSoulInteractionRadius;
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

    protected Pair<MethodResult, CompoundNBT> getSettings(@Nonnull IComputerAccess access) {
        return getTurtleSide(access).mapRight(side -> turtle.getUpgradeNBTData(side));
    }

    protected Pair<MethodResult, Boolean> setIntSetting(@Nonnull IComputerAccess access, String name, int value) {
        return getSettings(access).mapRight(data -> {
            data.putInt(name, value);
            return true;
        });
    }

    protected Pair<MethodResult, Integer> getIntSetting(@Nonnull IComputerAccess access, String name) {
        return getSettings(access).mapRight(data -> data.getInt(name));
    }

    protected Pair<MethodResult, CompoundNBT> getCompoundSetting(@Nonnull IComputerAccess access, String name) {
        return getSettings(access).mapRight(data -> data.getCompound(name));
    }

    protected Pair<MethodResult, Boolean> setCompoundSetting(@Nonnull IComputerAccess access, String name, CompoundNBT value) {
        return getSettings(access).mapRight(data -> {
            data.put(name, value);
            return true;
        });
    }

    protected Pair<MethodResult, Boolean> removeSetting(@Nonnull IComputerAccess access, String name) {
        return getSettings(access).mapRight(data -> {
            data.remove(name);
            return true;
        });
    }

    protected AxisAlignedBB getBox(BlockPos pos, int radius) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        return new AxisAlignedBB(
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius
        );
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
    public Map<String, Object> getConfiguration(@Nonnull IComputerAccess access) {
        Map<String, Object> result = super.getConfiguration();
        result.put("digCost", AdvancedPeripheralsConfig.digBlockCost);
        result.put("digCooldown", AdvancedPeripheralsConfig.digBlockCooldown);
        result.put("useOnBlockCost", AdvancedPeripheralsConfig.clickBlockCost);
        result.put("useOnBlockCooldown", AdvancedPeripheralsConfig.useOnBlockCooldown);
        result.put("suckCost", AdvancedPeripheralsConfig.suckItemCost);
        result.put("suckCooldown", AdvancedPeripheralsConfig.suckItemCooldown);
        result.put("fuelConsumptionRate", getFuelConsumptionRate(access));
        result.put("maxFuelConsumptionRate", getMaxFuelConsumptionRate());
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
        return MethodResult.of(RepresentationUtil.entityToLua(entityHit.getEntity()));
    }

    @LuaFunction(mainThread = true)
    public MethodResult digBlock(@Nonnull IComputerAccess access) {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = cooldownCheck(DIG_OPERATION);
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = consumeFuelOp(access, AdvancedPeripheralsConfig.digBlockCost);
        if (checkResults.isPresent()) return checkResults.map(result -> fuelErrorCallback(access, result)).get();

        Pair<Boolean, String> result = FakePlayerProviderTurtle.withPlayer(turtle, turtleFakePlayer -> turtleFakePlayer.digBlock(turtle.getDirection().getOpposite()));
        if (!result.getLeft()) {
            return MethodResult.of(null, result.getRight());
        }

        trackOperation(access, DIG_OPERATION);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult useOnBlock(@Nonnull IComputerAccess access) {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = cooldownCheck(USE_ON_BLOCK_OPERATION);
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = consumeFuelOp(access, AdvancedPeripheralsConfig.clickBlockCost);
        if (checkResults.isPresent()) return checkResults.map(result -> fuelErrorCallback(access, result)).get();

        ActionResultType result = FakePlayerProviderTurtle.withPlayer(turtle, TurtleFakePlayer::useOnBlock);
        trackOperation(access, USE_ON_BLOCK_OPERATION);
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
        checkResults = consumeFuelOp(access, AdvancedPeripheralsConfig.suckItemCost);
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

        trackOperation(access, SUCK_OPERATION);
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
            if (!consumeFuel(access, consumedCount * AdvancedPeripheralsConfig.suckItemCost))
                return fuelErrorCallback(access, MethodResult.of(null, "Not enough fuel to continue sucking"));
            requiredQuantity -= suckItem(entity, requiredQuantity);
            if (requiredQuantity <= 0) {
                break;
            }
        }

        trackOperation(access, SUCK_OPERATION);
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
        if (fuelConsumptionDisabled())
            return MethodResult.of(null, "Fuel consumption is disabled, why do you even need this?");
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
