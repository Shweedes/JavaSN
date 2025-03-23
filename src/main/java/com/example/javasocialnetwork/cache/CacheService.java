package com.example.javasocialnetwork.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CacheService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);
    private static final int MAX_CACHE_SIZE = 100;

    private final Map<String, CacheEntry> cache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
            boolean shouldRemove = size() > MAX_CACHE_SIZE;
            if (shouldRemove) {
                LOGGER.info("[CACHE] Removing eldest entry: {}", eldest.getKey());
            }
            return shouldRemove;
        }
    };

    private static final long TTL = TimeUnit.MINUTES.toMillis(15);

    private static class CacheEntry {
        @Getter
        private final Object value;
        private final long createdAt;

        public CacheEntry(Object value) {
            this.value = value;
            this.createdAt = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - createdAt > TTL;
        }
    }

    public void enforceSizeLimit() {
        while (cache.size() > MAX_CACHE_SIZE) {
            String eldestKey = cache.keySet().iterator().next();
            cache.remove(eldestKey);
            LOGGER.info("[CACHE] Removed eldest entry due to size limit: {}", eldestKey);
        }
    }

    public void put(String key, Object value) {
        cache.put(key, new CacheEntry(value));
        enforceSizeLimit();
        LOGGER.info("[CACHE] Data added to cache with key: {}", key);
    }

    public Optional<Object> get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            LOGGER.info("[CACHE] Data not found in cache for key: {}", key);
            return Optional.empty();
        }
        if (entry.isExpired()) {
            LOGGER.info("[CACHE] Data expired in cache for key: {}", key);
            cache.remove(key);
            return Optional.empty();
        }
        LOGGER.info("[CACHE] Data retrieved from cache for key: {}", key);
        return Optional.of(entry.getValue());
    }

    public void evictByPrefix(String prefix) {
        cache.keySet().removeIf(key -> {
            if (key.startsWith(prefix)) {
                LOGGER.info("[CACHE] Data evicted from cache for key: {}", key);
                return true;
            }
            return false;
        });
    }

    public void evict(String key) {
        CacheEntry removedEntry = cache.remove(key);
        if (removedEntry != null) {
            LOGGER.info("[CACHE] Data evicted from cache for key: {}", key);
        }
    }

    public void invalidateUserCache() {
        evictByPrefix("users_by_post_content_");
    }
}