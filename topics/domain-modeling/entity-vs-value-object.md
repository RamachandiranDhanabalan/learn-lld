# Entity vs Value Object

## Definition

Every class you model in a domain is one of two kinds:

- **Entity** — has a distinct **identity** that persists over time, independent of its attribute values. You care about *which one* it is.
- **Value Object (VO)** — has **no identity**; it is defined entirely by its attribute values. You care only about *what* it is. Two VOs with equal values are interchangeable.

It is NOT about "big class vs small class" or "has a DB row vs doesn't." It is about **identity vs value**.

---

## The One-Question Test

> **If two instances have identical field values, are they the same thing?**

- **Yes — interchangeable** → Value Object. Two `Money(100, USD)` are the same money.
- **No — distinct things** → Entity. Two `Customer` named "John" are different people.

---

## WHY It Matters

| Benefit | Explanation |
|---------|-------------|
| Correct equality | Entities compare by ID; VOs compare by value. Getting this wrong merges or splits objects incorrectly. |
| Fewer bugs | Immutable VOs are safe to share, thread-safe, and safe as Map keys. |
| Rich domain, thin entities | Validation + small behaviour live in VOs (`money.add()`, `range.overlaps()`), keeping entities focused on lifecycle. |
| Kills primitive obsession | `Money`, `Address`, `DateRange` replace loose `double`/`String` fields that invite bugs. |
| Interview signal | "Money is never a double; it's an immutable Value Object" is an instant maturity marker. |

---

## Comparison Table

| Dimension | Entity | Value Object |
|-----------|--------|--------------|
| Identity | Unique ID, tracked over time | None; defined by values |
| Equality | Same ID → equal | All fields equal → equal |
| Mutability | Usually mutable (lifecycle) | **Immutable** (return new instance) |
| Question it answers | "Which one?" | "What?" |
| Lifespan | Has a lifecycle | Interchangeable, disposable |
| `equals`/`hashCode` on | ID only | All fields |
| Examples | Customer, Order, Account, Booking, Seat | Money, Address, DateRange, Coordinates, Color |

---

## Detailed Explanation

### Identity = continuity

An entity stays the *same entity* even as its data changes.

```java
Account acc = new Account("ACC-123");
acc.deposit(500);   // balance changes
acc.withdraw(200);  // balance changes again
// STILL the same account. Identity (ACC-123) is independent of balance.
```

Your name changing does not make you a different person — you are an entity with continuity. That continuity is exactly what an ID captures.

### Value = no continuity

A value object has no continuity. If the value changes, it is simply a *different value* — the old one did not "become" the new one.

```java
Money price = new Money(100, USD);
Money bigger = price.add(new Money(50, USD));  // NEW Money(150, USD)
// price is still Money(100, USD). It didn't transform; we produced a new value.
```

---

## Examples (Java)

### ❌ BAD — primitive obsession, everything an entity

```java
class Order {
    String id;
    double amount;        // no currency; float rounding bugs (0.1 + 0.2 != 0.3)
    String currency;
    String street, city, zip;   // scattered address — no validation, easy to mishandle
    // equals? none — or worse, generated on all fields including id
}
```

Problems: money as `double` (rounding), address scattered as loose strings (no validation, no reuse), unclear equality.

### ✅ GOOD — Money and Address as immutable Value Objects

