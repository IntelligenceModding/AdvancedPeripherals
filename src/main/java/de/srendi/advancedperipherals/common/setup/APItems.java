package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.items.*;
import de.srendi.advancedperipherals.common.items.base.SmartGlassesMaterials;
import de.srendi.advancedperipherals.common.smartglasses.modules.hotkey.HotkeyModuleItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.nightvision.NightVisionModuleItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayGlassesItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class APItems {

    public static final RegistryObject<Item> CHUNK_CONTROLLER = APRegistration.ITEMS.register("chunk_controller", () -> new APItem(new Item.Properties().stacksTo(16), APConfig.PERIPHERALS_CONFIG.enableChunkyTurtle));
    public static final RegistryObject<SmartGlassesItem> SMART_GLASSES = APRegistration.ITEMS.register("smart_glasses", () -> new SmartGlassesItem(SmartGlassesMaterials.CHAIN));
    public static final RegistryObject<SmartGlassesItem> SMART_GLASSES_NETHERITE = APRegistration.ITEMS.register("smart_glasses_netherite", () -> new SmartGlassesItem(SmartGlassesMaterials.NETHERITE));
    public static final RegistryObject<SmartGlassesInterfaceItem> SMART_GLASSES_INTERFACE = APRegistration.ITEMS.register("smart_glasses_interface", SmartGlassesInterfaceItem::new);
    public static final RegistryObject<OverlayGlassesItem> OVERLAY_MODULE = APRegistration.ITEMS.register("overlay_module", OverlayGlassesItem::new);
    public static final RegistryObject<HotkeyModuleItem> HOTKEY_MODULE = APRegistration.ITEMS.register("hotkey_module", HotkeyModuleItem::new);
    public static final RegistryObject<NightVisionModuleItem> NIGHT_VISION_MODULE = APRegistration.ITEMS.register("nightvision_module", NightVisionModuleItem::new);
    public static final RegistryObject<Item> COMPUTER_TOOL = APRegistration.ITEMS.register("computer_tool", () -> new APItem(new Item.Properties().stacksTo(1), () -> true));
    public static final RegistryObject<Item> MEMORY_CARD = APRegistration.ITEMS.register("memory_card", MemoryCardItem::new);
    public static final RegistryObject<Item> END_AUTOMATA_CORE = APRegistration.ITEMS.register("end_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), APConfig.METAPHYSICS_CONFIG.enableEndAutomataCore));
    public static final RegistryObject<Item> HUSBANDRY_AUTOMATA_CORE = APRegistration.ITEMS.register("husbandry_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), APConfig.METAPHYSICS_CONFIG.enableHusbandryAutomataCore));
    public static final RegistryObject<Item> WEAK_AUTOMATA_CORE = APRegistration.ITEMS.register("weak_automata_core", () -> new WeakAutomataCore(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> OVERPOWERED_WEAK_AUTOMATA_CORE = APRegistration.ITEMS.register("overpowered_weak_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), APConfig.METAPHYSICS_CONFIG.enableWeakAutomataCore));
    public static final RegistryObject<Item> OVERPOWERED_END_AUTOMATA_CORE = APRegistration.ITEMS.register("overpowered_end_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), APConfig.METAPHYSICS_CONFIG.enableEndAutomataCore));
    public static final RegistryObject<Item> OVERPOWERED_HUSBANDRY_AUTOMATA_CORE = APRegistration.ITEMS.register("overpowered_husbandry_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), APConfig.METAPHYSICS_CONFIG.enableHusbandryAutomataCore));

    protected static void register() {
    }

}
