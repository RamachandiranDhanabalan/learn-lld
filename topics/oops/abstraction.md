# Abstraction

## Definition

Hiding implementation complexity and exposing only what the caller needs to know. The caller interacts with a simplified interface — knows **WHAT** to do, not **HOW** it's done internally.

---

## WHY It Matters

| Benefit | Explanation |
|---------|-------------|
| **Reduces cognitive load** | Callers deal with WHAT, not HOW |
| **Enables multiple implementations** | The "how" can change independently |
| **Enables polymorphism** | Swap implementations without changing callers |
| **Creates contracts** | Interface is the promise, implementation is the detail |
| **Decouples modules** | Caller depends on abstraction, not concrete class |
| **Maintainability** | Change implementation without touching callers |

---

## Levels of Abstraction in Java

| Mechanism | Abstraction Level | Use When |
|-----------|-------------------|----------|
| **Interface** | Highest | Pure contract, no implementation. Unrelated types share a capability. |
| **Abstract class** | Medium | Shared code + contract. Related types with common behavior. |
| **Concrete class with private methods** | Lowest | Hide internal helper logic within a class. |

---

## Interface vs Abstract Class

| Aspect | Interface | Abstract Class |
|--------|-----------|---------------|
| Implementation | Zero (just method signatures) | Can have concrete methods |
| Inheritance | Multiple interfaces per class | Only one abstract class |
| Fields | Only constants (`static final`) | Can have instance fields |
| Constructor | None | Can have constructor |
| Use when | Unrelated types share a capability | Related types share behavior AND contract |
| Pattern | Strategy, Observer, Repository | Template Method |
| Example | `Searchable`, `Cacheable`, `Comparable` | `AbstractList`, `HttpServlet` |

**Java 8+ note**: Interfaces CAN have `default` methods (shared code). This blurs the line, but the semantic distinction remains: interfaces are capabilities, abstract classes are type hierarchies.

---

## Examples

### Basic Abstraction — Interface with Multiple Implementations

```java
// CONTRACT — caller only knows this
public interface PaymentGateway {
    PaymentResult charge(String customerId, BigDecimal amount);
    PaymentResult refund(String transactionId);
}

// IMPLEMENTATION 1 — Stripe (REST API, webhook confirmation)
public class StripeGateway implements PaymentGateway {
    private final StripeClient client;

    public PaymentResult charge(String customerId, BigDecimal amount) {
        StripeCharge charge = client.createCharge(customerId, amount.intValue());
        return new PaymentResult(charge.getId(), charge.getStatus());
    }

    public PaymentResult refund(String transactionId) {
        StripeRefund refund = client.createRefund(transactionId);
        return new PaymentResult(refund.getId(), "REFUNDED");
    }
}

// IMPLEMENTATION 2 — Razorpay (different SDK, different flow)
public class RazorpayGateway implements PaymentGateway {
    private final RazorpayClient razorpay;

    public PaymentResult charge(String customerId, BigDecimal amount) {
        JSONObject order = razorpay.orders.create(/* razorpay-specific params */);
        return new PaymentResult(order.getString("id"), "CREATED");
    }

    public PaymentResult refund(String transactionId) {
        // Razorpay-specific refund logic
    }
}

// CALLER — knows NOTHING about Stripe or Razorpay
public class OrderService {
    private final PaymentGateway gateway;  // depends on ABSTRACTION

    public OrderService(PaymentGateway gateway) {
        this.gateway = gateway;  // injected — could be Stripe, Razorpay, or a Mock
    }

    public void checkout(Order order) {
        PaymentResult result = gateway.charge(order.getUserId(), order.getTotal());
        if (result.isSuccess()) {
            order.markPaid(result.getTransactionId());
        }
    }
}
```

**What's abstracted**: OrderService doesn't know which payment provider is used. Swap Stripe for Razorpay? Change one line in Spring config. OrderService code doesn't change.

---

### Abstract Class — Template Method (Shared Flow + Custom Steps)

```java
// Shared algorithm skeleton + customization points
public abstract class DataImportJob {

    // Template method — the fixed algorithm flow
    public final void execute() {
        connect();
        List<Record> records = extractData();
        List<Record> transformed = transform(records);
        load(transformed);
        disconnect();
    }

    // Steps that subclasses MUST implement
    protected abstract void connect();
    protected abstract List<Record> extractData();
    protected abstract void disconnect();

    // Hook — optional override (default does nothing)
    protected List<Record> transform(List<Record> records) {
        return records;
    }

    // Common implementation shared by all
    private void load(List<Record> records) {
        records.forEach(r -> database.insert(r));
    }
}

// Concrete — only implements the varying parts
public class CsvImportJob extends DataImportJob {
    protected void connect() { /* open CSV file */ }
    protected List<Record> extractData() { /* parse CSV rows */ }
    protected void disconnect() { /* close file handle */ }
}

public class ApiImportJob extends DataImportJob {
    protected void connect() { /* establish HTTP connection */ }
    protected List<Record> extractData() { /* call REST API, paginate */ }
    protected void disconnect() { /* close HTTP connection */ }

    @Override
    protected List<Record> transform(List<Record> records) {
        return records.stream().map(this::mapApiResponse).collect(toList());
    }
}
```

