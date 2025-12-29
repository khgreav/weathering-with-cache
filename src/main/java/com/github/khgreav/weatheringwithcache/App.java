package com.github.khgreav.weatheringwithcache;

import java.net.InetSocketAddress;

import com.github.khgreav.weatheringwithcache.controllers.WeatherController;
import com.github.khgreav.weatheringwithcache.utils.Env;
import com.sun.net.httpserver.HttpServer;

/**
 * Hello world!
 *
 */
public class App {

    public static void main( String[] args ) {
        
        try {
            // Load env file
            Env.initialize();
            // Create server
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
            // Weather cotroller registers handler for API endpoint
            WeatherController controller = new WeatherController(server);
            // Register SIGTERM handler
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Received shutdown signal, shutting down...");
                server.stop(0);
            }));
            // Start server
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
