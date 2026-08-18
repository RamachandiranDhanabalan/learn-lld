# Day 06 — Open/Closed Principle + Liskov Substitution Principle

## Cheat Sheet

- **OCP** = Open for extension (add new classes), Closed for modification (don't change existing code)
- **LSP** = If caller code works with parent, it MUST work with any child. No surprises.
- **OCP gives the mechanism** (interfaces + new classes). **LSP ensures it works** (new class keeps the promise).
- **LSP violation signal** = child throws `UnsupportedOperationException` or caller needs `instanceof`
- **LSP fix** = narrow the interface (only promise what ALL implementations can deliver)
- **OCP not worth it** = only 2-3 variants, unlikely to grow (YAGNI)

## Critical Examples

### OCP violation (one to remember)
```java
// ❌ New shape = modify existing method
double calculate(Object shape) {
    if (shape instanceof Circle) { ... }
    else if (shape instanceof Rectangle) { ... }
    // Pentagon? Modify here.
}

// ✅ New shape = new class, calculator NEVER changes
interface Shape { double area(); }
class Pentagon implements Shape { public double area() { ... } }
```

### LSP violation (one to remember)
```java
// ❌ Penguin breaks Bird's promise to fly
class Penguin implements Bird {
    void fly() { throw new UnsupportedOperationException(); }
}
// Caller that does bird.fly() will BREAK with Penguin

// ✅ Fix: separate capabilities — only promise what all can deliver
interface Bird { void eat(); }
interface Flyable { void fly(); }
class Penguin implements Bird { }  // doesn't promise to fly
```

## Decision Framework

| Question | Yes → | No → |
|---|---|---|
| "Will new variants be added?" | Apply OCP (interface + strategy) | Keep simple if-else (YAGNI) |
| "Does the child throw or reject something the parent promised?" | LSP violated — narrow the interface | LSP satisfied ✅ |
| "Does the caller need instanceof for a specific child?" | LSP violated | LSP satisfied ✅ |

### OCP + LSP Together
```
Add new type? → New class (OCP ✅)
Does new class honor parent's contract? → Yes (LSP ✅) → Everything works
                                        → No (LSP ❌) → Caller breaks
```

## Interview Questions & Answers

**Q: "What's OCP?"**
A: "Open for extension, closed for modification. Adding new behavior = adding new classes, not changing existing ones. Strategy pattern is the classic enabler."

**Q: "What's LSP?"**
A: "If caller code works with parent type, it must work with any child type. If a child throws UnsupportedOperationException or requires instanceof checking, LSP is violated."

**Q: "Give an LSP violation."**
A: "Square extends Rectangle. setWidth() should only set width (parent's contract). Square forces width == height, breaking callers who set them independently. Fix: Shape interface with area()."

**Q: "How do OCP and LSP relate?"**
A: "OCP says extend via new classes. LSP says the extension must honor the contract. Without LSP, OCP's extensions break callers instead of extending safely."

**Q: "How do you fix LSP violations?"**
A: "The parent promised too much. Narrow the interface — only promise what ALL implementations can deliver. This leads to Interface Segregation."

## Clues & Signals

- **Senior signal**: "I'd separate Flyable from Bird because not all birds can fly — I don't want to make a promise I can't keep"
- **Senior signal**: "OCP isn't worth it here — only 2 variants, unlikely to grow"
- **Red flag**: `throw new UnsupportedOperationException()` in any interface implementation
- **Red flag**: `instanceof` checks in caller code
- **Key insight**: LSP violations are ALWAYS fixed by narrowing the interface (promise less)

## Trade-offs

| Decision | Apply OCP | Don't Apply |
|---|---|---|
| 5+ variants, growing | ✅ Interface + Strategy | |
| 2-3 variants, stable | | ✅ Simple if-else |
| Product roadmap shows more types coming | ✅ Design for extension | |
| Throwaway code | | ✅ Not worth abstraction |

## Quick Links

- **Detailed topic**: [Open/Closed Principle](../topics/solid/open-closed.md)
- **Detailed topic**: [Liskov Substitution Principle](../topics/solid/liskov-substitution.md)
- **Related**: [Strategy Pattern](../topics/design-patterns/behavioural/strategy.md)
- **Related**: [Composition vs Inheritance](../topics/oops/composition-vs-inheritance.md)
