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
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorage;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorageNode;
import de.srendi.advancedperipherals.common.blocks.tileentity.RsBridgeTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;

public class RsBridgePeripheral extends BasePeripheral {

    private final RsBridgeTileEntity tileEntity;

    public RsBridgePeripheral(String type, RsBridgeTileEntity tileEntity) {
        super(type, tileEntity);
        this.tileEntity = tileEntity;
    }

    private RefinedStorageNode getNode() {
        return this.tileEntity.getNode();
    }

    private INetwork getNetwork() {
        return getNode().getNetwork();
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableRsBridge;
    }

    @LuaFunction()
    public final Object listItems() {
        return RefinedStorage.listItems(false, getNetwork());
    }

    @LuaFunction()
    public final Object listCraftableItems() {
        return RefinedStorage.listItems(true, getNetwork());
    }

    @LuaFunction()
    public final Object listFluids() {
        return RefinedStorage.listFluids(false, getNetwork());
    }

    @LuaFunction()
    public final Object listCraftableFluids() {
        return RefinedStorage.listFluids(true, getNetwork());
    }

    @LuaFunction()
    public final int getEnergyUsage() {
        return getNetwork().getEnergyUsage();
    }

    @LuaFunction()
    public final int getMaxEnergyStorage() {
        return getNetwork().getEnergyStorage().getMaxEnergyStored();
    }

    @LuaFunction()
    public final int getEnergyStorage() {
        return getNetwork().getEnergyStorage().getEnergyStored();
    }

