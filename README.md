# ⚡ GeoPulse 

> **High-Throughput Real-Time Geospatial Driver Ingestion & Matching Engine**  
> Built with Java 21 Virtual Threads, Spring Boot 3 WebFlux, Apache Kafka, and Redis GEO.

---

## 📌 Overview

**GeoPulse** is an event-driven, distributed backend service designed to solve high-frequency geospatial tracking and real-time trip matching at scale (simulating systems like Uber, Lyft, or DoorDash).

The core service ingests GPS coordinates from tens of thousands of active drivers per second, publishes streaming events through Apache Kafka, indexes spatial coordinates in Redis GEO, and executes sub-millisecond radius searches for nearby driver-rider matching.

*Event-Driven Microservices Architecture with Spring Boot, Kafka, and Redis.*

---

## 🧰 Technology Stack

| Technology | Role |
| --- | --- |
| Java 21 | Application runtime and access to virtual threads |
| Spring Boot 3 | Service framework and application configuration |
| Spring WebFlux | Reactive HTTP endpoints for location ingestion and rider searches |
| Apache Kafka | Durable event streaming between ingestion and location processing |
| Redis GEO | Geospatial index for current driver locations and proximity searches |

## ✨ Core Capabilities

- Accept frequent GPS updates from active drivers.
- Publish location updates as events so ingestion and location processing can scale independently.
- Keep driver coordinates searchable in a geospatial index.
- Find nearby drivers within a search radius and return a limited set of matches.

## 🔄 Request Flows

### Driver location update

1. A driver sends a GPS update to the ingestion service.
2. The service publishes a location event to Kafka.
3. A consumer processes the event and updates that driver's position in Redis GEO.

### Rider nearby-driver search

1. A rider submits a search location.
2. The matching service searches the Redis GEO index within the configured radius.
3. The service returns up to the configured number of nearby drivers.

The architecture diagram illustrates a **3 km search radius** and **up to 5 matches**. Treat these as example defaults; production values should be configurable for the product's needs.

## 📐 Design Considerations

- Validate latitude and longitude values before publishing or indexing location updates.
- Define how outdated driver locations expire so inactive drivers are not returned as available.
- Make event processing safe to retry, since Kafka consumers may process an event more than once.
- Monitor ingestion throughput, Kafka consumer lag, Redis search latency, and stale-location rates.
- Confirm coordinate ordering and distance units at the Redis GEO boundary.

---

## 🏗️ Architecture & Data Flow

```text
[ Virtual Drivers (GPS Pings) ]
            │
            ▼
[ Spring Boot 3 (WebFlux Ingestion) ]
            │ (Publish Location Event)
            ▼
   [ Apache Kafka Topic ]
            │
            ▼ (High-Throughput Stream Consumer)
  [ Redis Geo Spatial Index ]  ◄───── [ Rider Search Request ]
            │                                  │
            └─────────── (GEORADIUS 3km) ───────┘
                                   │
                                   ▼
                       [ Top 5 Matching Drivers ]
```
