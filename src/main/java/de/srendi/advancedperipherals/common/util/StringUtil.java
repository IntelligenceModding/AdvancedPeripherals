package de.srendi.advancedperipherals.common.util;

import java.nio.charset.StandardCharsets;

public class StringUtil {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String toHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

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
}
