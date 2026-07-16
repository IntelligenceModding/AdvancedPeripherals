package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatus;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.NetworkComponent;
import com.refinedmods.refinedstorage.api.network.autocrafting.AutocraftingNetworkComponent;
import com.refinedmods.refinedstorage.api.network.energy.EnergyNetworkComponent;
import com.refinedmods.refinedstorage.api.network.impl.node.AbstractNetworkNode;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;
import com.refinedmods.refinedstorage.neoforge.support.resource.VariantUtil;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.StorageSystemBlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSApi;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSChemicalHandler;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSCraftJob;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSFluidHandler;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSItemHandler;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSMekanismApi;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSStorageTypes;
import de.srendi.advancedperipherals.common.blocks.blockentities.RSBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalFilter;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.GenericFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemFluidHandler;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemItemHandler;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class RSBridgePeripheral extends AbstractStorageSystemPeripheral<StorageSystemBlockEntityPeripheralOwner<RSBridgeEntity>> {
    public static final String PERIPHERAL_TYPE = "rs_bridge";

    private final RSBridgeEntity bridge;

    public RSBridgePeripheral(RSBridgeEntity be) {
        super(PERIPHERAL_TYPE, new StorageSystemBlockEntityPeripheralOwner<>(be) {
            @Override
            @NotNull
            public IStorageSystemItemHandler getStorageSystemItemHandler() {
                return new RSItemHandler(((AbstractNetworkNode) getBlockEntity().getNode()).getNetwork());
            }

            @Override
            @NotNull
            public IStorageSystemFluidHandler getStorageSystemFluidHandler() {
                return new RSFluidHandler(((AbstractNetworkNode) getBlockEntity().getNode()).getNetwork());
            }
        });
        this.bridge = be;
    }

    @Override
    public boolean isEnabled() {
        return APAddon.REFINEDSTORAGE.isLoaded() && APConfig.PERIPHERALS_CONFIG.enableRSBridge.get();
    }

    public Network getNetwork() {
        return getNode().getNetwork();
    }

    @Override
    @NotNull
    public APAddon getChemicalOpAddon() {
        return APAddon.REFINEDSTORAGE_MEKANISM;
    }

    @Override
    @NotNull
    public Object /*IChemicalHandler*/ getStorageSystemChemicalHandler() {
        return new RSChemicalHandler(getNetwork());
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean isOnlineImpl() {
        return getComponent(EnergyNetworkComponent.class).getStored() > 0;
    }

    @Override
    public MethodResult getItemImpl(ItemFilter filter) throws LuaException {
        Map<?, ?> resourceProperties = RSApi.getParsedItem(getNetwork(), filter);
        if (resourceProperties == null) {
            return MethodResult.of(null, "NOT_FOUND");
        }
        return MethodResult.of(resourceProperties);
    }

    @Override
    public MethodResult getFluidImpl(FluidFilter filter) throws LuaException {
        Map<?, ?> resourceProperties = RSApi.getParsedFluid(getNetwork(), filter);
        if (resourceProperties == null) {
            return MethodResult.of(null, "NOT_FOUND");
        }
        return MethodResult.of(resourceProperties);
    }

    @Override
    public MethodResult getChemicalImpl(Object /*ChemicalFilter*/ filter) throws LuaException {
        Map<?, ?> resourceProperties = RSMekanismApi.getParsedChemical(getNetwork(), (ChemicalFilter) filter);
        if (resourceProperties == null) {
            return MethodResult.of(null, "NOT_FOUND");
        }
        return MethodResult.of(resourceProperties);
    }

    @Override
    public MethodResult getItemsImpl(ItemFilter filter) throws LuaException {
        return MethodResult.of(RSApi.getParsedItems(getNetwork(), filter));
    }

    @Override
    public MethodResult getFluidsImpl(FluidFilter filter) throws LuaException {
        return MethodResult.of(RSApi.getParsedFluids(getNetwork(), filter));
    }

    @Override
    public MethodResult getChemicalsImpl(Object /*ChemicalFilter*/ filter) throws LuaException {
        return MethodResult.of(RSMekanismApi.getParsedChemicals(getNetwork(), (ChemicalFilter) filter));
    }

    @Override
    public MethodResult getCraftableItemsImpl(ItemFilter filter) throws LuaException {
        return MethodResult.of(RSApi.getCraftableItems(getNetwork(), filter));
    }

    @Override
    public MethodResult getCraftableFluidsImpl(FluidFilter filter) throws LuaException {
        return MethodResult.of(RSApi.getCraftableFluids(getNetwork(), filter));
    }

    @Override
    public MethodResult getCraftableChemicalsImpl(Object /*ChemicalFilter*/ filter) throws LuaException {
        return MethodResult.of(RSMekanismApi.getCraftableChemicals(getNetwork(), (ChemicalFilter) filter));
    }

    @Override
    public List<?> getCellsImpl() {
        return RSApi.listCells(getNetwork());
    }

    @Override
    public List<?> getDrivesImpl() {
        return RSApi.listDrives(getNetwork());
    }

    @Override
    public double getStoredEnergyImpl() {
        return getNetwork().getComponent(EnergyNetworkComponent.class).getStored();
    }

    @Override
    public double getEnergyCapacityImpl() {
        return getNetwork().getComponent(EnergyNetworkComponent.class).getCapacity();
    }

    @Override
    public double getEnergyUsageImpl() {
        return RSApi.getEnergyUsage(getNetwork());
    }

    @Override
    public double getAverageEnergyInputImpl() {
        // Unsupported by Refined Storage
        return 0;
    }

    @Override
    public double getMaxExternalItemStorageImpl() {
        // TODO: implement same logic than ME bridge
        return RSApi.getMaxExternalStorage(getNetwork(), RSStorageTypes.ITEM) / 64.0;
    }

    @Override
    public double getMaxExternalItemCountImpl() {
        return RSApi.getMaxExternalStorage(getNetwork(), RSStorageTypes.ITEM);
    }

    @Override
    public double getMaxExternalFluidStorageImpl() {
        return RSApi.getMaxExternalStorage(getNetwork(), RSStorageTypes.FLUID);
    }

    @Override
    public double getMaxExternalChemicalStorageImpl() {
        return RSApi.getMaxExternalStorage(getNetwork(), RSStorageTypes.CHEMICAL);
    }

    @Override
    public double getMaxItemStorageImpl() {
        return RSApi.getMaxStorage(getNetwork(), RSStorageTypes.ITEM);
    }

    @Override
    public double getMaxFluidStorageImpl() {
        return RSApi.getMaxStorage(getNetwork(), RSStorageTypes.FLUID);
    }

    @Override
    public double getMaxChemicalStorageImpl() {
        return RSApi.getMaxStorage(getNetwork(), RSStorageTypes.CHEMICAL);
    }

    @Override
    public double getUsedExternalItemStorageImpl() {
        // TODO: implement same logic than ME bridge
        return RSApi.getUsedExternalStorage(getNetwork(), RSStorageTypes.ITEM) / 64.0;
    }

    @Override
    public double getUsedExternalItemCountImpl() {
        return RSApi.getUsedExternalStorage(getNetwork(), RSStorageTypes.ITEM);
    }

    @Override
    public double getUsedExternalFluidStorageImpl() {
        return RSApi.getUsedExternalStorage(getNetwork(), RSStorageTypes.FLUID);
    }

    @Override
    public double getUsedExternalChemicalStorageImpl() {
        return RSApi.getUsedExternalStorage(getNetwork(), RSStorageTypes.CHEMICAL);
    }

    @Override
    public double getUsedItemStorageImpl() {
        return RSApi.getUsedStorage(getNetwork(), RSStorageTypes.ITEM);
    }

    @Override
    public double getUsedFluidStorageImpl() {
        return RSApi.getUsedStorage(getNetwork(), RSStorageTypes.FLUID);
    }

    @Override
    public double getUsedChemicalStorageImpl() {
        return RSApi.getUsedStorage(getNetwork(), RSStorageTypes.CHEMICAL);
    }

    @Override
    public MethodResult craftItemImpl(IComputerAccess computer, IArguments arguments, ItemFilter filter) throws LuaException {
        RSCraftJob job = new RSCraftJob(computer, getLevel(), filter.getCount(), ItemResource.ofItemStack(filter.toItemStack()), getNetwork().getComponent(AutocraftingNetworkComponent.class));
        bridge.addJob(job);
        return MethodResult.of(job);
    }

    @Override
    public MethodResult craftFluidImpl(IComputerAccess computer, IArguments arguments, FluidFilter filter) throws LuaException {
        RSCraftJob job = new RSCraftJob(computer, getLevel(), filter.getAmount(), VariantUtil.ofFluidStack(filter.toFluidStack()), getNetwork().getComponent(AutocraftingNetworkComponent.class));
        bridge.addJob(job);
        return MethodResult.of(job);
    }

    @Override
    public MethodResult craftChemicalImpl(IComputerAccess computer, IArguments arguments, Object /*ChemicalFilter*/ filter0) throws LuaException {
        ChemicalFilter filter = (ChemicalFilter) filter0;

        RSCraftJob job = new RSCraftJob(computer, getLevel(), filter.getAmount(), ChemicalResource.ofChemicalStack(filter.toChemicalStack()), getNetwork().getComponent(AutocraftingNetworkComponent.class));
        bridge.addJob(job);
        return MethodResult.of(job);
    }

    @Override
    public List<?> getCraftingTasksImpl() {
        return RSApi.getCraftingTasks(getNetwork(), bridge);
    }

    @Override
    public MethodResult getCraftingTaskImpl(int id) {
        for (RSCraftJob job : bridge.getJobs()) {
            if (job.getId() == id) {
                return MethodResult.of(job);
            }
        }
        return MethodResult.of(null, StatusConstants.NOT_FOUND.toString());
    }

    @Override
    public int cancelCraftingTasksImpl(GenericFilter<?> filter) throws LuaException {
        AutocraftingNetworkComponent craftingManager = getComponent(AutocraftingNetworkComponent.class);

        int canceled = 0;
        for (TaskStatus status : craftingManager.getStatuses()) {
            if (filter.testRS(new ResourceAmount(status.info().resource(), 1))) {
                craftingManager.cancel(status.info().id());
                canceled++;
            }
        }
        return canceled;
    }

    @Override
    public boolean isCraftableImpl(GenericFilter<?> filter) throws LuaException {
        return RSApi.findPatternFromFilters(getNetwork(), null, filter).leftPresent();
    }

    @Override
    public MethodResult isCraftingImpl(IArguments arguments, GenericFilter<?> filter) throws LuaException {
        AutocraftingNetworkComponent craftingManager = getComponent(AutocraftingNetworkComponent.class);

        for (TaskStatus status : craftingManager.getStatuses()) {
            if (filter.testRS(new ResourceAmount(status.info().resource(), 1))) {
                return MethodResult.of(true);
            }
        }
        return MethodResult.of(false);
    }

    @Override
    public MethodResult getPatternsImpl(@Nullable GenericFilter<?> inputFilter, @Nullable GenericFilter<?> outputFilter) throws LuaException {
        if (inputFilter == null && outputFilter == null) {
            return MethodResult.of(RSApi.getPatterns(getNetwork()));
        }

        Pair<Pattern, String> pattern = RSApi.findPatternFromFilters(getNetwork(), inputFilter, outputFilter);
        if (pattern.rightPresent()) {
            return MethodResult.of(null, pattern.right());
        }

        AutocraftingNetworkComponent autocrafting = getNetwork().getComponent(AutocraftingNetworkComponent.class);
        return MethodResult.of(RSApi.parsePattern(pattern.left(), autocrafting));
    }

    private AbstractNetworkNode getNode() {
        return (AbstractNetworkNode) owner.getBlockEntity().getNode();
    }

    private <I extends NetworkComponent> I getComponent(@NotNull Class<I> componentClass) {
        return getNetwork().getComponent(componentClass);
    }
}
