# Day 23 — Aggregates + Ownership + Domain Services

## Cheat Sheet

- **Aggregate** = cluster of entities+VOs changed as ONE unit, bound by an **invariant**.
- **Aggregate Root** = the ONLY entry point into the aggregate. Outside code holds only the root.
- **3 root rules**: (1) reference only the root, (2) all changes via root methods, (3) root enforces the invariant.
- **Consistency boundary** = what you save/change in one transaction. Inside = strong consistency.
- **Reference other aggregates by ID**, never embed the object. (`CustomerId`, not `Customer`.)
- **Domain Service** = stateless logic spanning multiple aggregates / owned by no single entity (e.g. money transfer).
- **Don't** hollow out entities into data bags + fat services → that's the **anemic domain model**.

## Critical Example

```java
class Order {                              // AGGREGATE ROOT
    private final List<OrderItem> items = new ArrayList<>();  // internal, hidden
    private CustomerId customerId;         // OTHER aggregate → by ID, not embedded

    void addItem(Product p, int qty) {     // ONLY way in
        if (status != DRAFT) throw new IllegalStateException();  // invariant HERE
        items.add(new OrderItem(p, qty));
    }
    Money total() { /* derived — never stored stale */ }
    List<OrderItem> items() { return List.copyOf(items); }  // COPY, no external mutation
    // NO setItems(), NO getItems() → live list, NO exposing OrderItem to edit
}

// DOMAIN SERVICE — belongs to neither account
class TransferService {
    void transfer(Account from, Account to, Money amt) {
        from.withdraw(amt);   // each account guards its OWN invariant
        to.deposit(amt);
    }
}
```

## Decision Framework

| Signal | Answer |
|--------|--------|
| Child meaningless without parent (OrderItem, Message) | Inside aggregate; root owns it |
| Child has own lifecycle (Customer, Product) | Separate aggregate → reference by ID |
| Rule spans the cluster (total, max items) | Enforce on the root |
| Operation spans 2 aggregates (transfer, matching) | Domain Service |
| Behaviour owned by one entity | Put it on the entity, NOT a service |

## Aggregate vs plain Composition

- Composition (Day 3) = "parent owns child, child dies with parent."
- Aggregate = composition **+ a single controlled entry point + an enforced invariant + a transaction boundary.**
- Every aggregate root owns its internals by composition; not every composition is an aggregate root.

## Interview Questions & Answers

**Q: "What's an aggregate / aggregate root?"**
A: "An aggregate is a cluster of objects treated as one unit for changes, bound by an invariant. The root is the single entry point — outside code only references the root, all changes go through it, and it enforces the invariant so the cluster is never invalid."

**Q: "Should OrderItem be modifiable directly?"**
A: "No. OrderItem has no lifecycle without its Order, so Order is the aggregate root and owns it. All changes go through Order methods (addItem, changeQty) so Order can keep its invariant — like total = sum of items — always true. I don't expose the internal list; I return a copy."

**Q: "How do you reference a Customer from an Order?"**
A: "By CustomerId, not by embedding the Customer object. They're separate aggregates with separate lifecycles and transactions. Embedding would blur two consistency boundaries."

**Q: "Where does 'transfer money between accounts' live?"**
A: "A domain service — it belongs to neither account alone. The service coordinates withdraw + deposit, but each Account still guards its own balance invariant."

**Q: "What's the anemic domain model?"**
A: "When entities are dumb data bags with only getters/setters and all logic lives in service classes. It discards OOP. Fix: push entity-owned behaviour back onto the entity; reserve services for genuinely cross-aggregate logic."

## Clues & Signals

- **Senior signal**: "Order is the root; items have no independent lifecycle, so I hide the list and expose addItem()."
- **Senior signal**: "Reference other aggregates by ID to keep transaction boundaries separate."
- **Senior signal**: "This is a domain service because it spans two aggregates; each still guards its own invariant."
- **Red flag**: `getItems()` returns the live mutable list.
- **Red flag**: storing a `total` field that drifts out of sync (derive it instead).
- **Red flag**: embedding a whole aggregate inside another.
- **Red flag**: anemic entities + all logic in *Service classes.

## Trade-offs

| Choice | Pro | Con |
|--------|-----|-----|
| Small aggregate | small transactions, less contention | more cross-aggregate coordination |
| Large aggregate | fewer moving parts, easy consistency | lock contention, load/save whole graph |
| Reference by ID | decoupled boundaries | extra lookup to load the other aggregate |
| Domain service | models cross-entity ops cleanly | overused → anemic entities |

## Quick Links

- **Detailed topic**: [Aggregates + Ownership + Domain Services](../topics/domain-modeling/aggregates.md)
- **Related**: [Entity vs Value Object](../topics/domain-modeling/entity-vs-value-object.md)
- **Related**: [Class Relationships (composition vs aggregation)](../topics/oops/class-relationships.md)
- **Next**: UML + Activity Diagrams (Day 24)
