package edu.eci.tdse.server;

import java.util.Map;

/**
 * Maps file extensions to MIME types for HTTP responses.
 */
public class MimeTypes {

    private static final Map<String, String> MIME = Map.ofEntries(
            Map.entry("html", "text/html"),
            Map.entry("htm",  "text/html"),
            Map.entry("css",  "text/css"),
            Map.entry("js",   "application/javascript"),
            Map.entry("json", "application/json"),
            Map.entry("txt",  "text/plain"),
            Map.entry("png",  "image/png"),
            Map.entry("jpg",  "image/jpeg"),
            Map.entry("jpeg", "image/jpeg"),
            Map.entry("gif",  "image/gif"),
            Map.entry("svg",  "image/svg+xml"),
            Map.entry("ico",  "image/x-icon")
    );

    public static String fromFilename(String filename) {
        if (filename == null) return "application/octet-stream";
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) return "application/octet-stream";
        String ext = filename.substring(dot + 1).toLowerCase();
        return MIME.getOrDefault(ext, "application/octet-stream");
    }
}
