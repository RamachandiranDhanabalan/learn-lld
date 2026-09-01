# Day 16 — Observer Pattern

## Cheat Sheet

- **Observer** = One-to-many. Subject changes state → all observers notified automatically.
- **Subject** = publisher (has state, holds observer list, notifies). **Observer** = subscriber (reacts).
- **Push** (subject sends data in event) preferred over **Pull** (observer queries subject).
- **Sync** = blocking, in-order, same thread. **Async** = non-blocking, isolated failures.
- **OCP** = new observer = new class + subscribe. Subject NEVER changes.
- **Abstract subject NOT needed** most of the time — concrete subject is fine.
- **Keep entities clean** — use dedicated observer classes, not entity itself (SRP).
- **Spring** = `@EventListener` + `publishEvent()` — same pattern, annotation-based.

## Critical Example

```java
// Subject — notifies all observers, doesn't know who they are
class Stock {
    private List<StockObserver> observers = new ArrayList<>();
    void subscribe(StockObserver o) { observers.add(o); }

    void updatePrice(BigDecimal newPrice) {
        this.price = newPrice;
        StockEvent event = new StockEvent(symbol, price);
        observers.forEach(o -> o.onPriceChange(event));  // notify ALL
    }
}

// Observer interface
interface StockObserver { void onPriceChange(StockEvent event); }

// Concrete — each reacts independently
class TradingBot implements StockObserver { ... }
class AuditLogger implements StockObserver { ... }
// Add TaxCalculator? New class + subscribe. Zero changes to Stock.
```

## Decision Framework

| Signal | Pattern |
|---|---|
| "Multiple things need to react to one change" | Observer |
| "Subject shouldn't know its concrete dependents" | Observer |
| "Adding a new reaction should not change the source" | Observer |
| "Only one algorithm to swap" | Strategy (not Observer) |
| "Need ordered chain, one handler processes" | Chain of Responsibility (not Observer) |
| "Only 1-2 fixed reactions, never grows" | Direct calls — Observer overkill |

## Interview Questions & Answers

**Q: "What's Observer?"**
A: "One-to-many dependency. When subject's state changes, all observers notified automatically. Subject knows only the observer interface — loose coupling."

**Q: "Push vs Pull?"**
A: "Push: subject sends data in the event. Pull: subject says 'changed', observer queries. Push is cleaner and more common."

**Q: "Sync vs async?"**
A: "Sync = blocking, in publisher's thread. Async = separate threads, isolated failures. Use sync for critical, async for side effects (email, analytics)."

**Q: "Observer pitfalls?"**
A: "Memory leaks (unsubscribe!), order dependency (make observers independent), one failure breaks all (try-catch per observer or async)."

**Q: "Observer in Spring?"**
A: "ApplicationEventPublisher + @EventListener. Same pattern with annotations instead of explicit interface. Parameter type matching routes events."

## Clues & Signals

- **Senior signal**: "I'd keep User as a clean entity and use a separate MessageDeliveryListener for SRP"
- **Senior signal**: "I'd use push model with an immutable event object — observer can't modify the source"
- **Red flag**: Subject calling 5 concrete services directly in one method (should be observers)
- **Key learning**: Entities hide in verbs. In Chat: "send message" → Message entity flows through the system.

## Design Practice: Chat Application

Applied:
- **Observer**: ChatRoom = subject, listeners notified on sendMessage()
- **Strategy**: JoinPolicy (PublicJoinPolicy, PrivateJoinPolicy) for room access
- **Entity discovery**: Message (flows through system), User, ChatRoom
- **Relationships**: ChatRoom aggregates Users (independent), composes Messages (owned)
- **SRP**: Separate observers (MessageDeliveryListener, AuditLogListener) not User class

Gaps caught:
- Room access control (PUBLIC/PRIVATE) — needed Strategy for join policy
- Sender exclusion — filter sender in observer notification
- Defensive returns — unmodifiable collections

## Quick Links

- **Detailed topic**: [Observer Pattern](../topics/design-patterns/behavioural/observer.md)
- **Related**: [Strategy](../topics/design-patterns/behavioural/strategy.md)
- **Framework**: [Problem-Solving + Entity Discovery](../topics/lld-approach/problem-solving-framework.md)
