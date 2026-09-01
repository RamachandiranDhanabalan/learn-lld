# Day 15 — Strategy Pattern

## Cheat Sheet

- **Strategy** = Family of interchangeable algorithms behind a common interface. Swap at runtime.
- **Context HAS-A Strategy** (composition) — this is the core relationship. Context is optional though.
- **3 ways to select** = Caller injects | Factory creates | Context swaps via setter.
- **vs State** = Strategy chosen externally. State transitions itself internally.
- **vs Template Method** = Strategy swaps WHOLE algorithm (composition). Template varies STEPS (inheritance).
- **vs Factory** = Factory CREATES. Strategy EXECUTES. Factory often creates the Strategy.
- **Enum vs Strategy** = Enum for 2-4 simple fixed. Strategy for 5+, complex, or dependency-needing.
- **Strategy composition** = strategy holding another strategy (multi-dimensional variation).

## Critical Example

```java
interface PaymentStrategy { PaymentResult pay(BigDecimal amount); }
class CreditCardStrategy implements PaymentStrategy { ... }
class UpiStrategy implements PaymentStrategy { ... }

class PaymentProcessor {                        // Context
    private PaymentStrategy strategy;
    PaymentProcessor(PaymentStrategy s) { this.strategy = s; }
    void setStrategy(PaymentStrategy s) { this.strategy = s; }  // runtime swap
    PaymentResult process(Order o) { return strategy.pay(o.getTotal()); }
}
// Add PayPal = new class, zero changes. OCP.
```

## Decision Framework

| Question | → |
|---|---|
| "Multiple algorithms for same task?" | Strategy |
| "Chosen externally by client?" | Strategy (not State) |
| "Object transitions through it itself?" | State (not Strategy) |
| "2-4 simple fixed variations?" | Enum with abstract methods |
| "5+, complex, or needs dependencies?" | Strategy |
| "Multiple dimensions vary independently?" | Compose strategies (strategy inside strategy) |

## Interview Questions & Answers

**Q: "What's Strategy?"**
A: "A family of interchangeable algorithms behind a common interface. Context uses one via composition, swappable at runtime. New algorithm = new class = OCP."

**Q: "Strategy vs State?"**
A: "Same structure. Strategy is chosen externally by the client. State transitions itself internally based on events. Strategies are independent; states know their valid transitions."

**Q: "When enum vs Strategy?"**
A: "Enum with abstract methods for 2-4 simple fixed variations. Strategy for complex logic, 5+ variations, or when strategies need injected dependencies."

**Q: "Strategy vs Factory?"**
A: "Factory creates an object. Strategy executes behavior. They compose — factory creates the right strategy, context executes it."

**Q: "Is Context mandatory?"**
A: "No. Client can use strategy directly for simple cases. Context adds value when there's surrounding logic or runtime swapping."

## Clues & Signals

- **Senior signal**: "I'd compose strategies here — pricing and discount vary independently, so 3+3 classes not 9"
- **Senior signal**: "Enum is enough for these 3 fixed types — Strategy would be over-engineering"
- **Red flag**: if-else on type that grows → should be Strategy
- **Key learning (design-from-scratch)**: Entities hide in verbs. "What flows through the system?" catches Request, Event, Command.
- **Key learning**: Entity (noun, has state) vs Service (verb-doer, orchestrates). Dispatcher is a service, not an entity.

## Design-From-Scratch Practice (Elevator System)

Applied full LLD flow instead of smell-hunting:
- **Clarifying questions**: direction changes? weight? all floors? algorithm? priority?
- **Entities**: Elevator, Request (hidden in "button press"), Floor
- **The heart**: request dispatching/scheduling algorithm
- **Strategy**: ElevatorRoutingStrategy (Scan, Nearest)
- **State**: elevator states (IDLE, MOVING, DOORS_OPEN, MAINTENANCE) — State pattern (Day 17)
- **Service**: ElevatorDispatcher (the heart), ElevatorService (holds/filters elevators)

Key lesson: Request entity hides in the verb "press button" — use "what flows through the system?" to catch it.

## Quick Links

- **Detailed topic**: [Strategy Pattern](../topics/design-patterns/behavioural/strategy.md)
- **Related**: [State](../topics/design-patterns/behavioural/state.md) — Day 17
- **Related**: [Factory Method](../topics/design-patterns/creational/factory-method.md)
- **Framework**: [Entity Discovery + Problem-Solving](../topics/lld-approach/problem-solving-framework.md)
