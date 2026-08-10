# State Pattern

## Intent
Allow an object to alter its behavior when its internal state changes. The object will appear to change its class.

## Problem It Solves
- Object has different behavior depending on its current state
- Large if-else/switch blocks checking state before every action
- State transitions are complex and error-prone

## Java Example

```java
// State interface
public interface OrderState {
    void next(OrderContext context);
    void prev(OrderContext context);
    void cancel(OrderContext context);
    String getStatus();
}

// Concrete states
public class PlacedState implements OrderState {
    public void next(OrderContext ctx) { ctx.setState(new PaidState()); }
    public void prev(OrderContext ctx) { throw new IllegalStateException("Can't go back from PLACED"); }
    public void cancel(OrderContext ctx) { ctx.setState(new CancelledState()); }
    public String getStatus() { return "PLACED"; }
}

public class PaidState implements OrderState {
    public void next(OrderContext ctx) { ctx.setState(new ShippedState()); }
    public void prev(OrderContext ctx) { ctx.setState(new PlacedState()); } // refund
    public void cancel(OrderContext ctx) { ctx.setState(new CancelledState()); }
    public String getStatus() { return "PAID"; }
}

// Context
public class OrderContext {
    private OrderState state;

    public OrderContext() { this.state = new PlacedState(); }
    public void setState(OrderState state) { this.state = state; }
    public void next() { state.next(this); }
    public void cancel() { state.cancel(this); }
    public String getStatus() { return state.getStatus(); }
}
```

## When to Use
- Entity with lifecycle (Order, Payment, Ticket, Elevator)
- State-dependent behavior (Vending Machine)
- Complex state transitions that must be validated

## Trade-offs

| Pros | Cons |
|------|------|
| Eliminates state-checking conditionals | Many classes (one per state) |
| Each state is self-contained | States may be tightly coupled to context |
| Adding new state = adding a class | Overhead for few states |
| Invalid transitions are compile-safe | |

## State vs Strategy

| State | Strategy |
|-------|----------|
| Object changes its own state (internal) | Client selects the algorithm (external) |
| States know about each other (transitions) | Strategies are independent |
| Replaces behavior implicitly | Replaces behavior explicitly |

## Resources
- [Refactoring Guru — State](https://refactoring.guru/design-patterns/state)
