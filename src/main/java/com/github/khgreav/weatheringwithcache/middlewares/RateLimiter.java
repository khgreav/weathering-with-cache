package com.github.khgreav.weatheringwithcache.middlewares;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import com.github.khgreav.weatheringwithcache.models.TokenBucket;

public class RateLimiter {
    
    private ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public RateLimiter() {}

    public boolean canRequest(String clientId) {
        var now = Instant.now().toEpochMilli();
        if (!buckets.containsKey(clientId)) {
            // new client
            var bucket = new TokenBucket();
            bucket.setTokenCount(bucket.getTokenCount() - 1);
            bucket.setLastRefill(now);
            buckets.put(clientId, bucket);
            return true;
        }
        var bucket = buckets.get(clientId);
        // get time between last refill and now and tokens regenerated over that time 
        var elapseSeconds = (long) ((now - bucket.getLastRefill()) / 1000.0);
        var newTokens = (int) Math.floor(elapseSeconds * TokenBucket.REFILL_RATE);

        // refill tokens if regenerated
        if (newTokens > 0) {
            bucket.setTokenCount(bucket.getTokenCount() + newTokens);
            bucket.setLastRefill(now);
        }

        // do i have enough tokens?
        if (bucket.getTokenCount() < 1) {
            return false;
        }

        // token consumed
        bucket.setTokenCount(bucket.getTokenCount() - 1);
        return true;
    }
}
