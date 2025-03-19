package de.srendi.advancedperipherals.common.addons.ae2;

import appeng.api.features.P2PTunnelAttunement;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import dan200.computercraft.shared.Registry.ModItems;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public final class AE2Registries {
    private AE2Registries() {}

    public static final RegistryObject<PartItem<WiredCableP2PTunnelPart>> CABLE_P2P_TUNNEL = registerPart("cable_p2p_tunnel", WiredCableP2PTunnelPart.class, WiredCableP2PTunnelPart::new);

    private static <T extends IPart> RegistryObject<PartItem<T>> registerPart(String id, Class<T> clazz, Function<IPartItem<T>, T> factory) {
        PartModels.registerModels(PartModelsHelper.createModels(clazz));
        return APRegistration.ITEMS.register(id, () -> new PartItem<>(new Item.Properties(), clazz, factory));
    }

    public static void finishRegister() {
        P2PTunnelAttunement.registerAttunementTag(CABLE_P2P_TUNNEL.get());
    }

    public static TagKey<Item> getCableP2PTag() {
        return P2PTunnelAttunement.getAttunementTag(CABLE_P2P_TUNNEL.get());
    }

    public static void registerTags(Function<TagKey<Item>, TagsProvider.TagAppender<Item>> tagger) {
        tagger.apply(getCableP2PTag())
            .add(ModItems.CABLE.get())
            .add(ModItems.WIRED_MODEM.get())
            .add(ModItems.WIRED_MODEM_FULL.get());
    }
}
