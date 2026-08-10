# Strategy Pattern

## Intent
Define a family of algorithms, encapsulate each one, and make them interchangeable. Let the algorithm vary independently from the clients that use it.

## Problem It Solves
- Multiple ways to do the same thing (different sorting, pricing, routing algorithms)
- Avoiding large if-else/switch blocks
- Need to swap behavior at runtime

## Java Example

```java
// Strategy interface
public interface PricingStrategy {
    BigDecimal calculatePrice(Order order);
}

// Concrete strategies
public class RegularPricing implements PricingStrategy {
    public BigDecimal calculatePrice(Order order) {
        return order.getBasePrice();
    }
}

public class PremiumPricing implements PricingStrategy {
    public BigDecimal calculatePrice(Order order) {
        return order.getBasePrice().multiply(BigDecimal.valueOf(0.85)); // 15% off
    }
}

public class HappyHourPricing implements PricingStrategy {
    public BigDecimal calculatePrice(Order order) {
        return order.getBasePrice().multiply(BigDecimal.valueOf(0.50)); // 50% off
    }
}

// Context
public class PricingService {
    private PricingStrategy strategy;

    public void setStrategy(PricingStrategy strategy) { this.strategy = strategy; }

    public BigDecimal getPrice(Order order) {
        return strategy.calculatePrice(order);
    }
}
```

## When to Use
- "If we need a new algorithm, just add a class" scenarios
- Rate limiter algorithms (Token Bucket, Sliding Window)
- Payment processing strategies
- Ride pricing (surge, flat, distance-based)

## Trade-offs

| Pros | Cons |
|------|------|
| Open/Closed — add without modifying | Client must know which strategy to choose |
| Eliminates conditional logic | Extra classes for simple variations |
| Easy to test each strategy in isolation | |
| Strategies are reusable | |

## vs Enum-based Approach

For 2–3 simple variations, an enum with abstract methods may be simpler.
For 5+ complex variations, Strategy wins on extensibility and testability.

## Resources
- [Refactoring Guru — Strategy](https://refactoring.guru/design-patterns/strategy)
