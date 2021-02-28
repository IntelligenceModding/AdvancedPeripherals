package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.Action;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorage;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorageNode;
import de.srendi.advancedperipherals.common.blocks.tileentity.RsBridgeTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
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

    @LuaFunction(mainThread = true)
    public final Object[] listItems() {
        HashMap<Integer, Object> items = new HashMap<>();
        int i = 1;
        for (ItemStack stack : RefinedStorage.getItems(getNetwork(), false)) {
            HashMap<String, Object> map = new HashMap<>();
            CompoundNBT nbt = stack.getTag();
            map.put("name", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
            map.put("amount", stack.getCount());
            map.put("displayName", stack.getDisplayName().getString());
            if (nbt != null && !nbt.isEmpty()) {
                map.put("nbt", nbt.toString());
            }
            items.put(i, map);
            i++;
        }
        return new Object[]{items};
    }

    @LuaFunction(mainThread = true)
    public final Object[] listCraftableItems() {
        HashMap<Integer, Object> items = new HashMap<>();
        int i = 1;
        for (ItemStack stack : RefinedStorage.getItems(getNetwork(), true)) {
            HashMap<String, Object> map = new HashMap<>();
            CompoundNBT nbt = stack.getTag();
            map.put("name", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
            map.put("craftamount", stack.getCount()); //Returns the result amount of an crafting recipe
            for (ItemStack oStack : RefinedStorage.getItems(getNetwork(), false)) { //Used to get the amount of the item
                if (oStack.isItemEqual(stack)) {
                    map.put("amount", oStack.getCount());
                    break;
                } else {
                    map.put("amount", 0);
                }
            }
            map.put("displayName", stack.getDisplayName().getString());
            if (nbt != null && !nbt.isEmpty()) {
                map.put("nbt", nbt.toString());
            }
            items.put(i, map);
            i++;
        }
        return new Object[]{items};
    }

    @LuaFunction(mainThread = true)
    public final Object[] listFluids() {
        HashMap<Integer, Object> items = new HashMap<>();
        int i = 1;
        for (FluidStack stack : RefinedStorage.getFluids(getNetwork(), false)) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
            map.put("amount", stack.getAmount());
            map.put("displayName", stack.getDisplayName().getString());
            items.put(i, map);
            i++;
        }
        return new Object[]{items};
    }

    @LuaFunction(mainThread = true)
    public final Object[] listCraftableFluids() {
        HashMap<Integer, Object> items = new HashMap<>();
        int i = 1;
        for (FluidStack stack : RefinedStorage.getFluids(getNetwork(), true)) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
            map.put("craftamount", stack.getAmount());
            for (FluidStack oStack : RefinedStorage.getFluids(getNetwork(), false)) { //Used to get the amount of the item
                if (oStack.isFluidEqual(stack)) {
                    map.put("amount", oStack.getAmount());
                    break;
                } else {
                    map.put("amount", 0);
                }
            }
            map.put("displayName", stack.getDisplayName().getString());
            items.put(i, map);
            i++;
        }
        return new Object[]{items};
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
    public final int exportItem(String item, int count, String directionString) throws LuaException {
        ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item)));
        stack.setCount(count);
        Direction direction = Direction.valueOf(directionString.toUpperCase(Locale.ROOT));

        TileEntity targetEntity = tileEntity.getWorld().getTileEntity(tileEntity.getPos().offset(direction));
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).resolve().orElse(null) : null;
        if (inventory == null)
            throw new LuaException("No valid inventory at " + directionString);

        ItemStack extracted = getNetwork().extractItem(stack, count, Action.SIMULATE);
        if (extracted.isEmpty())
            return 0;
        //throw new LuaException("Item " + item + " does not exists in the RS system or the system is offline");

        int transferableAmount = extracted.getCount();

        ItemStack remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, true);
        if (!remaining.isEmpty())
            transferableAmount -= remaining.getCount();

        extracted = getNetwork().extractItem(stack, transferableAmount, Action.PERFORM);
        remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, false);

        if (!remaining.isEmpty()) {
            getNetwork().insertItem(remaining, remaining.getCount(), Action.PERFORM);
        }

        return transferableAmount;
    }

    @LuaFunction(mainThread = true)
    public final int importItem(String item, int count, String directionString) throws LuaException {
        ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item)));
        stack.setCount(count);
        Direction direction = Direction.valueOf(directionString.toUpperCase(Locale.ROOT));

        TileEntity targetEntity = tileEntity.getWorld().getTileEntity(tileEntity.getPos().offset(direction));
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).resolve().orElse(null) : null;
        if (inventory == null)
            throw new LuaException("No valid inventory at " + directionString);

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
    public final int exportItemToChest(IComputerAccess computer, String item, int count, String chestName) throws LuaException {
        ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item)));
        stack.setCount(count);
        IPeripheral chest = computer.getAvailablePeripheral(chestName);
        if (chest == null)
            throw new LuaException("No valid chest for " + chestName);

        TileEntity targetEntity = (TileEntity) chest.getTarget();
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().orElse(null) : null;
        if (inventory == null)
            throw new LuaException("No valid inventory for " + chestName);

        ItemStack extracted = getNetwork().extractItem(stack, count, Action.SIMULATE);
        if (extracted.isEmpty())
            return 0;
        //throw new LuaException("Item " + item + " does not exists in the RS system or the system is offline");

        int transferableAmount = extracted.getCount();

        ItemStack remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, true);
        if (!remaining.isEmpty())
            transferableAmount -= remaining.getCount();

        extracted = getNetwork().extractItem(stack, transferableAmount, Action.PERFORM);
        remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, false);

        if (!remaining.isEmpty()) {
            getNetwork().insertItem(remaining, remaining.getCount(), Action.PERFORM);
        }

        return transferableAmount;
    }

    @LuaFunction(mainThread = true)
    public final int importItemFromChest(IComputerAccess computer, String item, int count, String chestName) throws LuaException {
        ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item)));
        stack.setCount(count);
        IPeripheral chest = computer.getAvailablePeripheral(chestName);
        if (chest == null)
            throw new LuaException("No valid chest for " + chestName);

        TileEntity targetEntity = (TileEntity) chest.getTarget();
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().orElse(null) : null;
        if (inventory == null)
            throw new LuaException("No valid inventory for " + chestName);

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
    public final boolean craftItem(String item, int count) {
        ICalculationResult result = getNetwork().getCraftingManager().create(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item))), count);
        CalculationResultType type = result.getType();
        if(result.getType() == CalculationResultType.OK)
            getNetwork().getCraftingManager().start(result.getTask());
        //TODO: check some stuff to prevent issues
        return type == CalculationResultType.OK;
    }

    @LuaFunction(mainThread = true)
    public final boolean craftFluid(String fluid, int count) {
        ICalculationResult result = getNetwork().getCraftingManager().create(new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluid)), 0), count);
        CalculationResultType type = result.getType();
        if(result.getType() == CalculationResultType.OK)
            getNetwork().getCraftingManager().start(result.getTask());
        //TODO: check some stuff to prevent issues
        return type == CalculationResultType.OK;
    }

    @LuaFunction(mainThread = true)
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
