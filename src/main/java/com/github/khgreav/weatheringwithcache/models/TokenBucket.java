package com.github.khgreav.weatheringwithcache.models;

import java.time.Instant;
import java.util.Objects;

public class TokenBucket {

    public static final int CAPACITY = 10;

    public static final int REFILL_RATE = 1;
    
    private int tokenCount;

    private long lastRefillEpoch;

    public TokenBucket() {
        this.tokenCount = CAPACITY;
        this.lastRefillEpoch = Instant.now().toEpochMilli();
    }

    public final int getTokenCount() {
        return this.tokenCount;
    }

    public final void setTokenCount(int tokenCount) {
        this.tokenCount = tokenCount;
    }

    public final long getLastRefill() {
        return this.lastRefillEpoch;
    }

    public final void setLastRefill(long lastRefillEpoch) {
        this.lastRefillEpoch = lastRefillEpoch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TokenBucket other = (TokenBucket) o;
        return this.tokenCount == other.tokenCount &&
            this.lastRefillEpoch == other.lastRefillEpoch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            this.tokenCount,
            this.lastRefillEpoch
        );
    }
}
