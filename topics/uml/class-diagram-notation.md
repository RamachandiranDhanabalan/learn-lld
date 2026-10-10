# UML Class Diagram Notation

## Definition

A **class diagram** shows the static structure of a design: the classes, their attributes and methods, and the **relationships** between them (with **multiplicity**). It is the primary artifact you sketch on an interview whiteboard.

This file is about the **notation** — getting the arrows, diamonds, triangles, and numbers right. For the *semantics* (when something is composition vs aggregation), see [Class Relationships](../oops/class-relationships.md).

---

## WHY It Matters

| Benefit | Explanation |
|---------|-------------|
| Communicates ownership at a glance | A filled vs empty diamond instantly tells the reader who owns whom. |
| Multiplicity encodes design decisions | `1..*` vs `0..*` says whether an order can exist with no items — a real rule. |
| Interview credibility | Sloppy notation (all plain arrows, no numbers) reads as "doesn't know the difference." |
| Language-independent | The diagram conveys the design without any code. |

---

## Class Box

```
┌─────────────────────────┐
│        Order            │   ← name
├─────────────────────────┤
│ - id: OrderId           │   ← attributes  (- private, + public, # protected)
│ - items: List<OrderItem>│
├─────────────────────────┤
│ + addItem(p, qty): void │   ← methods
│ + total(): Money        │
└─────────────────────────┘
```
Visibility: `-` private, `+` public, `#` protected, `~` package.

For an **interface**, add the `«interface»` stereotype above the name.

---

## The Five Relationships

| Relationship | Meaning | Notation | Diamond/Triangle on |
|--------------|---------|----------|---------------------|
| Association | uses / knows about | `───▶` plain arrow | — |
| Aggregation | has-a (shared, independent lifecycle) | `◇───▶` **empty** diamond | the **owner** side |
| Composition | owns (exclusive, dies together) | `◆───▶` **filled** diamond | the **owner** side |
| Generalization | is-a (class inheritance) | `───▷` hollow triangle | points to **parent** |
| Realization | implements (interface) | `┈┈┈▷` dashed + hollow triangle | points to **interface** |

Two memory hooks:
- **Empty diamond = loose hold (aggregation). Filled diamond = strong hold (composition).**
- **The diamond sits on the owner. The triangle points at the parent/interface.**

---

## Multiplicity (the numbers)

Every association/aggregation/composition end should carry a multiplicity.

| Notation | Meaning |
|----------|---------|
| `1` | exactly one |
| `0..1` | optional (zero or one) |
| `*` or `0..*` | zero or many |
| `1..*` | at least one |
| `2..4` | a bounded range |

Read both ends of the line:

```
Order ◆──────────▶ OrderItem
      1          1..*
```
> One Order owns one-or-more OrderItems; each OrderItem belongs to exactly one Order.

```
Student ───────▶ Course
        *       *
```
> Many-to-many: a student takes many courses; a course has many students.

**State multiplicity as a design decision.** `1..*` vs `0..*` on Order→OrderItem answers "can an order have zero items?" — that is a rule, not a formatting detail.

---

## Worked Example (read it aloud)

```
                 «interface»
                ┌───────────┐
                │  Payment  │
                └───────────┘
                      △  (realization: dashed + hollow triangle)
             ┌────────┴────────┐
      ┌────────────┐    ┌────────────┐
      │ CardPayment│    │ UpiPayment │
      └────────────┘    └────────────┘

   ┌────────┐ 1     1..* ┌───────────┐
   │ Order  │◆──────────▶│ OrderItem │      (composition + multiplicity)
   └────────┘            └───────────┘
       │ *
       │
       ▼ 1
   ┌──────────┐
   │ Customer │                             (association: Order knows Customer)
   └──────────┘
```

Spoken: "Order owns 1..* OrderItems by composition. Many Orders associate with exactly one Customer. CardPayment and UpiPayment realize the Payment interface."

---

## Common Mistakes

| Mistake | Fix |
|---------|-----|
| All relationships are plain arrows | Use diamonds/triangles to show ownership + inheritance |
| No multiplicity | Add numbers to every association end |
| Filled diamond for shared objects | Empty diamond (aggregation) when the child is shared/independent |
| Triangle pointing to the child | Triangle always points to the **parent/interface** |
| Solid triangle for interface | Interface realization is **dashed** |
| Diamond on the wrong side | Diamond goes on the **owner** |

---

## When to Use / When NOT

| Situation | Use |
|-----------|-----|
| Communicating static structure of the design | Class diagram |
| Showing a workflow / steps / branches | Activity diagram (not this) |
| Showing object interactions over time | Sequence diagram (not this) |
| Showing an entity's lifecycle | State diagram (not this) |

---

## Resources

- [PlantUML class diagrams](https://plantuml.com/class-diagram)
- [uml-diagrams.org — class diagrams](https://www.uml-diagrams.org/class-diagrams-overview.html)

## Related

- [Class Relationships (semantics)](../oops/class-relationships.md)
- [Activity Diagrams](activity-diagrams.md)
- [Aggregates](../domain-modeling/aggregates.md)
- [UML index](README.md)
