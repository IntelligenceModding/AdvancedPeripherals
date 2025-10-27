package de.srendi.advancedperipherals.common.util;

public class StringUtil {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

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

    public static String removeFloatingPoints(String number) {
        if (number.contains(".")) {
            return number.substring(0, number.indexOf("."));
        }
        return number;
    }
}
