# Open/Closed Principle (OCP)

## Definition

> "Software entities should be **open for extension** but **closed for modification**."

- **Open for extension** = you can ADD new behavior (new classes, new implementations)
- **Closed for modification** = you don't CHANGE existing, tested code to add new behavior

---

## WHY It Matters

| With OCP | Without OCP |
|---|---|
| New feature = new class | New feature = modify existing if-else |
| Existing tests still pass | Must retest everything after modification |
| Zero risk to existing behavior | Risk breaking what already works |
| Team members work in parallel (new files) | Everyone edits the same file |

---

## The Problem OCP Solves

```java
// ❌ VIOLATES OCP — every new shape requires modifying this method
class AreaCalculator {
    double calculate(Object shape) {
        if (shape instanceof Circle c) return Math.PI * c.radius * c.radius;
        else if (shape instanceof Rectangle r) return r.width * r.height;
        else if (shape instanceof Triangle t) return 0.5 * t.base * t.height;
        // Pentagon? Modify. Hexagon? Modify. Never ends.
        throw new IllegalArgumentException("Unknown shape");
    }
}

// ✅ FOLLOWS OCP — new shape = new class, zero changes to calculator
interface Shape {
    double area();
}
class Circle implements Shape { public double area() { return Math.PI * radius * radius; } }
class Rectangle implements Shape { public double area() { return width * height; } }
// Adding Pentagon? New class implements Shape. Calculator NEVER changes.

class AreaCalculator {
    double totalArea(List<Shape> shapes) {
        return shapes.stream().mapToDouble(Shape::area).sum();
    }
}
```

---

## How OCP Is Achieved

| Mechanism | Pattern | Example |
|---|---|---|
| Interface + Polymorphism | Strategy, Factory | New payment type = new class |
| Abstract class + Override | Template Method | New import job = extend base |
| Composition + Injection | Decorator, Strategy | Inject new behavior at runtime |
| Registration/Config | Plugin architecture | Register new handlers without code change |

---

## When NOT to Apply OCP (YAGNI)

| Don't Apply When... | Why |
|---|---|
| Only 2-3 variants, unlikely to grow | Simple if-else is fine |
| Throwaway code, prototype | Not worth the abstraction cost |
| You're speculating about future variants | Don't solve problems you don't have |
| The if-else has been stable for years | If it never changes, it doesn't need OCP |

**Apply When:**
- Product roadmap shows more variants coming
- if-else is already at 4+ cases and growing
- Multiple team members add variants frequently

---

## OCP ≠ "Never Modify Code"

OCP targets the **common extension path**. You'll still modify code for:
- Bug fixes
- Requirement changes that alter existing behavior
- Performance improvements
- Redesigns

OCP ensures: **the most frequent type of change (adding a new variant) doesn't require touching existing code.**

---

## Resources

- [Refactoring Guru — OCP](https://refactoring.guru/design-patterns)
- [Baeldung — Open Closed Principle](https://www.baeldung.com/java-open-closed-principle)

## Related

- [Single Responsibility](single-responsibility.md) — SRP makes OCP easier (focused classes are easier to extend)
- [Liskov Substitution](liskov-substitution.md) — ensures extensions actually work correctly
- [Strategy Pattern](../design-patterns/behavioural/strategy.md) — classic OCP enabler
- [Problem-Solving Framework — pressure ④](../lld-approach/problem-solving-framework.md)
