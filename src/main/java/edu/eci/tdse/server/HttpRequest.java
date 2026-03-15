package edu.eci.tdse.server;

import java.util.*;

/**
 * Represents an incoming HTTP request parsed from the socket stream.
 */
public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, List<String>> queryParams;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(String method,
                       String path,
                       Map<String, List<String>> queryParams,
                       Map<String, String> headers,
                       String body) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
        this.headers = headers;
        this.body = body;
    }

    public String method()  { return method; }
    public String path()    { return path; }
    public Map<String, String> headers() { return headers; }
    public String body()    { return body; }
    public Map<String, List<String>> queryParams() { return queryParams; }

    public String getParam(String key) {
        List<String> values = queryParams.get(key);
        return (values == null || values.isEmpty()) ? null : values.get(0);
    }
}
