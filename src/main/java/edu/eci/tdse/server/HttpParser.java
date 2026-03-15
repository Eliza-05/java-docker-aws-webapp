package edu.eci.tdse.server;

import java.io.BufferedReader;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses raw HTTP/1.1 requests from a socket's BufferedReader.
 */
public class HttpParser {

    public static HttpRequest parse(BufferedReader in) throws Exception {
        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isBlank()) return null;

        String[] parts = requestLine.split(" ");
        if (parts.length < 2) return null;

        String method = parts[0].trim();
        String rawUrl = parts[1].trim();


        Map<String, String> headers = new LinkedHashMap<>();
        String line;
        while ((line = in.readLine()) != null) {
            if (line.isBlank()) break;
            int idx = line.indexOf(':');
            if (idx > 0) {
                headers.put(line.substring(0, idx).trim(), line.substring(idx + 1).trim());
            }
        }

        URI uri = new URI(rawUrl);
        String path  = uri.getPath();
        String query = uri.getQuery();
        Map<String, List<String>> queryParams = QueryStringParser.parse(query);


        String body = "";
        String cl = headers.get("Content-Length");
        if (cl != null) {
            int length = Integer.parseInt(cl.trim());
            if (length > 0) {
                char[] buf = new char[length];
                in.read(buf, 0, length);
                body = new String(buf);
            }
        }

        return new HttpRequest(method, path, queryParams, headers, body);
    }
}
