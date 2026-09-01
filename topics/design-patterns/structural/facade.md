# Facade Pattern

## Intent

Provide a simplified interface over a complex subsystem. One entry point that orchestrates many classes, so the client doesn't need to know them all.

## Problem It Solves

- Client must interact with many subsystem classes and orchestrate them
- Want to reduce coupling between client and a complex subsystem
- Need a simple entry point to a layered/complex operation

---

## Example

```java
// Complex subsystem — 5 classes
class InventoryService { boolean checkStock(String id) { ... } }
class PricingService { BigDecimal calculate(OrderRequest r) { ... } }
class PaymentService { PaymentResult charge(String cust, BigDecimal amt) { ... } }
class ShippingService { String schedule(OrderRequest r) { ... } }
class NotificationService { void sendConfirmation(String email, String tracking) { ... } }

// FACADE — one method orchestrates all 5
class OrderFacade {
    private final InventoryService inventory;
    private final PricingService pricing;
    private final PaymentService payment;
    private final ShippingService shipping;
    private final NotificationService notification;

    // dependencies injected via constructor

    public OrderResult placeOrder(OrderRequest request) {
        if (!inventory.checkStock(request.getProductId())) throw new OutOfStockException();
        BigDecimal price = pricing.calculate(request);
        PaymentResult pay = payment.charge(request.getCustomerId(), price);
        String tracking = shipping.schedule(request);
        notification.sendConfirmation(request.getEmail(), tracking);
        return new OrderResult(pay.getId(), tracking);
    }
}

// CLIENT — one clean call
orderFacade.placeOrder(request);
```

---

## Key Characteristics

| Aspect | Description |
|---|---|
| Simplifies | One method hides many subsystem calls |
| Decouples | Client depends on facade, not the subsystems |
| Orchestrates | Coordinates the order of operations |
| Doesn't hide everything | Subsystems still directly accessible if needed |

---

## Facade vs Adapter vs Decorator

| Pattern | Wraps | Purpose |
|---|---|---|
| Facade | MANY classes | Simplify a complex subsystem |
| Adapter | ONE class | Translate incompatible interface |
| Decorator | ONE object | Add behavior |

---

## When to Use

| Signal | Example |
|---|---|
| Client orchestrates many subsystems | Order flow: inventory + payment + shipping |
| Simple entry point to complex logic | `VideoConverter.convert(file, format)` |
| Layered architecture boundary | Service layer over multiple repositories |

**Caution**: Don't let Facade become a God Class. It should orchestrate, not contain business logic.

---

## Resources

- [Refactoring Guru — Facade](https://refactoring.guru/design-patterns/facade)

## Related

- [Adapter](adapter.md) — wraps ONE class to translate
- [Composite](composite.md) — another structural pattern
