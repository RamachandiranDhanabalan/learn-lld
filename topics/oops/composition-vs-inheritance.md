# Composition vs Inheritance

## The Rule
> "Favor composition over inheritance" — GoF Design Patterns, Effective Java Item 18

---

## Inheritance (IS-A)

A child class `extends` a parent, getting all its code (fields + methods) automatically.

```java
class Animal {
    void breathe() { System.out.println("breathing"); }
}
class Dog extends Animal {
    void bark() { System.out.println("woof"); }
}
// Dog IS-A Animal — Dog can breathe() + bark()
```

### When Inheritance Is Valid

All four conditions must be true:

| # | Question | If No → |
|---|----------|---------|
| 1 | Is B truly a TYPE of A forever? | Use composition |
| 2 | Can B change what type it is at runtime? | Use composition |
| 3 | Does B need ALL of A's behavior? | Use composition |
| 4 | Is A designed and documented for extension? | Use composition |

### When Inheritance Breaks

```java
// Problem 1: Penguin can't fly, but Bird says it can
class Bird { void fly() { ... } }
class Penguin extends Bird {
    void fly() { throw new UnsupportedOperationException(); }
    // ❌ Violates Liskov Substitution — callers expect all Birds can fly()
}

// Problem 2: User tier changes at runtime
class PremiumUser extends User { }
// ❌ Can't change object's class at runtime. User upgrades → broken.

// Problem 3: Stack exposes too much from Vector
class Stack extends Vector { }
// ❌ Stack shouldn't allow insert-at-index, but Vector does
```

---

## Composition (HAS-A)

A class **contains** another class as a field and delegates behavior to it.

```java
class Engine {
    void start() { System.out.println("engine started"); }
}

class Car {
    private final Engine engine;  // HAS-A

    Car(Engine engine) { this.engine = engine; }

    void start() {
        engine.start();  // delegates
        System.out.println("car ready");
    }
}
```

### Why Composition Wins

- **Runtime flexibility**: Swap behaviors by changing the composed field
- **No fragile base class**: You control what you delegate, parent changes don't break you
- **Multiple behaviors**: Compose unlimited interfaces (Java has single inheritance)
- **Testability**: Mock composed parts independently
- **Follows Open/Closed**: Add new behavior = add new class, don't modify existing

---

## The Fragile Base Class Problem

The #1 reason inheritance is dangerous with classes you don't control:

```java
// You extend HashSet to count additions
public class CountingSet<E> extends HashSet<E> {
    private int count = 0;

    @Override
    public boolean add(E e) {
        count++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        count += c.size();
        return super.addAll(c);
        // ❌ BUG: HashSet.addAll() internally calls this.add()!
        // So count is incremented TWICE for each element.
    }
}
// Expected count after addAll(3 items): 3
// Actual count: 6 (double-counted)
```

**Why**: You didn't know (and shouldn't need to know) that `HashSet.addAll()` calls `add()` internally. Your child depends on the parent's **internal implementation**, which can change in any Java update.

**Fix with composition:**

```java
public class CountingSet<E> {
    private final Set<E> delegate;  // wrap, don't extend
    private int count = 0;

    public CountingSet(Set<E> delegate) { this.delegate = delegate; }

    public boolean add(E e) {
        count++;
        return delegate.add(e);  // we control what we call
    }

    public boolean addAll(Collection<? extends E> c) {
        count += c.size();
        return delegate.addAll(c);  // doesn't call OUR add(), no double-count
    }
}
```

---

## Aggregation vs Composition (Both Are HAS-A)

| Aspect | Aggregation (weak HAS-A) | Composition (strong HAS-A) |
|--------|--------------------------|---------------------------|
| Lifetime | Child exists independently of parent | Child dies when parent dies |
| Ownership | Shared reference possible | Exclusive ownership |
| Creation | Created OUTSIDE, passed in | Created INSIDE the parent |
| Example | Department → Employee | Order → LineItem |
| UML symbol | Empty diamond ◇ | Filled diamond ◆ |
| DB analogy | FK without cascade | FK with ON DELETE CASCADE |

```java
// AGGREGATION — Employee created outside, passed in, lives independently
class Department {
    private List<Employee> employees = new ArrayList<>();

    void addEmployee(Employee emp) { employees.add(emp); }
    // Employee exists before, during, and after Department
    // Same employee can be in multiple departments
}

// COMPOSITION — LineItem created inside, owned exclusively, dies with Order
class Order {
    private final List<LineItem> items = new ArrayList<>();

    void addItem(String productId, int qty, BigDecimal price) {
        items.add(new LineItem(productId, qty, price));  // created HERE
    }
    // LineItems have no meaning outside this Order
    // No external reference to them exists
}
```

