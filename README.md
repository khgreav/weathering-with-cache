# Weathering With Cache
Weathering With Cache is a simple Weather API with response caching using Redis.

The application functions as a simple server exposing an API endpoint for retrieving location weather data and returning them in JSON format. [It uses the Visual Crossing Weather API](https://www.visualcrossing.com/resources/documentation/weather-api/timeline-weather-api/) to fetch today's weather and caches the response until the next day of the target location timezone.

The purpose of this project is to learn basics of java and mave. It is an implementation of the roadmap.sh [Weather API](https://roadmap.sh/projects/weather-api-wrapper-service) project spec.

## Requirements

- Java 17
- Maven
- Redis

## Configuration

The application uses the root directory `.env` file to get API key for Visual Crossing API, and Redis host and port.
The default placeholder values need to be changed before running the application.

## Caching

The application attempts to cache responses for queried locations if possible, however, if it cannot store data, it will still return response. To ensure responses are cached, run a redis server instance. For the purpose and scope of this project, a local container is enough.

Run redis:
```bash
docker run --rm -p <local_port>:<container_port> --name redis-local redis:latest
```

The local port should match the port in configuration.

Note: The --rm switch will remove the container once it has stopped and the data will be lost, either remove the switch, or pass a volume for persistent storage.

## API

The server exposes a single API endpoint `/weather` and expects a single query parameter `location`.

```bash
GET /weather?location=<location_name>
```
