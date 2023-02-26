package de.srendi.advancedperipherals.common.util;

import net.minecraft.network.chat.Component;

public enum EnumColor {

    BLACK("\u00a70", "Black", "black", "§0", 0, new float[]{0f, 0f, 0f, 1f}),
    DARK_BLUE("\u00a71", "Blue", "blue", "§1", 0xFF0000c9, new float[]{0f, 0f, 0.804f, 1f}),
    DARK_GREEN("\u00a72", "Green", "green", "§2", 0xFF0c9400, new float[]{0.048f, 0.592f, 0f, 1f}),
    DARK_AQUA("\u00a73", "Cyan", "cyan", "§3", 0xFF009494, new float[]{0f, 0.592f, 0.592f, 1f}),
    DARK_RED("\u00a74", "Dark Red", "dark_red", "§4", 0xFF940000, new float[]{0.592f, 0f, 0f, 1f}),
    DARK_PURPLE("\u00a75", "Purple", "purple", "§5", 0xFF510094, new float[]{0.324f, 0f, 0.592f, 1f}),
    GOLD("\u00a76", "Orange", "orange", "§6", 0xFFb59700, new float[]{1f, 0.407f, 0f, 1f}),
    GRAY("\u00a77", "Light Gray", "light_gray", "§7", 0xFF616161, new float[]{0.388f, 0.388f, 0.388f, 1f}),
    DARK_GRAY("\u00a78", "Gray", "gray", "§8", 0xFF4a4a4a, new float[]{0.296f, 0.296f, 0.296f, 1f}),
    BLUE("\u00a79", "Light Blue", "light_blue", "§9", 0xFF1919ff, new float[]{0.098f, 0.098f, 1f, 1f}),
    GREEN("\u00a7a", "Lime", "lime", "§a", 0xFF00e02d, new float[]{0f, 0.878f, 0.176f, 1f}),
    AQUA("\u00a7b", "Aqua", "aqua", "§b", 0xFF17ffe4, new float[]{0.090f, 1f, 0.894f, 1f}),
    RED("\u00a7c", "Red", "red", "§c", 0xFFff1c1c, new float[]{1f, 0.109f, 0.109f, 1f}),
    LIGHT_PURPLE("\u00a7d", "Magenta", "magenta", "§d", 0xFF7424ff, new float[]{0.454f, 0.141f, 1f, 1f}),
    YELLOW("\u00a7e", "Yellow", "yellow", "§e", 0xFFc8ff00, new float[]{0.784f, 1f, 0f, 1f}),
    WHITE("\u00a7f", "White", "white", "§f", 0xFFffffff, new float[]{1f, 1f, 1f, 1f});

    private final String code;
    private final String name;
    private final String technicalName;
    private final String alternativeCode;
    private final int hex;
    private final float[] rgb;

    EnumColor(String code, String name, String technicalName, String alternativeCode, int hex, float[] rgb) {
        this.code = code;
        this.name = name;
        this.technicalName = technicalName;
        this.alternativeCode = alternativeCode;
        this.hex = hex;
        this.rgb = rgb;
    }

    public static Component buildTextComponent(Component textComponent) {
        String text = textComponent.getString();
        for (EnumColor color : EnumColor.values()) {
            text = text.replaceAll(color.alternativeCode, color.code);
        }
        return Component.literal(text);
    }

    public float[] getRgb() {
        return rgb;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return name;
    }

    public String getTechnicalName() {
        return technicalName;
    }

    public int getHex() {
        return hex;
    }
}
