# Factory Method Pattern

## Intent

Encapsulate object creation logic so the caller doesn't know or decide which concrete class to instantiate. The factory decides based on input/config.

## Problem It Solves

- Caller is coupled to concrete classes via `new ConcreteClass()` in if-else
- Adding a new type requires modifying caller code (OCP violation)
- Object creation involves complex setup (config, validation, dependencies)
- Different environments/contexts need different implementations

---

## Three Variants

### 1. Simple Factory (Use 90% of the Time)

One method that returns the right type. Caller creates + uses.

```java
interface Notification { void send(String message); }
class EmailNotification implements Notification { /* SMTP */ }
class SmsNotification implements Notification { /* Twilio */ }
class PushNotification implements Notification { /* Firebase */ }

class NotificationFactory {
    public static Notification create(String channel) {
        return switch (channel) {
            case "EMAIL" -> new EmailNotification();
            case "SMS" -> new SmsNotification();
            case "PUSH" -> new PushNotification();
            default -> throw new IllegalArgumentException("Unknown: " + channel);
        };
    }
}

// Caller — doesn't know concrete type
Notification n = NotificationFactory.create("EMAIL");
n.send("Hello");
```

### 2. Factory Method (GoF — Subclass Decides)

Abstract method in base class. Subclass controls WHAT gets created. Base class controls the flow.

```java
// Base class owns the algorithm, delegates creation to subclass
abstract class DocumentProcessor {
    protected abstract Document createDocument();  // FACTORY METHOD

    public final void process(String content) {    // TEMPLATE METHOD (uses factory method)
        Document doc = createDocument();
        doc.setContent(content);
        doc.format();
        doc.save();
    }
}

class PdfProcessor extends DocumentProcessor {
    @Override
    protected Document createDocument() {
        PdfDocument doc = new PdfDocument();
        doc.setPageSize("A4");
        doc.setHeader("Report");
        return doc;
    }
}

class WordProcessor extends DocumentProcessor {
    @Override
    protected Document createDocument() {
        return new WordDocument();
    }
}

// Caller just triggers — doesn't touch Document directly
new PdfProcessor().process("Monthly data...");
```

**When to use over Simple Factory**: Base class owns a fixed flow, only the "what to create" step varies per subclass.

### 3. Registry Factory (True OCP)

Map-based. Types register themselves. Adding new type doesn't modify factory.

```java
class NotificationFactory {
    private static final Map<String, Supplier<Notification>> registry = new HashMap<>();

    static {
        registry.put("EMAIL", EmailNotification::new);
        registry.put("SMS", SmsNotification::new);
    }

    public static void register(String type, Supplier<Notification> creator) {
        registry.put(type, creator);
    }

    public static Notification create(String type) {
        Supplier<Notification> creator = registry.get(type);
        if (creator == null) throw new IllegalArgumentException("Unknown: " + type);
        return creator.get();
    }
}

// Adding WhatsApp from ANY module — zero modification to factory:
NotificationFactory.register("WHATSAPP", WhatsAppNotification::new);
```

---

## When to Use Factory vs `new`

| Use Factory | Use `new` Directly |
|---|---|
| Type depends on runtime input | Always one known type |
| Complex creation/setup per type | Simple construction |
| Want to decouple caller from concrete classes | Value objects (Address, Money) |
| Need to mock creation in tests | Standard library (ArrayList, HashMap) |
| Multiple implementations exist/expected | Internal utility, never swapped |

---

## Factory vs Strategy

| Aspect | Factory | Strategy |
|---|---|---|
| Purpose | Decide WHICH object to **create** | Decide WHICH algorithm to **execute** |
| Caller's question | "Give me the right object" | "Do this work the right way" |
| What happens after | Caller uses the returned object | Work is already done |
| Combined | Factory creates the right Strategy | Strategy is what factory returns |

```java
// Factory creates a Strategy — common combination
PricingStrategy strategy = PricingFactory.create(customer.getTier());
BigDecimal price = strategy.calculate(order);
```

---

## Simple Factory vs Factory Method (GoF)

| Aspect | Simple Factory | Factory Method |
|---|---|---|
| Who calls creation? | Caller | Base class (internally) |
| Who uses the created object? | Caller | Base class (controls flow) |
| Structure | Static/instance method | Abstract class + subclass override |
| Combined with | — | Template Method |
| Use when | Caller wants the object back | Base class owns a fixed algorithm |

---

## OCP and Factories

- **if-else IN the factory is acceptable** — the factory IS the one place that's allowed to know about concrete types. It centralizes the decision.
- **OCP violation** = when this type-checking decision is scattered across business logic
- **True OCP** = Registry factory (types register themselves, factory code never changes)
- **Pragmatic OCP** = Switch-in-factory is fine for small/stable sets of types

---

## Factory in Spring Boot

```java
// Spring container is already a factory. For conditional creation:
@Component
class PaymentGatewayFactory {
    private final Map<String, PaymentGateway> gateways;

    PaymentGatewayFactory(List<PaymentGateway> gatewayList) {
        gateways = gatewayList.stream()
            .collect(Collectors.toMap(PaymentGateway::getType, Function.identity()));
    }

    public PaymentGateway getGateway(String type) {
        return Optional.ofNullable(gateways.get(type))
            .orElseThrow(() -> new IllegalArgumentException("Unknown: " + type));
    }
}
// Spring auto-discovers all PaymentGateway implementations. True registry.
```

---

## Factory + Caching = Multiton Pattern

```java
class CacheFactory {
    private static final ConcurrentHashMap<String, Cache> instances = new ConcurrentHashMap<>();

    static Cache create(String name) {
        return instances.computeIfAbsent(name, InMemoryCache::new);  // thread-safe
    }
}
// Same name → same instance (Singleton per key). Watch for thread-safety.
```

---

## Common Mistakes

| Mistake | Fix |
|---|---|
| Factory returns `null` for unknown type | Throw `IllegalArgumentException` — fail fast |
| Using Factory Method pattern when Simple Factory suffices | Keep it simple — only use GoF version when base class owns a flow |
| `new ConcreteClass()` scattered in business logic | Centralize in factory |
| Factory with 20+ types in one switch | Consider Registry or splitting by category |
| Not making factory itself injectable | In Spring, make it a `@Component` so it's mockable |

---

## Resources

- [Refactoring Guru — Factory Method](https://refactoring.guru/design-patterns/factory-method)
- [Effective Java Item 1 — Static Factory Methods](https://www.oreilly.com/library/view/effective-java/9780134686097/)

## Related

- [Abstract Factory](abstract-factory.md) — creates families of related objects
- [Builder](builder.md) — complex construction step-by-step
- [Strategy](../behavioural/strategy.md) — Factory creates, Strategy executes
- [Template Method](../behavioural/template-method.md) — often combined with Factory Method
- [Open/Closed Principle](../../solid/open-closed.md) — Factory enables OCP
