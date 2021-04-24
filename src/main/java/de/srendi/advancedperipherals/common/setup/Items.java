package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.items.ARGogglesItem;
import de.srendi.advancedperipherals.common.items.ChunkControllerItem;
import de.srendi.advancedperipherals.common.items.ComputerToolItem;
import de.srendi.advancedperipherals.common.items.MemoryCardItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;

public class Items {

    public static final RegistryObject<Item> CHUNK_CONTROLLER = Registration.ITEMS.register("chunk_controller", ChunkControllerItem::new);
    public static final RegistryObject<ARGogglesItem> AR_GOGGLES = Registration.ITEMS.register("ar_goggles", ARGogglesItem::new);
    public static final RegistryObject<Item> COMPUTER_TOOL = Registration.ITEMS.register("computer_tool", ComputerToolItem::new);
    public static final RegistryObject<Item> MEMORY_CARD = Registration.ITEMS.register("memory_card", MemoryCardItem::new);

    public static void register() {}


}
