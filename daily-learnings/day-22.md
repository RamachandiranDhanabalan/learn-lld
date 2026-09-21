# Day 22 — Entity vs Value Object

## Cheat Sheet

- **Entity** = has identity, tracked over time, answers "which one?". Equals by **ID**.
- **Value Object (VO)** = defined by values, interchangeable, answers "what?". Equals by **all fields**.
- **The test**: *"Two instances with identical fields — same thing?"* Yes → VO. No → Entity.
- **VOs are IMMUTABLE.** To "change" one, return a new instance.
- **Money is NEVER a `double`** → `Money` VO with integer cents / `BigDecimal`.
- Entity `equals`/`hashCode` → **ID only**. VO → **all fields**.
- VOs are where validation + small behaviour live (`money.add()`, `range.overlaps()`).

## Critical Example

```java
// ENTITY — identity by ID; balance changes but it's the SAME account
class Account {
    private final String id;      // identity
    private Money balance;        // mutable state
    public boolean equals(Object o){ return o instanceof Account a && id.equals(a.id); } // ID only
    public int hashCode(){ return id.hashCode(); }
}

// VALUE OBJECT — immutable, equality by value, no mutation
final class Money {
    private final long cents;     // integer cents — no float bugs
    private final Currency currency;
    Money add(Money o){ return new Money(cents + o.cents, currency); }  // NEW instance
    public boolean equals(Object o){ return o instanceof Money m && cents==m.cents && currency==m.currency; }
    public int hashCode(){ return Objects.hash(cents, currency); }
}
```

## Decision Framework

| Signal | Model as |
|--------|----------|
| "track it over time / has a lifecycle" | Entity |
| "two identical-looking ones are still distinct" | Entity |
| "defined purely by value, interchangeable" | Value Object |
| Money, Address, DateRange, Coordinates, Quantity | Value Object |
| Customer, Order, Account, Booking, Seat, Payment | Entity |

## Why VOs are immutable

1. Safe to share (no aliasing bugs)
2. Safe as Map key / Set element (hashCode never changes)
3. Thread-safe for free
4. No spooky side effects via shared references

## Interview Questions & Answers

**Q: "Entity vs Value Object?"**
A: "Entity has identity tracked over time — equals by ID. Value Object is defined by its values, immutable, interchangeable — equals by all fields. Test: if two instances with the same fields are the same thing, it's a VO; if they're distinct, it's an entity."

**Q: "How would you model money?"**
A: "A `Money` value object — immutable, integer cents or BigDecimal (never double, to avoid rounding), with currency. Arithmetic returns new instances and rejects currency mismatches."

**Q: "Why must Value Objects be immutable?"**
A: "So they're safe to share, safe as map keys (hashCode won't change mid-life), thread-safe, and free of side effects. To 'change' one you return a new instance — like String or LocalDate."

**Q: "Where do you put equals/hashCode?"**
A: "Entity: ID only — a customer renaming is still the same customer. Value Object: all fields — two addresses are equal iff every field matches."

**Q: "Is Address an entity or a value object?"**
A: "Usually a VO — defined by its values, interchangeable. It becomes an entity only if the domain tracks a specific address over time with its own identity (rare)."

## Clues & Signals

- **Senior signal**: "Money is never a double — it's an immutable Money VO with integer cents."
- **Senior signal**: pushing validation + small behaviour into VOs to keep entities thin.
- **Senior signal**: explicitly stating equality semantics (ID vs all-fields) while modeling.
- **Red flag**: everything is an entity with a DB `id` (primitive-obsessed, anemic).
- **Red flag**: `double amount` for money.
- **Red flag**: mutable value object used as a Map key.
- **Red flag**: no equals/hashCode reasoning at all.

## Trade-offs

| Choice | Pro | Con |
|--------|-----|-----|
| Value Object | immutable, safe, self-validating, reusable | more classes than raw primitives |
| Entity | models identity + lifecycle naturally | must manage ID + correct equals |
| Everything-entity (anti) | fewer classes upfront | primitive obsession, bugs, no reuse |

## Quick Links

- **Detailed topic**: [Entity vs Value Object](../topics/domain-modeling/entity-vs-value-object.md)
- **Related**: [Class Relationships](../topics/oops/class-relationships.md)
- **Related**: [Encapsulation](../topics/oops/encapsulation.md)
- **Next**: Aggregates + Ownership (Day 23)
