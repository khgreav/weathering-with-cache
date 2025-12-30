package com.github.khgreav.weatheringwithcache.utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

public final class HttpUtils {

    private HttpUtils() {
        throw new AssertionError("Cannot instantiate.");
    }

    public static HashMap<String, String> getQueryParamsMap(String params) {
        var map = new HashMap<String, String>();
        if (params == null || params.length() == 0) {
            return map;
        }
        int start = 0;
        int n = params.length();
        while (start < n) {
            var keyEnd = params.indexOf('=', start);
            var pairEnd = params.indexOf('&', start);

            if (keyEnd == -1) {
                break;
            }
            
            String key = URLDecoder.decode(params.substring(start, keyEnd), StandardCharsets.UTF_8);
            if (pairEnd == -1) {
                String value = URLDecoder.decode(params.substring(keyEnd + 1), StandardCharsets.UTF_8);
                map.put(key, value);
                break;
            } else {
                String value = URLDecoder.decode(params.substring(keyEnd + 1, pairEnd), StandardCharsets.UTF_8);
                map.put(key, value);
                start = pairEnd + 1;
            }
        }
        return map;
    }

    public static boolean acceptsJson(HttpExchange exchange) {
        List<String> accept = exchange.getRequestHeaders().get("Accept");

        if (accept == null || accept.isEmpty()) {
            return true;
        }

        for (String val : accept) {
            if (val.contains("application/json") || val.contains("*/*")) {
                return true;
            }
        }
        return false;
    }
}
