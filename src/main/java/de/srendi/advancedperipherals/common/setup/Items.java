package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;

public class Items {

    public static final RegistryObject<Item> CHUNK_CONTROLLER = Registration.ITEMS.register("chunk_controller", ChunkController::new);

    public static void register() {}

    private static class ChunkController extends Item {
        public ChunkController() {
            super(new Properties().group(AdvancedPeripherals.TAB));
        }
    }

}
