package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.ae2.AE2Registries;
import de.srendi.advancedperipherals.common.addons.ae2.disk.AEDiskCell;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.items.APItem;
import de.srendi.advancedperipherals.common.items.KeyboardItem;
import de.srendi.advancedperipherals.common.items.MemoryCardItem;
import de.srendi.advancedperipherals.common.items.SmartGlassesInterfaceItem;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.items.WeakAutomataCore;
import de.srendi.advancedperipherals.common.smartglasses.modules.hotkey.HotkeyModuleItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.nightvision.NightVisionModuleItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayGlassesItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

public class APItems {

    public static final DeferredHolder<Item, APItem> COMPUTER_TOOL = APRegistration.ITEMS.register("computer_tool", () -> new APItem(new Item.Properties().stacksTo(1), () -> true));
    public static final DeferredHolder<Item, KeyboardItem> KEYBOARD = APRegistration.ITEMS.register("keyboard", KeyboardItem::new);
    public static final DeferredHolder<Item, APItem> MEMORY_CARD = APRegistration.ITEMS.register("memory_card", MemoryCardItem::new);

    public static final DeferredHolder<Item, APItem> CHUNK_CONTROLLER = APRegistration.ITEMS.register("chunk_controller", () -> new APItem(new Item.Properties().stacksTo(16), APConfig.PERIPHERALS_CONFIG.enableChunkyTurtle));
    public static final DeferredHolder<Item, APItem> WEAK_AUTOMATA_CORE = APRegistration.ITEMS.register("weak_automata_core", () -> new WeakAutomataCore(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, APItem> OVERPOWERED_WEAK_AUTOMATA_CORE = APRegistration.ITEMS.register("overpowered_weak_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), APConfig.METAPHYSICS_CONFIG.enableWeakAutomataCore));
    public static final DeferredHolder<Item, APItem> HUSBANDRY_AUTOMATA_CORE = APRegistration.ITEMS.register("husbandry_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), APConfig.METAPHYSICS_CONFIG.enableHusbandryAutomataCore));
    public static final DeferredHolder<Item, APItem> OVERPOWERED_HUSBANDRY_AUTOMATA_CORE = APRegistration.ITEMS.register("overpowered_husbandry_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), APConfig.METAPHYSICS_CONFIG.enableHusbandryAutomataCore));
    public static final DeferredHolder<Item, APItem> END_AUTOMATA_CORE = APRegistration.ITEMS.register("end_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), APConfig.METAPHYSICS_CONFIG.enableEndAutomataCore));
    public static final DeferredHolder<Item, APItem> OVERPOWERED_END_AUTOMATA_CORE = APRegistration.ITEMS.register("overpowered_end_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), APConfig.METAPHYSICS_CONFIG.enableEndAutomataCore));

    public static final DeferredHolder<Item, SmartGlassesItem> SMART_GLASSES = APRegistration.ITEMS.register("smart_glasses", () -> new SmartGlassesItem(ArmorMaterials.CHAIN));
    public static final DeferredHolder<Item, SmartGlassesItem> SMART_GLASSES_NETHERITE = APRegistration.ITEMS.register("smart_glasses_netherite", () -> new SmartGlassesItem(ArmorMaterials.NETHERITE));
    public static final DeferredHolder<Item, SmartGlassesInterfaceItem> SMART_GLASSES_INTERFACE = APRegistration.ITEMS.register("smart_glasses_interface", SmartGlassesInterfaceItem::new);

    public static final DeferredHolder<Item, HotkeyModuleItem> HOTKEY_MODULE = APRegistration.ITEMS.register("hotkey_module", HotkeyModuleItem::new);
    public static final DeferredHolder<Item, NightVisionModuleItem> NIGHT_VISION_MODULE = APRegistration.ITEMS.register("nightvision_module", NightVisionModuleItem::new);
    public static final DeferredHolder<Item, OverlayGlassesItem> OVERLAY_MODULE = APRegistration.ITEMS.register("overlay_module", OverlayGlassesItem::new);

    public static final DeferredHolder<Item, APItem> CABLE_P2P_TUNNEL = APAddon.AE2.isLoaded() ? (DeferredHolder<Item, APItem>) (DeferredHolder<Item, ?>) AE2Registries.CABLE_P2P_TUNNEL : null;
    public static final DeferredHolder<Item, AEDiskCell> AE_DISK_CELL_1M = APAddon.AE2.isLoaded() ? APRegistration.ITEMS.register("ae_disk_cell_1m", () -> new AEDiskCell(new Item.Properties(), AEDiskCell.Tier.DISK_1M)) : null;
    public static final DeferredHolder<Item, AEDiskCell> AE_DISK_CELL_4M = APAddon.AE2.isLoaded() ? APRegistration.ITEMS.register("ae_disk_cell_4m", () -> new AEDiskCell(new Item.Properties(), AEDiskCell.Tier.DISK_4M)) : null;
    public static final DeferredHolder<Item, AEDiskCell> AE_DISK_CELL_16M = APAddon.AE2.isLoaded() ? APRegistration.ITEMS.register("ae_disk_cell_16m", () -> new AEDiskCell(new Item.Properties(), AEDiskCell.Tier.DISK_16M)) : null;
    public static final DeferredHolder<Item, AEDiskCell> AE_DISK_CELL_64M = APAddon.AE2.isLoaded() ? APRegistration.ITEMS.register("ae_disk_cell_64m", () -> new AEDiskCell(new Item.Properties(), AEDiskCell.Tier.DISK_64M)) : null;
    public static final DeferredHolder<Item, AEDiskCell> AE_DISK_CELL_256M = APAddon.AE2.isLoaded() ? APRegistration.ITEMS.register("ae_disk_cell_256m", () -> new AEDiskCell(new Item.Properties(), AEDiskCell.Tier.DISK_256M)) : null;

    protected static void register() {
    }

}
