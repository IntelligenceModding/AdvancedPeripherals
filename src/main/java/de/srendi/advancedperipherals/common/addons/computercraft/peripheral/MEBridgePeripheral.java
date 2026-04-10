package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.crafting.pattern.EncodedPatternItem;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.ae2.AEApi;
import de.srendi.advancedperipherals.common.addons.ae2.AECraftJob;
import de.srendi.advancedperipherals.common.addons.ae2.AEMekanismApi;
import de.srendi.advancedperipherals.common.addons.ae2.MEChemicalHandler;
import de.srendi.advancedperipherals.common.addons.ae2.MEFluidHandler;
import de.srendi.advancedperipherals.common.addons.ae2.MEItemHandler;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.StorageSystemBlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.MEBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalFilter;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.GenericFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemFluidHandler;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemItemHandler;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MEBridgePeripheral extends AbstractStorageSystemPeripheral<StorageSystemBlockEntityPeripheralOwner<MEBridgeEntity>> {
    public static final String PERIPHERAL_TYPE = "me_bridge";

    private final MEBridgeEntity bridge;
    private IGridNode node;

    public MEBridgePeripheral(MEBridgeEntity be) {
        super(PERIPHERAL_TYPE, new StorageSystemBlockEntityPeripheralOwner<>(be) {
            @Override
            @NotNull
            public IStorageSystemItemHandler getStorageSystemItemHandler() {
                return new MEItemHandler(AEApi.getMonitor(be.getActionableNode()), be);
            }

            @Override
            @NotNull
            public IStorageSystemFluidHandler getStorageSystemFluidHandler() {
                return new MEFluidHandler(AEApi.getMonitor(be.getActionableNode()), be);
            }
        });
        this.bridge = be;
        this.node = be.getActionableNode();
    }

    public void setNode(IManagedGridNode node) {
        this.node = node.getNode();
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableMEBridge.get();
    }

    public MEBridgeEntity getBridge() {
        return bridge;
    }

    @Override
    @NotNull
    public APAddon getChemicalOpAddon() {
        return APAddon.APP_MEKANISTICS;
    }

    @Override
    @NotNull
    public Object /*IChemicalHandler*/ getStorageSystemChemicalHandler() {
        return new MEChemicalHandler(AEApi.getMonitor(node), bridge);
    }

    @Override
    public boolean isAvailable() {
        return node.hasGridBooted();
    }

    @Override
    public boolean isOnlineImpl() {
        return node.isOnline();
    }

    @Override
    public MethodResult getItemImpl(ItemFilter filter) throws LuaException {
        return MethodResult.of(AEApi.parseAeStack(AEApi.findAEStackFromFilter(AEApi.getMonitor(node), getCraftingService(), filter), getCraftingService()));
    }

    @Override
    public MethodResult getFluidImpl(FluidFilter filter) throws LuaException {
        return MethodResult.of(AEApi.parseAeStack(AEApi.findAEFluidFromFilter(AEApi.getMonitor(node), getCraftingService(), filter), getCraftingService()));
    }

    @Override
    public MethodResult getChemicalImpl(Object /*ChemicalFilter*/ filter) throws LuaException {
        return MethodResult.of(AEApi.parseAeStack(AEMekanismApi.findAEChemicalFromFilter(AEApi.getMonitor(node), getCraftingService(), (ChemicalFilter) filter), getCraftingService()));
    }

    @Override
    public MethodResult getItemsImpl(ItemFilter filter) throws LuaException {
        return MethodResult.of(AEApi.listItems(AEApi.getMonitor(node), getCraftingService(), filter));
    }

    @Override
    public MethodResult getFluidsImpl(FluidFilter filter) throws LuaException {
        return MethodResult.of(AEApi.listFluids(AEApi.getMonitor(node), getCraftingService(), filter));
    }

    @Override
    public MethodResult getChemicalsImpl(Object /*ChemicalFilter*/ filter) throws LuaException {
        return MethodResult.of(AEMekanismApi.listChemicals(AEApi.getMonitor(node), getCraftingService(), (ChemicalFilter) filter));
    }

    @Override
    public MethodResult getCraftableItemsImpl(ItemFilter filter) throws LuaException {
        return MethodResult.of(AEApi.listCraftableItems(AEApi.getMonitor(node), getCraftingService(), filter));
    }

    @Override
    public MethodResult getCraftableFluidsImpl(FluidFilter filter) throws LuaException {
        return MethodResult.of(AEApi.listCraftableFluids(AEApi.getMonitor(node), getCraftingService(), filter));
    }

    @Override
    public MethodResult getCraftableChemicalsImpl(Object /*ChemicalFilter*/ filter) throws LuaException {
        return MethodResult.of(AEMekanismApi.listCraftableChemicals(AEApi.getMonitor(node), getCraftingService(), (ChemicalFilter) filter));
    }

    @Override
    public List<?> getCellsImpl() {
        return AEApi.listCells(node);
    }

    @Override
    public List<?> getDrivesImpl() {
        return AEApi.listDrives(node.getGrid());
    }

    @Override
    public double getStoredEnergyImpl() {
        return node.getGrid().getEnergyService().getStoredPower();
    }

    @Override
    public double getEnergyCapacityImpl() {
        return node.getGrid().getEnergyService().getMaxStoredPower();
    }

    @Override
    public double getEnergyUsageImpl() {
        return node.getGrid().getEnergyService().getAvgPowerUsage();
    }

    @Override
    public double getAverageEnergyInputImpl() {
        return node.getGrid().getEnergyService().getAvgPowerInjection();
    }

    @Override
    public double getTotalExternalItemStorageImpl() {
        return AEApi.getTotalExternalItemStorage(node);
    }

    @Override
    public double getTotalExternalFluidStorageImpl() {
        return AEApi.getTotalExternalFluidStorage(node);
    }

    @Override
    public double getTotalExternalChemicalStorageImpl() {
        return AEApi.getTotalExternalChemicalStorage(node);
    }

    @Override
    public double getTotalItemStorageImpl() {
        return AEApi.getTotalItemStorage(node);
    }

    @Override
    public double getTotalFluidStorageImpl() {
        return AEApi.getTotalFluidStorage(node);
    }

    @Override
    public double getTotalChemicalStorageImpl() {
        return AEApi.getTotalChemicalStorage(node);
    }

    @Override
    public double getUsedExternalItemStorageImpl() {
        return AEApi.getUsedExternalItemStorage(node);
    }

    @Override
    public double getUsedExternalFluidStorageImpl() {
        return AEApi.getUsedExternalFluidStorage(node);
    }

    @Override
    public double getUsedExternalChemicalStorageImpl() {
        return AEApi.getUsedExternalChemicalStorage(node);
    }

    @Override
    public double getUsedItemStorageImpl() {
        return AEApi.getUsedItemStorage(node);
    }

    @Override
    public double getUsedFluidStorageImpl() {
        return AEApi.getUsedFluidStorage(node);
    }

    @Override
    public double getUsedChemicalStorageImpl() {
        return AEApi.getUsedChemicalStorage(node);
    }

    @Override
    public double getAvailableExternalItemStorageImpl() {
        return AEApi.getAvailableExternalItemStorage(node);
    }

    @Override
    public double getAvailableExternalFluidStorageImpl() {
        return AEApi.getAvailableExternalFluidStorage(node);
    }

    @Override
    public double getAvailableExternalChemicalStorageImpl() {
        return AEApi.getAvailableExternalChemicalStorage(node);
    }

    @Override
    public double getAvailableItemStorageImpl() {
        return AEApi.getAvailableItemStorage(node);
    }

    @Override
    public double getAvailableFluidStorageImpl() {
        return AEApi.getAvailableFluidStorage(node);
    }

    @Override
    public double getAvailableChemicalStorageImpl() {
        return AEApi.getAvailableChemicalStorage(node);
    }

    @Override
    public MethodResult craftItemImpl(IComputerAccess computer, IArguments arguments, ItemFilter filter) throws LuaException {
        String cpuName = arguments.optString(1, "");

        ICraftingCPU target = AEApi.getCraftingCPU(node, cpuName);
        if (!cpuName.isEmpty() && target == null) {
            return MethodResult.of(null, StatusConstants.CPU_NOT_FOUND.toString(), cpuName);
        }

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);
        Pair<Long, AEItemKey> stack = AEApi.findAEStackFromFilter(AEApi.getMonitor(bridge.getGridNode()), craftingGrid, filter);
        if (stack.right() == null && stack.left() == 0) {
            return MethodResult.of(null, StatusConstants.NOT_CRAFTABLE.toString());
        }

        AECraftJob job = new AECraftJob(owner.getLevel(), computer, node, stack.right(), filter.getCount(), bridge, target);
        bridge.addJob(job);
        return MethodResult.of(job.withCPU(target));
    }

    @Override
    public MethodResult craftFluidImpl(IComputerAccess computer, IArguments arguments, FluidFilter filter) throws LuaException {
        String cpuName = arguments.optString(1, "");

        ICraftingCPU target = AEApi.getCraftingCPU(node, cpuName);
        if (!cpuName.isEmpty() && target == null) {
            return MethodResult.of(null, StatusConstants.CPU_NOT_FOUND, cpuName);
        }

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);
        Pair<Long, AEFluidKey> stack = AEApi.findAEFluidFromFilter(AEApi.getMonitor(bridge.getGridNode()), craftingGrid, filter);
        if (stack.right() == null && stack.left() == 0) {
            return MethodResult.of(false, StatusConstants.NOT_CRAFTABLE.toString());
        }

        AECraftJob job = new AECraftJob(owner.getLevel(), computer, node, stack.right(), filter.getAmount(), bridge, target);
        bridge.addJob(job);
        return MethodResult.of(job.withCPU(target));
    }

    @Override
    public MethodResult craftChemicalImpl(IComputerAccess computer, IArguments arguments, Object /*ChemicalFilter*/ filter0) throws LuaException {
        ChemicalFilter filter = (ChemicalFilter) filter0;
        String cpuName = arguments.optString(1, "");

        ICraftingCPU target = AEApi.getCraftingCPU(node, cpuName);
        if (!cpuName.isEmpty() && target == null) {
            return MethodResult.of(null, StatusConstants.CPU_NOT_FOUND, cpuName);
        }

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);
        Pair<Long, MekanismKey> stack = AEMekanismApi.findAEChemicalFromFilter(AEApi.getMonitor(bridge.getGridNode()), craftingGrid, filter);
        if (stack.right() == null && stack.left() == 0) {
            return MethodResult.of(false, StatusConstants.NOT_CRAFTABLE.toString());
        }

        AECraftJob job = new AECraftJob(owner.getLevel(), computer, node, stack.right(), filter.getAmount(), bridge, target);
        bridge.addJob(job);
        return MethodResult.of(job.withCPU(target));
    }

    @Override
    public List<?> getCraftingTasksImpl() {
        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);

        List<Object> jobs = new ArrayList<>();

        for (AECraftJob job : bridge.getJobs()) {
            for (ICraftingCPU cpu : craftingGrid.getCpus()) {
                if (cpu.isBusy() && job.getToCraft().matches(cpu.getJobStatus().crafting()))
                    jobs.add(AEApi.parseCraftingJob(cpu.getJobStatus(), job, cpu));
            }
        }
        return jobs;
    }

    @Override
    public Object getCraftingTaskImpl(int id) {
        for (AECraftJob job : bridge.getJobs()) {
            if (job.getId() == id) {
                return MethodResult.of(job);
            }
        }
        return MethodResult.of(null, StatusConstants.NOT_FOUND.toString());
    }

    @Override
    public int cancelCraftingTasksImpl(GenericFilter<?> filter) throws LuaException {
        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);

        int canceled = 0;
        for (ICraftingCPU cpu : craftingGrid.getCpus()) {
            if (cpu.getJobStatus() != null && filter.testAE(cpu.getJobStatus().crafting())) {
                cpu.cancelJob();
                canceled++;
            }
        }
        return canceled;
    }

    @Override
    public boolean isCraftableImpl(GenericFilter<?> filter) throws LuaException {
        return AEApi.findPatternFromFilters(node.getGrid(), getLevel(), null, filter).left() != null;
    }

    @Override
    public MethodResult isCraftingImpl(IArguments arguments, GenericFilter<?> filter) throws LuaException {
        String cpuName = arguments.optString(1, "");

        ICraftingService grid = node.getGrid().getService(ICraftingService.class);
        ICraftingCPU craftingCPU = AEApi.getCraftingCPU(node, cpuName);
        return MethodResult.of(AEApi.isCrafting(grid, filter, craftingCPU));
    }

    @Override
    public MethodResult getPatternsImpl(@Nullable GenericFilter<?> inputFilter, @Nullable GenericFilter<?> outputFilter) throws LuaException {
        if (inputFilter == null && outputFilter == null) {
            return MethodResult.of(AEApi.listPatterns(node.getGrid(), getLevel()));
        }
        Pair<Pair<EncodedPatternItem<?>, IPatternDetails>, String> pattern = AEApi.findPatternFromFilters(node.getGrid(), getLevel(), inputFilter, outputFilter);
        if (pattern.right() != null) {
            return MethodResult.of(null, pattern.right());
        }
        return MethodResult.of(AEApi.parsePattern(pattern.left()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getCraftingCPUs() {
        if (!isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        ICraftingService grid = node.getGrid().getService(ICraftingService.class);
        List<Object> map = new ArrayList<>();

        for (ICraftingCPU iCraftingCPU : grid.getCpus()) {
            Object cpu = AEApi.parseCraftingCPU(iCraftingCPU, false);
            map.add(cpu);
        }
        return MethodResult.of(map);
    }

    private ICraftingService getCraftingService() {
        return node.getGrid().getCraftingService();
    }

    private MethodResult notConnected(@Nullable Object defaultValue) {
        return MethodResult.of(defaultValue, StatusConstants.NOT_CONNECTED.toString());
    }
}
