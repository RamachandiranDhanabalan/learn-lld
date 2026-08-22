# Abstract Factory Pattern

## Intent

Create **families of related objects** that must be compatible with each other. One factory per family guarantees you never mix products from different families.

## Problem It Solves

- Multiple objects must come from the SAME family (mixing = bug)
- Swapping entire families at once (platform, environment, theme)
- Client shouldn't know which concrete family it's using

---

## When to Use

| Signal | Why Abstract Factory |
|---|---|
| Multiple objects must be from SAME family | Guarantees compatibility |
| Mixing families is a bug | Factory enforces consistency |
| Swap entire families at once | One factory swap = everything changes |
| Products have parallel variants | Each family has the same set of products |

## When NOT to Use

| Signal | Use Instead |
|---|---|
| Only ONE product type varies | Simple Factory or Factory Method |
| Objects don't need to be compatible | No family constraint |
| Only 1-2 families, unlikely to grow | Over-engineering |
| Products from different families CAN be mixed | Simple Factory per product |

---

## Structure

```
AbstractFactory (interface)
├── createProductA()
├── createProductB()

ConcreteFactory1 implements AbstractFactory
├── createProductA() → ConcreteA1
├── createProductB() → ConcreteB1

ConcreteFactory2 implements AbstractFactory
├── createProductA() → ConcreteA2
├── createProductB() → ConcreteB2

Client depends on AbstractFactory → gets consistent family
```

---

## Example: Cloud Infrastructure

```java
// Product interfaces
interface Database { void save(Object data); }
interface Cache { void put(String key, Object val); }
interface MessageQueue { void send(String topic, String msg); }

// Family: AWS
class DynamoDB implements Database { /* AWS implementation */ }
class ElastiCache implements Cache { /* AWS implementation */ }
class SQS implements MessageQueue { /* AWS implementation */ }

// Family: Local Development
class H2Database implements Database { /* in-memory */ }
class InMemoryCache implements Cache { /* HashMap-based */ }
class InMemoryQueue implements MessageQueue { /* BlockingQueue-based */ }

// Abstract Factory
interface CloudFactory {
    Database createDatabase();
    Cache createCache();
    MessageQueue createQueue();
}

class AWSFactory implements CloudFactory {
    public Database createDatabase() { return new DynamoDB(); }
    public Cache createCache() { return new ElastiCache(); }
    public MessageQueue createQueue() { return new SQS(); }
}

class LocalFactory implements CloudFactory {
    public Database createDatabase() { return new H2Database(); }
    public Cache createCache() { return new InMemoryCache(); }
    public MessageQueue createQueue() { return new InMemoryQueue(); }
}

// Client — doesn't know which cloud
class OrderService {
    private final CloudFactory cloud;
    OrderService(CloudFactory cloud) { this.cloud = cloud; }

    void process(Order order) {
        cloud.createDatabase().save(order);
        cloud.createQueue().send("orders", order.getId());
    }
}
// Prod: new AWSFactory(). Dev: new LocalFactory(). Zero code change.
```

---

## When a Family Doesn't Support a Product (ISP on Factory)

If not all families have all products — don't force them. Split the factory:

```java
// Core — all families have this
interface CoreCloudFactory {
    Database createDatabase();
    Cache createCache();
}

// Extended — only some families
interface CDNCapable {
    CDN createCDN();
}

class AWSFactory implements CoreCloudFactory, CDNCapable { /* has everything */ }
class LocalFactory implements CoreCloudFactory { /* no CDN */ }

// Client that needs CDN — depends on CDNCapable
class ContentService {
    private final CDNCapable cdn;  // only AWS/GCP can be injected
}

// Client that only needs core — depends on CoreCloudFactory
class OrderService {
    private final CoreCloudFactory cloud;  // Local, AWS, GCP — all work
}
```

Compiler enforces it. No runtime `instanceof` checks.

---

## The Asymmetry

| Action | Difficulty |
|---|---|
| Add new FAMILY (new factory class) | ✅ Easy — one new class implementing interface |
| Add new PRODUCT to all families | ❌ Hard — modify ALL factory interfaces + ALL implementations |

This is the known weakness of Abstract Factory.

---

## Abstract Factory vs Simple Factory vs Factory Method

| | Simple Factory | Factory Method | Abstract Factory |
|---|---|---|---|
| Creates | ONE product type | ONE product type | FAMILY of products |
| Decision | Which type (switch) | Subclass decides | Which family |
| Constraint | None | Base class owns flow | Products must be compatible |
| Structure | One method | Abstract class + override | Interface + multiple create methods |
| OCP | Pragmatic (switch in factory) | True (new subclass) | True (new factory class) |

---

## Resources

- [Refactoring Guru — Abstract Factory](https://refactoring.guru/design-patterns/abstract-factory)
- [Christopher Okhravi — Abstract Factory](https://www.youtube.com/watch?v=v-GiuMmsXj4)

## Related

- [Factory Method](factory-method.md) — single product, base class owns flow
- [Interface Segregation](../../solid/interface-segregation.md) — split factory when not all families have all products
- [Dependency Inversion](../../solid/dependency-inversion.md) — client depends on factory interface
