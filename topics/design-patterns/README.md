# Design Patterns

## What Are Design Patterns?

Reusable solutions to common design problems. They are NOT code — they are templates for solving recurring structural issues in OO design.

## Categories

### [Creational](creational/)
Concerned with object creation mechanisms.

| Pattern | When to Use | Interview Frequency |
|---------|-------------|-------------------|
| [Factory Method](creational/factory-method.md) | Create objects without specifying exact class | ★★★★★ |
| [Abstract Factory](creational/abstract-factory.md) | Create families of related objects | ★★★☆☆ |
| [Builder](creational/builder.md) | Construct complex objects step-by-step | ★★★★★ |
| [Singleton](creational/singleton.md) | Exactly one instance (prefer DI instead) | ★★★★☆ |

### [Structural](structural/)
Concerned with how classes and objects are composed.

| Pattern | When to Use | Interview Frequency |
|---------|-------------|-------------------|
| [Adapter](structural/adapter.md) | Make incompatible interfaces work together | ★★★★☆ |
| [Decorator](structural/decorator.md) | Add behavior dynamically without subclassing | ★★★★★ |
| [Proxy](structural/proxy.md) | Control access to an object | ★★★☆☆ |
| [Facade](structural/facade.md) | Simplify complex subsystem interface | ★★★☆☆ |
| [Composite](structural/composite.md) | Treat individual and groups uniformly (tree) | ★★★★☆ |

### [Behavioural](behavioural/)
Concerned with communication between objects.

| Pattern | When to Use | Interview Frequency |
|---------|-------------|-------------------|
| [Strategy](behavioural/strategy.md) | Swap algorithms at runtime | ★★★★★ |
| [Observer](behavioural/observer.md) | Notify dependents of state changes | ★★★★★ |
| [State](behavioural/state.md) | Object behavior changes with internal state | ★★★★★ |
| [Command](behavioural/command.md) | Encapsulate a request as an object | ★★★★☆ |
| [Chain of Responsibility](behavioural/chain-of-responsibility.md) | Pass request along a chain of handlers | ★★★★☆ |
| [Template Method](behavioural/template-method.md) | Define skeleton, let subclasses fill steps | ★★★★☆ |
| [Iterator](behavioural/iterator.md) | Traverse collection without exposing internals | ★★☆☆☆ |

## Pattern Selection Guide

Ask yourself:
1. **Is the problem about creating objects?** → Creational
2. **Is it about combining objects/classes?** → Structural
3. **Is it about how objects communicate?** → Behavioural

## Resources

- [Refactoring Guru — All Patterns](https://refactoring.guru/design-patterns)
- [Christopher Okhravi — YouTube Playlist](https://youtube.com/playlist?list=PLrhzvIcii6GNjpARdnO4ueTUAVR9eMBpc)
- [Head First Design Patterns (Book)](https://www.oreilly.com/library/view/head-first-design/9781492077992/)

## Related

- [SOLID Principles](../solid/README.md) — patterns enforce SOLID
- [OOP](../oops/README.md) — prerequisite concepts
