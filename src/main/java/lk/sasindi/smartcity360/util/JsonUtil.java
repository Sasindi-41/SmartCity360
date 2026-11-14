package lk.sasindi.smartcity360.util;

public final class JsonUtil {

    private JsonUtil() {
    }

    public static String escape(String value) {

        if (value == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        for (char c : value.toCharArray()) {

            switch (c) {
                case '"' -> result.append("\\\"");
                case '\\' -> result.append("\\\\");
                case '\b' -> result.append("\\b");
                case '\f' -> result.append("\\f");
                case '\n' -> result.append("\\n");
                case '\r' -> result.append("\\r");
                case '\t' -> result.append("\\t");

                default -> {
                    if (c < 0x20) {
                        result.append(String.format("\\u%04x", (int) c));
                    } else {
                        result.append(c);
                    }
                }
            }
        }

        return result.toString();
    }
}