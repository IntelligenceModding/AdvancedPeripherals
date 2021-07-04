package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.items.APItem;
import de.srendi.advancedperipherals.common.items.ARGogglesItem;
import de.srendi.advancedperipherals.common.items.MemoryCardItem;
import de.srendi.advancedperipherals.common.items.WeakMechanicSoul;
import net.minecraft.item.Item;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;

public class Items {

    public static final RegistryObject<Item> CHUNK_CONTROLLER = Registration.ITEMS.register("chunk_controller", () -> new APItem(new Item.Properties().stacksTo(16),
            "chunky_turtle", null, new TranslationTextComponent("item.advancedperipherals.tooltip.chunk_controller")));
    public static final RegistryObject<Item> END_MECHANIC_SOUL = Registration.ITEMS.register("end_mechanic_soul", () -> new APItem(new Item.Properties().stacksTo(1),
            "end_mechanic_soul_turtle", null, new TranslationTextComponent("item.advancedperipherals.tooltip.end_mechanic_soul")));
    public static final RegistryObject<Item> WEAK_MECHANIC_SOUL = Registration.ITEMS.register("weak_mechanic_soul", () -> new WeakMechanicSoul(new Item.Properties().stacksTo(1),
            "weak_mechanic_soul_turtle", null, new TranslationTextComponent("item.advancedperipherals.tooltip.weak_mechanic_soul")));
    public static final RegistryObject<Item> OVERPOWERED_WEAK_MECHANIC_SOUL = Registration.ITEMS.register("overpowered_weak_mechanic_soul", () -> new APItem(new Item.Properties().stacksTo(1),
            "overpowered_weak_mechanic_soul_turtle", null, new TranslationTextComponent("item.advancedperipherals.tooltip.overpowered_weak_mechanic_soul")));
    public static final RegistryObject<ARGogglesItem> AR_GOGGLES = Registration.ITEMS.register("ar_goggles", ARGogglesItem::new);
    public static final RegistryObject<Item> COMPUTER_TOOL = Registration.ITEMS.register("computer_tool", () -> new APItem(new Item.Properties().stacksTo(1),
            "chunky_turtle", null, new TranslationTextComponent("item.advancedperipherals.tooltip.computer_tool")));
    public static final RegistryObject<Item> MEMORY_CARD = Registration.ITEMS.register("memory_card", MemoryCardItem::new);

    public static void register() {
    }


}
