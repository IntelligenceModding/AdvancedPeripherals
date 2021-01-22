package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.IRSAPI;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class RefinedStorage {
    private final IRSAPI api;

    public void initiate() {
        api.getNetworkNodeRegistry().add(new ResourceLocation(AdvancedPeripherals.MOD_ID, "peripheral"),
                (tag, world, pos) -> read(tag, new RefinedStorageNode(world, pos)));
    }

    public RefinedStorage() {
        this.api = API.instance();
    }

    public IRSAPI getApi() {
        return api;
    }

    private static INetworkNode read(CompoundNBT tag, NetworkNode node) {
        node.read(tag);
        return node;
    }
}
