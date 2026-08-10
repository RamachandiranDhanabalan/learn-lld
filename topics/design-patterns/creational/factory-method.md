# Factory Method Pattern

## Intent
Define an interface for creating an object, but let subclasses decide which class to instantiate.

## Problem It Solves
- Client code shouldn't know the concrete class it's instantiating
- Adding new types shouldn't require modifying existing creation logic

## Structure

```
Creator (abstract)
├── factoryMethod(): Product  ← abstract
└── someOperation()           ← uses factoryMethod()

ConcreteCreatorA              ConcreteCreatorB
├── factoryMethod(): ProductA ├── factoryMethod(): ProductB

Product (interface)
├── ProductA
├── ProductB
```

## Java Example

```java
// Product interface
public interface Notification {
    void send(String message);
}

// Concrete products
public class EmailNotification implements Notification {
    public void send(String message) { /* send email */ }
}

public class SMSNotification implements Notification {
    public void send(String message) { /* send SMS */ }
}

public class PushNotification implements Notification {
    public void send(String message) { /* send push */ }
}

// Factory
public class NotificationFactory {
    public static Notification create(String type) {
        return switch (type) {
            case "EMAIL" -> new EmailNotification();
            case "SMS" -> new SMSNotification();
            case "PUSH" -> new PushNotification();
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }
}
```

## When to Use
- You don't know ahead of time which concrete class you need
- You want to centralize object creation logic
- You want to decouple client from concrete implementations

## Trade-offs

| Pros | Cons |
|------|------|
| Follows OCP (add new product = add new class) | Can lead to many small classes |
| Decouples client from concrete types | Simple cases may be over-engineered |
| Single point of creation logic | |

## SOLID Connection
- **OCP**: New types don't modify existing factory (if using registration)
- **DIP**: Client depends on Product interface, not concrete class

## Interview Questions
1. "How is Factory Method different from Abstract Factory?"
2. "When would you use a Factory vs just `new`?"
3. "How would you make the factory extensible without modifying it?" (→ Registry pattern)

## Resources
- [Refactoring Guru — Factory Method](https://refactoring.guru/design-patterns/factory-method)
