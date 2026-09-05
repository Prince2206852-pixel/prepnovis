package com.prepnovis.backend.security;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

    private static final Logger log =
            LoggerFactory.getLogger(RateLimitService.class);

    private final StringRedisTemplate redisTemplate;

    public RateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(
            String key,
            int maxRequests,
            Duration window) {

        try {

            Long requestCount =
                    redisTemplate.opsForValue().increment(key);

            if (requestCount != null && requestCount == 1) {
                redisTemplate.expire(key, window);
            }

            return requestCount == null
                    || requestCount <= maxRequests;

        } catch (RuntimeException exception) {

            // Fail open:
            // Redis problems must not make authentication unavailable.
            log.warn(
                    "Rate-limit check failed. Continuing without rate limiting."
            );

            return true;
        }
    }
}