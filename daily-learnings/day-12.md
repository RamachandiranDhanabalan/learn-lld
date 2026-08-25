# Day 12 — Adapter Pattern

## Cheat Sheet

- **Adapter** = Translates an incompatible interface to one your system expects. The "integration glue."
- **When** = Third-party SDK, legacy code, or any class you CAN'T MODIFY but need to use behind your interface.
- **Structure** = Implements YOUR interface, wraps THEIR class, translates between them.
- **Object Adapter** (composition, preferred) vs Class Adapter (inheritance, avoid in Java).
- **Adapter vs Strategy** = Same structure. Adapter WRAPS a foreign class + translates. Strategy HAS its own logic directly.
- **Adapter vs Decorator** = Adapter changes the INTERFACE. Decorator keeps same interface, adds BEHAVIOR.
- **"Concrete behind interface" is not a pattern** — it's DIP/Abstraction. Pattern name comes from INTENT (what problem you're solving).

## Critical Examples

### Adapter (wraps + translates)
```java
interface PaymentGateway { PaymentResult charge(BigDecimal amount); }

// Wraps incompatible third-party SDK
class StripeAdapter implements PaymentGateway {
    private final StripeSDK stripe;  // foreign class
    public PaymentResult charge(BigDecimal amount) {
        StripeCharge c = stripe.createCharge(toStripeCents(amount));  // TRANSLATE
        return new PaymentResult(c.getId(), c.isSuccess());           // TRANSLATE back
    }
}
```

### How to distinguish Adapter vs Strategy vs Factory
```
"I have an incompatible foreign interface to translate" → ADAPTER
"I have multiple algorithms for the same task to swap"  → STRATEGY
"I need to pick which object to create based on input"  → FACTORY

They often compose: Factory PICKS the right Adapter/Strategy.
All three put concrete behind an interface — the INTENT differs.
```

## Decision Framework

| Signal | Pattern |
|---|---|
| Third-party SDK with different API | Adapter |
| Your own code with swappable algorithms | Strategy |
| Need to pick which implementation at runtime | Factory (selects Adapter or Strategy) |
| Can't modify the class + need different interface | Adapter |
| Can redesign everything from scratch | Strategy (no wrapping needed) |

### Adapter vs Decorator vs Facade vs Proxy

| Pattern | Changes Interface? | Adds Behavior? | Wraps How Many? |
|---|---|---|---|
| Adapter | ✅ Yes (translates) | ❌ No | One class |
| Decorator | ❌ No (same interface) | ✅ Yes (adds) | One class |
| Facade | ✅ Yes (simplifies) | ❌ No | Many classes |
| Proxy | ❌ No (same interface) | ✅ Yes (controls) | One class |

## Interview Questions & Answers

**Q: "What's Adapter?"**
A: "Makes incompatible interfaces work together by wrapping a class and translating its interface to match what the client expects. Used for third-party SDKs, legacy systems, migrations."

**Q: "Adapter vs Strategy?"**
A: "Same structure — both implement an interface. Adapter WRAPS a foreign class and translates. Strategy HAS its own logic directly. Intent differs: translation vs algorithm selection."

**Q: "Adapter vs Decorator?"**
A: "Adapter changes the INTERFACE (translate signatures). Decorator keeps same interface, adds BEHAVIOR (logging, caching). Adapter = translation. Decorator = enhancement."

**Q: "Object vs Class Adapter?"**
A: "Object uses composition (HAS-A), preferred in Java. Class uses inheritance (EXTENDS), locked to one specific class. Always prefer Object Adapter."

**Q: "When NOT to use Adapter?"**
A: "When you control both sides — just redesign the interface directly. Don't wrap your own new classes with adapters."

## Clues & Signals

- **Senior signal**: "These are third-party SDKs with incompatible APIs — I'd use Adapter to wrap each behind our own interface"
- **Senior signal**: "From the caller's perspective, Adapter and Strategy look identical. The difference is intent: wrapping foreign vs own logic."
- **Red flag**: Creating adapter for your own new class (just design it correctly from the start)
- **Key learning**: Pattern name = INTENT, not structure. Many patterns have same structure (interface + implementations). What differs is WHY.
- **Key learning**: Adapter, Strategy, Factory often compose together — Factory picks, Adapter/Strategy executes.

## Trade-offs

| Decision | Use Adapter | Don't Use |
|---|---|---|
| Can't modify the existing class | ✅ | |
| Need uniform interface over many providers | ✅ | |
| You own both sides, can redesign | | ✅ Just fix the interface |
| Trivial one-method rename | | ✅ Over-engineering |
| Migrating incrementally (old + new coexist) | ✅ | |

## Quick Links

- **Detailed topic**: [Adapter Pattern](../topics/design-patterns/structural/adapter.md)
- **Related**: [Strategy](../topics/design-patterns/behavioural/strategy.md) — same structure, different intent
- **Related**: [Decorator](../topics/design-patterns/structural/decorator.md) — Day 13
- **Related**: [DIP](../topics/solid/dependency-inversion.md) — Adapter enables DIP for foreign code
