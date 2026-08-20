package de.srendi.advancedperipherals.common.util;

import java.nio.charset.StandardCharsets;

public class StringUtil {
    /**
     * This method will convert "&[0-9a-z]" to "§[0-9a-z]", then we can make colored message in CC easier
     * If a '&' is behind reverse slash '\', it will be ignored.
     * Note: In CC, you need to use <code>"\\&"</code> to get an unescaped '&' character
     * If the character after '&' is not a digital number or lowercase letter, the & operator will not be escaped as well.
     *
     * Some convert example:
     * "&a" -> "§a"
     * "&" -> "&"
     * "\\&" -> "&"
     * "\\&a" -> "&a"
     * "&A" -> "&A"
     * "& a" -> "& a"
     * "&&a" -> "&§a"
     */
    public static String convertAndToSectionMark(String str) {
        return str == null ? null : str.replaceAll("(?<!\\\\)&(?=[0-9a-z])", "\u00a7").replaceAll("\\\\&", "&");
    }

    public static String removeFloatingPoints(String number) {
        int i = number.indexOf(".");
        // . should not be the first character anyway
        if (i > 0) {
            return number.substring(0, i);
        }
        return number;
    }

    /**
     * Converts from a lua-sourced byte string to a UTF-8 string.
     * </p>
     * Lua encodes bytes as 8bit ASCII (latin1) strings.
     * To convert this to a UTF-8 string, we need to interpret the byte string as ISO-8859-1 to get each individual byte,
     * then convert that to a UTF-8 string.
     *
     * @param asciiByteString the utf encoded string sourced from lua.
     * @return A String, with all characters correctly interpreted as UTF-8.
     */
    public static String byteStringToUTF8(String asciiByteString) {
        return new String(asciiByteString.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    /**
     * Converts from a UTF-8 string to a lua-sourced byte string.
     * </p>
     * Lua enforces that all characters in any string passed to it are valid latin1 characters (0-255)
     * </p>
     * to get around this, we first convert the UTF-8 string to bytes, which are interpreted as characters seperately.
     *
     * @param utf8String the utf encoded string sourced from lua.
     * @return a string, with all multibyte sequence characters split into their individual byte characters.
     */
    public static String utf8ToByteString(String utf8String) {
        return new String(utf8String.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    }

    public static String validateName(String name) {
        if (name == null) {
            return null;
        }
        name = net.minecraft.util.StringUtil.stripColor(name);
        return name.length() <= 50 ? name : null;
    }
}
