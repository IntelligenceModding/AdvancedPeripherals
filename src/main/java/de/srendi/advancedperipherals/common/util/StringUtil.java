package de.srendi.advancedperipherals.common.util;

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
     *   Note: In CC, you need to use <code>"\\&"</code> to get an unescaped '&' character
     * If the character after '&' is not a digital number or lowercase letter, the & operator will not be escaped as well.
     *
     * Some convert example:
     *  "&a" -> "§a"
     *  "&" -> "&"
     *  "\\&" -> "&"
     *  "\\&a" -> "&a"
     *  "&A" -> "&A"
     *  "& a" -> "& a"
     *  "&&a" -> "&§a"
     */
    public static String convertAndToSectionMark(String str) {
        return str == null ? null : str.replaceAll("(?<!\\\\)&(?=[0-9a-z])", "\u00a7").replaceAll("\\\\&", "&");
    }
}
