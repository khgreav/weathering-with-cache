package com.github.khgreav.weatheringwithcache.services;

import java.time.Duration;

import com.github.khgreav.weatheringwithcache.utils.Env;

import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.params.SetParams;

public class RedisService {

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
           System.err.println(e.getMessage());
        }
        return null;
    }

    public void setValue(String key, String value, long ttl) {
        try {
            client.set(key, value, SetParams.setParams().ex(ttl));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
