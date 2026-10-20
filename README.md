# GeoPulse

**A Java geospatial location-ingestion prototype using Spring WebFlux and Apache Kafka.**

GeoPulse explores an event-driven foundation for driver-location updates and nearby-driver matching. The repository currently implements the location event model, Kafka topic configuration, and a producer service.

> **Project status:** This is an early-stage prototype, not a production-scale matching service. The Redis GEO index, Kafka consumer, HTTP endpoints, and radius-search/matching flow described in the original concept are not implemented yet.

## What’s implemented

- Java 21 and Spring Boot application foundation.
- Spring WebFlux and reactive Redis dependencies.
- A Kafka topic named `driver-locations`, configured with three partitions and one replica.
- `LocationProducerService` publishes `DriverLocationEvent` messages to that topic, keyed by driver ID.
- `DriverLocationEvent` and `RiderSearchRequest` data models.

Redis is currently a declared dependency; the application does not yet write locations to Redis or query a geospatial index.

## Technology

| Technology | Use |
| --- | --- |
| Java 21 | Application runtime |
| Spring Boot 4 | Application framework |
| Spring WebFlux | Reactive web framework dependency |
| Apache Kafka / Spring Kafka | Driver-location event publishing |
| Spring Data Redis Reactive | Declared dependency; integration is not implemented yet |
| Maven Wrapper | Build and test commands |

## Location event

The current event model contains:

| Field | Type | Description |
| --- | --- | --- |
| `driverId` | `String` | Driver identifier and Kafka message key |
| `latitude` | `double` | Latitude coordinate |
| `longitude` | `double` | Longitude coordinate |
| `timestamp` | `long` | Event timestamp |

## Run locally

Prerequisites: Java 21 and an accessible Kafka broker. Spring Kafka uses `localhost:9092` by default unless configured otherwise.

From the application directory:

```powershell
cd geopulse
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

On macOS or Linux, use `./mvnw` instead of `.\mvnw.cmd`. Configure a non-default Kafka broker with `spring.kafka.bootstrap-servers`.

## Current limitations and next steps

- Add a validated HTTP endpoint for driver-location ingestion.
- Add a Kafka consumer and define retry/error-handling behavior.
- Implement Redis GEO writes and nearby-driver queries.
- Add a rider-search endpoint and define how stale locations are handled.
- Add tests for event publishing and the matching workflow before making performance claims.

## Repository layout

The Spring Boot application is in [`geopulse/`](./geopulse/).
