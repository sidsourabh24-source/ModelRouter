# 🏗️ ModelRouter — Technical Architecture & Design Specification

## 1. System Architecture Overview

ModelRouter acts as an intelligent proxy between client applications and downstream AI model providers.

```text
                               ┌────────────────────────────────────────┐
                               │       Next.js Admin Dashboard          │
                               └───────────────────┬────────────────────┘
                                                   │
                                                   v
┌──────────────────────┐               ┌────────────────────────────────┐
│ Customer Application │──────────────>│   ModelRouter Gateway API      │
└──────────────────────┘               │   (Java 21 / Spring Boot 3.x)   │
                                       └───────────────┬────────────────┘
                                                       │
                           ┌───────────────────────────┼───────────────────────────┐
                           │                           │                           │
                           v                           v                           v
                ┌─────────────────────┐     ┌─────────────────────┐     ┌─────────────────────┐
                │ Security & Auth     │     │ Task Classifier     │     │ Candidate Filter    │
                └─────────────────────┘     └─────────────────────┘     └─────────────────────┘
                           │                           │                           │
                           └───────────────────────────┼───────────────────────────┘
                                                       │
                                                       v
                                            ┌─────────────────────┐
                                            │ Scoring Engine      │
                                            │ (Weighted Score Math)│
                                            └──────────┬──────────┘
                                                       │
                           ┌───────────────────────────┴───────────────────────────┐
                           │                           │                           │
                           v                           v                           v
                ┌─────────────────────┐     ┌─────────────────────┐     ┌─────────────────────┐
                │ Redis Exact Cache   │     │ PostgreSQL DB       │     │ Provider Adapters   │
                │ (TTL & Rate Limits) │     │ (State & Analytics) │     │ (OpenAI, Anthropic, │
                └─────────────────────┘     └─────────────────────┘     │  Gemini, Mock)      │
                                                                        └─────────────────────┘
```

---

## 2. Request Lifecycle & Pipeline Sequence

Every request processed by `POST /api/v1/chat` passes through an 8-stage pipeline:

1. **Authentication & Context Resolution**: Verify `X-API-Key` header, hash key, resolve Organization and Rate Limits.
2. **Request Validation**: Enforce payload schema rules and validate token limits.
3. **Exact Cache Lookup**: Hash prompt (`SHA-256`). If cached response exists in Redis (`cache:chat:<hash>`), return immediately with `cacheHit: true`.
4. **Task Classification**: Analyze prompt length, code patterns, reasoning markers, and estimate task complexity (`LOW`, `MEDIUM`, `HIGH`).
5. **Candidate Filtering**: Filter candidate models by:
   - Provider enabled status
   - Model health status (from Redis health key)
   - Capability match (e.g. `coding`, `vision`, `long-context`)
   - Context window limit
   - Organization policy & budget constraints
6. **Weighted Score Calculation**: Calculate score for each eligible model:
   $$\text{Score} = (w_q \cdot S_q) + (w_l \cdot S_l) + (w_c \cdot S_c) + (w_r \cdot S_r) + (w_m \cdot S_m)$$
7. **Execution & Fallback Engine**: Call highest-scoring model adapter. If timeout or 5xx error occurs, automatically call next highest candidate.
8. **Telemetry & Persistence**: Asynchronously record token counts, latency, cost, and routing trace into PostgreSQL.

---

## 3. Weighted Scoring Modes

The scoring engine supports 5 operating modes:

| Mode | Quality Weight ($w_q$) | Latency Weight ($w_l$) | Cost Weight ($w_c$) | Reliability Weight ($w_r$) | Capability Match ($w_m$) |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **`CHEAP`** | 0.10 | 0.10 | **0.70** | 0.05 | 0.05 |
| **`FAST`** | 0.10 | **0.70** | 0.10 | 0.05 | 0.05 |
| **`QUALITY`** | **0.70** | 0.10 | 0.10 | 0.05 | 0.05 |
| **`BALANCED`** | **0.35** | **0.20** | **0.20** | **0.20** | **0.05** |
| **`CUSTOM`** | Configurable | Configurable | Configurable | Configurable | Configurable |

---

## 4. Relational Database Schema (PostgreSQL)

```sql
-- Organizations (Tenants)
CREATE TABLE organizations (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    plan VARCHAR(50) DEFAULT 'FREE',
    budget_limit NUMERIC(10, 4),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- API Keys
CREATE TABLE api_keys (
    id VARCHAR(36) PRIMARY KEY,
    organization_id VARCHAR(36) REFERENCES organizations(id),
    key_hash VARCHAR(64) UNIQUE NOT NULL,
    name VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    last_used_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Providers Registry
CREATE TABLE providers (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    base_url VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Models Catalog
CREATE TABLE models (
    id VARCHAR(36) PRIMARY KEY,
    provider_id VARCHAR(36) REFERENCES providers(id),
    name VARCHAR(100) NOT NULL,
    capabilities VARCHAR(255),
    context_limit INT,
    input_price_per_1k NUMERIC(10, 6),
    output_price_per_1k NUMERIC(10, 6),
    quality_score NUMERIC(3, 2),
    latency_score NUMERIC(3, 2),
    reliability_score NUMERIC(3, 2),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Routing Requests Telemetry
CREATE TABLE routing_requests (
    id VARCHAR(36) PRIMARY KEY,
    organization_id VARCHAR(36) REFERENCES organizations(id),
    request_id VARCHAR(100) UNIQUE NOT NULL,
    selected_model_id VARCHAR(36) REFERENCES models(id),
    status VARCHAR(20) NOT NULL,
    mode VARCHAR(50) NOT NULL,
    latency_ms INT NOT NULL,
    input_tokens INT NOT NULL,
    output_tokens INT NOT NULL,
    estimated_cost NUMERIC(10, 6) NOT NULL,
    cache_hit BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
```
