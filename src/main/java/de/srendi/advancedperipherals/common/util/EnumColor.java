package de.srendi.advancedperipherals.common.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public enum EnumColor {

    BLACK("\u00a70", "Black", "black", "§0"),
    DARK_BLUE("\u00a71", "Blue", "blue", "§1"),
    DARK_GREEN("\u00a72", "Green", "green", "§2"),
    DARK_AQUA("\u00a73", "Cyan", "cyan", "§3"),
    DARK_RED("\u00a74", "Dark Red", "dark_red", "§4"),
    DARK_PURPLE("\u00a75", "Purple", "purple", "§5"),
    GOLD("\u00a76", "Orange", "orange", "§6"),
    GRAY("\u00a77", "Light Gray", "light_gray", "§7"),
    DARK_GRAY("\u00a78", "Gray", "gray", "§8"),
    BLUE("\u00a79", "Light Blue", "light_blue", "§9"),
    GREEN("\u00a7a", "Lime", "lime", "§a"),
    AQUA("\u00a7b", "Aqua", "aqua", "§b"),
    RED("\u00a7c", "Red", "red", "§c"),
    LIGHT_PURPLE("\u00a7d", "Magenta", "magenta", "§d"),
    YELLOW("\u00a7e", "Yellow", "yellow", "§e"),
    WHITE("\u00a7f", "White", "white", "§f");

    String code;
    String name;
    String technicalName;
    String alternativeCode;

    EnumColor(String code, String name, String technicalName, String alternativeCode) {
        this.code = code;
        this.name = name;
        this.technicalName = technicalName;
        this.alternativeCode = alternativeCode;
    }

    public static ITextComponent buildTextComponent(ITextComponent textComponent) {
        String text = textComponent.getString();
        for (EnumColor color : EnumColor.values()) {
            text.replaceAll(color.alternativeCode, color.code);
        }
        return new StringTextComponent(text);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getTechnicalName() {
        return technicalName;
    }
}
