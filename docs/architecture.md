# 📐 ModelRouter System Architecture Blueprint

## High-Level Component Flow

```mermaid
flowchart TD
    Client[Client App / API Request] --> Auth[ApiKeyAuthenticationFilter SHA-256]
    Auth --> Gateway[POST /api/v1/chat GatewayController]
    Gateway --> Cache{Redis Cache Hit?}
    Cache -- Yes (<10ms) --> Client
    Cache -- No --> Classifier[Task & Complexity Classifier]
    Classifier --> Filter[Candidate Filter Engine]
    Filter --> Scoring[Multi-Objective Scoring Engine]
    Scoring --> Fallback[Resilience & Fallback Engine]
    Fallback --> Adapters[OpenAI / Anthropic / Mock Provider Adapters]
    Adapters --> RedisCache[Save Response to Redis]
    Adapters --> Telemetry[Async Telemetry Persistence]
    Telemetry --> DB[(PostgreSQL Database)]
```

## Multi-Objective Scoring Equation

$$Score = (w_{quality} \cdot Q) + (w_{latency} \cdot L) + (w_{cost} \cdot C) + (w_{reliability} \cdot R) + (w_{capability} \cdot Cap)$$

## Entity Relationship Diagram (ERD)

```mermaid
erDiagram
    ORGANIZATIONS ||--o{ API_KEYS : issues
    ORGANIZATIONS ||--o{ ROUTING_REQUESTS : tracks
    PROVIDERS ||--o{ MODELS : hosts
    MODELS ||--o{ ROUTING_REQUESTS : executes

    ORGANIZATIONS {
        string id PK
        string name
        string plan_tier
    }
    API_KEYS {
        string id PK
        string organization_id FK
        string key_hash
    }
    MODELS {
        string id PK
        string provider_id FK
        string name
        decimal input_price
        decimal output_price
        string status
    }
    ROUTING_REQUESTS {
        string id PK
        string organization_id FK
        string selected_model_id FK
        int latency_ms
        decimal estimated_cost
        boolean cache_hit
    }
```
