# Day 24 — UML Relationships + Activity Diagrams

## Cheat Sheet

- **5 relationships**: Association, Aggregation, Composition, Generalization, Realization.
- **Diamond on the OWNER. Triangle points to the PARENT/interface.**
- **Empty diamond ◇ = aggregation (loose/shared). Filled diamond ◆ = composition (owns/dies together).**
- **Generalization ▷ = solid + hollow triangle (class). Realization ▷ = DASHED + hollow triangle (interface).**
- **Always write multiplicity** (`1`, `0..1`, `*`, `1..*`) on association ends — it's a design decision.
- **Activity diagram = workflow**: decision ◇ = choose ONE; fork/join ▬ = run ALL in parallel.
- **Diamond = OR. Bar = AND.**

## Notation Table

| Relationship | Meaning | Symbol |
|--------------|---------|--------|
| Association | uses/knows | `───▶` plain arrow |
| Aggregation | has-a (shared) | `◇───▶` empty diamond |
| Composition | owns (exclusive) | `◆───▶` filled diamond |
| Generalization | is-a (extends) | `───▷` hollow triangle (solid line) |
| Realization | implements | `┈┈▷` hollow triangle (dashed line) |

## Multiplicity

| Notation | Means |
|----------|-------|
| `1` | exactly one |
| `0..1` | optional |
| `*` / `0..*` | zero or many |
| `1..*` | at least one |
| `2..4` | bounded range |

```
Order ◆────────▶ OrderItem      "1 Order owns 1..* OrderItems;
      1       1..*                each item belongs to exactly 1 Order"
```

## Activity Diagram Elements

| Element | Symbol |
|---------|--------|
| Start | ● |
| Activity | rounded rect |
| Decision / Merge | ◇ (guards in `[ ]`) |
| Fork / Join | ▬ thick bar |
| End | ⊙ |
| Swimlane | partition by actor/system |

### Decision vs Fork

| | Decision ◇ | Fork ▬ |
|---|-----------|--------|
| Meaning | choose ONE (if/else) | run ALL (parallel) |
| Rejoin | Merge ◇ | Join ▬ |
| Example | pay success OR fail | email AND SMS AND inventory |

## Critical Example — Activity (fork/join)

```
[payment success]
     │
     ▬──────────┬────────────┐        ← fork (parallel)
[send email] [send SMS] [update inventory]
     │          │            │
     ▬──────────┴────────────┘        ← join (wait for all)
     │
[show confirmation] ──▶ ⊙
```

## Interview Questions & Answers

**Q: "Difference between aggregation and composition in UML?"**
A: "Both are has-a. Aggregation is an empty diamond — the child has an independent lifecycle and can be shared. Composition is a filled diamond — the child is exclusively owned and dies with the parent. The diamond sits on the owner's side."

**Q: "How do you show a class implementing an interface vs extending a class?"**
A: "Both use a hollow triangle pointing to the parent. Extends (generalization) is a solid line; implements (realization) is a dashed line."

**Q: "What's multiplicity and why does it matter?"**
A: "The numbers on an association end — 1, 0..1, *, 1..*. It encodes a design decision, like whether an order must have at least one item (1..*) or can be empty (0..*). I always annotate it."

**Q: "Decision node vs fork in an activity diagram?"**
A: "A decision (diamond) chooses one path based on guards — like if/else. A fork (thick bar) splits into parallel paths that all run, then a join waits for all of them. Diamond = OR, bar = AND."

**Q: "When would you draw an activity diagram vs a sequence diagram?"**
A: "Activity for the workflow of a use case — steps, branches, parallelism. Sequence for the ordered messages between specific objects over time."

## Clues & Signals

- **Senior signal**: annotating every association with multiplicity and calling it a design decision.
- **Senior signal**: using fork/join for genuinely parallel steps (notifications, independent I/O) instead of forcing everything sequential.
- **Senior signal**: correct diamond side (owner) and triangle direction (parent).
- **Red flag**: all plain arrows (no ownership shown).
- **Red flag**: no multiplicity anywhere.
- **Red flag**: decision diamond where the work is actually parallel.
- **Red flag**: solid triangle for interface implementation (should be dashed).

## Quick Links

- **Detailed topic**: [Class Diagram Notation](../topics/uml/class-diagram-notation.md)
- **Detailed topic**: [Activity Diagrams](../topics/uml/activity-diagrams.md)
- **Related (semantics)**: [Class Relationships](../topics/oops/class-relationships.md)
- **Next**: Class Diagrams — full Parking Lot (Day 25)
