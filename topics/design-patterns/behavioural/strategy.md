# Strategy Pattern

## Intent

Define a family of interchangeable algorithms behind a common interface. Swap them at runtime without changing the client. New algorithm = new class (OCP).

## Problem It Solves

- Multiple ways to do the same thing (sorting, pricing, routing, compression)
- if-else/switch that grows with each new algorithm
- Need to change behavior at runtime

---

## Structure

```
Strategy (interface)          ← the family of algorithms
├── ConcreteStrategyA         ← one algorithm
├── ConcreteStrategyB         ← another algorithm

Context                       ← HAS-A strategy, delegates to it
```

---

## Example

```java
interface PaymentStrategy {
    PaymentResult pay(BigDecimal amount);
}

class CreditCardStrategy implements PaymentStrategy {
    public PaymentResult pay(BigDecimal amount) { /* credit card */ }
}
class UpiStrategy implements PaymentStrategy {
    public PaymentResult pay(BigDecimal amount) { /* UPI */ }
}

// Context — HAS-A strategy (composition)
class PaymentProcessor {
    private PaymentStrategy strategy;
    PaymentProcessor(PaymentStrategy strategy) { this.strategy = strategy; }
    void setStrategy(PaymentStrategy s) { this.strategy = s; }  // runtime swap

    PaymentResult process(Order order) {
        return strategy.pay(order.getTotal());
    }
}
// Adding PayPal = new class, zero changes to PaymentProcessor.
```

---

## Three Ways to Select the Strategy

```java
// 1. Caller decides
new PaymentProcessor(new CreditCardStrategy());

// 2. Factory decides (based on input)
PaymentStrategy s = PaymentStrategyFactory.create(order.getMethod());

// 3. Context swaps at runtime (setter)
processor.setStrategy(new UpiStrategy());
```

---

## Is Context Mandatory?

No. For simple cases, the client uses the strategy directly:

```java
PaymentStrategy strategy = new CreditCardStrategy();
strategy.pay(amount);  // no context needed
```

Use a Context when there's surrounding logic (validation, logging, orchestration) or you want runtime swapping.

---

## Strategy Composition (Multi-Dimensional Variation)

When multiple dimensions vary independently, a strategy can hold another:

```java
class TieredPricingStrategy implements PricingStrategy {
    private final DiscountStrategy discount;  // strategy inside strategy

    public BigDecimal calculate(Order order) {
        return discount.apply(order.getTotal());
    }
}
```

3 pricing × 3 discount = compose (3+3 classes) instead of 9 combined classes.

---

## Strategy vs Enum-with-Behavior

```java
// Enum — good for 2-4 fixed, simple variations
enum ShippingMethod {
    STANDARD { BigDecimal cost(double w) { return valueOf(w * 5); } },
    EXPRESS  { BigDecimal cost(double w) { return valueOf(w * 15); } };
    abstract BigDecimal cost(double weight);
}
```

| Use Enum | Use Strategy |
|---|---|
| 2-4 fixed variations | 5+ or growing |
| Simple one-liner logic | Complex logic with dependencies |
| All known at compile time | Added by other teams/modules |

---

## Strategy vs Other Patterns

| Pattern | Difference |
|---|---|
| **State** | State transitions itself internally; Strategy chosen externally. State knows next states; strategies are independent. |
| **Factory** | Factory CREATES an object; Strategy EXECUTES. Factory often creates the Strategy. |
| **Template Method** | Template = inheritance, vary STEPS in fixed flow. Strategy = composition, swap WHOLE algorithm. |

### Strategy vs Template Method (Detailed)

| | Template Method | Strategy |
|---|---|---|
| Mechanism | Inheritance | Composition |
| What varies | Individual steps in fixed flow | Whole algorithm |
| Change behavior on existing object? | No (fixed to class) | Yes (swap the field) |

---

## Real-World Strategy

- `Comparator` — sorting algorithm (`list.sort(comparator)`)
- Spring `PasswordEncoder` — BCrypt vs Argon2
- Payment processing, compression, rate limiting, ride pricing

---

## Resources

- [Refactoring Guru — Strategy](https://refactoring.guru/design-patterns/strategy)
- [Baeldung — Strategy](https://www.baeldung.com/java-strategy-pattern)

## Related

- [State](state.md) — same structure, self-transitions vs external selection
- [Factory Method](../creational/factory-method.md) — creates the strategy
- [Template Method](template-method.md) — inheritance vs composition
- [Open/Closed Principle](../../solid/open-closed.md) — Strategy is the classic OCP enabler
