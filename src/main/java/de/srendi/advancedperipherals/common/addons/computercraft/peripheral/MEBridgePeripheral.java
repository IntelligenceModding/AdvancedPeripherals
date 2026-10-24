package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.CraftingJobStatus;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.addons.ae2.AEApi;
import de.srendi.advancedperipherals.common.addons.ae2.AECraftJob;
import de.srendi.advancedperipherals.common.addons.ae2.MEFluidHandler;
import de.srendi.advancedperipherals.common.addons.ae2.MEItemHandler;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.StorageSystemBlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.MEBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.CCMountUtil;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.GenericFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemFluidHandler;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemItemHandler;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class MEBridgePeripheral extends AbstractStorageSystemPeripheral<StorageSystemBlockEntityPeripheralOwner<MEBridgeEntity>> {
    public static final String PERIPHERAL_TYPE = "me_bridge";

    private final MEBridgeEntity bridge;
    private IGridNode node;

    private int diskRescanCD = 1;
    private Map<Integer, Mount> lastDisks = Map.of();
    private final Map<IComputerAccess, Map<Integer, String>> computers = new HashMap<>();

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

    @Override
    public Set<String> getAdditionalTypes() {
        Set<String> types = new HashSet<>(super.getAdditionalTypes());
        types.add("multi_drive");
        return types;
    }

    public synchronized void setNode(IManagedGridNode node) {
        this.node = node.getNode();
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableMEBridge.get();
    }

    @Override
    public synchronized void forEachConnectedComputers(Consumer<? super IComputerAccess> consumer) {
        computers.keySet().forEach(consumer);
    }

    private void mountDisks(IComputerAccess computer, Map<Integer, Mount> disks, Map<Integer, String> mounts) {
        for (Map.Entry<Integer, Mount> entry : disks.entrySet()) {
            String path = CCMountUtil.mountDisk(computer, Objects.requireNonNull(entry.getValue()));
            mounts.put(entry.getKey(), path);
        }
        computer.queueEvent("disk", computer.getAttachmentName());
    }

    private void unmountDisks(IComputerAccess computer, Map<Integer, String> mounts) {
        mounts.values().forEach(computer::unmount);
        mounts.clear();
        computer.queueEvent("disk_eject", computer.getAttachmentName());
    }

    @Override
    public synchronized void attach(@NotNull IComputerAccess computer) {
        Map<Integer, String> mounts = new HashMap<>();
        computers.put(computer, mounts);
        this.mountDisks(computer, this.lastDisks, mounts);
    }

    @Override
    public synchronized void detach(@NotNull IComputerAccess computer) {
        Map<Integer, String> mounts = computers.remove(computer);
        this.unmountDisks(computer, mounts);
    }

    protected synchronized void refreshDisks(@Nullable Map<Integer, Mount> disks) {
        this.diskRescanCD = 20;

        computers.forEach((computer, mounts) -> this.unmountDisks(computer, mounts));

        if (disks == null) {
            disks = this.node != null ? AEApi.getDiskMounts(this.node.getGrid()) : Map.of();
        }
        final Map<Integer, Mount> finalDisks = disks;
        this.lastDisks = disks;

        if (!finalDisks.isEmpty()) {
            computers.forEach((computer, mounts) -> this.mountDisks(computer, finalDisks, mounts));
        }
    }

    @Override
    public void update() {
        if (this.node == null) {
            return;
        }

        boolean needRefresh = false;

        Map<Integer, Mount> disks = null;
        if (!this.lastDisks.isEmpty()) {
            disks = AEApi.getDiskMounts(this.node.getGrid());
            for (Integer id : this.lastDisks.keySet()) {
                if (!disks.containsKey(id)) {
                    needRefresh = true;
                    break;
                }
            }
        }

        if (!needRefresh) {
            this.diskRescanCD--;
            if (this.diskRescanCD <= 0) {
                this.diskRescanCD = 20;
                disks = AEApi.getDiskMounts(this.node.getGrid());
                for (Integer id : disks.keySet()) {
                    if (!this.lastDisks.containsKey(id)) {
                        needRefresh = true;
                        break;
                    }
                }
            }
        }

        if (needRefresh) {
            this.refreshDisks(disks);
        }
    }

    public MEBridgeEntity getBridge() {
        return bridge;
    }

    @Override
    public boolean isAvailable() {
        return node.hasGridBooted();
    }

    @Override
    public boolean isOnlineImpl() {
        return node.isOnline();
    }

    @LuaFunction
    public Collection<String> getMountPaths(IComputerAccess computer) {
        return this.computers.get(computer).values();
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
    public MethodResult getItemsImpl(ItemFilter filter) throws LuaException {
        return MethodResult.of(AEApi.listItems(AEApi.getMonitor(node), getCraftingService(), filter));
    }

    @Override
    public MethodResult getFluidsImpl(FluidFilter filter) throws LuaException {
        return MethodResult.of(AEApi.listFluids(AEApi.getMonitor(node), getCraftingService(), filter));
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
    public List<?> getCellsImpl() {
        return AEApi.listCells(node.getGrid());
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
    public double getMaxExternalItemStorageImpl() {
        return AEApi.getMaxExternalItemStorage(node.getGrid());
    }

    @Override
    public double getMaxExternalItemCountImpl() {
        return AEApi.getMaxExternalItemCount(node.getGrid());
    }

    @Override
    public double getMaxExternalFluidStorageImpl() {
        return AEApi.getMaxExternalFluidStorage(node.getGrid());
    }

    @Override
    public double getMaxItemStorageImpl() {
        return AEApi.getMaxItemStorage(node.getGrid());
    }

    @Override
    public double getMaxFluidStorageImpl() {
        return AEApi.getMaxFluidStorage(node.getGrid());
    }

    @Override
    public double getUsedExternalItemStorageImpl() {
        return AEApi.getUsedExternalItemStorage(node.getGrid());
    }

    @Override
    public double getUsedExternalItemCountImpl() {
        return AEApi.getUsedExternalItemCount(node.getGrid());
    }

    @Override
    public double getUsedExternalFluidStorageImpl() {
        return AEApi.getUsedExternalFluidStorage(node.getGrid());
    }

    @Override
    public double getUsedItemStorageImpl() {
        return AEApi.getUsedItemStorage(node.getGrid());
    }

    @Override
    public double getUsedFluidStorageImpl() {
        return AEApi.getUsedFluidStorage(node.getGrid());
    }

    @Override
    public double getAvailableExternalItemStorageImpl() {
        return AEApi.getAvailableExternalItemStorage(node.getGrid());
    }

    @Override
    public double getAvailableExternalItemCountImpl() {
        return AEApi.getAvailableExternalItemCount(node.getGrid());
    }

    @Override
    public double getAvailableExternalFluidStorageImpl() {
        return AEApi.getAvailableExternalFluidStorage(node.getGrid());
    }

    @Override
    public double getAvailableItemStorageImpl() {
        return AEApi.getAvailableItemStorage(node.getGrid());
    }

    @Override
    public double getAvailableFluidStorageImpl() {
        return AEApi.getAvailableFluidStorage(node.getGrid());
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

        AECraftJob job = new AECraftJob(owner.getLevel(), computer, new GenericStack(stack.right(), filter.getCount()), node, bridge, target);
        bridge.addJob(job);
        return MethodResult.of(job);
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

        AECraftJob job = new AECraftJob(owner.getLevel(), computer, new GenericStack(stack.right(), filter.getAmount()), node, bridge, target);
        bridge.addJob(job);
        return MethodResult.of(job);
    }

    @Override
    public List<?> getCraftingTasksImpl() {
        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);

        List<Object> jobs = new ArrayList<>();

        for (ICraftingCPU cpu : craftingGrid.getCpus()) {
            if (!cpu.isBusy()) {
                continue;
            }
            AECraftJob job = null;
            if (cpu instanceof CraftingCPUCluster cluster) {
                ICraftingLink lastLink = cluster.craftingLogic.getLastLink();
                if (lastLink != null) {
                    job = bridge.getJob(lastLink.getCraftingID());
                }
            }
            jobs.add(AEApi.parseCraftingJob(cpu.getJobStatus(), job, cpu));
        }

        return jobs;
    }

    @Override
    public MethodResult getCraftingTaskImpl(int id) {
        AECraftJob job = bridge.getJob(id);
        if (job != null) {
            return MethodResult.of(job);
        }
        return MethodResult.of(null, StatusConstants.NOT_FOUND.toString());
    }

    @Override
    public MethodResult getCraftingTaskImpl(UUID uuid) {
        AECraftJob job = bridge.getJob(uuid);
        if (job != null) {
            return MethodResult.of(job);
        }
        ICraftingCPU cpu = AECraftJob.searchJobCPU(uuid, bridge.getGridNode().getGrid().getCraftingService());
        if (cpu instanceof CraftingCPUCluster cluster) {
            job = new AECraftJob(cluster.craftingLogic.getFinalJobOutput(), cluster.craftingLogic.getLastLink(), cpu);
            bridge.watchJob(job);
            return MethodResult.of(job);
        }
        return MethodResult.of(null, StatusConstants.NOT_FOUND.toString());
    }

    @Override
    public int cancelCraftingTasksImpl(GenericFilter<?> filter) throws LuaException {
        ICraftingService craftingService = node.getGrid().getService(ICraftingService.class);

        int canceled = 0;
        for (ICraftingCPU cpu : craftingService.getCpus()) {
            CraftingJobStatus status = cpu.getJobStatus();
            if (status != null && filter.testAE(status.crafting())) {
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
        Pair<Pair<EncodedPatternItem, IPatternDetails>, String> pattern = AEApi.findPatternFromFilters(node.getGrid(), getLevel(), inputFilter, outputFilter);
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
}
