# Day 21 — Pattern Selection (Choosing the Right Tool, or None)

## Cheat Sheet

- No new pattern. The skill = pick the **simplest** tool + justify why you didn't climb higher.
- **Every pattern buys flexibility with indirection. No flexibility needed → don't pay.**
- **"We might need it later" is NOT a force.** Add the pattern when the 2nd case actually arrives (YAGNI).
- A pattern with **exactly one implementation** = premature pattern.
- Senior move: name the rung above and below your choice, say why you rejected each.

## The Complexity Ladder (climb only on a real force)

| Rung | Tool | Use when |
|------|------|----------|
| 0 | `if/else` | 2–3 cases, closed, short, one call site |
| 1 | `enum` / `switch` | fixed closed set (weekdays, HTTP methods) |
| 2 | `Map<Key, Handler>` | dispatch by key, small behaviour |
| 3 | Strategy | open set, runtime swap, isolate for tests |
| 4 | Factory (+Strategy) | above + creation logic non-trivial |
| 5 | Config / rules engine | non-devs change rules, no redeploy |

## Forces That Justify Climbing (not "it feels dirty")

| Force | Symptom → Fix |
|-------|---------------|
| **OCP** | new type edits same if/else + other files → Strategy |
| **Duplication** | same `switch(type)` in many methods → polymorphism |
| **Runtime swap** | behaviour changes per request/tenant → Strategy |
| **Testability** | can't test one branch alone → extract injectable unit |
| **Creation complexity** | choosing+building object is messy → Factory |
| **Business rules** | non-devs change rules weekly → config/rules engine |

None present → `if/else` **is** the senior answer.

## Critical Example — "Multiple pricing algorithms"

```java
// Level 0: 2 stable cases → if/else is CORRECT. Strategy = over-engineering.
double price(double base, boolean weekend) { return weekend ? base*1.5 : base; }

// Level 1: fixed known set → enum method
enum Season { REGULAR{double f(){return 1;}}, PEAK{double f(){return 1.8;}};
              abstract double f(); }

// Level 3: open set + runtime swap → Strategy EARNS its indirection
interface PricingStrategy { double price(double base); }
class Checkout {
    private PricingStrategy s;              // swappable
    void setStrategy(PricingStrategy x){ s=x; }  // runtime swap per tenant
}
```

## Decision Framework

| Situation | Answer |
|-----------|--------|
| 2–3 branches, closed, stable | ❌ if/else / enum |
| new variant most sprints | ✅ Strategy |
| swap at runtime | ✅ Strategy / State |
| "might need later" | ❌ YAGNI |
| same switch duplicated | ✅ polymorphism |
| non-devs change rules | ✅ config/rules engine |

## Interview Questions & Answers

**Q: "Multiple pricing algorithms — how do you design this?"**
A: "First I'd ask: how many, do they change at runtime, who picks, how often is a new one added? If it's 2–3 stable cases, if/else or an enum. If new schemes get added often and the active one varies per tenant at runtime, Strategy — for OCP and runtime swap."

**Q: "Why not just use if/else?"**
A: "if/else is correct when the set is closed, small, and stable with one call site. I'd only replace it under a real force: OCP pressure (new types keep editing it), duplication (same switch in many methods), runtime swapping, or testability. Absent those, if/else is the simpler, better answer."

**Q: "When is a pattern over-engineering?"**
A: "When it adds indirection you don't use — Strategy with one impl, Singleton for a Spring bean, Observer for two hardcoded listeners, Builder for a 3-field object. A pattern with a single implementation is usually premature."

**Q: "How do you decide between if/else, enum, and Strategy?"**
A: "Closed + stable + compile-time known → enum (exhaustive, behaviour with data). Open set + runtime swap + added often → Strategy. Tiny + one call site → if/else. I climb the ladder only when a named force appears."

## Clues & Signals

- **Senior signal**: asking about growth/runtime BEFORE naming a pattern.
- **Senior signal**: "I'll start with an enum and refactor to Strategy *when* a second variant appears."
- **Senior signal**: naming the indirection cost out loud and saying it's worth it here.
- **Red flag**: naming a pattern in the first 5 seconds.
- **Red flag**: Strategy/Factory with one concrete class.
- **Red flag**: "it's cleaner" as the reason (not a force).
- **Red flag**: Singleton in a DI/Spring context.

## Trade-offs

| Choice | Pro | Con |
|--------|-----|-----|
| if/else | simplest, readable, local | violates OCP if it grows |
| enum method | exhaustive, behaviour+data together | closed set only, recompile to add |
| Strategy | OCP, runtime swap, testable | indirection, more classes |
| Factory | centralizes creation | another layer if creation is trivial |
| Rules engine | non-devs change rules | heavy infra, harder to debug |

## Quick Links

- **Detailed topic**: [Pattern Selection](../topics/design-patterns/pattern-selection.md)
- **Related**: [How to Remember Patterns](../topics/design-patterns/remembering-patterns.md)
- **Related**: [Strategy](../topics/design-patterns/behavioural/strategy.md)
- **Related**: [Open/Closed Principle](../topics/solid/open-closed.md)
