# UML Activity Diagrams

## Definition

An **activity diagram** models a **workflow or algorithm** — the steps of a use case, the decisions that branch it, and the paths that run in parallel. It is the "flowchart" of LLD: it answers *"what happens, in what order, with what branches, and what runs concurrently?"*

It models **behaviour over a process**, not structure (that's a class diagram) and not object messaging over time (that's a sequence diagram).

---

## WHY It Matters

| Benefit | Explanation |
|---------|-------------|
| Clarifies control flow | Makes branches and parallelism explicit before coding. |
| Surfaces concurrency | Fork/join shows what can run in parallel (notifications, independent I/O). |
| Communicates a use case | "Walk me through checkout" is answered with an activity diagram. |
| Interview signal | Using fork/join for genuinely parallel work is a senior touch. |

---

## Vocabulary

| Element | Symbol | Meaning |
|---------|--------|---------|
| Initial node | ● filled circle | start |
| Activity/action | rounded rectangle | a step |
| Decision | ◇ diamond | branch: one in, multiple **guarded** outs (choose ONE) |
| Merge | ◇ diamond | branches rejoin |
| Fork | ▬ thick bar | split into **parallel** flows |
| Join | ▬ thick bar | parallel flows synchronize |
| Guard | `[condition]` | label on a decision's outgoing edge |
| Final node | ⊙ circle-with-dot | end |
| Swimlane | vertical/horizontal partition | who/what performs the step (actor/system) |

---

## Decision (branch) — choose ONE path

```
        ●
        │
   [validate cart]
        │
        ◇──[empty]────▶ [show error] ──▶ ⊙
        │
    [not empty]
        │
   [process payment]
        │
        ◇──[failed]───▶ [show failure] ──▶ ⊙
        │
    [success]
        │
   [confirm order] ──▶ ⊙
```

Every edge out of a decision has a **guard** in brackets. Guards should be mutually exclusive and cover all cases.

---

## Fork / Join — run paths in PARALLEL

```
   [payment success]
        │
        ▬──────────────┬───────────────┐    ← FORK: all three start together
        │              │               │
  [send email]    [send SMS]   [update inventory]
        │              │               │
        ▬──────────────┴───────────────┘    ← JOIN: wait for ALL to finish
        │
   [show confirmation] ──▶ ⊙
```

The **join** waits for every parallel branch before continuing. Use fork/join when steps are independent and can overlap (notifications, calling multiple services).

---

## Decision vs Fork (the classic confusion)

| | Decision ◇ | Fork ▬ |
|---|-----------|--------|
| Shape | diamond | thick bar |
| Meaning | choose **ONE** path | run **ALL** paths in parallel |
| Analogy | `if / else` | parallel threads |
| Rejoin with | Merge ◇ | Join ▬ |
| Example | payment success **OR** failure | email **AND** SMS **AND** inventory |

> **Diamond = OR (one branch). Bar = AND (all branches).** This single sentence fixes the most common activity-diagram mistake.

---

## Swimlanes (who does what)

Partition the diagram by actor/system to show responsibility:

```
   Customer      |        System         |     PaymentGateway
   ──────────────┼───────────────────────┼─────────────────────
   [click pay] ─▶│ [validate cart]       │
                 │ [create payment] ─────▶│ [charge card]
                 │ [confirm] ◀───────────│ [return result]
```

Swimlanes answer "who is responsible for each step" — useful for multi-actor flows.

---

## Common Mistakes

| Mistake | Fix |
|---------|-----|
| Decision diamond where work is parallel | Use fork/join for concurrent steps |
| Fork with no join | Every fork should eventually join (or all branches reach an end) |
| Decision edges without guards | Label every branch with `[condition]` |
| Everything sequential | Show independent steps (notifications, I/O) as parallel |
| Modeling object messaging | That's a sequence diagram, not an activity diagram |

---

## When to Use / When NOT

| Situation | Use |
|-----------|-----|
| Walk through a use case's steps + branches | Activity diagram |
| Show concurrency in a process | Activity diagram (fork/join) |
| Show classes and relationships | Class diagram |
| Show ordered method calls between objects | Sequence diagram |
| Show an entity's state lifecycle | State diagram |

---

## Resources

- [PlantUML activity diagrams](https://plantuml.com/activity-diagram-beta)
- [uml-diagrams.org — activity diagrams](https://www.uml-diagrams.org/activity-diagrams.html)

## Related

- [Class Diagram Notation](class-diagram-notation.md)
- [UML index](README.md)
- [Problem-Solving Framework](../lld-approach/problem-solving-framework.md)