**Quick test**: "If I delete the parent, does the child make sense on its own?"
- Yes → Aggregation (Department deleted, Employee still works somewhere)
- No → Composition (Order deleted, "3x iPhone at ₹80k" is meaningless)

---

## The Sweet Spot: Inheritance + Composition Together

Use inheritance for the **type hierarchy** and composition for the **varying behaviors**:

```java
// Strategies — varying behaviors (composition)
interface MovementStrategy { void move(); }
interface FuelStrategy { void refuel(); }

class RoadMovement implements MovementStrategy {
    public void move() { System.out.println("driving on road"); }
}
class WaterMovement implements MovementStrategy {
    public void move() { System.out.println("sailing on water"); }
}
class PetrolFuel implements FuelStrategy {
    public void refuel() { System.out.println("filling petrol"); }
}
class BatteryFuel implements FuelStrategy {
    public void refuel() { System.out.println("charging battery"); }
}

// Base class — composes strategies (inheritance for type, composition for behavior)
class Vehicle {
    private final MovementStrategy movement;
    private final FuelStrategy fuel;

    Vehicle(MovementStrategy movement, FuelStrategy fuel) {
        this.movement = movement;
        this.fuel = fuel;
    }

    void move() { movement.move(); }
    void refuel() { fuel.refuel(); }
}

// Concrete types — pre-configured Vehicles
class Car extends Vehicle {
    Car() { super(new RoadMovement(), new PetrolFuel()); }
}
class ElectricCar extends Vehicle {
    ElectricCar() { super(new RoadMovement(), new BatteryFuel()); }
}
class Boat extends Vehicle {
    Boat() { super(new WaterMovement(), new PetrolFuel()); }
}
class ElectricBoat extends Vehicle {
    ElectricBoat() { super(new WaterMovement(), new BatteryFuel()); }
}
// Adding AmphibiousVehicle or HydrogenFuel → one new class, zero changes
```

---

## Notification System — Why Composition Wins Over Inheritance

```java
// ❌ INHERITANCE — breaks immediately
class Notifier { void send(String msg) { /* email */ } }
class SmsNotifier extends Notifier { void send(String msg) { /* SMS */ } }
// 💥 User wants Email + SMS? Can't extend two classes in Java.

// ✅ COMPOSITION — flexible, combinable
interface NotificationChannel {
    void send(String recipient, String message);
}

class EmailChannel implements NotificationChannel { /* SMTP logic */ }
class SmsChannel implements NotificationChannel { /* Twilio logic */ }
class PushChannel implements NotificationChannel { /* Firebase logic */ }

class NotificationService {
    private final List<NotificationChannel> channels;

    NotificationService(List<NotificationChannel> channels) {
        this.channels = channels;
    }

    void notifyUser(String recipient, String message) {
        channels.forEach(ch -> ch.send(recipient, message));
    }
}

// Combine freely:
var service = new NotificationService(List.of(new EmailChannel(), new SmsChannel()));
// Add WhatsApp? One new class. Zero changes to existing code.
```

---

## Trade-offs Summary

| Criteria | Inheritance | Composition |
|----------|-------------|-------------|
| Code reuse | Automatic (inherit everything) | Explicit (delegate what you need) |
| Flexibility | Static (compile-time) | Dynamic (runtime swap) |
| Coupling | High (child knows parent internals) | Low (depends on interface only) |
| Testability | Harder (instantiate hierarchy) | Easier (mock composed parts) |
| Multiple behaviors | ❌ Single inheritance | ✅ Unlimited interfaces |
| Readability | Simpler for small hierarchies | More classes, more indirection |
| Fragile base class risk | YES | NO |

---

## Resources

- [Effective Java Item 18 — Favor Composition](https://www.baeldung.com/java-inheritance-composition)
- [Refactoring Guru — Relations Between Objects](https://refactoring.guru/design-patterns/what-is-pattern)
- [Head First Design Patterns — Strategy chapter](https://www.oreilly.com/library/view/head-first-design/9781492077992/)

## Related

- [OOP Principles](README.md)
- [SOLID Principles](../solid/README.md)
- [Strategy Pattern](../design-patterns/behavioural/strategy.md)
