# Composition vs Inheritance

## The Rule
> "Favor composition over inheritance" — GoF Design Patterns

## When to Use Inheritance
- True IS-A relationship that won't change
- You need polymorphism via a type hierarchy
- The base class is stable and well-designed (e.g., AbstractList)

## When to Use Composition
- HAS-A relationship
- You need to combine behaviors from multiple sources
- The relationship might change at runtime
- You want to avoid fragile base class problems

## Trade-offs

| Criteria | Inheritance | Composition |
|----------|-------------|-------------|
| Code reuse | Automatic (inherit all) | Explicit (delegate) |
| Flexibility | Static (compile-time) | Dynamic (runtime) |
| Coupling | High (child depends on parent internals) | Low (depends on interface) |
| Testability | Harder (must instantiate hierarchy) | Easier (mock the composed parts) |
| Multiple behaviors | Limited (single inheritance in Java) | Unlimited (compose many) |

## Java Example

```java
// INHERITANCE — rigid
class EmailNotifier extends Notifier {
    void send(String msg) { /* send email */ }
}

// COMPOSITION — flexible
class NotificationService {
    private final List<NotificationChannel> channels; // compose behaviors

    void notify(String msg) {
        channels.forEach(ch -> ch.send(msg));
    }
}
```

## Interview Signal
When asked "why composition over inheritance?", don't just quote the rule.
Say: "Inheritance creates compile-time coupling to a specific implementation.
Composition lets me swap behaviors at runtime and combine them freely,
which respects the Open/Closed principle."
