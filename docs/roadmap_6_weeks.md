# 📅 ModelRouter — 6-Week Day-by-Day Master Execution Roadmap

## Executive Overview
This document outlines the exact day-by-day development schedule to build **ModelRouter** into a fully functional, production-ready AI inference gateway and control plane.

---

## 👥 Roles & Responsibilities

| Task Area | Antigravity AI Agent (Senior Engineer) | User (Product Lead / Co-Engineer) |
| :--- | :--- | :--- |
| **Architecture & Docs** | Writes `SRS.md`, `architecture.md`, `api.md`, ADRs | Reviews & approves architectural decisions |
| **Backend Code** | Writes Java 21 / Spring Boot 3.x controllers, services, routing math, security | Tests APIs, provides feedack |
| **Database & Cache** | Writes PostgreSQL DDL schema, JPA entities, Redis cache services | Runs `docker compose up -d` to launch DB/Redis |
| **Frontend UI** | Builds Next.js 14 + Tailwind + Shadcn UI admin dashboard | Explores UI dashboard, tests controls |
| **API Credentials** | Implements Mock Provider Adapters & OpenAI/Anthropic/Gemini adapters | Adds real provider API keys to local `.env` when testing live calls |
| **Testing & Verification**| Writes JUnit 5, Mockito, and integration test suites | Runs test suite & verifies feature completion |

---

## 🗓️ Phase Breakdown

### Week 1: Core Foundation, DB Schema & Security Architecture
- **Day 1**: Scaffolding GitHub repo layout (`backend/`, `frontend/`, `docs/`), setting up Java 21 Spring Boot `pom.xml`.
- **Day 2**: Designing relational PostgreSQL database schema (`users`, `organizations`, `api_keys`, `providers`, `models`, `routing_policies`, `routing_requests`, `routing_attempts`).
- **Day 3**: Implementing Spring Data JPA Entities, Repositories, DB Indexes, and Audit Log entity.
- **Day 4**: Implementing Security Filter (`ApiKeyAuthenticationFilter`), SHA-256 key hashing, and Organization context resolver.
- **Day 5**: Creating REST Controllers for Admin Organization & API Key Management (`/api/v1/admin/orgs`, `/api/v1/admin/keys`).

### Week 2: Provider Abstraction & Gateway Engine
- **Day 6**: Defining `ModelProvider` Strategy Interface & request/response DTO contracts (`InferenceRequest`, `InferenceResponse`).
- **Day 7**: Building `MockProviderAdapter` with simulated latency, cost per token calculation, and configurable error rates.
- **Day 8**: Building OpenAI (`OpenAiProviderAdapter`) and Anthropic (`AnthropicProviderAdapter`) integration adapters.
- **Day 9**: Implementing main Gateway API Controller (`POST /api/v1/chat`).
- **Day 10**: Writing Provider Contract unit and integration tests using Mockito & MockWebServer.

### Week 3: Intelligent Classifier, Weighted Scoring & Fallback System
- **Day 11**: Developing Task & Complexity Classifier (Detecting coding, reasoning, creative tasks, context length).
- **Day 12**: Building Candidate Filter Engine (Filtering disabled, unhealthy, or capability-mismatched models).
- **Day 13**: Implementing Multi-Objective Scoring Algorithm (`qualityWeight`, `latencyWeight`, `costWeight`, `reliabilityWeight`).
- **Day 14**: Building Resilience & Fallback Engine (Timeout enforcement, transient error retry, automatic failover chain).
- **Day 15**: Building Explainability Engine (Generating human-readable decision reasons attached to every response).

### Week 4: Redis Cache Layer, Rate Limiting & Telemetry Persistence
- **Day 16**: Implementing Redis Response Cache (`cache:chat:<sha256>`) with exact-match retrieval & configurable TTL.
- **Day 17**: Implementing Redis Rate Limiting (`rate:<org>:<window>`) using sliding window algorithm.
- **Day 18**: Implementing Real-Time Model Health Tracking in Redis (`health:model:<id>`).
- **Day 19**: Implementing Async Telemetry Persistence for token usage, latency, and estimated cost tracking.
- **Day 20**: Developing Analytics APIs (`/api/v1/admin/analytics/overview`, `/api/v1/admin/requests`).

### Week 5: Next.js 14 Operational Admin Dashboard
- **Day 21**: Scaffolding Next.js 14 (App Router) + TypeScript + Tailwind CSS application in `frontend/`.
- **Day 22**: Building Overview KPI Dashboard (Total Requests, Total Cost, Savings %, Avg Latency, Cache Hit Rate).
- **Day 23**: Building Models & Providers Management Page (Enable/disable models, health status, pricing editor).
- **Day 24**: Building Routing Policies & Weight Configurator UI (Interactive sliders for scoring modes).
- **Day 25**: Building Request Explorer & Trace Inspector (Viewing complete step-by-step model selection audit logs).

### Week 6: Containerization, Testing, Benchmarking & GitHub Portfolio Polish
- **Day 26**: Authoring `docker-compose.yml` orchestrating PostgreSQL, Redis, Backend API, and Next.js Dashboard.
- **Day 27**: Writing Comprehensive Integration Test Suite (JUnit 5 + Spring Boot Test + Testcontainers).
- **Day 28**: Running Benchmark Suite (Verifying <100ms routing overhead and measuring cost savings).
- **Day 29**: Finalizing Technical Documentation (`SRS.md`, `architecture.md`, `api.md`, `ADR-001-Routing-Engine.md`).
- **Day 30**: Polishing GitHub Repository README, badges, architecture diagrams, and demo setup instructions.
