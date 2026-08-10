# Adapter Pattern

## Intent
Convert the interface of a class into another interface that clients expect.

## Problem It Solves
- You have an existing class with useful functionality but incompatible interface
- You can't modify the existing class (third-party, legacy)
- You want to reuse existing code with a new system

## Java Example

```java
// Target interface (what our system expects)
public interface PaymentGateway {
    PaymentResponse charge(String customerId, BigDecimal amount);
}

// Adaptee (third-party SDK with different interface)
public class StripeSDK {
    public StripeCharge createCharge(StripeChargeRequest request) { ... }
}

// Adapter
public class StripePaymentAdapter implements PaymentGateway {
    private final StripeSDK stripe;

    public StripePaymentAdapter(StripeSDK stripe) {
        this.stripe = stripe;
    }

    @Override
    public PaymentResponse charge(String customerId, BigDecimal amount) {
        StripeChargeRequest request = new StripeChargeRequest(customerId, amount.intValue());
        StripeCharge charge = stripe.createCharge(request);
        return new PaymentResponse(charge.getId(), charge.getStatus());
    }
}
```

## When to Use
- Integrating third-party libraries
- Migrating from one system to another incrementally
- Creating a uniform interface over heterogeneous systems

## Trade-offs

| Pros | Cons |
|------|------|
| Single Responsibility (conversion logic isolated) | Extra layer of indirection |
| Open/Closed (new adapters without changing client) | Can mask complexity of adaptee |
| Testability (mock the target interface) | |

## Resources
- [Refactoring Guru — Adapter](https://refactoring.guru/design-patterns/adapter)
