# Day 04 — Cohesion + Coupling + Code Smells

## Cheat Sheet

- **Cohesion** = How focused a class is. HIGH = all methods serve one purpose. LOW = grab-bag of unrelated stuff.
- **Coupling** = How dependent classes are on each other. LOW = depend on abstractions. HIGH = depend on concrete implementations.
- **Goal** = HIGH cohesion + LOW coupling. They're inversely correlated.
- **SRP is the bridge** = "One reason to change" enforces cohesion, which reduces coupling.
- **Value objects** fix Primitive Obsession and Data Clumps (Email, Address, Money).

## Critical Examples

### Low cohesion detection (one signal to remember)
```java
// If a method doesn't use most of the class's fields → low cohesion
class UserManager {
    private Database db;      // used by saveUser only
    private SmtpClient smtp;  // used by sendEmail only
    private PdfGen pdf;       // used by generateReport only
    // These methods don't belong in the same class
}
```

### High coupling detection (one signal to remember)
```java
// If you see `new ConcreteClass()` in business logic → high coupling
class OrderService {
    void process() {
        MySQLConnection conn = new MySQLConnection(...);  // ❌ can't test, can't swap
    }
}
// Fix: depend on interface, inject via constructor
```

### Value Object (fix for Data Clumps + Primitive Obsession)
```java
// ❌ street, city, zip always together as loose strings
void addStudent(String name, String street, String city, String zip) { ... }

// ✅ Extract value object
class Address { private final String street, city, zip; }
void addStudent(String name, Address address) { ... }
```

## Decision Framework

| Signal You See | What It Means | Action |
|---|---|---|
| Method doesn't use most fields | Low cohesion | Split class along field-usage clusters |
| `new ConcreteClass()` in business logic | High coupling | Extract interface + inject |
| Class name is "Manager"/"Helper"/"Util" | Likely God Class | Split by responsibility |
| Same 3+ fields always appear together | Data Clump | Extract value object |
| Method takes 5+ parameters | Long param list | Parameter object |
| `a.getB().getC().doThing()` | Law of Demeter violation | Ask `a` directly |

## Interview Questions & Answers

**Q: "What's cohesion?"**
A: "How focused a class is. High cohesion = all methods/fields serve one purpose. Test: if a method doesn't use most fields, cohesion is low."

**Q: "What's coupling?"**
A: "How dependent classes are on each other. Low coupling = depend on abstractions, change independently. High coupling = concrete dependencies, changes ripple."

**Q: "How are they related?"**
A: "Inversely. High cohesion → fewer external dependencies → low coupling. SRP is the principle that enforces both."

**Q: "Name 3 code smells and fixes."**
A: "God Class → split by responsibility. Data Clumps → extract value object. Long Parameter List → parameter object. Feature Envy → move method to the class it envies."

**Q: "How do you detect a God class?"**
A: "Vague name (Manager/Helper), 500+ lines, methods that don't share fields, changes for multiple unrelated reasons."

## Clues & Signals

- **Senior signal**: Saying "this class has low cohesion because methods X and Y don't share any fields"
- **Senior signal**: Extracting value objects proactively (Email, Money, Address)
- **Senior signal**: Identifying Middle Man smell (orchestrator that just delegates with no logic)
- **Red flag**: 500-line class that "works fine" — works ≠ maintainable
- **Red flag**: Can't write unit test without real DB/network → coupling problem

## Trade-offs

| Decision | Fewer classes (less cohesive) | More classes (highly cohesive) |
|---|---|---|
| Readability | Everything in one place | Navigate between files |
| Testability | Hard to isolate | Easy to test each alone |
| Change impact | Ripple risk | Isolated changes |
| Team work | Merge conflicts | Parallel work |
| When to prefer | Prototype, throwaway | Production, growing team |

## Quick Links

- **Detailed topic**: [Cohesion and Coupling](../topics/oops/cohesion-and-coupling.md)
- **Related**: [Problem-Solving Framework — pressure ② and ③](../topics/lld-approach/problem-solving-framework.md)
- **Related**: [SOLID Principles](../topics/solid/README.md)
