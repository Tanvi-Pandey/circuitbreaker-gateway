package com.circuitbreaker.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Logs which live backend instance the Gateway resolved for each request.
 *
 * This is the Routing Audit evidence: the Gateway only ever knows the
 * logical service id from its route config (e.g. lb://PRODUCT-SERVICE).
 * This filter runs AFTER Spring Cloud LoadBalancer has picked a concrete
 * instance from Eureka, so the URI it prints below is proof the choice
 * was made dynamically at request time, not hardcoded anywhere.
 */
@Component
public class RoutingAuditFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RoutingAuditFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            URI resolvedUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
            String routeId = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR) != null
                    ? exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR).toString()
                    : "unknown-route";

            log.info("[ROUTING AUDIT] path={} -> routeId={} -> resolvedInstance={}",
                    exchange.getRequest().getPath(), routeId, resolvedUri);
        }));
    }

    @Override
    public int getOrder() {
        // Run late, after routing/load-balancing has already picked an instance.
        return Ordered.LOWEST_PRECEDENCE;
    }
}
