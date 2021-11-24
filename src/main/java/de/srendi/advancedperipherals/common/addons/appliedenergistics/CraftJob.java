package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.crafting.ICraftingSimulationRequester;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.StorageChannels;
import appeng.api.storage.data.IAEItemStack;
import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.tileentity.MeBridgeTile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

//TODO: This is not finished, finish it
public class CraftJob implements ILuaCallback {

    public static final String EVENT = "crafting";

    private final IComputerAccess computer;
    private final IGridNode node;
    private final IActionSource source;
    private final ICraftingSimulationRequester requester;
    private Future<ICraftingPlan> futureJob;
    private final ItemStack item;
    private final Level world;

    private boolean startedCrafting = false;

    private MethodResult result;
    private LuaException exception;

    public CraftJob(Level world, final IComputerAccess computer, IGridNode node, ItemStack item, IActionSource source, ICraftingSimulationRequester requester) {
        this.computer = computer;
        this.node = node;
        this.world = world;
        this.source = source;
        this.item = item;
        this.requester = requester;
    }

    protected void fireEvent(boolean success, @Nullable String exception) {
            this.result = MethodResult.of(success, exception);
            this.exception = new LuaException(exception);
            this.computer.queueEvent(EVENT, success, exception);

    }

    protected void fireNotConnected() {
        fireEvent(false, "not connected");
    }

    public void setStartedCrafting(boolean startedCrafting) {
        this.startedCrafting = startedCrafting;
    }

    public boolean isCraftingStarted() {
        return startedCrafting;
    }

    public void startCrafting() {
        IGrid grid = node.getGrid();
        if (grid == null) { //true when the block is not connected
            fireNotConnected();
            return;
        }

        IStorageService storage = grid.getService(IStorageService.class);
        ICraftingService crafting = grid.getService(ICraftingService.class);
        IMEMonitor<IAEItemStack> monitor = storage.getInventory(StorageChannels.items());
        ItemStack itemstack = item;
        IAEItemStack aeItem = AppEngApi.findAEStackFromItemStack(monitor, itemstack);
        if (aeItem == null) {
            fireEvent(false, item.getDescriptionId() + " does not exists in the me system");
            return;
        }
        if (!aeItem.isCraftable()) {
            fireEvent(false, item.getDescriptionId() + " is not craftable");
            return;
        }
        aeItem.setStackSize(itemstack.getCount());

        futureJob = crafting.beginCraftingCalculation(world, this.requester, aeItem);
        fireEvent(true, "Started calculation of the recipe. After it's finished, the system will start crafting the item.");
    }

    //@Override
    //public void calculationComplete(ICraftingPlan job) {
       // ServerWorker.add(() -> calcComplete(job));
    //}

    public void maybeCraft() {

        if(startedCrafting || futureJob == null || !futureJob.isDone())
            return;
        ICraftingPlan job = null;
        try {
            job = futureJob.get();
        } catch(ExecutionException | InterruptedException ex) {
            AdvancedPeripherals.debug("Tried to get job, but job calculation is not done.", org.apache.logging.log4j.Level.FATAL);
            ex.printStackTrace();
        }

        if(job == null) {
            AdvancedPeripherals.debug("Job is null, should not be null", org.apache.logging.log4j.Level.FATAL);
            return;
        }

        setStartedCrafting(true);

        if (job.simulation()) {
            return;
        }

        IGrid grid = node.getGrid();
        if (grid == null) {
            return;
        }

        //TODO: Create events or methods like `isCraftingFinished` or `getCraftingJobState`
        ICraftingService crafting = grid.getService(ICraftingService.class);
        crafting.submitJob(job, null, null, false, this.source);

        this.futureJob = null;
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