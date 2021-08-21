package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleChunky;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.*;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.items.APItem;
import de.srendi.advancedperipherals.common.items.ARGogglesItem;
import de.srendi.advancedperipherals.common.items.MemoryCardItem;
import de.srendi.advancedperipherals.common.items.WeakAutomataCore;
import net.minecraft.item.Item;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;

public class Items {

    public static final RegistryObject<Item> CHUNK_CONTROLLER = Registration.ITEMS.register("chunk_controller", () -> new APItem(new Item.Properties().stacksTo(16),
            TurtleChunky.ID, null, new TranslationTextComponent("item.advancedperipherals.tooltip.chunk_controller"), () -> AdvancedPeripheralsConfig.enableChunkyTurtle));
    public static final RegistryObject<ARGogglesItem> AR_GOGGLES = Registration.ITEMS.register("ar_goggles", ARGogglesItem::new);
    public static final RegistryObject<Item> COMPUTER_TOOL = Registration.ITEMS.register("computer_tool", () -> new APItem(new Item.Properties().stacksTo(1),
            null, null, new TranslationTextComponent("item.advancedperipherals.tooltip.computer_tool"), () -> true));
    public static final RegistryObject<Item> MEMORY_CARD = Registration.ITEMS.register("memory_card", MemoryCardItem::new);

    public static final RegistryObject<Item> END_AUTOMATA_CORE = Registration.ITEMS.register("end_automata_core", () -> new APItem(new Item.Properties().stacksTo(1),
            EndAutomata.ID, null, new TranslationTextComponent("item.advancedperipherals.tooltip.end_automata_core"), () -> AdvancedPeripheralsConfig.enableEndAutomataCore));
    public static final RegistryObject<Item> HUSBANDRY_AUTOMATA_CORE = Registration.ITEMS.register("husbandry_automata_core", () -> new APItem(new Item.Properties().stacksTo(1),
            HusbandryAutomata.ID, null, new TranslationTextComponent("item.advancedperipherals.tooltip.husbandry_automata_core"), () -> AdvancedPeripheralsConfig.enableHusbandryAutomataCore));
    public static final RegistryObject<Item> WEAK_AUTOMATA_CORE = Registration.ITEMS.register("weak_automata_core", () -> new WeakAutomataCore(new Item.Properties().stacksTo(1),
            WeakAutomata.ID, null, new TranslationTextComponent("item.advancedperipherals.tooltip.weak_automata_core")));
    public static final RegistryObject<Item> OVERPOWERED_WEAK_AUTOMATA_CORE = Registration.ITEMS.register("overpowered_weak_automata_core", () -> new APItem(new Item.Properties().stacksTo(1),
            OverpoweredWeakAutomata.ID, null, new TranslationTextComponent("item.advancedperipherals.tooltip.overpowered_weak_automata_core"), () -> AdvancedPeripheralsConfig.enableWeakAutomataCore));
    public static final RegistryObject<Item> OVERPOWERED_END_AUTOMATA_CORE = Registration.ITEMS.register("overpowered_end_automata_core", () -> new APItem(new Item.Properties().stacksTo(1),
            OverpoweredEndAutomata.ID, null, new TranslationTextComponent("item.advancedperipherals.tooltip.overpowered_end_automata_core"), () -> AdvancedPeripheralsConfig.enableEndAutomataCore));
    public static final RegistryObject<Item> OVERPOWERED_HUSBANDRY_AUTOMATA_CORE = Registration.ITEMS.register("overpowered_husbandry_automata_core", () -> new APItem(new Item.Properties().stacksTo(1),
            OverpoweredHusbandryAutomata.ID, null, new TranslationTextComponent("item.advancedperipherals.tooltip.overpowered_husbandry_automata_core"), () -> AdvancedPeripheralsConfig.enableHusbandryAutomataCore));

    public static void register() {
    }


}
