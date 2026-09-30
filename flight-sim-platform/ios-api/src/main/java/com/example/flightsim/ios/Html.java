package com.example.flightsim.ios;

/** Minimal HTML escaping for the few server-rendered pages. */
public final class Html {

    private Html() {
    }

    /**
     * Escapes text for use in HTML element content and quoted attributes.
     *
     * @param text text, may be null
     * @return escaped text, empty for null
     */
    public static String escape(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(text.length() + 16);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '&' -> out.append("&amp;");
                case '<' -> out.append("&lt;");
                case '>' -> out.append("&gt;");
                case '"' -> out.append("&quot;");
                case '\'' -> out.append("&#39;");
                default -> out.append(c);
            }
        }
        return out.toString();
    }
}
