# Chat — Philosophy

*Durable "why." Change rarely, and only deliberately.*

---

## 1. Core Claim

> **Chat is a personal systems lab for mastering how client–server communication really works — by building it,
verifying it deterministically, and explaining it publicly.**

It is not a commercial product. Success is measured by architectural clarity, deterministic verification, and the
engineering artifacts produced (production-grade codebase, public case studies, and resume proof-of-work) — not user
count.

---

## 2. The Ownership Loop

I collaborate with AI agents as high-speed executors, but I retain 100% architectural ownership through a strict
three-stage loop:

1. **Build (Core over Plumbing)**: I own the critical paths — system boundaries, wire contracts, concurrency, and state
   lifecycles. AI agents generate peripheral plumbing, boilerplate, and code from strict schemas (OpenAPI / Protobuf /
   Kotlin contracts).
2. **Verify (Deterministic Harness over Manual Checks)**: Generated code is an unverified hypothesis. Every critical
   flow must transition from observable runtime behavior into an automated, headless verification harness (CLI levers,
   contract tests, E2E pipelines) so agents can self-verify without human babysitting.
3. **Synthesize (Code to Public Artifact)**: Understanding is incomplete until explained. Every major architectural
   milestone must be distilled into mental models, sequence diagrams, and deep-dive technical articles that prove
   mastery to the broader engineering community.

---

## 3. Decision Filter & Boundary Rules

> **Does this deepen my mastery of distributed client–server mechanics, and can I deterministically verify and explain
it?**

- **Expose the Mechanics (Abstraction Ceiling)**: Use foundational transport and storage primitives (e.g., Spring Boot,
  OkHttp/Ktor, raw WebSockets/gRPC, PostgreSQL, Room). Strictly ban black-box BaaS or chat SDKs (Firebase, Sendbird,
  Pusher, Supabase Realtime) that hide connection lifecycles, backpressure, or state synchronization.
- **Schema as Single Source of Truth**: Client–server contracts must be explicit, versioned, and compile-safe. Never
  hand-write duplicate DTOs across Android and Spring Boot; all wire models must derive from a unified contract
  definition.
- **Compound Engineering Value**: Prioritize problems that teach transferable systems engineering (idempotency,
  offline-first sync, cursor pagination, reconnection backoff, automated agent harnesses).
- **Reject Cosmetic Bloat**: UI polish, animations, and clone features that do not stress-test the architecture or
  contribute to the core technical narrative do not pass.

---

## 4. Technology Is a Vehicle

Frameworks and runtimes are replaceable vehicles (Android → KMP, Spring Boot → Ktor, REST → WebSockets/gRPC). Switching
technologies is only justified when the migration itself exposes a deeper architectural trade-off worth measuring and
writing about.

---

## 5. AI & Agentic Governance

> **AI is the execution engine; I am the systems architect and governor.**

- **Constrained Autonomy**: Agents do not invent architecture. They operate inside strict module boundaries, type
  invariants, and contract schemas defined by me.
- **Build the Verification Lever**: Whenever an agent struggles to verify its work or repeats a mistake, the solution is
  not longer prompts — it is building a better deterministic tool (linter rule, headless test runner, or structured JSON
  CLI output).
- **Mandatory Agent Planning Protocol**: Whenever an AI agent proposes an implementation plan in this repository, the
  plan must explicitly state:
    1. **Core Mechanic & Trade-off**: Which state lifecycle, failure mode, or communication invariant is being solved,
       and what was traded off?
    2. **Contract & Boundary Impact**: How does this change affect the client–server schema and module boundaries?
    3. **Deterministic Verification Step**: What exact automated test or CLI command will prove this change works
       end-to-end without manual UI clicking?

---

Features adapt to the philosophy. The philosophy changes only deliberately.
