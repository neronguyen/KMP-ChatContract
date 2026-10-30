# Chat — Philosophy

*Durable "why." Change rarely, and only deliberately.*

## 1. Core Claim

> **Chat is a personal lab for learning how client–server communication systems really work — by building them,
verifying them, and explaining them.**

It is not a product. Success is understanding, not users.

## 2. The Loop

I work with AI as a fast pair-programmer, but I maintain full ownership through this loop:

1. **Build (Core over Plumbing)**: I master the critical paths — data flow, contracts, and state lifecycles. AI handles
   boilerplate and peripheral configs (`OpenApi`, generators), but the architectural intent remains mine.
2. **Verify (From Manual to Automated)**: Never trust generated code blindly. Verify by seeing real messages flow, then
   turn those manual checks into automated end-to-end tests so agents can verify their own work.
3. **Synthesize (Block by Block)**: To truly own what I build, I distill it in layers — starting with mental models
   (Philosophy, Architecture, Sequence Flows), then scaling up to full articles. Writing is how I verify that I actually
   understand the system.

## 3. Decision Filter

> **Does this deepen my understanding of how systems communicate, and can I verify and explain it?**

- Build the mechanics directly; avoid black-box SDKs that hide the lesson.
- Keep the client–server contract explicit, typed, and compile-safe.
- Build for compounding knowledge, not one-off gimmicks.
- UI polish and feature cloning do not pass.

## 4. Technology Is a Vehicle

Platforms and frameworks are replaceable (Android → KMP, one backend → another).
Changing them is valid when the change itself is the lesson.

## 5. AI Relationship

> **AI is my co-pilot and senior pair-programmer; I am the gatekeeper of the architecture.**

- **Shared execution, personal direction**: Agents propose solutions, write boilerplate, and draft code. I steer the
  intent, choose the trade-offs, and guard the core data paths.
- **Pragmatic understanding**: I don't need to memorize every line of peripheral plumbing, but I must understand the
  core mechanics and explain why the system works the way it does.
- **Trust through verification**: Generated code is an unverified hypothesis until proven by automated tests or working
  flows.
- **A playground for agentic workflows**: How to guide, constrain, and automate verification for AI agents inside this
  codebase is an active subject of study.

---

Features adapt to the philosophy. The philosophy changes only deliberately.
