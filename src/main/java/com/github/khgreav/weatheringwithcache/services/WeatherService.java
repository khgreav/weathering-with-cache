package com.github.khgreav.weatheringwithcache.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.khgreav.weatheringwithcache.exceptions.ContentTypeException;
import com.github.khgreav.weatheringwithcache.exceptions.GenericException;
import com.github.khgreav.weatheringwithcache.exceptions.InvalidApiKeyException;
import com.github.khgreav.weatheringwithcache.exceptions.LocationNotFoundException;
import com.github.khgreav.weatheringwithcache.exceptions.MalformedUpstreamDataException;
import com.github.khgreav.weatheringwithcache.exceptions.UsageLimitedException;
import com.github.khgreav.weatheringwithcache.utils.DateTimeUtils;
import com.github.khgreav.weatheringwithcache.utils.Env;

public class WeatherService {

    private static final String BASE_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";

    private static final int TIMEOUT = 5;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

    private final HttpClient client;

    private final RedisService cacheService;

    public WeatherService() {
        this.client = HttpClient.newHttpClient();
        this.cacheService = new RedisService();
    }

    public String getToday(String location) throws HttpTimeoutException, IOException, InterruptedException, URISyntaxException {

        var cached = this.cacheService.getValue(location);
        if (cached != null) {
            return cached;
        }

        var key = Env.get("VISUAL_CROSSING_API_KEY");
        if (key == null || key.length() == 0) {
            throw new InvalidApiKeyException();
        }

        String uri = new StringBuilder()
            .append(BASE_URL)
            .append(location)
            .append("/today?key=")
            .append(key)
            .toString();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(uri))
            .version(HttpClient.Version.HTTP_2)
            .header("Accept", "application/json; charset=utf-8")
            .timeout(Duration.ofSeconds(TIMEOUT))
            .GET()
            .build();

        var response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
        switch (response.statusCode()) {
            case 200 -> {
                var body = response.body();
                try {
                    JsonNode json = OBJECT_MAPPER.readTree(body);
                    String timezone = json.get("timezone").asText();
                    if (timezone == null || timezone.length() == 0) {
                        throw new MalformedUpstreamDataException();
                    }
                    long ttl = DateTimeUtils.getTodayLastSecondEpoch(body);
                    this.cacheService.setValue(location, body, ttl);
                    return body;
                } catch (JsonProcessingException e) {
                    throw new MalformedUpstreamDataException(e);
                }
            }
            case 400 -> throw new IllegalArgumentException();
            case 401 -> throw new InvalidApiKeyException();
            case 403 -> throw new UsageLimitedException();
            case 404 -> throw new LocationNotFoundException();
            case 406 -> throw new ContentTypeException();
            case 429 -> throw new UsageLimitedException();
            default -> throw new GenericException();
        }
    }
}
