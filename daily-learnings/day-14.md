# Day 14 — Facade + Composite (+ Week 2 Review)

## Cheat Sheet

- **Facade** = One simple method over a complex subsystem. Orchestrates many classes. Reduces coupling.
- **Composite** = Treat leaf (one) and composite (many) uniformly. For trees/part-whole hierarchies.
- **Facade wraps MANY** classes. Adapter wraps ONE. Decorator wraps ONE object.
- **Composite eliminates type-checking** — no `if (isLeaf)`, polymorphism handles it. Enables infinite nesting.
- **Part-whole** = a thing made of smaller things of same type (folder contains files AND folders).
- **Facade caution** = don't let it become a God Class. Orchestrate, don't contain business logic.

## Critical Examples

### Facade — orchestrate subsystems
```java
class OrderFacade {
    // 5 subsystems injected
    public OrderResult placeOrder(OrderRequest req) {
        inventory.checkStock(...);
        pricing.calculate(...);
        payment.charge(...);
        shipping.schedule(...);
        notification.send(...);
        return result;  // ONE method hides 5-subsystem flow
    }
}
```

### Composite — recursive tree, uniform treatment
```java
interface FileSystemItem { long getSize(); }

class File implements FileSystemItem {          // leaf
    public long getSize() { return size; }
}
class Directory implements FileSystemItem {     // composite
    private List<FileSystemItem> children;      // contains File OR Directory (recursive!)
    public long getSize() {
        return children.stream().mapToLong(FileSystemItem::getSize).sum();  // recurse
    }
}
// Client: root.getSize() — treats file and folder the same
```

## Decision Framework

| Signal | Pattern |
|---|---|
| Client orchestrates many subsystems | Facade |
| Simplify a complex multi-class operation | Facade |
| Tree/hierarchy with nesting | Composite |
| "Is this one item or a group?" type-checking | Composite (eliminates it) |
| Part-whole (folder/file, manager/employee) | Composite |
| Recursive operation (sum, count, render tree) | Composite |

### Structural Patterns — Complete Comparison

| Pattern | Wraps | Interface | Purpose |
|---|---|---|---|
| Adapter | One class | Different | Translate |
| Decorator | One object | Same | Add behavior |
| Proxy | One object | Same | Control access |
| Facade | Many classes | New simple | Simplify |
| Composite | Tree of objects | Same (leaf & group) | Uniform treatment |

## Interview Questions & Answers

**Q: "What's Facade?"**
A: "Simplified interface over a complex subsystem. One entry point orchestrating many classes, so the client doesn't know them all. Reduces coupling."

**Q: "Facade vs Adapter?"**
A: "Adapter wraps ONE class to translate its interface. Facade wraps MANY classes to simplify. Adapter = translation, Facade = simplification."

**Q: "What's Composite?"**
A: "Treats individual objects and groups uniformly via a common interface. For tree structures — file systems, org charts. Client doesn't distinguish leaf from group."

**Q: "When Composite?"**
A: "Part-whole hierarchies with recursive operations. Like summing file sizes — file returns its size, folder recursively sums children, client calls getSize() the same way."

**Q: "How does Composite eliminate if-else?"**
A: "Instead of checking 'is this an item or submenu?', each class (leaf/composite) implements the interface. Polymorphism replaces the type-checking, and it supports infinite nesting."

## Clues & Signals

- **Senior signal**: "The 'type' string field disappears with Composite — the class itself is the type"
- **Senior signal**: "This if-else only handles 2 levels — Composite handles infinite nesting"
- **Senior signal**: Recognizing Facade should stay thin (orchestrate, not compute)
- **Red flag**: Facade with business logic inside (God Class risk)
- **Red flag**: Composite where leaf is forced to have add()/remove() it can't support (design carefully)

## Week 2 Pattern Summary

### Creational (Days 8-11)
| Pattern | Use When |
|---|---|
| Factory Method | Type varies at runtime |
| Abstract Factory | Products must match a family |
| Builder | Many optional params, immutable |
| Singleton | Shared resource, no DI (prefer DI) |

### Structural (Days 12-14)
| Pattern | Use When |
|---|---|
| Adapter | Third-party SDK, incompatible interface |
| Decorator | Combine behaviors, stackable |
| Proxy | Cache, auth, lazy load |
| Facade | Simplify complex subsystem |
| Composite | Tree structures, uniform treatment |

## Quick Links

- **Detailed topic**: [Facade](../topics/design-patterns/structural/facade.md)
- **Detailed topic**: [Composite](../topics/design-patterns/structural/composite.md)
- **Related**: [Adapter](../topics/design-patterns/structural/adapter.md), [Decorator](../topics/design-patterns/structural/decorator.md)
