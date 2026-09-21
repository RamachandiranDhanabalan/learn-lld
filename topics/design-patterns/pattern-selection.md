# Pattern Selection — Choosing the Right Tool (or None)

## Definition

Pattern selection is the discipline of choosing the **simplest** construct that solves a problem — including plain `if/else` — and being able to justify why you did *not* climb higher up the complexity ladder.

- It IS: matching a real *force* (a pressure in the requirements) to the minimal tool that relieves it.
- It is NOT: reaching for a named pattern because the problem "smells like" one, or because a pattern feels more "senior."

> Every pattern buys flexibility by adding indirection. Indirection is a cost. If you don't need the flexibility, you're paying for nothing.

---

## WHY It Matters

| Benefit | Explanation |
|---------|-------------|
| Avoids over-engineering | Patterns add classes, interfaces, indirection. Unneeded flexibility is pure cost. |
| Interview signal | Seniors justify choices; juniors name-drop patterns. Justification is the differentiator. |
| Maintainability | The next dev reads 3 branches faster than a Strategy hierarchy with one impl. |
| YAGNI in practice | "We might add more later" is not a force. Add the pattern *when* the second case actually arrives. |

---

## The Complexity Ladder

For any "behaviour varies" problem, start at the bottom. Climb only when a concrete force pushes you up.

| Rung | Tool | Use when |
|------|------|----------|
| 0 | Inline `if/else` | 2–3 cases, closed set, 1–2 lines each, one call site |
| 1 | `switch` / `enum` method | Fixed closed set (weekdays, HTTP methods, suits in a deck) |
| 2 | `Map<Key, Handler>` | Dispatch/lookup by key, behaviour is small |
| 3 | Strategy | Multiple algorithms, open set, swap at runtime, isolate for testing |
| 4 | Factory (+ Strategy) | As above + creation logic is non-trivial or centralized |
| 5 | Rules engine / config-driven | Non-developers change rules without a redeploy |

**Senior move:** when you pick a rung, name the rung above and below and say why you rejected each.

---

## The Forces That Justify Climbing

Do not climb because code "feels dirty." Climb when a **named force** appears:

| Force | Symptom | Fix |
|-------|---------|-----|
| OCP pressure | Each new type edits the same `if/else` **and** several other files | Strategy / polymorphism |
| Duplication | The same `switch(type)` appears in multiple methods | Put behaviour on the type (polymorphism) |
| Runtime swap | Behaviour must change per-request / per-tenant / per-config | Strategy (inject it) |
| Testability | A single branch can't be tested without the whole class | Extract to an injectable unit |
| Creation complexity | Choosing + building the object is itself messy logic | Factory |
| Business-owned rules | Rules change weekly, owned by non-engineers | Config / rules engine |

If **none** of these are present, the `if/else` or `enum` **is** the correct, senior answer.

---

## Detailed Walkthrough — "Multiple Pricing Algorithms"

The classic prompt. Watch the answer change with the forces.

### Level 0 — two stable cases → `if/else`
```java
// Weekday vs weekend pricing. Won't grow. One call site.
double price(double base, boolean weekend) {
    return weekend ? base * 1.5 : base;   // ✅ correct. Do NOT reach for Strategy.
}
```
Justification: closed set, 2 cases, no runtime injection, no duplication. A pattern here is over-engineering.

### Level 1 — fixed known set → `enum` method
```java
enum Season {
    REGULAR { double factor() { return 1.0; } },
    PEAK    { double factor() { return 1.8; } },
    OFF     { double factor() { return 0.7; } };
    abstract double factor();
}
double price(double base, Season s) { return base * s.factor(); }
```
Justification: the set is closed and known at compile time. Enum keeps behaviour with the data and is exhaustive (compiler helps).

### Level 3 — open set, runtime swap → Strategy
```java
interface PricingStrategy { double price(double base); }
class SurgePricing implements PricingStrategy { public double price(double b){ return b * surge(); } }
class PromoPricing  implements PricingStrategy { public double price(double b){ return b * 0.8; } }

class Checkout {
    private PricingStrategy strategy;                 // WHY: swappable algorithm
    void setStrategy(PricingStrategy s){ strategy = s; } // WHY: runtime swap per request/tenant
    double total(double base){ return strategy.price(base); }
}
```
Justification: new pricing schemes get added often (OCP), and the active one changes per tenant at runtime (swap). Now the pattern earns its indirection.

### Level 5 — business changes rules → config / rules engine
When marketing changes pricing rules weekly without a deploy, externalize to config or a rules engine. Code stops encoding individual rules.

---

## Common Mistakes (Pattern Overuse)

| Overuse | Reality |
|---------|---------|
| Strategy with **one** implementation | YAGNI. Inline it until the second impl actually arrives. |
| Singleton for shared service | In Spring, a `@Component` bean is already a singleton — and testable. Manual Singleton adds global state. |
| Abstract Factory for one product family | A simple factory method is enough. |
| Observer for 2 hardcoded listeners | If they never change, just call them directly. |
| Builder for a 3-field object, no optionals | A constructor is clearer. |
| Decorator for always-on behaviour | If it's always applied, put it in the method. |
| Chain of Responsibility with 1 handler | Just call the handler. |

Rule of thumb: **a pattern with a single implementation is usually a premature pattern.**

---

## When to Use / When NOT to Use

| Situation | Use a pattern? |
|-----------|----------------|
| 2–3 branches, closed, stable | ❌ `if/else` or `enum` |
| New variant added most sprints | ✅ Strategy / polymorphism |
| Behaviour swapped at runtime | ✅ Strategy / State |
| "We *might* need more later" | ❌ YAGNI — wait for the second case |
| One place switches on a type | ❌ keep it inline |
| Same `switch` duplicated across methods | ✅ polymorphism |
| Non-devs change rules frequently | ✅ config / rules engine |

---

## The Justification Template (say this out loud in interviews)

> "I'll use **[pattern]** because **[named force]** — e.g. new [X] are added frequently and I want OCP. If [X] were a fixed set of 2–3 that never changes, I'd just use an **enum / if-else** instead."

The second sentence — stating when you would **not** use it — is the senior signal.

---

## Interview Red Flags to Avoid

- Naming a pattern in the first 5 seconds without asking about scale/growth/runtime needs.
- Building a Strategy/Factory with exactly one concrete class.
- Being unable to answer "why not just if/else?" without saying "it's cleaner" (not a force).
- Adding a Singleton in a Spring/DI context.

## Senior Signals to Demonstrate

- Ask clarifying questions about *growth* and *runtime* before choosing.
- Explicitly say "I'll start simple and refactor to Strategy *if/when* a second variant appears."
- Name the tradeoff: "This adds indirection; it's worth it here because…"

---

## Resources

- Refactoring Guru — [When NOT to use patterns](https://refactoring.guru/design-patterns/criticism)
- "Refactoring" (Fowler) — *Replace Conditional with Polymorphism* (the force-driven version)
- YAGNI — Martin Fowler

## Related

- [How to Remember Patterns](remembering-patterns.md)
- [Problem-Solving Framework](../lld-approach/problem-solving-framework.md)
- [Strategy](behavioural/strategy.md)
- [Open/Closed Principle](../solid/open-closed.md)
