# CircuitBreaker — Cloud-Native E-Commerce API Gateway
**Project 3 · Microservices Architecture & Cloud-Native Resilience**

## Week 1 deliverables (this build)
- ✅ Service Registry (Netflix Eureka)
- ✅ 3 microservices: Product, Inventory, Recommendation
- ✅ API Gateway (Spring Cloud Gateway) routing to all three via service discovery

## Architecture

```
                     ┌───────────────────────┐
                     │   Eureka Server        │
                     │   localhost:8761       │
                     └───────────▲────────────┘
                                 │ register/heartbeat
        ┌────────────┬──────────┼──────────┬────────────┐
        │             │          │          │            │
┌───────▼──────┐┌─────▼────┐┌────▼─────┐┌───▼─────────┐  │
│ API Gateway  ││ Product  ││Inventory ││Recommendation│  │
│ :8080        ││ :8081    ││ :8082    ││   :8083      │  │
└───────────────┘└──────────┘└──────────┘└──────────────┘
   ▲
   │ client traffic (frontend / Postman)
```

The Gateway never hardcodes a host:port for any backend — it resolves
`lb://PRODUCT-SERVICE`, `lb://INVENTORY-SERVICE`, `lb://RECOMMENDATION-SERVICE`
against the live Eureka registry on every request.

## Module layout

| Module | Port | Role |
|---|---|---|
| `eureka-server` | 8761 | Service registry / discovery |
| `product-service` | 8081 | Product catalog, top sellers |
| `inventory-service` | 8082 | Stock levels per product |
| `recommendation-service` | 8083 | Recommendations, plus a `/slow` endpoint that simulates the Black-Friday overload |
| `api-gateway` | 8080 | Single entry point, routes to the three services above |

## Build & run order

Build everything from the repo root once:

```bash
mvn clean install
```

Then start each module in its own terminal, **in this order** (Eureka must be up first so the others have something to register with):

```bash
# 1. Registry — wait for it to fully start before continuing
cd eureka-server  && mvn spring-boot:run

# 2-4. Backend services (any order, can run in parallel)
cd product-service        && mvn spring-boot:run
cd inventory-service      && mvn spring-boot:run
cd recommendation-service && mvn spring-boot:run

# 5. Gateway last, once the above show as UP in Eureka
cd api-gateway && mvn spring-boot:run
```

## Verifying it works

1. **Eureka dashboard**: open `http://localhost:8761` — you should see
   `PRODUCT-SERVICE`, `INVENTORY-SERVICE`, `RECOMMENDATION-SERVICE`, and
   `API-GATEWAY` all listed as `UP`.

2. **Direct service calls** (bypassing the gateway, for sanity-checking):
   - `GET http://localhost:8081/api/products/top-sellers`
   - `GET http://localhost:8082/api/inventory`
   - `GET http://localhost:8083/api/recommendations/U100`

3. **Through the Gateway** (this is the demo path):
   - `GET http://localhost:8080/gateway/products/top-sellers`
   - `GET http://localhost:8080/gateway/inventory`
   - `GET http://localhost:8080/gateway/recommendations/U100`

   Each response includes a `servedBy` field naming the exact instance that
   handled it — useful for proving the request actually hopped through the
   Gateway to a dynamically discovered backend, not a hardcoded URL.

4. **Simulate the Black-Friday overload** (sets up Week 2):
   - `GET http://localhost:8080/gateway/recommendations/U100/slow?delayMs=6000`
     This hangs for 6 seconds — today it just makes the caller wait; next
     week a Resilience4j Circuit Breaker + Timeout on this route will fail
     fast and return a fallback instead.

## Next (Week 2)
- Wrap the recommendation-service route in a Resilience4j `CircuitBreaker`,
  `TimeLimiter`, `Bulkhead`, and `RateLimiter`.
- Add a `/fallback/recommendations` endpoint on the Gateway returning a
  cached/static response (e.g. static "Top Sellers") when the breaker is open.
- Build the React monitoring dashboard against the Gateway's
  `/actuator/health` and Resilience4j metrics endpoints.
