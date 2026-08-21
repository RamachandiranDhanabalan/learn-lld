# Day 08 — Factory Method Pattern

## Cheat Sheet

- **Factory** = Encapsulates object creation so caller doesn't know/decide the concrete class
- **Simple Factory** = One method, returns the right type based on input (use 90% of the time)
- **Factory Method (GoF)** = Abstract method in base class — subclass decides what to create. Base class owns the flow.
- **Registry Factory** = Map-based, types register themselves. True OCP (even factory doesn't change).
- **Factory vs Strategy** = Factory decides WHAT to create. Strategy decides WHICH algorithm to execute.
- **Factory vs `new`** = Use factory when type depends on runtime input or creation is complex. Use `new` for simple value objects.
- **if-else IN the factory is acceptable** — the factory IS the one place allowed to know about concrete types. OCP violation is when this decision is scattered in business logic.

## Critical Examples

### Simple Factory (most common)
```java
class PaymentFactory {
    static PaymentGateway create(String provider) {
        return switch (provider) {
            case "STRIPE" -> new StripeGateway();
            case "RAZORPAY" -> new RazorpayGateway();
            default -> throw new IllegalArgumentException("Unknown: " + provider);
        };
    }
}
// Caller: PaymentGateway gw = PaymentFactory.create("STRIPE"); gw.charge(100);
```

### Factory Method (GoF — base class owns the flow)
```java
abstract class ReportCreator {
    protected abstract Report createReport();       // factory method (subclass decides)
    public final void generate(List<Data> data) {   // template method (fixed flow)
        Report report = createReport();
        report.render(data);
        report.save();
    }
}
class PdfReportCreator extends ReportCreator {
    protected Report createReport() { return new PdfReport(); }  // I decide: PDF
}
```

### Registry Factory (true OCP)
```java
class NotificationFactory {
    private static Map<String, Supplier<Notification>> registry = new HashMap<>();
    public static void register(String type, Supplier<Notification> creator) { registry.put(type, creator); }
    public static Notification create(String type) { return registry.get(type).get(); }
}
// Adding WhatsApp from ANY module: NotificationFactory.register("WHATSAPP", WhatsApp::new);
```

## Decision Framework

| Question | → Use |
|---|---|
| "Type depends on runtime input?" | Factory |
| "Complex creation/setup per type?" | Factory (hides complexity) |
| "Always one known type, simple construction?" | Just `new` |
| "Caller needs object back to use themselves?" | Simple Factory |
| "Base class owns a fixed flow, only creation step varies?" | Factory Method + Template Method |
| "Want true OCP (even factory doesn't change)?" | Registry Factory |

### Factory vs Strategy vs Factory Method

| | Simple Factory | Factory Method | Strategy |
|---|---|---|---|
| Purpose | Create the right object | Subclass decides what to create within a flow | Execute using the right algorithm |
| Who calls | Caller | Base class (internally) | Caller |
| Who uses result | Caller | Base class (internally) | Already executed |
| Combined? | Factory creates a Strategy | Factory Method + Template Method | Strategy returned by Factory |

## Interview Questions & Answers

**Q: "What's Factory Method?"**
A: "Encapsulates object creation so the caller doesn't know which concrete class is instantiated. Factory decides based on input. Follows OCP — add new types without modifying caller code."

**Q: "When factory vs just `new`?"**
A: "Factory when type depends on runtime input, or creation involves complex setup. For value objects (Address, Money) or single implementations, `new` is fine."

**Q: "Factory vs Strategy?"**
A: "Factory = give me the right object (creation). Strategy = do this work the right way (behavior). They compose: factory creates the right strategy."

**Q: "How to make factory fully OCP?"**
A: "Registry-based — types register themselves via a map. Adding new type doesn't modify factory code at all."

**Q: "Does if-else in a factory violate OCP?"**
A: "Not necessarily. The factory IS the one place allowed to know concrete types — it centralizes the decision. OCP violation is when this decision is scattered in business logic. Registry is more OCP, but switch-in-factory is acceptable."

## Clues & Signals

- **Senior signal**: "I'd use Simple Factory here — Factory Method is overkill since the caller just needs the object back"
- **Senior signal**: "Registry-based factory if we expect frequent new types from other teams/modules"
- **Red flag**: `new ConcreteClass()` scattered in business logic based on if-else
- **Red flag**: Factory returning `null` instead of throwing exception
- **Key learning**: Factory Method (GoF) = base class owns the flow + delegates creation to subclass. Different from Simple Factory where caller gets object and uses it.
- **Key learning**: Factory with caching = Multiton/Flyweight pattern (reuse instances by key). Watch for thread-safety (`ConcurrentHashMap.computeIfAbsent`).

## Trade-offs

| Decision | Use Factory | Use `new` Directly |
|---|---|---|
| Type depends on runtime input | ✅ | |
| Complex setup per type | ✅ | |
| Always one specific type | | ✅ |
| Simple value objects | | ✅ |
| Want true OCP (registry) | ✅ | |
| One-off, small app | | ✅ |

## Quick Links

- **Detailed topic**: [Factory Method](../topics/design-patterns/creational/factory-method.md)
- **Related**: [Abstract Factory (Day 9)](../topics/design-patterns/creational/abstract-factory.md)
- **Related**: [Strategy Pattern](../topics/design-patterns/behavioural/strategy.md)
- **Related**: [Open/Closed Principle](../topics/solid/open-closed.md)
