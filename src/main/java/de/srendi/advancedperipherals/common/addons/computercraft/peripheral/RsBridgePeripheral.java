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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RsBridgePeripheral implements IPeripheral {

    private final List<IComputerAccess> connectedComputers = new ArrayList<>();
    private final RsBridgeTileEntity tileEntity;

    public RsBridgePeripheral(RsBridgeTileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    private RefinedStorageNode getNode() {
        return this.tileEntity.getNode();
    }

    private INetwork getNetwork() {
        return getNode().getNetwork();
    }

    public List<IComputerAccess> getConnectedComputers() {
        return connectedComputers;
    }

    @NotNull
    @Override
    public String getType() {
        return "rsBridge";
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        connectedComputers.add(computer);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        connectedComputers.remove(computer);
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return this == iPeripheral;
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
    public final int retrieve(String item, int count, String directionString) throws LuaException {
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
    public final boolean craftItem(String item, int count) {
        ICalculationResult result = getNetwork().getCraftingManager().create(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item))), count);
        CalculationResultType type = result.getType();
        getNetwork().getCraftingManager().start(result.getTask());
        //TODO: check some stuff to prevent issues
        return type == CalculationResultType.OK;
    }

    @LuaFunction(mainThread = true)
    public final boolean craftFluid(String item, int count) {
        ICalculationResult result = getNetwork().getCraftingManager().create(new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(item)), 0), count);
        CalculationResultType type = result.getType();
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
