package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.items.ARGogglesItem;
import de.srendi.advancedperipherals.common.items.ChunkControllerItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;

public class Items {

    public static final RegistryObject<Item> CHUNK_CONTROLLER = Registration.ITEMS.register("chunk_controller", ChunkControllerItem::new);
    public static final RegistryObject<Item> AR_GOGGLES = Registration.ITEMS.register("ar_goggles", ARGogglesItem::new);

    public static void register() {}


}
