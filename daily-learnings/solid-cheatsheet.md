# SOLID Principles — Complete Cheat Sheet

## One-Liner Each

| # | Principle | Rule | Violation Signal |
|---|---|---|---|
| **S** | Single Responsibility | One class, one reason to change (one actor) | Class changes for unrelated reasons, vague name ("Manager") |
| **O** | Open/Closed | Add new behavior via new classes, don't modify existing | if-else grows with every new type |
| **L** | Liskov Substitution | Subtypes honor parent's contract — no surprises for callers | Child throws exception or changes parent's semantics |
| **I** | Interface Segregation | Don't force clients to implement methods they don't use | No-op methods, `throw UnsupportedOperationException` |
| **D** | Dependency Inversion | Business logic depends on abstractions, not concrete classes | `new ConcreteClass()` in service, can't mock in tests |

---

## How They Relate

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   SRP ←────── same idea ──────→ ISP                            │
│   (one class, one job)          (one interface, one capability) │
│                                                                 │
│   OCP ←────── partners ───────→ LSP                            │
│   (extend via new classes)      (extensions honor the contract) │
│                                                                 │
│   ISP ─────── fixes ──────────→ LSP                            │
│   (narrow interface)            (child can now keep all promises)│
│                                                                 │
│   DIP ─────── enables ────────→ OCP                            │
│   (depend on abstractions)      (new impl without touching code)│
│                                                                 │
│   SRP ─────── enables ────────→ OCP                            │
│   (focused class)               (easier to extend cleanly)      │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**In plain English:**
- SRP and ISP are the same idea at different levels (class vs interface)
- OCP says "extend via new types." LSP says "make sure extensions actually work."
- When LSP is violated (child can't keep promise) → ISP fixes it (narrow the promise)
- DIP enables OCP — when you depend on abstractions, adding new implementations is trivial
- SRP enables OCP — focused classes are easier to extend without breaking things

---

## The Quick Detection Table

| I see this... | Principle violated | Fix |
|---|---|---|
| Class does 5 unrelated things | SRP | Split by actor/reason-to-change |
| if-else grows with every new type | OCP | Interface + one class per type (Strategy/Factory) |
| Child throws or rejects what parent promised | LSP | Narrow interface (ISP) or use composition |
| Implementer has no-op / throw methods | ISP (+ LSP) | Split into smaller focused interfaces |
| `new StripeClient()` in business code | DIP | Interface + inject via constructor |

---

## One Example That Hits All 5

```java
// ❌ Violates ALL SOLID principles
class PaymentManager {
    private StripeClient stripe = new StripeClient("sk_live");          // DIP ❌

    void process(Order order, String type) {
        if (type.equals("CARD")) { stripe.charge(order.total); }       // OCP ❌
        else if (type.equals("UPI")) { /* upi logic */ }
        else if (type.equals("WALLET")) { /* wallet logic */ }

        sendReceipt(order);                                            // SRP ❌
        generateReport(order);                                         // SRP ❌
    }
    private void sendReceipt(Order order) { /* email logic */ }
    private void generateReport(Order order) { /* pdf logic */ }
}

interface FullPayment {
    void charge(BigDecimal amount);
    void refund(String txnId);
    void subscribe(String planId);
    void cancelSubscription(String subId);
}
class UpiPayment implements FullPayment {
    void subscribe(String planId) { throw new UnsupportedOperationException(); }  // ISP ❌
    void cancelSubscription(String subId) { throw new UnsupportedOperationException(); } // LSP ❌
}
```

```java
// ✅ Follows ALL SOLID principles
// SRP: each class has one job
// OCP: new payment = new class
// LSP: each implementation honors its interface
// ISP: interfaces are focused (Chargeable vs Subscribable)
// DIP: business logic depends on interfaces, injected

interface Chargeable { PaymentResult charge(BigDecimal amount); }
interface Refundable { PaymentResult refund(String txnId); }
interface Subscribable { void subscribe(String planId); void cancel(String subId); }

class StripePayment implements Chargeable, Refundable, Subscribable { /* all */ }
class UpiPayment implements Chargeable { /* only charge */ }

class PaymentService {
    private final Chargeable gateway;  // DIP — interface, injected
    PaymentService(Chargeable gateway) { this.gateway = gateway; }

    PaymentResult process(Order order) {
        return gateway.charge(order.getTotal());  // OCP — works with any Chargeable
    }
}

class ReceiptService { void send(Order order) { /* email */ } }  // SRP — separate
class ReportService { void generate(Order order) { /* pdf */ } } // SRP — separate
```

---

## Interview Phrasing (Crisp Answers)

**"Walk me through SOLID":**

> "S — one class, one reason to change. Keeps classes focused.
> O — extend via new classes, don't modify existing. Strategy pattern is the classic enabler.
> L — subtypes must work wherever parent is expected. If they throw or change semantics, the hierarchy is wrong.
> I — split fat interfaces so implementers only deal with what they actually need. This fixes L violations.
> D — business logic depends on interfaces, not MySQL or Stripe. Inject implementations. Enables testing and swapping."

**"Which principle is most important?":**

> "SRP — it's foundational. A focused class naturally has low coupling, is easy to extend (OCP), and doesn't force unrelated contracts (ISP). Get SRP right and the others follow more naturally."

**"Give me a real example of SOLID in production":**

> "Spring's repository layer: interface per entity (ISP), concrete implementations hidden (DIP), adding a new finder is a method on the interface (OCP), each repo is focused on one entity (SRP), and any impl that returns `List<User>` from `findAll()` works — you can swap JPA for Mongo (LSP)."

---

## The Pressure → Principle → Pattern Map

| Pressure | Principle | Pattern |
|---|---|---|
| Class does too much | SRP | Split classes |
| if-else grows with types | OCP | Strategy, Factory |
| Child can't keep parent's promise | LSP | Narrow interface (ISP), composition |
| Interface forces unwanted methods | ISP | Split interfaces |
| Coupled to concrete classes | DIP | Interface + DI |
| Behavior varies at runtime | OCP + DIP | Strategy |
| Multiple listeners for changes | SRP + OCP | Observer |
| Same flow, different steps | OCP | Template Method |

---

## Quick Links

- [S — Single Responsibility](../topics/solid/single-responsibility.md)
- [O — Open/Closed](../topics/solid/open-closed.md)
- [L — Liskov Substitution](../topics/solid/liskov-substitution.md)
- [I — Interface Segregation](../topics/solid/interface-segregation.md)
- [D — Dependency Inversion](../topics/solid/dependency-inversion.md)
- [Problem-Solving Framework](../topics/lld-approach/problem-solving-framework.md)
