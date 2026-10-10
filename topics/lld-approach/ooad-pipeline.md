# The OOAD Pipeline (Full LLD Walkthrough)

## Definition

The end-to-end sequence for taking an LLD prompt from a blank page to a defensible
design. This *is* the structure of a 45-minute LLD interview. No new concepts — it
chains everything from Phase 4 (Days 22–27) plus the problem-solving framework.

---

## The 8 Steps (in order)

```
1. Requirements & Scope    — clarify, state assumptions, ask fork questions (Day 25)
2. Use Cases / Actors      — who does what (the verbs)
3. Entities & Value Objects — nouns, classified (Day 22)
4. Aggregates & Ownership  — who owns whom, roots, reference-by-ID (Day 23)
5. Class Diagram           — relationships + multiplicity (Day 24/25)
6. Interactions (Sequence) — walk ONE key flow (Day 26)
7. State Machine           — the stateful entity's lifecycle (Day 27)
8. "What if?" pressure     — extensibility, concurrency, failure (framework)
```

Each step feeds the next. Skip step 1 and everything is unscoped. Jump to step 5
without 2–4 and you get unjustified boxes.

---

## WHY the order matters

| Steps | Purpose | Signal |
|-------|---------|--------|
| 1–2 Scope + actors | bound the problem, buy thinking time | seniority marker |
| 3–4 Entities + aggregates | the real design decisions | where points live |
| 5 Class diagram | the artifact — DERIVED from 3–4 | the memorable output |
| 6–7 Sequence + state | go deep on the INTERESTING part only | judgment |
| 8 What-if | extensibility / concurrency / failure | senior vs mid |

> You won't do all 8 exhaustively in 45 min. Do 1–5 solidly, then 6–7 for the ONE
> interesting part, then 8 verbally. Knowing **what to go deep on** is the skill.

---

## Worked Spine — Order Management

### 1. Scope
"In: place order with items, pay, ship, deliver, cancel/refund within rules.
Out: inventory, multi-warehouse, real gateway, returns-after-delivery. Focus: the
order lifecycle."

### 2. Actors / Use Cases
Customer: place / pay / cancel / track. System: validate / charge / update / notify.
Admin: refund (defer if out of scope).

### 3–4. Entities / VOs / Aggregates
| Thing | Type |
|-------|------|
| Order | Entity, aggregate root (owns items) |
| OrderItem | Entity, internal (composition) |
| Customer | separate aggregate → reference by ID |
| Money, Address | Value Objects |
| OrderStatus, PaymentStatus | enums |
| Payment | Entity, small aggregate |

### 5. Class diagram
`Order ◆──1..* OrderItem` · `Order ──▶ CustomerId` · `Order ──▶ Payment` ·
`Order ──▶ PricingStrategy` only if pricing varies.

### 7. State machine (the interesting part)
`CREATED → PAID → SHIPPED → DELIVERED`; `CANCELLED` (before ship), `REFUNDED` (after pay).
Forbidden: `DELIVERED → CANCELLED`, any backwards move.

### 8. What if?
- New order type → strategy / subtype (OCP).
- Two cancels race → guard on status, idempotent.
- Paid but ship fails → compensation/saga (Day 68).

---

## Going Deep Selectively (the key judgment)

Don't draw every diagram. Pick:
- **State machine** → for the entity with a real lifecycle (Order, Payment, Ride).
- **Sequence** → for the one tricky collaboration (payment-with-rollback).
- **Class diagram** → always (it's the backbone).
- Skip activity/sequence for trivial CRUD paths.

---

## Common Mistakes

| Mistake | Fix |
|---------|-----|
| Jump straight to classes/code | Scope + actors first |
| Draw every diagram exhaustively | Go deep only on the interesting part |
| Boxes with no justification | Derive from entity/VO/aggregate decisions |
| No failure/concurrency talk | End with "what if?" unprompted |
| Over-engineer with patterns everywhere | Apply Day 21 restraint throughout |

---

## Resources

- [Problem-Solving Framework](problem-solving-framework.md)
- [Full Class Diagram Walkthrough](../uml/full-class-diagram-walkthrough.md)

## Related

- [Entity vs Value Object](../domain-modeling/entity-vs-value-object.md)
- [Aggregates](../domain-modeling/aggregates.md)
- [State Diagrams](../uml/state-diagrams.md)
- [Pattern Selection](../design-patterns/pattern-selection.md)
