# Aggregates, Ownership & Domain Services

## Definition

- **Aggregate** — a cluster of entities and value objects treated as **one unit** for data changes, bound together by a **consistency rule (invariant)**.
- **Aggregate Root** — the single entity that is the **only entry point** into the aggregate. Outside code references the root; never the internals.
- **Consistency Boundary** — everything inside the aggregate is changed and saved **together, atomically**. Other aggregates are referenced **by ID**, not embedded.
- **Domain Service** — stateless domain logic that spans multiple aggregates or belongs to no single entity.

It is NOT: "any group of related classes." An aggregate exists specifically to **guard an invariant** through a single controlled entry point.

---

## The Problem It Solves

Given `Order` and `OrderItem`, ask:

> Can external code modify an `OrderItem` directly, or must all changes go through `Order`?

If anyone can call `orderItem.setQuantity(5)` from anywhere, the rule *"order total = sum of item subtotals"* can be broken by code that doesn't even know `Order` exists. **No one guards the invariant.** The aggregate fixes this by funneling all changes through the root.

---

## WHY It Matters

| Benefit | Explanation |
|---------|-------------|
| Invariants stay true | The root enforces rules on every change; the aggregate is never in an invalid state. |
| Clear ownership | One entry point. No "anything can touch anything" spaghetti. |
| Transaction boundaries | The aggregate = what you load/save together. Keeps transactions small and correct. |
| Decoupling | Referencing other aggregates by ID keeps consistency boundaries separate. |
| Interview signal | "Order is the aggregate root; items have no independent lifecycle" is a strong modeling statement. |

---

## The Three Rules of an Aggregate Root

1. **External code references only the root.** Nobody holds a raw internal entity (`OrderItem`).
2. **All changes go through the root's methods.** `order.addItem(...)`, never `orderItem.setQuantity(...)`.
3. **The root enforces the invariant** on every mutation, so the whole aggregate is always valid.

```java
class Order {                                  // AGGREGATE ROOT
    private final OrderId id;                  // identity
    private OrderStatus status = OrderStatus.DRAFT;
    private final List<OrderItem> items = new ArrayList<>();   // internal — never exposed raw

    void addItem(Product product, int qty) {   // the ONLY door to add an item
        if (status != OrderStatus.DRAFT)
            throw new IllegalStateException("cannot modify a placed order");
        if (items.size() >= 50)
            throw new IllegalStateException("max 50 items per order");   // invariant guarded HERE
        items.add(new OrderItem(product, qty));
    }

    Money total() {                            // derived, always consistent
        return items.stream().map(OrderItem::subtotal)
                    .reduce(Money.zero(), Money::add);
    }

    List<OrderItem> items() { return List.copyOf(items); }  // COPY, not the live list
    // NO getItems() returning the mutable list. NO setItems(). NO exposing OrderItem for edits.
}
```

---

## The Consistency Boundary

The aggregate is a **transactional boundary**: everything inside changes together.

| Inside the boundary | Outside the boundary |
|---------------------|----------------------|
| One transaction, strong consistency | Separate transactions, eventual consistency |
| `Order` + its `OrderItem`s | `Order` → `Customer` (another aggregate) |
| Contained (composition) | Referenced by ID |

### Reference other aggregates by ID, not by object

```java
class Order {
    private CustomerId customerId;   // ✅ reference — Customer is its own aggregate/transaction
    // private Customer customer;    // ❌ embedding blurs two consistency boundaries
}
```

Embedding a whole `Customer` inside `Order` means changing an order could imply loading/saving customer state, entangling two independent lifecycles. Reference by ID; load the other aggregate separately when needed.

---

## Domain Services

Some logic belongs to **no single entity**. Forcing it onto one creates an unnatural coupling.

```java
// ❌ Awkward — Account A reaching into Account B; transfer belongs to neither alone
class Account {
    void transferTo(Account other, Money amount) {
        this.withdraw(amount);
        other.deposit(amount);   // why does an account know how to mutate another account?
    }
}
```

```java
// ✅ Domain Service — stateless, orchestrates two aggregate roots
class TransferService {
    void transfer(Account from, Account to, Money amount) {
        from.withdraw(amount);   // each account still guards its OWN invariant (no overdraft)
        to.deposit(amount);
    }
}
```

Each `Account` keeps enforcing its own rules. The service only coordinates the cross-aggregate operation.

### When to use a Domain Service

- Logic spans **multiple aggregates** (money transfer, matching driver ↔ rider).
- Logic belongs to **no single entity** (tax = f(order, customer, region)).
- It is a genuine **domain operation**, not just application plumbing (that's an application service).

### When NOT to (anemic-domain anti-pattern)

If the logic naturally belongs to one entity, **put it on that entity**. A codebase where entities are dumb data bags and every behaviour lives in `XxxService` classes is an **anemic domain model** — it throws away OOP and turns objects into structs.

---

## Examples (Java)

### ❌ BAD — no root, leaky internals, broken invariant

```java
class Order {
    public List<OrderItem> items = new ArrayList<>();  // public + mutable
    public double total;                                // hand-maintained, drifts out of sync
}

// anywhere in the codebase:
order.items.add(new OrderItem(...));  // total NOT updated → invariant broken
order.items.get(0).quantity = 999;    // mutated behind the order's back
```

### ✅ GOOD — root guards invariant, internals hidden

```java
class Order {
    private final List<OrderItem> items = new ArrayList<>();

    void addItem(Product p, int qty) {          // controlled entry
        items.add(new OrderItem(p, qty));       // total is DERIVED, never stored stale
    }
    Money total() { /* sum items */ }           // always correct
    List<OrderItem> items() { return List.copyOf(items); }  // no external mutation
}
```

---

## Common Violations / Mistakes

| Mistake | Fix |
|---------|-----|
| Exposing the internal list raw (`getItems()` → live `List`) | Return `List.copyOf(...)` / unmodifiable view |
| Storing a derived value (`total`) that drifts | Derive it on read from the source of truth |
| Embedding another aggregate as an object | Reference it by ID |
| Editing internals directly (`orderItem.setQuantity`) | Route through the root (`order.changeItemQty`) |
| Anemic entities + fat services | Put entity-owned logic on the entity; reserve services for cross-aggregate logic |
| Giant aggregate (whole object graph) | Keep aggregates small; split at true consistency boundaries |

---

## When to Use / When NOT to Use

| Situation | Guidance |
|-----------|----------|
| Child has no meaning without parent (OrderItem, Message) | Inside the aggregate; root owns it |
| Child has its own lifecycle (Customer, Product) | Separate aggregate; reference by ID |
| Rule spans the whole cluster (total, max items) | Enforce on the root |
| Operation spans two aggregates (transfer, matching) | Domain Service |
| Behaviour clearly owned by one entity | Put it on that entity, not a service |

---

## Resources

- Eric Evans, *Domain-Driven Design* — Aggregates chapter
- Vaughn Vernon — [Effective Aggregate Design (3-part paper)](https://www.dddcommunity.org/library/vernon_2011/)
- Martin Fowler — [DDD Aggregate](https://martinfowler.com/bliki/DDD_Aggregate.html)

## Related

- [Entity vs Value Object](entity-vs-value-object.md) — the building blocks of an aggregate
- [Class Relationships](../oops/class-relationships.md) — composition vs aggregation (an aggregate root owns by composition)
- [Encapsulation](../oops/encapsulation.md) — how the root hides internals
- [SRP](../solid/single-responsibility.md)
