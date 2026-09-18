package com.circuitbreaker.apigateway.fallback;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Static fallback served by the Gateway when the recommendation-service
 * circuit breaker (recommendationCB) is OPEN or a call to it times out.
 * Returns a cached/static "Top Sellers" style response instead of failing
 * the caller outright.
 */
@RestController
public class FallbackController {

    @GetMapping("/fallback/recommendations")
    public Mono<ResponseEntity<Map<String, Object>>> recommendationsFallback() {
        Map<String, Object> body = Map.of(
                "source", "fallback-gateway",
                "message", "Recommendation Engine is temporarily unavailable — showing cached top sellers instead.",
                "recommendations", List.of("PROD-101", "PROD-102", "PROD-103"),
                "servedBy", "api-gateway-fallback",
                "timestamp", Instant.now().toString()
        );

        // 200 OK on purpose: the caller's UI shouldn't treat a graceful
        // degradation as an error — it got a usable, if static, response.
        return Mono.just(ResponseEntity.ok(body));
    }
}