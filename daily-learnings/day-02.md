# Day 02 — Encapsulation + Abstraction

## Core Concepts

### Encapsulation

Bundling state with the **behavior that governs** that state, and restricting direct access to internal data.

**It is NOT**: "Make fields private and add getters/setters."  
**It IS**: "Expose behavior methods that enforce business rules on the data."

**Benefits:**
- **Data hiding** — object's state is not exposed to the outside world
- **Controlled access** — any modification happens via methods that ensure valid state always
- **Maintainability** — internal changes don't affect callers
- **Security/safety** — no one can corrupt the system state externally

### Abstraction

Exposing the **contract** (WHAT to do) without exposing the **implementation** (HOW it's done). The caller interacts with a simplified interface.

**Benefits:**
- **Complexity hidden** — caller deals with behavior, not internals
- **Maintainability** — implementation changes don't affect clients
- **Polymorphism** — swap implementations without caller changing a line

### How They Differ

| Aspect | Encapsulation | Abstraction |
|--------|--------------|-------------|
| Focus | Hiding **state** | Hiding **complexity** |
| Direction | Inward (protect my data) | Outward (simplify for my callers) |
| Mechanism | Private fields + behavior methods | Interfaces, abstract classes |
| Question | "Who can modify my data?" | "What does the caller need to know?" |
| Analogy | Pill capsule (contents sealed) | Steering wheel (turn it, don't know mechanics) |

**They work together**: Encapsulation hides data inside a class. Abstraction hides the class itself behind an interface.

---

## Key Rules / Things to Remember

1. **Getters/setters ≠ encapsulation** — If every field has a public getter AND setter, you just have public fields with ceremony.
2. **Expose behavior, not data** — `withdraw(amount)` > `setBalance(balance - amount)`
3. **Defensive copies** — Return `Collections.unmodifiableList()` instead of raw mutable lists.
4. **Validate at the gate** — Constructor and methods enforce rules. Object can never exist in invalid state.
5. **Don't abstract prematurely (YAGNI)** — Extract an interface when you have 2+ implementations, not before.
6. **Abstraction enables polymorphism** — The real power: swap implementations without changing callers.

---

## Examples (Java)

### ❌ BAD — Getters/setters violate encapsulation

```java
class Order {
    private List<Item> items = new ArrayList<>();

    // Leaks mutable internal state — caller can do getItems().clear()
    public List<Item> getItems() { return items; }

    // Bypasses all business rules
    public void setItems(List<Item> items) { this.items = items; }
}
```

### ✅ GOOD — Behavior methods enforce rules

```java
class Order {
    private final List<Item> items = new ArrayList<>();

    public void addItem(Item item) {
        if (item == null) throw new IllegalArgumentException("Item cannot be null");
        items.add(item);
    }

    public void removeItem(String itemId) {
        items.removeIf(i -> i.getId().equals(itemId));
    }

    // Defensive copy — caller can read but not mutate
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal getTotal() {
        return items.stream()
            .map(Item::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

### Abstraction — Interface value shows with multiple implementations

```java
public interface Priceable {
    BigDecimal getTotal();
}

public class ShoppingCart implements Priceable { /* discount logic */ }
public class Subscription implements Priceable { /* flat fee */ }
public class GiftCard implements Priceable { /* remaining balance */ }

// Caller is decoupled from HOW each computes its total
public class InvoiceGenerator {
    public Invoice generate(List<Priceable> lineItems) {
        BigDecimal total = lineItems.stream()
            .map(Priceable::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Invoice(total);
    }
}
```

---

## Common Mistakes

| Mistake | Why It's Bad | Fix |
|---------|-------------|-----|
| `@Data` on entities (Lombok) | Generates setters for all fields — breaks encapsulation | Use `@Value` for immutable DTOs, manual methods for entities |
| Returning mutable collections | Caller can `list.clear()` your internals | Return `Collections.unmodifiableList()` |
| Premature interface extraction | One implementation behind an interface = unnecessary indirection | Extract when you have 2+ implementations or a library boundary |
| No validation in setters | Allows invalid state | Validate in every method that modifies state |
| Exposing password/sensitive data via getter | Security violation | Return masked/encoded, or don't expose at all |

---

## Interview Signals

- **"What's encapsulation?"** → "Bundling state with the behavior that governs it, controlling access so the object can never be put in an invalid state from outside."

- **"What's abstraction?"** → "Exposing a contract (what to do) without exposing implementation (how). It enables polymorphism — I can swap implementations without changing callers."

- **"Are getters/setters encapsulation?"** → "No. True encapsulation means exposing behavior, not data. A `withdraw()` that enforces rules is encapsulation. A `setBalance()` is a public field with extra steps."

- **"Interface vs abstract class?"** → "Interface for pure contract — when unrelated types share a capability (Searchable, Cacheable, Priceable). Abstract class when related types share implementation code — template method pattern."

- **"When do you extract an interface?"** → "When I see a second use case that needs it, or when I'm at a module boundary. I don't pre-extract — that's premature abstraction."

---

## Trade-offs

| Decision | Option A | Option B | Guidance |
|----------|----------|----------|----------|
| Getters vs behavior methods | Getter (flexible) | Behavior method (controlled) | Prefer behavior — keeps logic with data |
| Interface vs abstract class | Interface (pure contract) | Abstract class (shared code) | Interface by default. Abstract only with shared implementation |
| Defensive copy vs direct return | Copy (safe, slightly slower) | Direct (fast, risky) | Defensive for public APIs. Direct for internal hot paths |
| Extract interface now vs later | Now (future-proof) | Later (YAGNI) | Later — extract when 2nd implementation exists |
| Lombok @Data vs manual | @Data (less code) | Manual (intentional API) | @Value for DTOs, manual for domain entities |

---

## Quick Reference

- **Encapsulation** = State + governing behavior + controlled access. NOT just private + getters.
- **Abstraction** = Contract without implementation. Enables polymorphism.
- **Encapsulation is inward** (protect my state). **Abstraction is outward** (simplify for callers).
- **YAGNI** = Don't extract interface until you need 2+ implementations.
- **Defensive copy** = `Collections.unmodifiableList()` — prevent external mutation.
- **Validate at construction** = Object can never exist in invalid state.
- Use `BigDecimal` for money, never `double`.

---

## References

- [OOP Principles](../topics/oops/README.md)
- [Composition vs Inheritance](../topics/oops/composition-vs-inheritance.md)
- [Oracle Java OOP Tutorial](https://docs.oracle.com/javase/tutorial/java/concepts/object.html)
- [Refactoring Guru](https://refactoring.guru/refactoring/what-is-refactoring)
