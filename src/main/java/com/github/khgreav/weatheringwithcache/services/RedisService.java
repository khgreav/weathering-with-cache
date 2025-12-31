package com.github.khgreav.weatheringwithcache.services;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.khgreav.weatheringwithcache.utils.Env;

import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.params.SetParams;

public class RedisService {

    private final Logger logger = LoggerFactory.getLogger(RedisService.class);

    private RedisClient client;

    public RedisService() {
        var pool = new ConnectionPoolConfig();
        pool.setMaxTotal(20);
        pool.setMaxIdle(10);
        pool.setMinIdle(2);
        pool.setMaxWait(Duration.ofMillis(200));
        pool.setBlockWhenExhausted(true);
        pool.setTestOnBorrow(true);
        pool.setTestWhileIdle(true);
        this.client = RedisClient.builder()
            .hostAndPort(
                Env.get("REDIS_HOST"),
                Integer.parseInt(Env.get("REDIS_PORT"))
            )
            .poolConfig(pool)
            .build();
    }

    public String getValue(String key) {
        try {
            return client.get(key);
        } catch (Exception e) {
           logger.warn("Unable to get data from cache: '{}'.", e.getMessage());
        }
        return null;
    }

    public void setValue(String key, String value, long ttl) {
        try {
            client.set(key, value, SetParams.setParams().ex(ttl));
        } catch (Exception e) {
            logger.warn("Unable to store data in cache: '{}'.", e.getMessage());
        }
    }
}