```java
import java.util.Objects;

enum Currency { USD, INR, EUR }

// VALUE OBJECT — immutable, self-validating, equality by value
final class Money {
    private final long cents;         // integer cents — no floating-point error
    private final Currency currency;

    Money(long cents, Currency currency) {
        if (cents < 0) throw new IllegalArgumentException("money cannot be negative");
        this.cents = cents;
        this.currency = Objects.requireNonNull(currency);
    }

    Money add(Money other) {
        requireSameCurrency(other);
        return new Money(this.cents + other.cents, currency);  // NEW instance, no mutation
    }

    Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(this.cents - other.cents, currency);
    }

    private void requireSameCurrency(Money other) {
        if (this.currency != other.currency)
            throw new IllegalArgumentException("currency mismatch");
    }

    @Override public boolean equals(Object o) {
        return (o instanceof Money m) && cents == m.cents && currency == m.currency;
    }
    @Override public int hashCode() { return Objects.hash(cents, currency); }
    @Override public String toString() { return currency + " " + (cents / 100.0); }
}

// VALUE OBJECT — immutable, equality by ALL fields
final class Address {
    private final String street, city, zip;

    Address(String street, String city, String zip) {
        this.street = Objects.requireNonNull(street);
        this.city   = Objects.requireNonNull(city);
        this.zip    = Objects.requireNonNull(zip);
    }

    @Override public boolean equals(Object o) {
        return (o instanceof Address a)
            && street.equals(a.street) && city.equals(a.city) && zip.equals(a.zip);
    }
    @Override public int hashCode() { return Objects.hash(street, city, zip); }
}

// ENTITY — identity by ID; other fields can change
class Order {
    private final String id;          // identity
    private Money amount;             // a Value Object field
    private Address shipTo;          // a Value Object field
    private String status;           // mutable state over lifecycle

    Order(String id, Money amount, Address shipTo) {
        this.id = id;
        this.amount = amount;
        this.shipTo = shipTo;
        this.status = "CREATED";
    }

    void applyDiscount(Money discount) { this.amount = amount.subtract(discount); }

    @Override public boolean equals(Object o) {
        return (o instanceof Order other) && id.equals(other.id);  // ID ONLY
    }
    @Override public int hashCode() { return id.hashCode(); }
}
```

Note the split: **entity equals on ID only**, **value objects equals on all fields**.

---

## Why Value Objects Must Be Immutable

1. **Safe to share** — no aliasing bugs; two entities can hold the same VO instance.
2. **Safe as Map key / Set element** — a mutable object whose `hashCode` changes corrupts the collection (lost entries).
3. **Thread-safe for free** — no mutation → no data races.
4. **No spooky action at a distance** — `orderA`'s price can't be silently changed by code holding the same reference.

To "change" a VO, return a new one (`money.add()` returns a new `Money`). Same idea as Java's `String` and `LocalDate`.

---

## Common Violations / Mistakes

| Mistake | Fix |
|---------|-----|
| `double amount` for money | `Money` VO with integer cents or `BigDecimal` |
| Address as `street`, `city`, `zip` strings on the entity | Extract an `Address` value object |
| Mutable value object | Make fields `final`, return new instances on "change" |
| `equals` on all fields of an entity | Compare ID only |
| `equals` on ID for a value object | Compare all fields (or don't invent an ID) |
| Everything is an entity with a DB `id` | Ask the one-question test; many are VOs |
| Mutable VO used as `Map` key | Make it immutable first |

---

## When to Use / When NOT to Use

| Situation | Model as |
|-----------|----------|
| You need to track it over time / distinguish two identical-looking ones | Entity |
| Defined purely by its values; interchangeable | Value Object |
| Money, dates/ranges, coordinates, quantities, addresses, colors | Value Object |
| Customer, Order, Account, Booking, Payment, Seat instance | Entity |
| "It has a lifecycle / state transitions" | Entity |
| "Two with the same values are the same thing" | Value Object |

---

## Resources

- Eric Evans, *Domain-Driven Design* — Entities & Value Objects chapters
- Martin Fowler — [ValueObject](https://martinfowler.com/bliki/ValueObject.html)
- Vaughn Vernon, *Implementing Domain-Driven Design*

## Related

- [Aggregates + Ownership](aggregates.md) *(Day 23)*
- [Class Relationships](../oops/class-relationships.md)
- [Encapsulation](../oops/encapsulation.md)
- [Builder](../design-patterns/creational/builder.md) — often builds complex VOs/entities
