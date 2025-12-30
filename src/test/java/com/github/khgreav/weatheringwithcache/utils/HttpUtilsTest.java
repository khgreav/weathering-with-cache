package com.github.khgreav.weatheringwithcache.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.khgreav.weatheringwithcache.stubs.MockHttpExchange;

public final class HttpUtilsTest {

    @Test
    void testGetQueryParamsMap() {
        assertEquals(new HashMap<>(), HttpUtils.getQueryParamsMap(null));
        assertEquals(new HashMap<>(), HttpUtils.getQueryParamsMap(""));
        assertEquals(new HashMap<>(), HttpUtils.getQueryParamsMap("key"));
        assertEquals(
            new HashMap<>(
                Map.of(
                    "key", ""
                )
            ),
            HttpUtils.getQueryParamsMap("key=")
        );
        assertEquals(
            new HashMap<>(
                Map.of(
                    "key", "value"
                )
            ),
            HttpUtils.getQueryParamsMap("key=value&key1")
        );
    }

    @Test
    void testAcceptsJson() {
        // no header
        var exchange = new MockHttpExchange();
        assertTrue(
            HttpUtils.acceptsJson(exchange)
        );
        // all
        exchange.getRequestHeaders().set("Accept", "*/*");
        assertTrue(
            HttpUtils.acceptsJson(exchange)
        );
        // json
        exchange.getRequestHeaders().set("Accept", "application/json");
        assertTrue(
            HttpUtils.acceptsJson(exchange)
        );
        // text/plain
        exchange.getRequestHeaders().set("Accept", "text/plain");
        assertFalse(
            HttpUtils.acceptsJson(exchange)
        );
    }
    
}
