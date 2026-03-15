package edu.eci.tdse.server;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Parses HTTP query strings into a map of parameter names to value lists.
 * Example: "name=John&age=30" → { "name": ["John"], "age": ["30"] }
 */
public class QueryStringParser {

    public static Map<String, List<String>> parse(String query) {
        Map<String, List<String>> params = new HashMap<>();
        if (query == null || query.isBlank()) return params;

        for (String pair : query.split("&")) {
            if (pair.isBlank()) continue;
            int idx = pair.indexOf('=');
            String key   = idx >= 0 ? decode(pair.substring(0, idx)) : decode(pair);
            String value = idx >= 0 ? decode(pair.substring(idx + 1)) : "";
            params.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
        return params;
    }

    private static String decode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }
}
