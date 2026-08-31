# 🌿 ModelRouter Git Branching & Commit Protocol

## Core Rule
**Never commit directly to `main`**. All work is structured **Module-Wise (Phase Branches)** and committed **Day-Wise**.

---

## 📌 Module-Wise Feature Branches

Each 5-day execution module maps to a dedicated feature branch:

| Module / Phase | Branch Name | Days Included | Focus Area |
| :--- | :--- | :--- | :--- |
| **Module 1** | `feature/phase-1-foundation` | **Days 1 – 5** | Core Foundation, DB Schema, Security Filter, Admin Controllers |
| **Module 2** | `feature/phase-2-provider-gateway` | **Days 6 – 10** | Provider Adapters (OpenAI, Anthropic, Mock), Gateway `/chat` |
| **Module 3** | `feature/phase-3-routing-scoring` | **Days 11 – 15** | Task Classifier, Weighted Scoring Math, Fallbacks, Explainability |
| **Module 4** | `feature/phase-4-redis-telemetry` | **Days 16 – 20** | Redis Cache, Sliding Window Rate Limiting, Async Telemetry |
| **Module 5** | `feature/phase-5-nextjs-dashboard` | **Days 21 – 25** | Next.js 14 Operational Admin Dashboard |
| **Module 6** | `feature/phase-6-docker-testing` | **Days 26 – 30** | Docker Orchestration, Benchmarks & Portfolio Polish |

---

## 📅 Day-Wise Commit Message Standard

Within each module branch, commits are created day-by-day using conventional commit format:

- **Day 1**: `feat(day-1): setup project scaffolding, java 21 spring boot pom, and architecture docs`
- **Day 2**: `feat(day-2): add relational postgresql schema ddl, seed data and docker compose`
- **Day 3**: `feat(day-3): implement spring data jpa entities and repositories for orgs, keys, providers, and models`
- **Day 4**: `feat(day-4): implement api key security filter, sha-256 key hashing, and org security context`
- **Day 5**: `feat(day-5): implement admin REST controllers for orgs and api keys with request telemetry persistence`
- **Day 6+**: `feat(day-X): [descriptive summary of feature built on Day X]`

---

## 🔄 Daily Workflow Command Template

```bash
# 1. Checkout/Ensure Module Branch
git checkout feature/phase-X-[module-name]

# 2. Stage Day X Files
git add [path/to/day-X-files]

# 3. Commit Day X Work
git commit -m "feat(day-X): [summary]"

# 4. Push Module Branch to GitHub
git push -u origin feature/phase-X-[module-name]
```
