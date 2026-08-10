# Abstract Factory Pattern

## Intent
Provide an interface for creating families of related objects without specifying their concrete classes.

## Problem It Solves
- Need to create a set of related objects that work together
- System should be independent of how its products are created

## When to Use
- Multiple families of products (e.g., UI themes: Dark vs Light, each with Button + TextField + Dialog)
- Products within a family must be used together
- You want to enforce that ProductA and ProductB are from the same family

## Java Example

```java
// Abstract products
public interface Button { void render(); }
public interface TextField { void render(); }

// Concrete family: Material Design
public class MaterialButton implements Button { ... }
public class MaterialTextField implements TextField { ... }

// Concrete family: iOS
public class IOSButton implements Button { ... }
public class IOSTextField implements TextField { ... }

// Abstract Factory
public interface UIFactory {
    Button createButton();
    TextField createTextField();
}

// Concrete factories
public class MaterialUIFactory implements UIFactory {
    public Button createButton() { return new MaterialButton(); }
    public TextField createTextField() { return new MaterialTextField(); }
}

public class IOSUIFactory implements UIFactory {
    public Button createButton() { return new IOSButton(); }
    public TextField createTextField() { return new IOSTextField(); }
}
```

## Trade-offs

| Pros | Cons |
|------|------|
| Guarantees product compatibility | Hard to add new product types (modify all factories) |
| Isolates concrete classes | Complex when families are small |
| Easy to swap entire families | |

## Resources
- [Refactoring Guru — Abstract Factory](https://refactoring.guru/design-patterns/abstract-factory)
