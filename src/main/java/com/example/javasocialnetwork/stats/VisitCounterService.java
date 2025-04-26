package com.example.javasocialnetwork.stats;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class VisitCounterService {
    private final ConcurrentHashMap<String, AtomicLong> urlCounters = new ConcurrentHashMap<>();

    public void recordVisit(String url) {
        urlCounters.computeIfAbsent(url, k -> new AtomicLong(0)).addAndGet(1);
    }

    public long getVisitCount(String url) {
        return urlCounters.getOrDefault(url, new AtomicLong(0)).get();
    }

    public Map<String, Long> getAllStats() {
        Map<String, Long> result = new HashMap<>();
        urlCounters.forEach((k, v) -> result.put(k, v.get()));
        return result;
    }
}
