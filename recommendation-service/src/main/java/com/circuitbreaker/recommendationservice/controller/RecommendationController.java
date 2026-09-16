package com.circuitbreaker.recommendationservice.controller;

import com.circuitbreaker.recommendationservice.model.Recommendation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private static final List<Recommendation> RECS = List.of(
            new Recommendation("P002", "Frequently bought with your last order", 0.91),
            new Recommendation("P005", "Trending in Fitness", 0.87),
            new Recommendation("P001", "Because you viewed similar Electronics", 0.79)
    );

    @Value("${server.port}")
    private String port;

    /** Normal, fast path — what the Gateway calls in the happy path. */
    @GetMapping("/{userId}")
    public Map<String, Object> getRecommendations(@PathVariable String userId) {
        return Map.of(
                "servedBy", "recommendation-service:" + port,
                "userId", userId,
                "recommendations", RECS
        );
    }

    /**
     * Simulates the Black-Friday overload scenario from the problem statement:
     * the Recommendation Engine gets overwhelmed and starts timing out.
     * delayMs defaults to 6000ms, comfortably past the Gateway's timeout config
     * (see api-gateway application.yml) so it trips the circuit breaker on purpose.
     * Use this in Week 2 to demonstrate the fallback path.
     */
    @GetMapping("/{userId}/slow")
    public Map<String, Object> getSlowRecommendations(
            @PathVariable String userId,
            @RequestParam(defaultValue = "6000") long delayMs) throws InterruptedException {
        Thread.sleep(delayMs);
        return Map.of(
                "servedBy", "recommendation-service:" + port,
                "userId", userId,
                "note", "This response was artificially delayed " + delayMs + "ms",
                "recommendations", RECS
        );
    }
}
