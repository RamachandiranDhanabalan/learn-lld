# Day 03 — Composition vs Inheritance

## Cheat Sheet

- **Inheritance** = `extends` = IS-A = get parent's code (permanent type relationship)
- **Composition** = HAS-A field + delegate = flexible, testable, runtime-swappable
- **Interface** = `implements` = CAN-DO = fulfill contract (no code inherited)
- **Rule**: Default to composition. Inheritance only when ALL 4 questions pass.
- **Aggregation** = weak HAS-A, independent lifetime (Department → Employee)
- **Composition** = strong HAS-A, owned lifetime (Order → LineItem)
- **Fragile base class** = child breaks when parent's internal implementation changes

## Critical Examples

### Fragile Base Class (memorize this one)
```java
class CountingSet<E> extends HashSet<E> {
    private int count = 0;
    @Override public boolean add(E e) { count++; return super.add(e); }
    @Override public boolean addAll(Collection<? extends E> c) {
        count += c.size();
        return super.addAll(c);  // BUG: HashSet.addAll() calls add() internally → double count!
    }
}
// Fix: COMPOSE a Set, don't EXTEND it.
```

### The Sweet Spot (inheritance + composition)
```java
// Strategies (composition — varying behaviors)
interface MovementStrategy { void move(); }
interface FuelStrategy { void refuel(); }

// Base (inheritance — true type hierarchy)
class Vehicle {
    private final MovementStrategy movement;
    private final FuelStrategy fuel;
    Vehicle(MovementStrategy m, FuelStrategy f) { movement = m; fuel = f; }
}

// Concrete — just pre-configured combinations
class Car extends Vehicle { Car() { super(new RoadMovement(), new PetrolFuel()); } }
class ElectricBoat extends Vehicle { ElectricBoat() { super(new WaterMovement(), new BatteryFuel()); } }
// Adding HydrogenFuel? One new class, zero changes.
```

### Aggregation vs Composition
```java
// Aggregation — created outside, passed in, lives independently
class Department {
    void addEmployee(Employee emp) { employees.add(emp); }  // emp exists elsewhere too
}

// Composition — created inside, owned, dies with parent
class Order {
    void addItem(String id, int qty, BigDecimal price) {
        items.add(new LineItem(id, qty, price));  // created here, owned here
    }
}
```

## Decision Framework

### The 4-Question Test (must ALL be Yes for inheritance)
```
1. Is B truly a TYPE of A forever?              → No → Composition
2. Can B change what type it is at runtime?     → Yes → Composition
3. Does B need ALL of A's behavior?             → No → Composition
4. Is A designed for extension?                 → No → Composition
```

### Aggregation vs Composition Quick Test
> "If I delete the parent, does the child make sense on its own?"
> - Yes → Aggregation
> - No → Composition

## Interview Questions & Answers

**Q: "When inheritance vs composition?"**
A: "Inheritance for true, permanent IS-A where base is designed for extension. Composition for everything else. 4-question test. In practice, composition wins 90%."

**Q: "What's the fragile base class problem?"**
A: "Child depends on parent's internal implementation. Parent changes HOW it does something → child breaks silently. Example: HashSet.addAll() internally calls add(), so a counting subclass double-counts."

**Q: "Example where inheritance seems right but isn't?"**
A: "Java's Stack extends Vector. Stack is more restrictive — shouldn't expose insert-at-index. Composition: Stack wraps a list, exposes only push/pop."

**Q: "Aggregation vs composition?"**
A: "Both HAS-A. Composition = owned lifetime (Order → LineItems, cascade delete). Aggregation = independent lifetime (Department → Employees, they survive)."

**Q: "Why does Java not allow multiple inheritance?"**
A: "Diamond problem. Solution: multiple interfaces (contracts) + single class inheritance (code). Composition is the real way to combine behaviors."

## Clues & Signals

- **Senior signal**: "I'd use inheritance here because it passes my 4-question test" (shows deliberate choice)
- **Senior signal**: Identifying when inheritance + composition work together (Vehicle example)
- **Red flag**: Deep inheritance hierarchies (more than 2–3 levels)
- **Red flag**: Using inheritance for runtime-changing properties (User tier, payment method)
- **Red flag**: Extending concrete classes you don't own/control

## Trade-offs

| Criteria | Inheritance | Composition |
|----------|-------------|-------------|
| Code reuse | Automatic | Explicit (delegate) |
| Flexibility | Static (compile-time) | Dynamic (runtime swap) |
| Coupling | High (knows parent internals) | Low (interface only) |
| Testability | Harder | Easier (mock parts) |
| Multiple behaviors | ❌ Single inheritance | ✅ Unlimited |
| Fragile base class | YES risk | NO risk |

## Quick Links

- **Detailed topic**: [Composition vs Inheritance](../topics/oops/composition-vs-inheritance.md)
- **Related**: [Strategy Pattern](../topics/design-patterns/behavioural/strategy.md)
- **Related**: [OOP Principles](../topics/oops/README.md)
