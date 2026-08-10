# Facade Pattern

## Intent
Provide a simplified interface to a complex subsystem.

## Problem It Solves
- Client needs to interact with many subsystem classes
- You want to hide complexity behind a single entry point
- Reduce coupling between client and subsystem

## Java Example

```java
// Complex subsystem classes
public class InventoryService { public boolean checkStock(String productId) { ... } }
public class PaymentService { public PaymentResult charge(BigDecimal amount) { ... } }
public class ShippingService { public String createShipment(Order order) { ... } }
public class NotificationService { public void sendConfirmation(String email) { ... } }

// Facade — one method, orchestrates everything
public class OrderFacade {
    private final InventoryService inventory;
    private final PaymentService payment;
    private final ShippingService shipping;
    private final NotificationService notification;

    public OrderResult placeOrder(OrderRequest request) {
        if (!inventory.checkStock(request.getProductId())) {
            throw new OutOfStockException();
        }
        PaymentResult paymentResult = payment.charge(request.getAmount());
        String trackingId = shipping.createShipment(request.toOrder());
        notification.sendConfirmation(request.getEmail());
        return new OrderResult(paymentResult.getId(), trackingId);
    }
}
```

## Trade-offs

| Pros | Cons |
|------|------|
| Simplifies client code | Can become a "god class" if overloaded |
| Decouples client from subsystem | Hides complexity (not always desirable) |
| Single entry point for testing | |

## Resources
- [Refactoring Guru — Facade](https://refactoring.guru/design-patterns/facade)