    @LuaFunction()
    public final Object getPattern(IArguments arguments) throws LuaException {
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork(), true));

        return RefinedStorage.getObjectFromPattern(getNetwork().getCraftingManager().getPattern(stack));
    }

    @LuaFunction(mainThread = true)
    public final int exportItem(IArguments arguments) throws LuaException {
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork(), false));
        Direction direction = Direction.valueOf(arguments.getString(1).toUpperCase(Locale.ROOT));

        TileEntity targetEntity = tileEntity.getWorld().getTileEntity(tileEntity.getPos().offset(direction));
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).resolve().orElse(null) : null;
        if (inventory == null)
            throw new LuaException("No valid inventory at " + arguments.getString(1));

        ItemStack extracted = getNetwork().extractItem(stack, stack.getCount(), 1, Action.SIMULATE);
        if (extracted.isEmpty())
            return 0;
        //throw new LuaException("Item " + item + " does not exists in the RS system or the system is offline");

        int transferableAmount = extracted.getCount();

        ItemStack remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, true);
        if (!remaining.isEmpty())
            transferableAmount -= remaining.getCount();

        extracted = getNetwork().extractItem(stack, transferableAmount, 1, Action.PERFORM);
        remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, false);

        if (!remaining.isEmpty()) {
            getNetwork().insertItem(remaining, remaining.getCount(), Action.PERFORM);
        }

        return transferableAmount;
    }

    @LuaFunction(mainThread = true)
    public final int importItem(IArguments arguments) throws LuaException {
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork(), false));
        Direction direction = Direction.valueOf(arguments.getString(1).toUpperCase(Locale.ROOT));
        int count = stack.getCount();

        TileEntity targetEntity = tileEntity.getWorld().getTileEntity(tileEntity.getPos().offset(direction));
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).resolve().orElse(null) : null;
        if (inventory == null)
            throw new LuaException("No valid inventory at " + arguments.getString(1));

        int amount = count;
        int transferableAmount = 0;

        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.getStackInSlot(i).isItemEqual(stack)) {
                if (inventory.getStackInSlot(i).getCount() >= amount) {
                    transferableAmount += amount;
                    getNetwork().insertItem(stack, amount, Action.PERFORM);
                    inventory.extractItem(i, amount, false);
                    break;
                } else {
                    amount = count - inventory.getStackInSlot(i).getCount();
                    transferableAmount += inventory.getStackInSlot(i).getCount();
                    getNetwork().insertItem(stack, inventory.getStackInSlot(i).getCount(), Action.PERFORM);
                    inventory.extractItem(i, inventory.getStackInSlot(i).getCount(), false);
                }
            }
        }
        return transferableAmount;
    }

    @LuaFunction(mainThread = true)
    public final int exportItemToChest(IComputerAccess computer, IArguments arguments) throws LuaException {
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork(), false));
        IPeripheral chest = computer.getAvailablePeripheral(arguments.getString(1));
        if (chest == null)
            throw new LuaException("No valid chest for " + arguments.getString(1));

        TileEntity targetEntity = (TileEntity) chest.getTarget();
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().orElse(null) : null;
        if (inventory == null)
            throw new LuaException("No valid inventory for " + arguments.getString(1));

        ItemStack extracted = getNetwork().extractItem(stack, stack.getCount(), 1, Action.SIMULATE);
        if (extracted.isEmpty())
            return 0;
        //throw new LuaException("Item " + item + " does not exists in the RS system or the system is offline");

        int transferableAmount = extracted.getCount();

        ItemStack remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, true);
        if (!remaining.isEmpty())
            transferableAmount -= remaining.getCount();

        extracted = getNetwork().extractItem(stack, transferableAmount, 1, Action.PERFORM);
        remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, false);

        if (!remaining.isEmpty()) {
            getNetwork().insertItem(remaining, remaining.getCount(), Action.PERFORM);
        }

        return transferableAmount;
    }

    @LuaFunction(mainThread = true)
    public final int importItemFromChest(IComputerAccess computer, IArguments arguments) throws LuaException {
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork(), false));
        IPeripheral chest = computer.getAvailablePeripheral(arguments.getString(1));
        int count = stack.getCount();
        if (chest == null)
            throw new LuaException("No valid chest for " + arguments.getString(1));

        TileEntity targetEntity = (TileEntity) chest.getTarget();
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().orElse(null) : null;
        if (inventory == null)
            throw new LuaException("No valid inventory for " + arguments.getString(1));

        int amount = count;

        int transferableAmount = 0;

        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.getStackInSlot(i).isItemEqual(stack)) {
                if (inventory.getStackInSlot(i).getCount() >= amount) {
                    transferableAmount += amount;
                    getNetwork().insertItem(stack, amount, Action.PERFORM);
                    inventory.extractItem(i, amount, false);
                    break;
                } else {
                    amount = count - inventory.getStackInSlot(i).getCount();
                    transferableAmount += inventory.getStackInSlot(i).getCount();
                    getNetwork().insertItem(stack, inventory.getStackInSlot(i).getCount(), Action.PERFORM);
                    inventory.extractItem(i, inventory.getStackInSlot(i).getCount(), false);
                }
            }
        }
        return transferableAmount;
    }

    @LuaFunction()
    public final Object getItem(IArguments arguments) throws LuaException {
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork(), false));
        if (stack == null)
            return null; //Return null instead of crashing the program.
        return RefinedStorage.getItem(RefinedStorage.getItems(getNetwork(), false), stack);
    }

    @LuaFunction()
    public final boolean craftItem(IArguments arguments) throws LuaException {
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork(), true));
        if (stack == null)
            throw new LuaException("The item " + arguments.getTable(0).get("name") + "is not craftable");
        ICalculationResult result = getNetwork().getCraftingManager().create(stack, stack.getCount());
        CalculationResultType type = result.getType();
        if (result.getType() == CalculationResultType.OK)
            getNetwork().getCraftingManager().start(result.getTask());
        return type == CalculationResultType.OK;
    }

    @LuaFunction()
    public final boolean craftFluid(String fluid, int count) {
        ICalculationResult result = getNetwork().getCraftingManager().create(new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluid)), 0), count);
        CalculationResultType type = result.getType();
        if (result.getType() == CalculationResultType.OK)
            getNetwork().getCraftingManager().start(result.getTask());
        return type == CalculationResultType.OK;
    }

    @LuaFunction()
    public final boolean isItemCrafting(String item) {
        ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item)));
        for (ICraftingTask task : getNetwork().getCraftingManager().getTasks()) {
            ItemStack taskStack = task.getRequested().getItem();
            if (taskStack.isItemEqual(stack)) {
                return true;
            }
        }
        return false;
    }
}
