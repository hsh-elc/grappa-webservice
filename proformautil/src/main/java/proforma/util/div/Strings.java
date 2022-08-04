package proforma.util.div;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class Strings {
    /**
     * Taken from com.google.common.base.Strings
     *
     * @return {@code true} if the string is null or is the empty string
     * @parama string reference to check
     */
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    /**
     * Source: https://stackoverflow.com/a/1195827
     */
    private static final Pattern GUESS_UTF8_PATTERN =
        Pattern.compile("\\A(\n" +
            "  [\\x09\\x0A\\x0D\\x20-\\x7E]             # ASCII\\n" +
            "| [\\xC2-\\xDF][\\x80-\\xBF]               # non-overlong 2-byte\n" +
            "|  \\xE0[\\xA0-\\xBF][\\x80-\\xBF]         # excluding overlongs\n" +
            "| [\\xE1-\\xEC\\xEE\\xEF][\\x80-\\xBF]{2}  # straight 3-byte\n" +
            "|  \\xED[\\x80-\\x9F][\\x80-\\xBF]         # excluding surrogates\n" +
            "|  \\xF0[\\x90-\\xBF][\\x80-\\xBF]{2}      # planes 1-3\n" +
            "| [\\xF1-\\xF3][\\x80-\\xBF]{3}            # planes 4-15\n" +
            "|  \\xF4[\\x80-\\x8F][\\x80-\\xBF]{2}      # plane 16\n" +
            ")*\\z", Pattern.COMMENTS);


    /**
     * Guesses, if the given byte sequence represents UTF-8 encoded text.
     * Source: https://stackoverflow.com/a/1195827
     *
     * @param bytes a given byte sequence that optionally starts with a UTF-8 BOM
     * @return true, if the heuristic identifies the bytes as UTF-8 encoded text.
     */
    public static boolean looksLikeUTF8(byte[] bytes) {
        String phonyString = new String(bytes, StandardCharsets.ISO_8859_1);
        if (phonyString.startsWith("\uFEFF")) {
            phonyString = phonyString.substring(2);
        }
        return GUESS_UTF8_PATTERN.matcher(phonyString).matches();
    }
}
