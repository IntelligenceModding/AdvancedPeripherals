package de.srendi.advancedperipherals.common.addons.ae2;

import appeng.api.features.P2PTunnelAttunement;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.storage.StorageCells;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import dan200.computercraft.shared.ModRegistry;
import de.srendi.advancedperipherals.common.addons.ae2.disk.AEDiskHandler;
import de.srendi.advancedperipherals.common.addons.ae2.disk.AEDiskKeys;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Function;

public final class AE2Registries {
    private AE2Registries() {}

    public static final DeferredHolder<Item, PartItem<WiredCableP2PTunnelPart>> CABLE_P2P_TUNNEL = registerPart("cable_p2p_tunnel", WiredCableP2PTunnelPart.class, WiredCableP2PTunnelPart::new);

    private static <T extends IPart> DeferredHolder<Item, PartItem<T>> registerPart(String id, Class<T> clazz, Function<IPartItem<T>, T> factory) {
        PartModels.registerModels(PartModelsHelper.createModels(clazz));
        return APRegistration.ITEMS.register(id, () -> new PartItem<>(new Item.Properties(), clazz, factory));
    }

    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey() == AEKeyType.REGISTRY_KEY) {
            AEKeyTypes.register(AEDiskKeys.INSTANCE);
        }
    }

    public static void finishRegister() {
        StorageCells.addCellHandler(AEDiskHandler.INSTANCE);
        P2PTunnelAttunement.registerAttunementTag(CABLE_P2P_TUNNEL.get());
    }

    public static TagKey<Item> getCableP2PTag() {
        return P2PTunnelAttunement.getAttunementTag(CABLE_P2P_TUNNEL.get());
    }

    public static void registerTags(Function<TagKey<Item>, TagsProvider.TagAppender<Item>> tagger) {
        tagger.apply(getCableP2PTag())
            .addOptional(ModRegistry.Items.CABLE.id())
            .addOptional(ModRegistry.Items.WIRED_MODEM.id())
            .addOptional(ModRegistry.Items.WIRED_MODEM_FULL.id());
    }
}
