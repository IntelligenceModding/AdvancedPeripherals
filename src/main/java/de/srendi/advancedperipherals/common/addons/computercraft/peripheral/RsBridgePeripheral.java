package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.Action;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorage;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorageNode;
import de.srendi.advancedperipherals.common.blocks.blockentities.RsBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RsBridgePeripheral extends BasePeripheral<BlockEntityPeripheralOwner<RsBridgeEntity>> {
    public static final String TYPE = "rsBridge";

    public RsBridgePeripheral(RsBridgeEntity tileEntity) {
        super(TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    private RefinedStorageNode getNode() {return owner.tileEntity.getNode();}

    private INetwork getNetwork() {return getNode().getNetwork();}

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.ENABLE_RS_BRIDGE.get();
    }

    @LuaFunction(mainThread = true)
    public final Object listItems() {return RefinedStorage.listItems(getNetwork());}

    @LuaFunction(mainThread = true)
    public final Object listCraftableItems() {
        List<Object> items = new ArrayList<>();
        RefinedStorage.getCraftableItems(getNetwork()).forEach(item -> items.add(RefinedStorage.getObjectFromStack(item, getNetwork())));
        return items;
    }

    @LuaFunction(mainThread = true)
    public final Object listCraftableFluids() {
        List<Object> fluids = new ArrayList<>();
        RefinedStorage.getCraftableFluids(getNetwork()).forEach(fluid -> fluids.add(RefinedStorage.getObjectFromFluid(fluid, getNetwork())));
        return fluids;
    }

    @LuaFunction(mainThread = true)
    public final List<Map<String, Object>> getAllDisks() {
        return RefinedStorage.getDisks(getNetwork());
    }

    @LuaFunction(mainThread = true)
    public final Integer getMaxItemDiskStorage() {
        return RefinedStorage.getMaxItemDiskStorage(getNetwork());
    }

    @LuaFunction(mainThread = true)
    public final Integer getUsedItemDiskStorage() {
        return RefinedStorage.getUsedItemDiskStorage(getNetwork());
    }

    @LuaFunction(mainThread = true)
    public final Integer getMaxFluidDiskStorage() {
        return RefinedStorage.getMaxFluidDiskStorage(getNetwork());
    }

    @LuaFunction(mainThread = true)
    public final Integer getUsedFluidDiskStorage() {
        return RefinedStorage.getUsedFluidDiskStorage(getNetwork());
    }

    @LuaFunction(mainThread = true)
    public final Integer getMaxItemExternalStorage() {
        return RefinedStorage.getMaxItemExternalStorage(getNetwork());
    }

    @LuaFunction(mainThread = true)
    public final Integer getUsedItemExternalStorage() {
        return RefinedStorage.getUsedItemExternalStorage(getNetwork());
    }

    @LuaFunction(mainThread = true)
    public final Integer getMaxFluidExternalStorage() {
        return RefinedStorage.getMaxFluidExternalStorage(getNetwork());
    }

    @LuaFunction(mainThread = true)
    public final Integer getUsedFluidExternalStorage() {
        return RefinedStorage.getUsedFluidExternalStorage(getNetwork());
    }

    @LuaFunction(mainThread = true)
    public final Object listFluids() {
        return RefinedStorage.listFluids(getNetwork());
    }

    @LuaFunction(mainThread = true)
    public final int getEnergyUsage() {
        return getNetwork().getEnergyUsage();
    }

    @LuaFunction(mainThread = true)
    public final int getMaxEnergyStorage() {
        return getNetwork().getEnergyStorage().getMaxEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final int getEnergyStorage() {
        return getNetwork().getEnergyStorage().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final Object getPattern(IArguments arguments) throws LuaException {
        return RefinedStorage.getObjectFromPattern(getNetwork().getCraftingManager().getPattern(ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()))), getNetwork());
    }

    @LuaFunction(mainThread = true)
    public final int exportItem(IArguments arguments) throws LuaException {
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()));
        Direction direction = validateSide(arguments.getString(1));

        BlockEntity targetEntity = owner.tileEntity.getLevel().getBlockEntity(owner.tileEntity.getBlockPos().relative(direction));
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).resolve().orElse(null) : null;
        if (inventory == null) throw new LuaException("No valid inventory at " + arguments.getString(1));

        ItemStack extracted = getNetwork().extractItem(stack, stack.getCount(), 1, Action.SIMULATE);
        if (extracted.isEmpty()) return 0;
        //throw new LuaException("Item " + item + " does not exists in the RS system or the system is offline");

        int transferableAmount = extracted.getCount();

        ItemStack remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, true);
        if (!remaining.isEmpty()) transferableAmount -= remaining.getCount();

        extracted = getNetwork().extractItem(stack, transferableAmount, 1, Action.PERFORM);
        remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, false);

        if (!remaining.isEmpty()) {
            getNetwork().insertItem(remaining, remaining.getCount(), Action.PERFORM);
        }

        return transferableAmount;
    }

    @LuaFunction(mainThread = true)
    public final int importItem(IArguments arguments) throws LuaException {
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()));
        Direction direction = validateSide(arguments.getString(1));

        BlockEntity targetEntity = owner.tileEntity.getLevel().getBlockEntity(owner.tileEntity.getBlockPos().relative(direction));
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).resolve().orElse(null) : null;
        if (inventory == null) throw new LuaException("No valid inventory at " + arguments.getString(1));

        int amount = stack.getCount();
        int transferableAmount = 0;

        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.getStackInSlot(i).sameItem(stack)) {
                if (inventory.getStackInSlot(i).getCount() >= amount) {
                    ItemStack insertedStack = getNetwork().insertItem(stack, amount, Action.PERFORM);
                    inventory.extractItem(i, amount - insertedStack.getCount(), false);
                    transferableAmount += amount - insertedStack.getCount();
                    break;
                } else {
                    amount -= inventory.getStackInSlot(i).getCount();
                    ItemStack insertedStack = getNetwork().insertItem(stack, inventory.getStackInSlot(i).getCount(), Action.PERFORM);
                    inventory.extractItem(i, inventory.getStackInSlot(i).getCount() - insertedStack.getCount(), false);
                    transferableAmount += inventory.getStackInSlot(i).getCount() - insertedStack.getCount();
                }
            }
        }
        return transferableAmount;
    }

    @LuaFunction(mainThread = true)
    public final int exportItemToPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()));
        IPeripheral chest = computer.getAvailablePeripheral(arguments.getString(1));
        if (chest == null) throw new LuaException("No valid inventory block for " + arguments.getString(1));

        BlockEntity targetEntity = (BlockEntity) chest.getTarget();
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().orElse(null) : null;
        if (inventory == null) throw new LuaException("No valid inventory for " + arguments.getString(1));

        ItemStack extracted = getNetwork().extractItem(stack, stack.getCount(), 1, Action.SIMULATE);
        if (extracted.isEmpty()) return 0;
        //throw new LuaException("Item " + item + " does not exists in the RS system or the system is offline");

        int transferableAmount = extracted.getCount();

        ItemStack remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, true);
        if (!remaining.isEmpty()) transferableAmount -= remaining.getCount();

        extracted = getNetwork().extractItem(stack, transferableAmount, 1, Action.PERFORM);
        remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, false);

        if (!remaining.isEmpty()) {
            getNetwork().insertItem(remaining, remaining.getCount(), Action.PERFORM);
        }

        return transferableAmount;
    }

    @LuaFunction(mainThread = true)
    public final int importItemFromPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()));
        IPeripheral chest = computer.getAvailablePeripheral(arguments.getString(1));
        int count = stack.getCount();
        if (chest == null) throw new LuaException("No inventory block for " + arguments.getString(1));

        BlockEntity targetEntity = (BlockEntity) chest.getTarget();
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().orElse(null) : null;
        if (inventory == null) throw new LuaException("No valid inventory for " + arguments.getString(1));

        int amount = count;

        int transferableAmount = 0;

        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.getStackInSlot(i).sameItem(stack)) {
                if (inventory.getStackInSlot(i).getCount() >= amount) {
                    ItemStack insertedStack = getNetwork().insertItem(stack, amount, Action.PERFORM);
                    inventory.extractItem(i, amount - insertedStack.getCount(), false);
                    transferableAmount += amount - insertedStack.getCount();
                    break;
                } else {
                    amount = count - inventory.getStackInSlot(i).getCount();
                    ItemStack insertedStack = getNetwork().insertItem(stack, inventory.getStackInSlot(i).getCount(), Action.PERFORM);
                    inventory.extractItem(i, inventory.getStackInSlot(i).getCount() - insertedStack.getCount(), false);
                    transferableAmount += inventory.getStackInSlot(i).getCount() - insertedStack.getCount();
                }
            }
        }
        return transferableAmount;
    }

    @LuaFunction(mainThread = true)
    public final Object getItem(IArguments arguments) throws LuaException {
        return RefinedStorage.getItem(getNetwork(), ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork())));
    }

    @LuaFunction(mainThread = true)
    public final boolean craftItem(IArguments arguments) throws LuaException {
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()));
        if (stack == null) throw new LuaException("The item " + arguments.getTable(0).get("name") + "is not craftable");
        ICalculationResult result = getNetwork().getCraftingManager().create(stack, stack.getCount());
        CalculationResultType type = result.getType();
        if (result.getType() == CalculationResultType.OK) getNetwork().getCraftingManager().start(result.getTask());
        return type == CalculationResultType.OK;
    }

    @LuaFunction(mainThread = true)
    public final boolean craftFluid(String fluid, int count) {
        ICalculationResult result = getNetwork().getCraftingManager().create(new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluid)), 0), count);
        CalculationResultType type = result.getType();
        if (result.getType() == CalculationResultType.OK) getNetwork().getCraftingManager().start(result.getTask());
        return type == CalculationResultType.OK;
    }

    @LuaFunction(mainThread = true)
    public final boolean isItemCrafting(String item) {
        ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item)));
        for (ICraftingTask task : getNetwork().getCraftingManager().getTasks()) {
            ItemStack taskStack = task.getRequested().getItem();
            if (taskStack.sameItem(stack)) {
                return true;
            }
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isItemCraftable(IArguments arguments) throws LuaException {
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()));
        return RefinedStorage.isItemCraftable(getNetwork(), stack);
    }
}
