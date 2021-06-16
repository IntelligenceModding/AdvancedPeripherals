package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.items.*;
import net.minecraft.item.Item;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;

public class Items {

    public static final RegistryObject<Item> CHUNK_CONTROLLER = Registration.ITEMS.register("chunk_controller",()-> new APItem(new Item.Properties().stacksTo(16),
            "chunky_turtle", null, new TranslationTextComponent("item.advancedperipherals.tooltip.chunk_controller")));
    public static final RegistryObject<ARGogglesItem> AR_GOGGLES = Registration.ITEMS.register("ar_goggles", ARGogglesItem::new);
    public static final RegistryObject<Item> COMPUTER_TOOL = Registration.ITEMS.register("computer_tool", ()-> new APItem(new Item.Properties().stacksTo(1),
            "chunky_turtle", null, new TranslationTextComponent("item.advancedperipherals.tooltip.computer_tool")));
    public static final RegistryObject<Item> MEMORY_CARD = Registration.ITEMS.register("memory_card", MemoryCardItem::new);

    public static void register() {
    }


}
