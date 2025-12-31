package com.github.khgreav.weatheringwithcache.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final Logger logger = LoggerFactory.getLogger(WeatherController.class);

    private WeatherService service;

    public WeatherController(HttpServer server) {
        this.service = new WeatherService();
    }

    public void fetchWeather(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        String address = exchange.getRemoteAddress().getAddress().getHostAddress();
        logger.info("Incoming request '{}' '{}' from '{}'.", method, uri.getPath(), address);
        if (!method.equals("GET")) {
            logger.warn("Method '{}' not allowed for endpoint /weather.", method);
            sendErrorResponse(exchange, 405, "Method not allowed.");
            return;
        }
        String queryParams = uri.getQuery();
        var params = HttpUtils.getQueryParamsMap(queryParams);
        if (params.size() != 1 || !params.containsKey("location")) {
            logger.warn("Too many query parameters, or query param location missing in '{}'.", queryParams);
            sendErrorResponse(exchange, 400, "Only accepts a single query parameter \"location\".");
            return;
        }
        if (!HttpUtils.acceptsJson(exchange)) {
            logger.warn("Client does not accept application/json content.");
            sendErrorResponse(exchange, 406, "The server only serves JSON data.");
            return;
        }

        try {
            var data = service.getToday(params.get("location"));
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, data.getBytes().length);
            try (var stream = exchange.getResponseBody()) {
                stream.write(data.getBytes());
                logger.info("Sent response to '{}'.", address);
            } catch (Exception e) {
                logger.error("Failed to send response to '{}'.", address, e);
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
            logger.error("Request to the weather API timed out.");
            sendErrorResponse(exchange, 502, "Request timed out.");
        } catch (UsageLimitedException e) {
            sendErrorResponse(exchange, 503, "Service unavailable, try again later.");
        }
    }

    private void sendErrorResponse(HttpExchange exchange, int code, String error) throws IOException {
        String address = exchange.getRemoteAddress().getAddress().getHostAddress();
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(code, error.getBytes().length);
        try (var stream = exchange.getResponseBody()) {
            stream.write(error.getBytes());
            logger.info("Sent error response to '{}'.", address);
        } catch (Exception e) {
            logger.error("Failed to send error response to '{}':", address);
        }
    }
}
