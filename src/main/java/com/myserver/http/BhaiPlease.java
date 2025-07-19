package com.myserver.http;

import java.util.HashMap;
import java.util.Map;

public class BhaiPlease {
    private final String method;
    private final String fullPath;
    private final Map<String, String> queryParams = new HashMap<>();
    private final String body;

    public BhaiPlease(String method, String fullPath, String body) {
        this.method = method;
        this.fullPath = fullPath;
        this.body = body;
        parseQueryParams(fullPath);
    }

    private void parseQueryParams(String path) {
        if (!path.contains("?")) return;

        String[] parts = path.split("\\?");
        if (parts.length < 2) return;

        for (String param : parts[1].split("&")) {
            String[] kv = param.split("=");
            if (kv.length == 2) {
                queryParams.put(kv[0], kv[1]);
            }
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return fullPath.split("\\?")[0];
    }

    public String getQueryParam(String key) {
        return queryParams.get(key);
    }

    public String getBody() {
        return body;
    }
}
