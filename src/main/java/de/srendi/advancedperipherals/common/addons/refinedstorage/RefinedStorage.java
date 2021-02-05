package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.IRSAPI;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RefinedStorage {
    private final IRSAPI api;

    public RefinedStorage() {
        this.api = API.instance();
    }

    private static INetworkNode read(CompoundNBT tag, NetworkNode node) {
        node.read(tag);
        return node;
    }

    public static List<ItemStack> getItems(INetwork network, boolean craftable) {
        Collection<StackListEntry<ItemStack>> entries;
        if (craftable) {
            entries = network.getItemStorageCache().getCraftablesList().getStacks();
        } else {
            entries = network.getItemStorageCache().getList().getStacks();
        }
        List<ItemStack> result = new ArrayList<>(entries.size());
        for (StackListEntry<ItemStack> entry : entries) {
            result.add(entry.getStack());
        }
        return result;
    }

    public static List<FluidStack> getFluids(INetwork network, boolean craftable) {
        Collection<StackListEntry<FluidStack>> entries;
        if (craftable) {
            entries = network.getFluidStorageCache().getCraftablesList().getStacks();
        } else {
            entries = network.getFluidStorageCache().getList().getStacks();
        }
        List<FluidStack> result = new ArrayList<>(entries.size());
        for (StackListEntry<FluidStack> entry : entries) {
            result.add(entry.getStack());
        }
        return result;
    }

    public void initiate() {
        api.getNetworkNodeRegistry().add(new ResourceLocation(AdvancedPeripherals.MOD_ID, "peripheral"), (tag, world, pos)->read(tag, new RefinedStorageNode(world, pos)));
    }

    public IRSAPI getApi() {
        return api;
    }

}
