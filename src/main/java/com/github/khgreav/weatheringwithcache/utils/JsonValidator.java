package com.github.khgreav.weatheringwithcache.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonValidator {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static boolean isValidJson(String data) {
        if (data == null || data.length() == 0) {
            return false;
        }
        try {
            mapper.readTree(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
