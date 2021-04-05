package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingCallback;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.util.ServerWorker;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class CraftJob implements ICraftingCallback, ILuaCallback {

    public static final String EVENT = "crafting";

    private final IComputerAccess computer;
    private final IGridNode node;
    private final IActionSource source;
    private final ItemStack item;
    private final World world;

    private MethodResult result;
    private LuaException exception;

    public CraftJob(World world, final IComputerAccess computer, IGridNode node, ItemStack item, IActionSource source) {
        this.computer = computer;
        this.node = node;
        this.world = world;
        this.source = source;
        this.item = item;
    }

    public void startCrafting() {
        IGrid grid = node.getGrid();
        if (grid == null) { //true when the block is not connected
            this.computer.queueEvent(EVENT, null, "not connected");
            result = MethodResult.of(null, "not connected");
            exception = new LuaException("not connected");
            return;
        }

        IStorageGrid storage = grid.getCache(IStorageGrid.class);
        ICraftingGrid crafting = grid.getCache(ICraftingGrid.class);
        IMEMonitor<IAEItemStack> monitor = storage.getInventory(AppEngApi.getInstance().getApi().storage().getStorageChannel(IItemStorageChannel.class));
        ItemStack itemstack = item;
        IAEItemStack aeItem = AppEngApi.getInstance().findAEStackFromItemStack(monitor, itemstack);
        if (aeItem == null) {
            this.computer.queueEvent(EVENT, null, item + " does not exists in the me system");
            result = MethodResult.of(null, item + " does not exists in the me system");
            exception = new LuaException(item + " is not craftable");
            return;
        }
        if (!aeItem.isCraftable()) {
            this.computer.queueEvent(EVENT, null, item + " is not craftable");
            result = MethodResult.of(null, item + " is not craftable");
            exception = new LuaException(item + " is not craftable");
            return;
        }
        aeItem.setStackSize(itemstack.getCount());

        crafting.beginCraftingJob(world, grid, this.source, aeItem, this);
    }

    @Override
    public void calculationComplete(ICraftingJob job) {
        ServerWorker.add(()->calcComplete(job));
    }

    private void calcComplete(ICraftingJob job) {
        if (job.isSimulation()) {
            this.computer.queueEvent(EVENT, null, "the me system has no ingredients for the crafting job");
            result = MethodResult.of(false, "the me system has no ingredients for the crafting job");
            exception = new LuaException("the me system has no ingredients for the crafting job");
            return;
        }

        IGrid grid = node.getGrid();
        if (grid == null) {
            this.computer.queueEvent(EVENT, null, "not connected");
            result = MethodResult.of(null, "not connected");
            exception = new LuaException("not connected");
            return;
        }

        ICraftingGrid crafting = grid.getCache(ICraftingGrid.class);
        ICraftingLink link = crafting.submitJob(job, null, null, false, this.source);
        if (link == null) {
            this.computer.queueEvent(EVENT, null, "an unexpected error has occurred");
            result = MethodResult.of(false, "an unexpected error has occurred");
            exception = new LuaException("grid is null");
        } else {
            this.computer.queueEvent(EVENT, (Object) null);
            result = MethodResult.of(AppEngApi.getInstance().getObjectFromJob(job));
        }
    }

    @NotNull
    @Override
    public MethodResult resume(Object[] objects) {
        if (result != null)
            return result;
        if (exception != null)
            return MethodResult.of(exception);
        return MethodResult.of();
    }
}