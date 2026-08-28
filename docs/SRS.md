# ModelRouter — Software Requirements Specification (SRS)

## 1. Executive Summary

ModelRouter is an adaptive AI inference gateway and model-routing control plane built for high-scale AI applications. Rather than connecting directly to single model vendors, applications interface with ModelRouter via a single unified API endpoint.

### Core Architecture Diagram
```text
Customer Application
  │
  ▼
ModelRouter API (/api/v1/chat)
  ├── Security / API Key Authentication
  ├── Task & Complexity Classification
  ├── Candidate Model Filtering
  ├── Multi-Objective Weighted Scoring
  ├── Redis Response Cache
  └── Provider Adapters (OpenAI, Anthropic, Gemini, DeepSeek, Mock)
        │
        ▼
Response + Usage/Cost/Routing Trace Telemetry
```

---

## 2. System Goals & Requirements

### 2.1 MVP Objectives
- **Single Gateway Endpoint**: `POST /api/v1/chat`
- **Multi-Provider Support**: Pluggable provider adapters (OpenAI, Anthropic, Mock).
- **Explainable Rule-Based Routing**: Dynamic scoring based on `CHEAP`, `FAST`, `QUALITY`, `BALANCED`, or `CUSTOM` policy modes.
- **Resilience & Fallback**: Automatic provider timeout handling and candidate fallback chaining.
- **Redis Response Caching**: Exact prompt matching cache (`cache:chat:<sha256>`).
- **Telemetry & Cost Tracking**: Token accounting, latency, provider selection reasoning, and cost estimation.
- **Operational Dashboard**: Next.js 14 dashboard for telemetry visualization and model policy management.

### 2.2 Functional Requirements (FR)

| ID | Requirement | Description |
| :--- | :--- | :--- |
| **FR-01** | **Authentication** | Clients authenticate using `X-API-Key` headers (hashed using SHA-256). |
| **FR-02** | **Provider Management** | Admins can register, enable, disable, and configure provider adapters. |
| **FR-03** | **Policy Configuration** | Admins can set scoring weights ($w_q, w_l, w_c, w_r, w_m$) per organization. |
| **FR-04** | **Inference Gateway** | Single REST endpoint accepting standard chat completion payloads. |
| **FR-05** | **Task Classification** | System estimates prompt complexity and task classification tags. |
| **FR-06** | **Candidate Filtering** | Ineligible models (disabled, unhealthy, context mismatch) are excluded. |
| **FR-07** | **Scoring & Selection** | System calculates weighted score and routes to highest-ranked candidate. |
| **FR-08** | **Automatic Fallback** | Provider timeout/failure triggers call to next eligible candidate. |
| **FR-09** | **Redis Caching** | Identical requests return cached responses instantly without provider calls. |
| **FR-10** | **Telemetry Tracking** | Tokens, latency, cost, and routing trace recorded per request. |
| **FR-11** | **Operational Dashboard** | UI displays cost savings, latency p95, model usage, and traces. |

---

## 3. Database & Entity Design (PostgreSQL)

The relational schema strictly enforces tenant isolation and audit capability:

```sql
-- Organizations
CREATE TABLE IF NOT EXISTS organizations (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    plan VARCHAR(50) DEFAULT 'FREE',
    budget_limit NUMERIC(10, 4),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- API Keys
CREATE TABLE IF NOT EXISTS api_keys (
    id VARCHAR(36) PRIMARY KEY,
    organization_id VARCHAR(36) REFERENCES organizations(id),
    key_hash VARCHAR(64) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    last_used_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Providers
CREATE TABLE IF NOT EXISTS providers (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    base_url VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Models Catalog
CREATE TABLE IF NOT EXISTS models (
    id VARCHAR(36) PRIMARY KEY,
    provider_id VARCHAR(36) REFERENCES providers(id),
    name VARCHAR(100) NOT NULL,
    capabilities VARCHAR(255),
    context_limit INT NOT NULL,
    input_price_per_1k NUMERIC(10, 6) NOT NULL,
    output_price_per_1k NUMERIC(10, 6) NOT NULL,
    quality_score NUMERIC(3, 2) DEFAULT 0.80,
    latency_score NUMERIC(3, 2) DEFAULT 0.80,
    reliability_score NUMERIC(3, 2) DEFAULT 0.99,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Routing Requests Telemetry
CREATE TABLE IF NOT EXISTS routing_requests (
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
    reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
```
