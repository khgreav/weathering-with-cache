package com.github.khgreav.weatheringwithcache.middlewares;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

public class RateLimitFilter extends Filter {

    private final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

    private RateLimiter limiter;

    public RateLimitFilter(RateLimiter limiter) {
        this.limiter = limiter;
    }

    @Override
    public String description() {
        return "Rate limiting filter.";
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        var clientId = exchange.getRemoteAddress().getAddress().getHostAddress();
        var agent = exchange.getRequestHeaders().getFirst("User-Agent");

        if (agent != null && agent.length() > 0) {
            clientId += "-" + agent;
        }

        logger.info("Incoming request from '{}'.", clientId);

        if (!limiter.canRequest(clientId)) {
            logger.info("Request from client '{}' refused due to request rate.", clientId);
            byte[] error = "Too many requests.".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(429, error.length);
            try (var stream = exchange.getResponseBody()) {
                stream.write(error);
            } catch (Exception e) {
                logger.error("Failed to send rate limited response to '{}':", clientId, e);
            }
            return;
        }

        // exec next filter
        chain.doFilter(exchange);
    }

    
    
}
