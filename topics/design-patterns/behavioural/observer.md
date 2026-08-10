# Observer Pattern

## Intent
Define a one-to-many dependency so that when one object changes state, all its dependents are notified automatically.

## Problem It Solves
- Multiple objects need to react to state changes in another object
- Publisher shouldn't know the concrete subscribers
- Adding new listeners shouldn't modify the publisher

## Java Example

```java
// Event
public record OrderEvent(String orderId, OrderStatus status) {}

// Observer interface
public interface OrderEventListener {
    void onOrderEvent(OrderEvent event);
}

// Concrete observers
public class InventoryService implements OrderEventListener {
    public void onOrderEvent(OrderEvent event) {
        if (event.status() == OrderStatus.PLACED) {
            reserveStock(event.orderId());
        }
    }
}

public class NotificationService implements OrderEventListener {
    public void onOrderEvent(OrderEvent event) {
        sendEmail(event.orderId(), "Order " + event.status());
    }
}

// Publisher
public class OrderService {
    private final List<OrderEventListener> listeners = new ArrayList<>();

    public void subscribe(OrderEventListener listener) { listeners.add(listener); }

    public void placeOrder(Order order) {
        // ... save order
        OrderEvent event = new OrderEvent(order.getId(), OrderStatus.PLACED);
        listeners.forEach(l -> l.onOrderEvent(event));
    }
}
```

## Pull vs Push

| Pull | Push |
|------|------|
| Observer queries subject for data | Subject sends data in notification |
| Observer decides what it needs | Subject decides what to send |
| More flexible, less coupling | Simpler for observer, may send too much |

## Real-World Java
- `java.util.Observer` (deprecated) → use custom or Spring events
- Spring `ApplicationEventPublisher` + `@EventListener`
- Kafka/RabbitMQ consumers (distributed observer)

## Trade-offs

| Pros | Cons |
|------|------|
| Loose coupling (pub doesn't know subs) | Unexpected updates (cascade) |
| Add observers without modifying publisher | Memory leaks if observers not unregistered |
| Follows OCP | Order of notification unpredictable |

## Resources
- [Refactoring Guru — Observer](https://refactoring.guru/design-patterns/observer)
