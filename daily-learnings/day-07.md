# Day 07 — Interface Segregation + Dependency Inversion

## Cheat Sheet

- **ISP** = Don't force clients to depend on interfaces they don't use. Split fat interfaces.
- **DIP** = High-level depends on abstractions (interfaces), not low-level (MySQL, Stripe). Both depend on the abstraction.
- **ISP = SRP for interfaces** — same idea, different level (class vs interface)
- **DIP = the principle. DI = the mechanism. Spring = the tool.**
- **ISP fixes LSP** — narrowing the interface means children only promise what they can deliver
- **DIP test** = "Will I need a 2nd implementation, or mock this in tests?" → Yes → apply DIP

## Critical Examples

### ISP violation (one to remember)
```java
// ❌ SmartSpeaker forced to implement printDocument, scanDocument
interface SmartDevice { turnOn(); printDocument(); scanDocument(); playMusic(); }
class SmartSpeaker implements SmartDevice {
    printDocument() { throw new UnsupportedOperationException(); }  // ❌
}

// ✅ Split into focused capabilities
interface Switchable { void turnOn(); void turnOff(); }
interface Printable { void print(); void scan(); }
interface AudioPlayer { void playMusic(); }
class SmartSpeaker implements Switchable, AudioPlayer { /* only what it can do */ }
```

### DIP violation (one to remember)
```java
// ❌ Business logic creates concrete deps — can't test, can't swap
class OrderService {
    private StripeClient stripe = new StripeClient("sk_live");  // concrete!
}

// ✅ Depend on abstraction, inject
class OrderService {
    private final PaymentGateway gateway;  // interface
    OrderService(PaymentGateway gateway) { this.gateway = gateway; }
}
```

### The Arrow Flip (DIP visual)
```
Before: OrderService → MySQLRepo (high depends on low)
After:  OrderService → OrderRepository (interface) ← MySQLRepo implements
```

## Decision Framework

| Question | → Action |
|---|---|
| "Does any implementer have empty/no-op methods?" | ISP violated → split interface |
| "Does implementer throw UnsupportedOperationException?" | ISP + LSP violated → split |
| "Am I using `new ConcreteClass()` in business logic?" | DIP violated → interface + inject |
| "Can I test this without real DB/network?" | No → DIP violated |
| "Do ALL implementers need ALL methods?" | Yes → interface is fine, don't over-split |
| "Will I ever mock this or swap implementations?" | Yes → apply DIP. No → use concrete directly |

## Interview Questions & Answers

**Q: "What's ISP?"**
A: "Don't force classes to implement interfaces they don't use. Fat interfaces lead to no-op methods or UnsupportedOperationException. Split into focused interfaces aligned with client needs."

**Q: "What's DIP?"**
A: "Business logic depends on abstractions (interfaces), not implementations (MySQL, Stripe). Both layers depend on the interface. This is why DI frameworks exist."

**Q: "ISP vs SRP?"**
A: "Same idea, different level. SRP = one class, one responsibility. ISP = one interface, one capability. Both say 'don't make one thing do too many unrelated things.'"

**Q: "DIP vs DI?"**
A: "DIP is the principle (depend on abstractions). DI is the mechanism (inject via constructor). Spring's @Autowired is DI implementing DIP."

**Q: "When is DIP NOT worth it?"**
A: "Internal utilities (StringUtils), standard lib (ArrayList), value objects, throwaway code. Apply when: external systems, things you mock, things that might be swapped."

## Clues & Signals

- **Senior signal**: "This interface is too fat — not every implementation needs clearCache/reindex. I'd split into Repository + CacheManager + TransactionManager"
- **Senior signal**: "I don't need DIP here — this is just an internal utility with no reason to swap"
- **Red flag**: `throw new UnsupportedOperationException()` anywhere
- **Red flag**: `new ConcreteClient()` in service classes
- **Key insight**: ISP fix resolves LSP violations simultaneously

## SOLID Complete — How They Connect

```
SRP  → "One class, one reason to change"       → focused classes
OCP  → "Extend via new classes, don't modify"   → safe growth
LSP  → "Subtypes honor parent's contract"       → extensions work correctly
ISP  → "Small interfaces, focused capabilities" → no forced dependencies
DIP  → "Depend on abstractions, not details"    → swappable, testable

SRP ↔ ISP (same idea: class vs interface)
OCP ↔ LSP (OCP gives extension, LSP validates it)
ISP → LSP (narrow interface fixes LSP)
DIP → OCP (abstractions enable extension)
```

## Quick Links

- **Detailed topic**: [Interface Segregation](../topics/solid/interface-segregation.md)
- **Detailed topic**: [Dependency Inversion](../topics/solid/dependency-inversion.md)
- **Related**: [Liskov Substitution](../topics/solid/liskov-substitution.md)
- **Related**: [Open/Closed](../topics/solid/open-closed.md)
