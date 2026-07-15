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
import de.srendi.advancedperipherals.lib.annotation.DefaultTooltip;
import de.srendi.advancedperipherals.lib.annotation.DefaultTranslation;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

public class APItems {
    @DefaultTranslation("Computer Tool")
    @DefaultTooltip("&7This tool was made to tune our blocks. But for now, it's just a blue useless wrench.")
    public static final DeferredHolder<Item, APItem> COMPUTER_TOOL = APRegistration.ITEMS.register("computer_tool", () -> new APItem(new Item.Properties().stacksTo(1), () -> true));
    @DefaultTranslation("Keyboard")
    @DefaultTooltip("&7Control a computer remotely, or intercept player inputs to smartglasses.")
    public static final DeferredHolder<Item, KeyboardItem> KEYBOARD = APRegistration.ITEMS.register("keyboard", KeyboardItem::new);
    @DefaultTranslation("Memory Card")
    @DefaultTooltip("&7Can save the rights of a player to use it in an inventory manager.")
    public static final DeferredHolder<Item, APItem> MEMORY_CARD = APRegistration.ITEMS.register("memory_card", MemoryCardItem::new);

    @DefaultTranslation("Chunk Controller")
    @DefaultTooltip("&7A crafting ingredient for the Chunky Turtle.")
    public static final DeferredHolder<Item, APItem> CHUNK_CONTROLLER = APRegistration.ITEMS.register("chunk_controller", () -> new APItem(new Item.Properties().stacksTo(16), APConfig.PERIPHERALS_CONFIG.enableChunkyTurtle));
    @DefaultTranslation("Weak Automata Core")
    @DefaultTooltip("&7Upgrade for turtles, which makes turtles more useful.")
    public static final DeferredHolder<Item, APItem> WEAK_AUTOMATA_CORE = APRegistration.ITEMS.register("weak_automata_core", () -> new WeakAutomataCore(new Item.Properties().stacksTo(1)));
    @DefaultTranslation("Overpowered Weak Automata Core")
    @DefaultTooltip("&7Improved version of the weak automata core, that provides some overpowered uses! Be careful, the upgrade is very fragile.")
    public static final DeferredHolder<Item, APItem> OVERPOWERED_WEAK_AUTOMATA_CORE = APRegistration.ITEMS.register("overpowered_weak_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), APConfig.METAPHYSICS_CONFIG.enableWeakAutomataCore));
    @DefaultTranslation("Husbandry Automata Core")
    @DefaultTooltip("&7Upgrade for turtles, that allows basic and advanced interactions with animals.")
    public static final DeferredHolder<Item, APItem> HUSBANDRY_AUTOMATA_CORE = APRegistration.ITEMS.register("husbandry_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), APConfig.METAPHYSICS_CONFIG.enableHusbandryAutomataCore));
    @DefaultTranslation("Overpowered Husbandry Automata Core")
    @DefaultTooltip("&7Improved version of the husbandry automata core, that provides some overpowered uses! Be careful, the upgrade is very fragile.")
    public static final DeferredHolder<Item, APItem> OVERPOWERED_HUSBANDRY_AUTOMATA_CORE = APRegistration.ITEMS.register("overpowered_husbandry_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), APConfig.METAPHYSICS_CONFIG.enableHusbandryAutomataCore));
    @DefaultTranslation("End Automata Core")
    @DefaultTooltip("&7Upgrade for turtles, that allows basic interaction with the world and teleportation in one dimension.")
    public static final DeferredHolder<Item, APItem> END_AUTOMATA_CORE = APRegistration.ITEMS.register("end_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), APConfig.METAPHYSICS_CONFIG.enableEndAutomataCore));
    @DefaultTranslation("Overpowered End Automata Core")
    @DefaultTooltip("&7Improved version of the end automata core, that provides some overpowered uses! Be careful, the upgrade is very fragile.")
    public static final DeferredHolder<Item, APItem> OVERPOWERED_END_AUTOMATA_CORE = APRegistration.ITEMS.register("overpowered_end_automata_core", () -> new APItem(new Item.Properties().stacksTo(1), APConfig.METAPHYSICS_CONFIG.enableEndAutomataCore));

    @DefaultTranslation("Smart Glasses")
    @DefaultTooltip("&7A portable computer that supports multiple peripherals and modules.")
    public static final DeferredHolder<Item, SmartGlassesItem> SMART_GLASSES = APRegistration.ITEMS.register("smart_glasses", () -> new SmartGlassesItem(ArmorMaterials.CHAIN));
    @DefaultTranslation("Netherite reinforced Smart Glasses")
    @DefaultTooltip("&7An upgraded version of smart glasses that provides a bit more armor.")
    public static final DeferredHolder<Item, SmartGlassesItem> SMART_GLASSES_NETHERITE = APRegistration.ITEMS.register("smart_glasses_netherite", () -> new SmartGlassesItem(ArmorMaterials.NETHERITE));
    @DefaultTranslation("Smart Glasses Interface")
    @DefaultTooltip("&7Can access equipped smart glasses without taking it off!")
    public static final DeferredHolder<Item, SmartGlassesInterfaceItem> SMART_GLASSES_INTERFACE = APRegistration.ITEMS.register("smart_glasses_interface", SmartGlassesInterfaceItem::new);

    @DefaultTranslation("Hotkey Module")
    @DefaultTooltip("&7Capture a predefined key and forward it as an event.")
    public static final DeferredHolder<Item, HotkeyModuleItem> HOTKEY_MODULE = APRegistration.ITEMS.register("hotkey_module", HotkeyModuleItem::new);
    @DefaultTranslation("Night Vision Module")
    @DefaultTooltip("&7Give wearer better view of the world.")
    public static final DeferredHolder<Item, NightVisionModuleItem> NIGHT_VISION_MODULE = APRegistration.ITEMS.register("nightvision_module", NightVisionModuleItem::new);
    @DefaultTranslation("Overlay Module")
    @DefaultTooltip("&7Draw anything on your eyes!")
    public static final DeferredHolder<Item, OverlayGlassesItem> OVERLAY_MODULE = APRegistration.ITEMS.register("overlay_module", OverlayGlassesItem::new);

    @DefaultTranslation("Cable P2P Tunnel")
    @DefaultTooltip("&7Connect wired network via AE2.")
    public static final DeferredHolder<Item, APItem> CABLE_P2P_TUNNEL = APAddon.AE2.isLoaded() ? (DeferredHolder<Item, APItem>) (DeferredHolder<Item, ?>) AE2Registries.CABLE_P2P_TUNNEL : null;
    @DefaultTranslation("AE Disk Cell 1M")
    @DefaultTooltip("&7Provides a bit external file storage via ME Bridge.")
    public static final DeferredHolder<Item, AEDiskCell> AE_DISK_CELL_1M = APAddon.AE2.isLoaded() ? APRegistration.ITEMS.register("ae_disk_cell_1m", () -> new AEDiskCell(new Item.Properties(), AEDiskCell.Tier.DISK_1M)) : null;
    @DefaultTranslation("AE Disk Cell 4M")
    @DefaultTooltip("&7Provides some external file storage via ME Bridge.")
    public static final DeferredHolder<Item, AEDiskCell> AE_DISK_CELL_4M = APAddon.AE2.isLoaded() ? APRegistration.ITEMS.register("ae_disk_cell_4m", () -> new AEDiskCell(new Item.Properties(), AEDiskCell.Tier.DISK_4M)) : null;
    @DefaultTranslation("AE Disk Cell 16M")
    @DefaultTooltip("&7Provides some more external file storage via ME Bridge.")
    public static final DeferredHolder<Item, AEDiskCell> AE_DISK_CELL_16M = APAddon.AE2.isLoaded() ? APRegistration.ITEMS.register("ae_disk_cell_16m", () -> new AEDiskCell(new Item.Properties(), AEDiskCell.Tier.DISK_16M)) : null;
    @DefaultTranslation("AE Disk Cell 64M")
    @DefaultTooltip("&7Provides even more external file storage via ME Bridge.")
    public static final DeferredHolder<Item, AEDiskCell> AE_DISK_CELL_64M = APAddon.AE2.isLoaded() ? APRegistration.ITEMS.register("ae_disk_cell_64m", () -> new AEDiskCell(new Item.Properties(), AEDiskCell.Tier.DISK_64M)) : null;
    @DefaultTranslation("AE Disk Cell 256M")
    @DefaultTooltip("&7Provides a lot external file storage via ME Bridge.")
    public static final DeferredHolder<Item, AEDiskCell> AE_DISK_CELL_256M = APAddon.AE2.isLoaded() ? APRegistration.ITEMS.register("ae_disk_cell_256m", () -> new AEDiskCell(new Item.Properties(), AEDiskCell.Tier.DISK_256M)) : null;

    protected static void register() {
    }

}
