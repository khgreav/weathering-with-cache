package com.github.khgreav.weatheringwithcache.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public final class JsonValidatorTest {
    
    @Test
    void testIsValidJson() {
        assertTrue(JsonValidator.isValidJson("{}"));
        assertTrue(JsonValidator.isValidJson("[]"));
        assertTrue(JsonValidator.isValidJson("{\"test\":true}"));
        assertFalse(JsonValidator.isValidJson(""));
        assertFalse(JsonValidator.isValidJson("{"));
        assertFalse(JsonValidator.isValidJson("{\"test\":\"value}"));
        assertFalse(JsonValidator.isValidJson("{\"test\": value}"));
        assertFalse(JsonValidator.isValidJson("{\"test\":\"value\"]"));
    }
}
