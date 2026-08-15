# Object-Oriented Programming Principles

## The Four Pillars

| Pillar | What It Is | Type |
|--------|-----------|------|
| [Encapsulation](encapsulation.md) | Hide state, expose behavior, protect invariants | Practice (you apply) |
| [Abstraction](abstraction.md) | Hide complexity, expose contract (WHAT not HOW) | Practice (you apply) |
| Inheritance | Reuse parent's code via `extends` (IS-A) | Mechanism (language feature) |
| Polymorphism | Same reference, different behavior at runtime | Outcome (you achieve) |

## How They Relate

```
PRACTICES (design decisions YOU make):
├── Encapsulation → "I hide my state and expose behavior"
└── Abstraction   → "I expose contract, hide implementation"

MECHANISMS (language features you USE):
├── extends       → inheritance (get parent's code)
└── implements    → interface (fulfill a contract)

OUTCOMES (what you GET):
└── Polymorphism  → same reference type, different runtime behavior
    (enabled by extends OR implements)
```

## Key Distinctions

| Java Keyword | Concept | Relationship |
|---|---|---|
| `extends` class | Inheritance | IS-A (I am a type of parent, I get their code) |
| `implements` interface | Abstraction + Polymorphism | CAN-DO (I fulfill this contract) |
| private field + method | Encapsulation | State protected, behavior enforces rules |
| Field of interface type | Composition | HAS-A (I delegate to it) |

## Quick Rules

- **Encapsulation ≠ getters/setters** — expose behavior, not data
- **Don't abstract prematurely** — extract interface when 2+ implementations exist
- **IS-A test** — "Can it change at runtime?" → If yes, use composition (HAS-A)
- **Polymorphism is the outcome** — inheritance and interfaces are the mechanisms

## Topic Files

- [Encapsulation](encapsulation.md) — data hiding, controlled access, defensive copies, common violations
- [Abstraction](abstraction.md) — interfaces, abstract classes, YAGNI, when to extract
- [Composition vs Inheritance](composition-vs-inheritance.md) — IS-A vs HAS-A, fragile base class, decision framework
- [Class Relationships](class-relationships.md) — Association → Aggregation → Composition spectrum, lifetime test
- [Cohesion and Coupling](cohesion-and-coupling.md) — design metrics, code smells, value objects

## Resources

- [Oracle Java OOP Tutorial](https://docs.oracle.com/javase/tutorial/java/concepts/)
- [Baeldung — OOP in Java](https://www.baeldung.com/java-oop)
- [Refactoring Guru](https://refactoring.guru/refactoring/smells)

## Related

- [SOLID Principles](../solid/README.md)
- [Design Patterns](../design-patterns/README.md)
