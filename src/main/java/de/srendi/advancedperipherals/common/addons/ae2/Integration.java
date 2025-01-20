package de.srendi.advancedperipherals.common.addons.ae2;

import appeng.api.features.P2PTunnelAttunement;
import dan200.computercraft.shared.Capabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class Integration implements Runnable {

    @Override
    public void run() {
    }

    public static void onComplete() {
        P2PTunnelAttunement.registerAttunementTag(Registries.CABLE_P2P_TUNNEL.get());
    }

    public static TagKey<Item> getCableP2PTag() {
        return P2PTunnelAttunement.getAttunementTag(Registries.CABLE_P2P_TUNNEL.get());
    }
}
