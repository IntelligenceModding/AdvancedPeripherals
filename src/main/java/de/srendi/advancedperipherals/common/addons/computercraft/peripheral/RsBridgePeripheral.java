package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.Action;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.base.IStoragePeripheral;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class RsBridgePeripheral extends BasePeripheral<BlockEntityPeripheralOwner<RsBridgeEntity>> implements IStoragePeripheral {

    public static final String PERIPHERAL_TYPE = "rsBridge";

    public RsBridgePeripheral(RsBridgeEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    private RefinedStorageNode getNode() {
        return owner.tileEntity.getNode();
    }

    private INetwork getNetwork() {
        return getNode().getNetwork();
    }

    private boolean canRun() {
        return getNetwork() != null || getNetwork().canRun();
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableRSBridge.get();
    }

    @Override
    public final MethodResult isConnected() {
        return MethodResult.of(getNetwork() != null);
    }

    @Override
    public MethodResult isOnline() {
        if (getNetwork() == null)
            return MethodResult.of(false, "Not connected");
        return MethodResult.of(getNetwork().canRun());
    }

    @Override
    public final MethodResult listItems() {
        if (!canRun())
            return MethodResult.of(null, "System not connected or offline");
        return MethodResult.of(RefinedStorage.listItems(getNetwork()));
    }

    @Override
    public final MethodResult listCraftableItems() {
        if (!canRun())
            return MethodResult.of(null, "System not connected or offline");
        List<Object> items = new ArrayList<>();
        RefinedStorage.getCraftableItems(getNetwork()).forEach(item -> items.add(RefinedStorage.getObjectFromStack(item, getNetwork())));
        return MethodResult.of(items);
    }

    @Override
    public final MethodResult listCraftableFluids() {
        if (!canRun())
            return MethodResult.of(null, "System not connected or offline");
        List<Object> fluids = new ArrayList<>();
        RefinedStorage.getCraftableFluids(getNetwork()).forEach(fluid -> fluids.add(RefinedStorage.getObjectFromFluid(fluid, getNetwork())));
        return MethodResult.of(fluids);
    }

    @Override
    public final MethodResult getMaxItemDiskStorage() {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        return MethodResult.of(RefinedStorage.getMaxItemDiskStorage(getNetwork()));
    }

    @Override
    public final MethodResult getMaxFluidDiskStorage() {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        return MethodResult.of(RefinedStorage.getMaxFluidDiskStorage(getNetwork()));
    }

    @Override
    public final MethodResult getMaxItemExternalStorage() {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        return MethodResult.of(RefinedStorage.getMaxItemExternalStorage(getNetwork()));
    }

    @Override
    public final MethodResult getMaxFluidExternalStorage() {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        return MethodResult.of(RefinedStorage.getMaxFluidExternalStorage(getNetwork()));
    }

    @Override
    public final MethodResult listFluids() {
        if (!canRun())
            return MethodResult.of(null, "System not connected or offline");
        return MethodResult.of(RefinedStorage.listFluids(getNetwork()));
    }

    @Override
    public final MethodResult getEnergyUsage() {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        return MethodResult.of(getNetwork().getEnergyUsage());
    }

    @Override
    public final MethodResult getEnergyCapacity() {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        return MethodResult.of(getNetwork().getEnergyStorage().getMaxEnergyStored());
    }

    @Override
    public final MethodResult getStoredEnergy() {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        return MethodResult.of(getNetwork().getEnergyStorage().getEnergyStored());
    }

    @Override
    public final MethodResult getPattern(IArguments arguments) {
        if (!canRun())
            return MethodResult.of(null, "System not connected or offline");
        try {
            return MethodResult.of(RefinedStorage.getObjectFromPattern(getNetwork().getCraftingManager().getPattern(ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()))), getNetwork()));
        } catch (LuaException e) {
            return MethodResult.of(null, "Could not get pattern " + e.getMessage());
        }
    }

    @Override
    public MethodResult getPatterns() {
        return null;
    }

    @Override
    public final MethodResult exportItem(IArguments arguments) throws LuaException {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()));
        Direction direction = validateSide(arguments.getString(1));

        BlockEntity targetEntity = owner.tileEntity.getLevel().getBlockEntity(owner.tileEntity.getBlockPos().relative(direction));
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).resolve().orElse(null) : null;
        if (inventory == null)
            return MethodResult.of(0, "No valid inventory at " + arguments.getString(1));

        ItemStack extracted = getNetwork().extractItem(stack, stack.getCount(), 1, Action.SIMULATE);
        if (extracted.isEmpty())
            return MethodResult.of(0);

        int transferableAmount = extracted.getCount();

        ItemStack remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, true);
        if (!remaining.isEmpty())
            transferableAmount -= remaining.getCount();

        extracted = getNetwork().extractItem(stack, transferableAmount, 1, Action.PERFORM);
        remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, false);

        if (!remaining.isEmpty())
            getNetwork().insertItem(remaining, remaining.getCount(), Action.PERFORM);

        return MethodResult.of(transferableAmount);
    }

    @Override
    public final MethodResult importItem(IArguments arguments) throws LuaException {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");

        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()));
        Direction direction = validateSide(arguments.getString(1));

        BlockEntity targetEntity = owner.tileEntity.getLevel().getBlockEntity(owner.tileEntity.getBlockPos().relative(direction));
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).resolve().orElse(null) : null;
        if (inventory == null)
            return MethodResult.of(0, "No valid inventory at " + arguments.getString(1));

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
        return MethodResult.of(transferableAmount);
    }

    @Override
    public final MethodResult exportItemToPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");

        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()));
        IPeripheral chest = computer.getAvailablePeripheral(arguments.getString(1));
        if (chest == null)
            return MethodResult.of(0, arguments.getString(1) + " does not exists");

        BlockEntity targetEntity = (BlockEntity) chest.getTarget();
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null) : null;
        if (inventory == null)
            return MethodResult.of(0, "No valid inventory at " + arguments.getString(1));

        ItemStack extracted = getNetwork().extractItem(stack, stack.getCount(), 1, Action.SIMULATE);
        if (extracted.isEmpty())
            return MethodResult.of(0, "Item " + stack.getItem() + " does not exists in the RS system");

        int transferableAmount = extracted.getCount();

        ItemStack remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, true);
        if (!remaining.isEmpty())
            transferableAmount -= remaining.getCount();

        extracted = getNetwork().extractItem(stack, transferableAmount, 1, Action.PERFORM);
        remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, false);

        if (!remaining.isEmpty())
            getNetwork().insertItem(remaining, remaining.getCount(), Action.PERFORM);

        return MethodResult.of(transferableAmount);
    }

    @Override
    public final MethodResult importItemFromPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");

        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()));
        IPeripheral chest = computer.getAvailablePeripheral(arguments.getString(1));
        int count = stack.getCount();
        if (chest == null)
            return MethodResult.of(0, arguments.getString(1) + " does not exists");

        BlockEntity targetEntity = (BlockEntity) chest.getTarget();
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null) : null;
        if (inventory == null)
            return MethodResult.of(0, "No valid inventory for " + arguments.getString(1));

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
        return MethodResult.of(transferableAmount);
    }

    @Override
    public final MethodResult getItem(IArguments arguments) {
        if (!canRun())
            return MethodResult.of(null, "System not connected or offline");
        try {
            return MethodResult.of(RefinedStorage.getItem(getNetwork(), ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()))));
        } catch (LuaException e) {
            return MethodResult.of(null, "Could not get item: " + e.getMessage());
        }

    }

    @Override
    public MethodResult getFluid(IArguments arguments) {
        if (!canRun())
            return MethodResult.of(null, "System not connected or offline");
        try {
            return MethodResult.of(RefinedStorage.getItem(getNetwork(), ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()))));
        } catch (LuaException e) {
            return MethodResult.of(null, "Could not get item: " + e.getMessage());
        }
    }

    @Override
    public final MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!canRun())
            return MethodResult.of(false, "System not connected or offline");

        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()));
        if (stack == null)
            return MethodResult.of(false, "The item " + arguments.getTable(0).get("name") + "is not craftable");
        ICalculationResult result = getNetwork().getCraftingManager().create(stack, stack.getCount());
        CalculationResultType type = result.getType();
        if (result.getType() == CalculationResultType.OK)
            getNetwork().getCraftingManager().start(result.getTask());
        return MethodResult.of(type == CalculationResultType.OK);
    }

    @Override
    public final MethodResult craftFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!canRun())
            return MethodResult.of(false, "System not connected or offline");

        ICalculationResult result = getNetwork().getCraftingManager().create(new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(arguments.getTable(0).toString())), 0), (int) arguments.getTable(0).get("count"));
        CalculationResultType type = result.getType();
        if (result.getType() == CalculationResultType.OK)
            getNetwork().getCraftingManager().start(result.getTask());
        return MethodResult.of(type == CalculationResultType.OK);
    }

    @Override
    public final MethodResult isItemCrafting(IArguments arguments) throws LuaException {
        if (!canRun())
            return MethodResult.of(false, "System not connected or offline");

        ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation((String) arguments.getTable(0).get("name"))));
        if(stack.isEmpty())
            return MethodResult.of(false, "Could not find item");

        for (ICraftingTask task : getNetwork().getCraftingManager().getTasks()) {
            ItemStack taskStack = task.getRequested().getItem();
            if (taskStack.sameItem(stack))
                return MethodResult.of(true);
        }
        return MethodResult.of(false);
    }

    @Override
    public final MethodResult isItemCraftable(IArguments arguments) throws LuaException {
        if (!canRun())
            return MethodResult.of(false, "System not connected or offline");

        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()));
        if(stack.isEmpty())
            return MethodResult.of(false, "Could not find item");
        return MethodResult.of(RefinedStorage.isItemCraftable(getNetwork(), stack));
    }
}
