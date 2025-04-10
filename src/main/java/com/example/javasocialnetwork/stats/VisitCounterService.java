package com.example.javasocialnetwork.stats;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class VisitCounterService {
    private final VisitStats visitStats = new VisitStats();

    public synchronized void recordVisit(String url) {
        visitStats.recordVisit(url);
    }

    public synchronized long getVisitCount(String url) {
        return visitStats.getVisitCount(url);
    }

    public synchronized Map<String, Long> getAllStats() {
        return visitStats.getAllStats();
    }
}
