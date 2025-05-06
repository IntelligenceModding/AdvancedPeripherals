package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.items.APItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class Items {

    public static final RegistryObject<Item> CHUNK_CONTROLLER = Registration.ITEMS.register("chunk_controller", () -> new APItem(new Item.Properties().stacksTo(16), CCRegistration.ID.CHUNKY_TURTLE, null, APConfig.PERIPHERALS_CONFIG.enableChunkyTurtle::get));

    public static void register() {
    }

}
