# Day 28 — Full OOAD Exercise (Phase 4 Capstone)

## Cheat Sheet

- The 8-step pipeline IS a 45-min LLD interview: scope → actors → entities/VOs → aggregates → class diagram → sequence → state → what-if.
- **Order matters**: scope first, classes derived from entity decisions, patterns last.
- **Go deep SELECTIVELY**: state machine for the stateful entity, sequence for the tricky flow, class diagram always. Don't draw everything.
- **End with "what if?"** (new type / concurrency / failure) — unprompted. Senior vs mid separator.
- Apply **Day 21 restraint** throughout — don't sprinkle patterns.

## The 8 Steps

| # | Step | Day |
|---|------|-----|
| 1 | Requirements & scope (clarify, assumptions, fork questions) | 25 |
| 2 | Actors & use cases (the verbs) | — |
| 3 | Entities & Value Objects (nouns, classified) | 22 |
| 4 | Aggregates & ownership (roots, reference-by-ID) | 23 |
| 5 | Class diagram (relationships + multiplicity) | 24/25 |
| 6 | Sequence — ONE key flow | 26 |
| 7 | State machine — the stateful entity | 27 |
| 8 | "What if?" — extensibility / concurrency / failure | framework |

## Time budget (45 min)

- 1–5 solidly (~25 min) — scope, entities, aggregates, class diagram.
- 6–7 for the ONE interesting part (~10 min) — state machine / tricky sequence.
- 8 verbally (~10 min) — what-if pressure.

## Where points live

| Steps | What |
|-------|------|
| 1–2 | scoping/actors → seniority signal + thinking time |
| 3–4 | entity/VO/aggregate decisions → the real design |
| 5 | class diagram → the artifact, derived not invented |
| 6–7 | selective depth → judgment |
| 8 | what-if → senior vs mid |

## Interview Questions & Answers

**Q: "Design an Order Management System." (how do you start?)**
A: "I'll scope first — place/pay/ship/deliver/cancel in, inventory and real gateway out. Then actors and use cases, then I model entities and value objects, decide aggregates, draw the class diagram, and go deep on the Order state machine. I'll finish with extensibility and failure cases."

**Q: "Which diagrams would you actually draw?"**
A: "Class diagram always — it's the backbone. A state machine for Order since it has a real lifecycle. A sequence only for a tricky flow like payment-with-rollback. I wouldn't draw diagrams for trivial CRUD paths."

**Q: "What makes a design senior-level vs just correct?"**
A: "Ending with the what-ifs: how do we add a new order type without touching existing code (OCP), what happens if two cancels race (guard + idempotency), what if payment succeeds but shipping fails (compensation). The happy-path model is table stakes; the pressure handling is the signal."

**Q: "How do you avoid over-engineering in a full design?"**
A: "Patterns come last and only on a real force. Enum for fixed sets, VO for value types, Strategy only where behaviour genuinely varies and grows. I state when I'd NOT add a pattern."

## Clues & Signals

- **Senior signal**: running the pipeline in order; scoping before classes.
- **Senior signal**: selective depth — not drawing every diagram.
- **Senior signal**: unprompted what-if (new type / race / failure).
- **Senior signal**: narrating each modeling decision + its force.
- **Red flag**: code/classes first, no scope.
- **Red flag**: exhaustive diagrams; patterns everywhere; no failure discussion.

## Quick Links

- **Detailed topic**: [OOAD Pipeline](../topics/lld-approach/ooad-pipeline.md)
- **Related**: [Problem-Solving Framework](../topics/lld-approach/problem-solving-framework.md)
- **Related**: [Full Class Diagram Walkthrough](../topics/uml/full-class-diagram-walkthrough.md)
- **Next**: Parking Lot — first full LLD problem (Day 29)
