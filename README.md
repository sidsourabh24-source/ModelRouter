# ModelRouter — Adaptive AI Inference & Model Routing Platform

## Overview
ModelRouter is an AI inference gateway / model-routing control plane built with **Java 21**, **Spring Boot**, **PostgreSQL**, **Redis**, and a **Next.js + TypeScript** operational dashboard.

It sits between client applications and multiple AI model providers (OpenAI, Anthropic, Gemini, DeepSeek, etc.), dynamically choosing the optimal model per request based on task complexity, cost, latency, quality, and reliability policies.

---

## Workspace Structure
```
d:\ModelRouter\
├── docs/
│   ├── implementation_plan.md    # Active implementation plan
│   ├── SRS.md                    # Formal Software Requirements Specification
│   ├── architecture.md           # Architecture blueprint & diagrams
│   └── api.md                    # REST API Contracts
├── ModelRouter_SRS_Full_Development_Specification.pdf
└── README.md
```

---

## Key Features & Goals (MVP)
1. **Unified Gateway Endpoint**: Single `/api/v1/chat` endpoint replacing multi-provider client logic.
2. **Explainable Rule-Based Routing**: Deterministic routing engine with modes (`CHEAP`, `FAST`, `QUALITY`, `BALANCED`).
3. **Provider Fallback & Resilience**: Automatic retry and fallback when primary providers time out or fail.
4. **Exact Caching**: Redis exact-response caching to minimize duplicate provider costs.
5. **Usage & Cost Telemetry**: Request-level tracking of input/output tokens, latency, status, and cost.
6. **Operational Dashboard**: Next.js admin UI for monitoring model usage, cost distribution, and system health.

---

## Saved Session Log Reference
- **Conversation ID**: `6212a2d0-9928-4dba-83bd-6de8d2f56d56`
- **Transcript Path**: `C:\Users\SOURABH\.gemini\antigravity-ide\brain\6212a2d0-9928-4dba-83bd-6de8d2f56d56\.system_generated\logs\transcript.jsonl`
- **Specification Source**: [ModelRouter_SRS_Full_Development_Specification.pdf](file:///d:/ModelRouter/ModelRouter_SRS_Full_Development_Specification.pdf)
