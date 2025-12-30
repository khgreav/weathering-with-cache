package com.github.khgreav.weatheringwithcache.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TocketBucketTest {
    
    private TokenBucket bucket;

    @BeforeEach
    void setUp() {
        bucket = new TokenBucket();
    }

    @Test
    void testGetTokenCount() {
        assertEquals(TokenBucket.CAPACITY, bucket.getTokenCount());
    }

    @Test
    void testSetTokenCount() {
        bucket.setTokenCount(5);
        assertEquals(5, bucket.getTokenCount());
        bucket.setTokenCount(20);
        assertEquals(10, bucket.getTokenCount());
    }

    @Test
    void testSetLastRefill() {
        var now = Instant.now().toEpochMilli();
        bucket.setLastRefill(now);
        assertEquals(now, bucket.getLastRefill());
    }

    @Test
    void testEquals() throws InterruptedException {
        assertFalse(bucket.equals(null));
        assertTrue(bucket.equals(bucket));
        Thread.sleep(10);
        var other = new TokenBucket();
        assertFalse(bucket.equals(other));
    }

    @Test
    void testHashCode() throws InterruptedException {
        // hash consistency
        assertEquals(bucket.hashCode(), bucket.hashCode());
        // different objects
        Thread.sleep(10);
        var other = new TokenBucket();
        assertNotEquals(bucket.hashCode(), other.hashCode());
    }
}
