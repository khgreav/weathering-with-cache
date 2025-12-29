package com.github.khgreav.weatheringwithcache.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpTimeoutException;

import com.github.khgreav.weatheringwithcache.exceptions.ContentTypeException;
import com.github.khgreav.weatheringwithcache.exceptions.GenericException;
import com.github.khgreav.weatheringwithcache.exceptions.InvalidApiKeyException;
import com.github.khgreav.weatheringwithcache.exceptions.LocationNotFoundException;
import com.github.khgreav.weatheringwithcache.exceptions.MalformedUpstreamDataException;
import com.github.khgreav.weatheringwithcache.exceptions.UsageLimitedException;
import com.github.khgreav.weatheringwithcache.services.WeatherService;
import com.github.khgreav.weatheringwithcache.utils.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class WeatherController {

    private WeatherService service;

    public WeatherController(HttpServer server) {
        server.createContext("/weather", this::fetchWeather);
        this.service = new WeatherService();
    }

    public void fetchWeather(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            sendErrorResponse(exchange, 405, "Method not allowed.");
            return;
        }
        var params = HttpUtils.getQueryParamsMap(exchange.getRequestURI().getQuery());
        if (params.size() != 1 || !params.containsKey("location")) {
            sendErrorResponse(exchange, 400, "Only accepts a single query parameter \"location\".");
            return;
        }
        if (!HttpUtils.acceptsJson(exchange)) {
            sendErrorResponse(exchange, 406, "The server only serves JSON data.");
            return;
        }

        try {
            var data = service.getToday(params.get("location"));
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, data.getBytes().length);
            try (var stream = exchange.getResponseBody()) {
                stream.write(data.getBytes());
            }
        } catch (IllegalArgumentException e) {
            sendErrorResponse(exchange, 400, "Invalid location format.");
        } catch (LocationNotFoundException e) {
            sendErrorResponse(exchange, 404, "Location not found.");
        } catch (InvalidApiKeyException | GenericException | URISyntaxException e) {
            sendErrorResponse(exchange, 500, "Internal error.");
        } catch (ContentTypeException e) {
            sendErrorResponse(exchange, 502, "Weather provider cannot send JSON data.");
        } catch (MalformedUpstreamDataException e) {
            sendErrorResponse(exchange, 502, "Weather provider sent malformed data.");
        } catch (HttpTimeoutException | InterruptedException e) {
            sendErrorResponse(exchange, 502, "Request timed out.");
        } catch (UsageLimitedException e) {
            sendErrorResponse(exchange, 503, "Service unavailable, try again later.");
        }
    }

    private void sendErrorResponse(HttpExchange exchange, int code, String error) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        if (error == null || error.length() == 0) {
            exchange.sendResponseHeaders(code, -1);
        } else {
            exchange.sendResponseHeaders(code, error.getBytes().length);
            try (var stream = exchange.getResponseBody()) {
                stream.write(error.getBytes());
            }
        }
    }
}
