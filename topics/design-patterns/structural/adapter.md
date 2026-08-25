# Adapter Pattern

## Intent

Make incompatible interfaces work together by wrapping one class and translating its interface to match what the client expects. The "integration glue" pattern.

## Problem It Solves

- Third-party SDK has useful functionality but incompatible interface
- You can't modify the existing class (third-party, legacy, another team)
- You want a uniform interface over multiple heterogeneous systems
- Migrating from one system to another incrementally

---

## Structure

```
CLIENT → uses TARGET interface
ADAPTER implements TARGET, wraps ADAPTEE
ADAPTEE = the existing class with incompatible interface

Client → Target Interface ← Adapter → Adaptee (foreign class)
```

---

## Example: Payment SDK Integration

```java
// TARGET — what your system expects
interface PaymentGateway {
    PaymentResult charge(String customerId, BigDecimal amount);
}

// ADAPTEE — third-party with different interface (can't modify)
class StripeSDK {
    StripeCharge createCharge(StripeChargeRequest request) { ... }
}

// ADAPTER — bridges them
class StripePaymentAdapter implements PaymentGateway {
    private final StripeSDK stripe;  // wraps adaptee (composition)

    StripePaymentAdapter(StripeSDK stripe) { this.stripe = stripe; }

    @Override
    public PaymentResult charge(String customerId, BigDecimal amount) {
        // TRANSLATE: our interface → Stripe's interface
        StripeChargeRequest req = new StripeChargeRequest();
        req.setCustomer(customerId);
        req.setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue()); // dollars → cents

        StripeCharge charge = stripe.createCharge(req);

        // TRANSLATE: Stripe's response → our response
        return new PaymentResult(charge.getId(), charge.getStatus().equals("succeeded"));
    }
}

// CLIENT — doesn't know about Stripe
class OrderService {
    private final PaymentGateway gateway;  // uses target interface
    OrderService(PaymentGateway gateway) { this.gateway = gateway; }
}
```

---

## What Adapter Does

| Job | How |
|---|---|
| Translates interface | Converts method signatures (our method → their method) |
| Translates data | Converts formats (BigDecimal → cents, our DTO → their DTO) |
| Wraps the adaptee | Composition (HAS-A), delegates calls |
| Implements the target | Looks like target interface to the client |
| Doesn't modify either | Target and adaptee remain unchanged |

---

## When to Use

| Signal | Example |
|---|---|
| Third-party SDK with different interface | Stripe, Twilio, AWS SDKs |
| Migrating systems incrementally | Old payment → new, keep same interface |
| Uniform interface over multiple providers | Multiple SMS providers behind one `SmsService` |
| Legacy class with wrong interface | Old `XmlParser` needs new `DataProcessor` interface |
| Can't modify the existing class | Third-party, compiled, another team |

## When NOT to Use

| Signal | Use Instead |
|---|---|
| You control both sides | Redesign directly |
| Adaptation is trivial (one method rename) | Over-engineering |
| You're adapting your own new classes | Design them compatible from the start |

---

## Adapter vs Other Structural Patterns

| Pattern | What It Does | Key Difference |
|---|---|---|
| **Adapter** | Translates interface A → B | Changes interface, NOT behavior |
| **Decorator** | Adds behavior to existing object | Same interface, adds functionality |
| **Facade** | Simplifies complex subsystem | Wraps MANY classes, defines new simple interface |
| **Proxy** | Controls access to an object | Same interface, adds control (cache, auth, lazy) |

---

## Object Adapter vs Class Adapter

| | Object Adapter (Composition) | Class Adapter (Inheritance) |
|---|---|---|
| Structure | HAS-A adaptee | EXTENDS adaptee |
| Flexibility | Can adapt any subclass of adaptee | Locked to one class |
| Java fit | ✅ Always prefer | ❌ Single inheritance limits |

---

## Adapter vs Strategy — The Distinction

| | Adapter | Strategy |
|---|---|---|
| Intent | "Translate incompatible foreign interface" | "Swap interchangeable algorithms" |
| What's inside | Wraps another class, translates its output | Has its own logic directly |
| When | Third-party/legacy you can't modify | Your own algorithms you designed |
| Structure | Same (interface + implementations) | Same (interface + implementations) |

From the caller's perspective, both look identical. The pattern name describes **intent**, not structure.

---

## Adapter in Spring Boot

| Where | Usage |
|---|---|
| Payment integration | Stripe/Razorpay SDK → your `PaymentGateway` interface |
| Email provider | SendGrid/SES → your `EmailService` interface |
| Legacy migration | Old API response → new internal DTO |
| Testing | `InMemoryPaymentAdapter` for tests |
| Database | Different DB clients → your `Repository` interface |

---

## Resources

- [Refactoring Guru — Adapter](https://refactoring.guru/design-patterns/adapter)
- [Baeldung — Adapter Pattern](https://www.baeldung.com/java-adapter-pattern)

## Related

- [Decorator](decorator.md) — same structure but adds behavior (not translates)
- [Facade](facade.md) — wraps many classes, simplifies
- [Strategy](../behavioural/strategy.md) — same structure but different intent (own algorithms vs foreign translation)
- [DIP](../../solid/dependency-inversion.md) — Adapter enables depending on your interface, not the foreign class
