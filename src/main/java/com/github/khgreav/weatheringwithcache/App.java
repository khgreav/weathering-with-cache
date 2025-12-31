package com.github.khgreav.weatheringwithcache;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.khgreav.weatheringwithcache.controllers.WeatherController;
import com.github.khgreav.weatheringwithcache.middlewares.RateLimitFilter;
import com.github.khgreav.weatheringwithcache.middlewares.RateLimiter;
import com.github.khgreav.weatheringwithcache.utils.Env;
import com.sun.net.httpserver.HttpServer;

/**
 * Hello world!
 *
 */
public class App {

    public static void main( String[] args ) {
        Logger logger = LoggerFactory.getLogger(App.class);

        try {
            // Load env file
            Env.initialize();
            // Create server
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
            // Rate limiter
            RateLimiter limiter = new RateLimiter();
            RateLimitFilter rateLimitFilter = new RateLimitFilter(limiter);
            // Weather controller
            WeatherController controller = new WeatherController(server);
            // Register weather endpoint with rate limiter
            server.createContext("/weather", controller::fetchWeather)
                .getFilters()
                .add(rateLimitFilter);
            // Register SIGTERM handler
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info(("Received shutdown signal, shutting down..."));
                server.stop(0);
            }));
            // Start server
            server.start();
        } catch (Exception e) {
            logger.error("A fatal error has occurred:", e);
        }
    }
}
