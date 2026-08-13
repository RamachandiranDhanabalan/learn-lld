# Day 02 — Encapsulation + Abstraction

## Cheat Sheet

- **Encapsulation** = State + governing behavior + controlled access. NOT just private + getters.
- **Abstraction** = Contract without implementation. Caller knows WHAT, not HOW.
- **Encapsulation is inward** (protect my state). **Abstraction is outward** (simplify for callers).
- **YAGNI** = Don't extract interface until you need 2+ implementations.
- **Defensive copy** = `Collections.unmodifiableList()` to prevent external mutation.
- Use `BigDecimal` for money, never `double`.

## Critical Examples

### Encapsulation violation (memorize this one)
```java
// ❌ Private fields but STILL violates encapsulation
class Order {
    private List<Item> items = new ArrayList<>();
    public List<Item> getItems() { return items; }      // leaks mutable reference!
    public void setItems(List<Item> i) { this.items = i; } // no validation!
}

// ✅ True encapsulation — behavior enforces rules
class Order {
    private final List<Item> items = new ArrayList<>();
    public void addItem(Item item) {
        if (item == null) throw new IllegalArgumentException();
        items.add(item);
    }
    public List<Item> getItems() { return Collections.unmodifiableList(items); }
}
```

### Abstraction value (only with 2+ implementations)
```java
interface Priceable { BigDecimal getTotal(); }
class ShoppingCart implements Priceable { /* discount logic */ }
class Subscription implements Priceable { /* flat fee */ }
class GiftCard implements Priceable { /* remaining balance */ }

// Caller decoupled from HOW each computes price
class InvoiceGenerator {
    Invoice generate(List<Priceable> items) { /* just calls getTotal() */ }
}
```

## Decision Framework

| Question | → Use |
|----------|-------|
| "Do I need to protect this data from invalid changes?" | Encapsulation (behavior methods) |
| "Do I have multiple implementations of the same operation?" | Abstraction (interface) |
| "Only one implementation exists" | Don't extract interface yet (YAGNI) |
| "Interface or abstract class?" | Interface = unrelated types, pure contract. Abstract = related types, shared code. |

## Interview Questions & Answers

**Q: "What's encapsulation?"**
A: "Bundling state with the behavior that governs it, controlling access so the object can never be in an invalid state from outside."

**Q: "What's abstraction?"**
A: "Exposing a contract without implementation. Enables polymorphism — swap implementations without changing callers."

**Q: "Are getters/setters encapsulation?"**
A: "No. True encapsulation = expose behavior, not data. `withdraw()` with rules is encapsulation. `setBalance()` is a public field with ceremony."

**Q: "Interface vs abstract class?"**
A: "Interface = pure contract, unrelated types sharing a capability. Abstract class = shared implementation + contract, related types. Template Method pattern is the classic use."

**Q: "When extract an interface?"**
A: "When I have a 2nd implementation, or at a module boundary. Single implementation behind an interface is premature abstraction."

## Clues & Signals

- **Senior signal**: Saying "I won't extract this interface yet — only one implementation" (shows YAGNI awareness)
- **Senior signal**: Returning `unmodifiableList()` without being asked
- **Red flag**: Every entity has `@Data` (Lombok) with getters AND setters
- **Red flag**: No validation in any methods

## Trade-offs

| Decision | A | B | Guidance |
|----------|---|---|----------|
| Getter vs behavior method | Getter (flexible caller) | Behavior method (controlled) | Prefer behavior — keeps logic with data |
| Interface now vs later | Now (future-proof) | Later (YAGNI) | Later — extract when 2nd impl exists |
| Defensive copy vs direct | Copy (safe) | Direct (fast) | Defensive for public APIs |
| Lombok @Data vs manual | @Data (less code) | Manual (intentional API) | @Value for DTOs, manual for entities |

## Quick Links

- **Detailed topic**: [OOP Principles (Encapsulation + Abstraction)](../topics/oops/README.md)
- **Related**: [Composition vs Inheritance](../topics/oops/composition-vs-inheritance.md)
