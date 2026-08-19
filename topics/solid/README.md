# SOLID Principles

## Overview

Five design principles that make software maintainable, flexible, and testable.

| # | Principle | One-liner | Day |
|---|---|---|---|
| S | [Single Responsibility](single-responsibility.md) | One class, one reason to change | Day 5 |
| O | [Open/Closed](open-closed.md) | Extend via new classes, don't modify existing | Day 6 |
| L | [Liskov Substitution](liskov-substitution.md) | Subtypes must honor parent's contract | Day 6 |
| I | [Interface Segregation](interface-segregation.md) | Don't force unused methods on clients | Day 7 |
| D | [Dependency Inversion](dependency-inversion.md) | Depend on abstractions, not details | Day 7 |

## How They Connect

```
SRP  → focused classes        ↔ ISP → focused interfaces (same idea, different level)
OCP  → extend safely          ↔ LSP → extensions work correctly
ISP  → narrow interfaces      → fixes LSP violations
DIP  → depend on abstractions → enables OCP (new implementations without changes)
```

## Quick Reference

| Principle | Violation Signal | Fix |
|---|---|---|
| SRP | Class changes for multiple unrelated reasons | Split by actor/stakeholder |
| OCP | Adding new type = modifying existing if-else | Interface + strategy |
| LSP | Child throws or behaves differently than parent promises | Narrow the interface or use composition |
| ISP | Implementer has no-op methods or throws UnsupportedOp | Split into focused interfaces |
| DIP | `new ConcreteClass()` in business logic, can't mock | Interface + constructor injection |

## Resources

- [Baeldung — SOLID](https://www.baeldung.com/solid-principles)
- [Refactoring Guru — SOLID](https://refactoring.guru/design-patterns)
- [Clean Architecture (Robert Martin)](https://www.oreilly.com/library/view/clean-architecture/9780134494272/)

## Related

- [Cohesion and Coupling](../oops/cohesion-and-coupling.md) — SOLID achieves high cohesion + low coupling
- [Design Patterns](../design-patterns/README.md) — patterns implement SOLID principles
- [Problem-Solving Framework](../lld-approach/problem-solving-framework.md) — pressures map to SOLID violations
