package edu.eci.tdse.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

/**
 * Serves static files (HTML, CSS, JS, PNG, etc.) from the
 * webroot directory bundled inside the JAR (classpath).
 */
public class StaticFileService {

    private static final String WEBROOT = "webroot";

    public StaticResult tryServe(String requestPath) {
        if (requestPath == null || requestPath.isBlank()) return null;

        String clean = requestPath.startsWith("/") ? requestPath.substring(1) : requestPath;
        if (clean.isBlank()) clean = "index.html";

        if (clean.contains("..")) return null;

        String resourcePath = WEBROOT + "/" + clean;

        try (InputStream is = StaticFileService.class.getClassLoader()
                .getResourceAsStream(resourcePath)) {
            if (is != null) {
                byte[] data = is.readAllBytes();
                String mime = MimeTypes.fromFilename(Paths.get(clean).getFileName().toString());
                return new StaticResult(200, mime, data);
            }
        } catch (IOException e) {
            return new StaticResult(500, "text/plain", ("Error: " + e.getMessage()).getBytes());
        }


        Path fsPath = Path.of("src", "main", "resources", resourcePath);
        if (Files.exists(fsPath) && !Files.isDirectory(fsPath)) {
            try {
                byte[] data = Files.readAllBytes(fsPath);
                String mime = MimeTypes.fromFilename(fsPath.getFileName().toString());
                return new StaticResult(200, mime, data);
            } catch (IOException e) {
                return new StaticResult(500, "text/plain", ("Error: " + e.getMessage()).getBytes());
            }
        }

        return null;
    }

    public record StaticResult(int status, String contentType, byte[] body) {}
}