**Why abstract class here (not interface):**
- The `execute()` flow is SHARED — every import job runs connect → extract → transform → load → disconnect
- Only the HOW of each step varies
- `final` on template method prevents subclasses from changing the flow

---

## When to Extract an Interface (YAGNI Principle)

| Situation | Extract Interface? | Why |
|-----------|-------------------|-----|
| Only one implementation exists | ❌ Not yet | Premature abstraction adds indirection with no benefit |
| 2+ implementations exist | ✅ Yes | Now the abstraction earns its place |
| At a module boundary | ✅ Yes | Other teams/modules will provide implementations |
| Building a library | ✅ Yes | Users will extend your system |
| Testing requires a mock | ✅ Yes | Mockito can mock classes, but interfaces make intent clear |

```java
// ❌ PREMATURE — only one implementation, interface adds noise
public interface OrderRepository {
    Order findById(String id);
}
public class JpaOrderRepository implements OrderRepository { ... }
// 99% of the time, there's never a second repository implementation

// ✅ JUSTIFIED — multiple implementations expected
public interface NotificationChannel {
    void send(String recipient, String message);
}
public class EmailChannel implements NotificationChannel { ... }
public class SmsChannel implements NotificationChannel { ... }
public class PushChannel implements NotificationChannel { ... }
```

**Note on Spring**: Spring encourages interface + implementation even for single impls (`@Service` behind an interface). This is debatable — it helps with AOP proxying but adds ceremony. At senior level, know this trade-off.

---

## Abstraction vs Encapsulation — Key Differences

| Aspect | Encapsulation | Abstraction |
|--------|--------------|-------------|
| Focus | Hiding **state** | Hiding **complexity** |
| Direction | Inward (protect my data) | Outward (simplify for callers) |
| Mechanism | `private` fields + behavior methods | Interfaces, abstract classes |
| Question | "Who can modify my data?" | "What does the caller need to know?" |
| Violation | Public fields, leaky getters | Exposing implementation details in API |

**They work together**: Encapsulation hides data INSIDE a class. Abstraction hides the class ITSELF behind an interface.

```java
// Abstraction: caller uses PaymentGateway interface
// Encapsulation: StripeGateway's internal client, retryCount, config are private
```

---

## Abstraction Enables Polymorphism

The real power of abstraction isn't hiding complexity — it's **enabling polymorphism**:

```java
// One reference type → different behaviors at runtime
PaymentGateway gateway;

gateway = new StripeGateway();
gateway.charge(...);  // Stripe's implementation runs

gateway = new RazorpayGateway();
gateway.charge(...);  // Razorpay's implementation runs

// Same caller code, different behavior. That's the payoff.
```

Without abstraction (interface), you'd need:
```java
// ❌ Without abstraction — caller is coupled to concrete types
if (provider.equals("STRIPE")) {
    stripeClient.createCharge(...);
} else if (provider.equals("RAZORPAY")) {
    razorpayClient.createOrder(...);
}
// Adding a new provider = modifying this code. Violates Open/Closed.
```

---

## Common Mistakes

| Mistake | Why It's Bad | Fix |
|---------|-------------|-----|
| Interface for every class (premature) | Adds indirection without value | Extract when 2+ implementations exist |
| Leaking implementation in method names | `sendViaSMTP()` instead of `send()` | Name methods for WHAT they do, not HOW |
| Abstract class with no shared code | Just use an interface | Abstract class earns its place with shared implementation |
| God interface (too many methods) | Forces implementers to write no-op stubs | Split into focused interfaces (Interface Segregation) |

---

## Resources

- [Oracle Java Tutorial — Interfaces](https://docs.oracle.com/javase/tutorial/java/IandI/createinterface.html)
- [Oracle Java Tutorial — Abstract Classes](https://docs.oracle.com/javase/tutorial/java/IandI/abstract.html)
- [Baeldung — Interface vs Abstract Class](https://www.baeldung.com/java-interface-vs-abstract-class)

## Related

- [Encapsulation](encapsulation.md)
- [OOP Overview](README.md)
- [SOLID — Interface Segregation](../solid/README.md)
- [Template Method Pattern](../design-patterns/behavioural/template-method.md)
- [Strategy Pattern](../design-patterns/behavioural/strategy.md)
