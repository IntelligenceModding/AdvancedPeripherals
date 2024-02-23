package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.items.APItem;
import de.srendi.advancedperipherals.common.items.MemoryCardItem;
import de.srendi.advancedperipherals.common.items.WeakAutomataCore;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class Items {

    public static final RegistryObject<Item> CHUNK_CONTROLLER = Registration.ITEMS.register("chunk_controller", () -> new APItem(new Item.Properties().stacksTo(16), CCRegistration.ID.CHUNKY_TURTLE, null, APConfig.PERIPHERALS_CONFIG.enableChunkyTurtle::get));
    public static final RegistryObject<Item> COMPUTER_TOOL = Registration.ITEMS.register("computer_tool", () -> new APItem(new Item.Properties().stacksTo(1), null, null, () -> true));
    public static final RegistryObject<Item> MEMORY_CARD = Registration.ITEMS.register("memory_card", MemoryCardItem::new);
    public static final RegistryObject<Item> END_AUTOMATA_CORE = Registration.ITEMS.register("end_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), CCRegistration.ID.END_AUTOMATA, null, APConfig.METAPHYSICS_CONFIG.enableEndAutomataCore));
    public static final RegistryObject<Item> HUSBANDRY_AUTOMATA_CORE = Registration.ITEMS.register("husbandry_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), CCRegistration.ID.WEAK_AUTOMATA, null, APConfig.METAPHYSICS_CONFIG.enableHusbandryAutomataCore));
    public static final RegistryObject<Item> WEAK_AUTOMATA_CORE = Registration.ITEMS.register("weak_automata_core", () -> new WeakAutomataCore(new Item.Properties().stacksTo(1), CCRegistration.ID.WEAK_AUTOMATA, null));
    public static final RegistryObject<Item> OVERPOWERED_WEAK_AUTOMATA_CORE = Registration.ITEMS.register("overpowered_weak_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), CCRegistration.ID.OP_WEAK_AUTOMATA, null, APConfig.METAPHYSICS_CONFIG.enableWeakAutomataCore));
    public static final RegistryObject<Item> OVERPOWERED_END_AUTOMATA_CORE = Registration.ITEMS.register("overpowered_end_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), CCRegistration.ID.OP_END_AUTOMATA, null, APConfig.METAPHYSICS_CONFIG.enableEndAutomataCore));
    public static final RegistryObject<Item> OVERPOWERED_HUSBANDRY_AUTOMATA_CORE = Registration.ITEMS.register("overpowered_husbandry_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), CCRegistration.ID.OP_HUSBANDRY_AUTOMATA, null, APConfig.METAPHYSICS_CONFIG.enableHusbandryAutomataCore));

    public static void register() {
    }

}
