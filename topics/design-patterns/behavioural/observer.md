# Observer Pattern

## Intent

Define a one-to-many dependency so that when one object (subject) changes state, all its dependents (observers) are notified automatically. The foundation of event-driven design.

## Problem It Solves

- Multiple objects need to react to state changes in another object
- Publisher shouldn't know the concrete subscribers
- Adding new listeners shouldn't modify the publisher

---

## Structure

```
Subject (publisher)          ← has state, notifies observers
├── subscribe(observer)
├── unsubscribe(observer)
└── notify()

Observer (interface)         ← contract for all listeners
├── ConcreteObserverA        ← reacts one way
├── ConcreteObserverB        ← reacts another way
```

---

## Example

```java
// EVENT — what happened (immutable data)
record OrderPaidEvent(String orderId, BigDecimal amount, String customerId) {}

// OBSERVER interface
interface OrderEventListener {
    void onOrderPaid(OrderPaidEvent event);
}

// CONCRETE OBSERVERS — each reacts independently
class InventoryListener implements OrderEventListener {
    public void onOrderPaid(OrderPaidEvent event) { /* reduce stock */ }
}
class EmailListener implements OrderEventListener {
    public void onOrderPaid(OrderPaidEvent event) { /* send confirmation */ }
}

// SUBJECT — notifies all, doesn't know who they are
class Order {
    private final List<OrderEventListener> listeners = new ArrayList<>();

    void subscribe(OrderEventListener l) { listeners.add(l); }
    void unsubscribe(OrderEventListener l) { listeners.remove(l); }

    void markPaid() {
        this.status = "PAID";
        OrderPaidEvent event = new OrderPaidEvent(id, amount, customerId);
        listeners.forEach(l -> l.onOrderPaid(event));  // notify all
    }
}
// New listener? Subscribe it. Zero changes to Order. OCP.
```

---

## Push vs Pull

| Model | How | When |
|---|---|---|
| **Push** | Subject sends data in notification (`onEvent(event)`) | Most common — observer gets what it needs immediately |
| **Pull** | Subject notifies "changed", observer queries for details | When observers need different subsets of data |

---

## Sync vs Async

| Sync | Async |
|---|---|
| Observers run in publisher's thread, blocking | Observers run independently (separate threads) |
| Simple, ordered | Non-blocking, isolated failures |
| For critical, must-complete reactions | For side effects (email, analytics) |
| Spring: `@EventListener` | Spring: `@EventListener` + `@Async` |

---

## Abstract Subject — Needed?

Usually NO. Concrete subject is fine. Only extract abstract base if multiple different subjects repeat subscribe/notify boilerplate:

```java
// Only when you have Stock, Auction, Chat ALL needing subscribe/notify
abstract class EventPublisher<T> {
    private final List<T> listeners = new ArrayList<>();
    void subscribe(T l) { listeners.add(l); }
    protected List<T> getListeners() { return listeners; }
}
```

---

## Observer Design Guideline

Keep entities clean (state + own behavior). Use **dedicated observer classes** for reactions — not the entity itself:

```java
// ✅ Separate observer (SRP respected)
class MessageDeliveryListener implements ChatRoomListener { }
class AuditLogListener implements ChatRoomListener { }

// ⚠️ Avoid: User implementing observer (SRP mixed — user entity + notification logic)
class User implements ChatRoomListener { }
```

---

## Common Pitfalls

| Pitfall | Fix |
|---|---|
| Memory leak (observers never unsubscribed) | Always provide unsubscribe |
| Notification order dependency | Make observers independent |
| One observer failure breaks all | try-catch per observer, or async |
| Infinite loops (observer updates subject → re-notifies) | Guard against re-entry |

---

## Spring Observer

```java
// Publisher
publisher.publishEvent(new OrderPaidEvent(orderId));

// Observer — no interface, uses annotation + parameter type matching
@Component
class InventoryListener {
    @EventListener
    public void handle(OrderPaidEvent event) { /* reduce stock */ }
}
```

Spring uses `@EventListener` annotation + reflection instead of explicit interface. Same pattern, less boilerplate.

---

## Observer vs Related Patterns

| Pattern | Difference |
|---|---|
| **Strategy** | One algorithm swapped vs many listeners notified |
| **Mediator** | Central hub for many-to-many vs one-to-many |
| **Chain of Responsibility** | One handler processes (or passes) vs ALL observers react |

---

## Resources

- [Refactoring Guru — Observer](https://refactoring.guru/design-patterns/observer)
- [Baeldung — Observer](https://www.baeldung.com/java-observer-pattern)

## Related

- [Strategy](strategy.md) — swaps one algorithm (vs notifies many)
- [State](state.md) — Day 17
