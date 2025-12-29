package com.github.khgreav.weatheringwithcache.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Env {

    private static Map<String, String> values;

    public static void initialize() {
        if (values != null) {
            return;
        }
        try {
            values = loadEnv(".env");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load .env file.", e);
        }
    }

    public static String get(String key) {
        return values.get(key);
    }

    private static Map<String, String> loadEnv(String path) throws IOException {
        Map<String, String> map = new HashMap<>();
        for (String line : Files.readAllLines(Path.of(path))) {
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }
            var splitIdx = line.indexOf('=');
            if (splitIdx == -1) {
                continue;
            }
            String key = line.substring(0, splitIdx).trim();
            String value = line.substring(splitIdx + 1).trim();
            map.put(key, value);
        }
        return map;
    }
}
