package de.srendi.advancedperipherals.common.addons.ae2;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public final class Registries {
    private Registries() {}

    public static final RegistryObject<PartItem<WiredCableP2PTunnelPart>> CABLE_P2P_TUNNEL = registerPart("cable_p2p_tunnel", WiredCableP2PTunnelPart.class, WiredCableP2PTunnelPart::new);

    private static <T extends IPart> RegistryObject<PartItem<T>> registerPart(String id, Class<T> clazz, Function<IPartItem<T>, T> factory) {
        PartModels.registerModels(PartModelsHelper.createModels(clazz));
        return APRegistration.ITEMS.register(id, () -> new PartItem<>(new Item.Properties(), clazz, factory));
    }
}
