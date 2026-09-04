package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.ae2.disk.AEDiskKeys;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import de.srendi.advancedperipherals.lib.annotation.DefaultTranslation;
import net.minecraft.resources.ResourceLocation;

public final class APTranslations {
    @DefaultTranslation(AdvancedPeripherals.NAME)
    public static final String NAME = AdvancedPeripherals.MOD_ID + ".name";

    @DefaultTranslation("&cThis item is disabled in config, so you can craft it, but it'll not have any functionality.")
    public static final String TOOLTIP_DISABLED = tooltip("disabled");
    @DefaultTranslation("&b[&7%s&b] &7For Description")
    public static final String TOOLTIP_SHOW_DESC = tooltip("show_desc");

    @DefaultTranslation("&7Bound to &b%s&7.")
    public static final String TOOLTIP_KEYBOARD_BOUND = tooltipItem(APItems.KEYBOARD.getId(), "bound");
    @DefaultTranslation("&7Bound to &b%s&7.")
    public static final String TOOLTIP_MEMORY_CARD_BOUND = tooltipItem(APItems.MEMORY_CARD.getId(), "bound");

    @DefaultTranslation("You're trying to feed an entity to a soul, but your own body refuses to do this. Maybe something more mechanical can do this?")
    public static final String AUTOMATA_CORE_FEED_BY_PLAYER = text(APItems.WEAK_AUTOMATA_CORE.getId(), "feed_by_player");
    @DefaultTranslation("Bounded the keyboard to %s")
    public static final String KEYBOARD_BOUND = text(APItems.KEYBOARD.getId(), "bound");
    @DefaultTranslation("Target computer is not initialized")
    public static final String KEYBOARD_BOUND_COMPUTER_UNINIT = text(APItems.KEYBOARD.getId(), "bound.computer_uninit");
    @DefaultTranslation("Cleared the keyboard")
    public static final String KEYBOARD_CLEAR = text(APItems.KEYBOARD.getId(), "clear");
    @DefaultTranslation("Press ESC to close the Keyboard Screen")
    public static final String KEYBOARD_CLOSE = text(APItems.KEYBOARD.getId(), "close");
    @DefaultTranslation("Target computer not found")
    public static final String KEYBOARD_COMPUTER_NOT_FOUND = text(APItems.KEYBOARD.getId(), "computer_not_found");
    @DefaultTranslation("Target computer is unusable")
    public static final String KEYBOARD_COMPUTER_UNUSABLE = text(APItems.KEYBOARD.getId(), "computer_unusable");
    @DefaultTranslation("The keyboard it not bound")
    public static final String KEYBOARD_NOT_BOUND = text(APItems.KEYBOARD.getId(), "not_bound");
    @DefaultTranslation("Bounded the memory card to you")
    public static final String MEMORY_CARD_BOUND = text(APItems.MEMORY_CARD.getId(), "bound");
    @DefaultTranslation("Cleared the memory card")
    public static final String MEMORY_CARD_CLEAR = text(APItems.MEMORY_CARD.getId(), "clear");
    @DefaultTranslation("Controlling %1$s. Press %2$s and %3$s to dismount.")
    public static final String SADDLE_TURTLE_DISMOUNT_HINT = text(CCRegistration.ID.Turtle.SADDLE, "dismount.hint");
    @DefaultTranslation("Modules")
    public static final String SMART_GLASSES_MODULES = text(APItems.SMART_GLASSES.getId(), "modules");
    @DefaultTranslation("Player is not wearing Smart Glasses")
    public static final String SMART_GLASSES_NOT_WEARING = text(APItems.SMART_GLASSES.getId(), "not_wearing");
    @DefaultTranslation("Peripherals")
    public static final String SMART_GLASSES_PERIPHERALS = text(APItems.SMART_GLASSES.getId(), "peripherals");

    @DefaultTranslation("An AE2 computer disk that offer extra large spaces.")
    public static final String AE_DISK_DESCRIPTION = APAddon.AE2.isLoaded() ? text(AEDiskKeys.ID, "description") : null;

    private static String tooltip(String key) {
        return "tooltip." + AdvancedPeripherals.MOD_ID + "." + key;
    }

    private static String tooltipItem(ResourceLocation key, String suffix) {
        return tooltip("item.", key, suffix);
    }

    private static String tooltip(String prefix, ResourceLocation key, String suffix) {
        return TranslationUtil.tooltip(prefix + key.getNamespace() + "." + key.getPath()) + "." + suffix;
    }

    private static String text(ResourceLocation key, String suffix) {
        return "text." + key.getNamespace() + "." + key.getPath() + "." + suffix;
    }

    private APTranslations() {}
}
