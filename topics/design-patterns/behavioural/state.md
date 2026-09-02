# State Pattern

## Intent

Allow an object to alter its behavior when its internal state changes. Each state is a class; states manage their own transitions. Eliminates nested if-else on a status field.

## Problem It Solves

- Object behaves differently per state, with transition rules
- Nested `if (status == X)` scattered across many methods
- Invalid transitions must be prevented (can't ship an unpaid order)

---

## The Smell (Without State)

```java
// ❌ Every method has if-else on status
class Order {
    private String status;
    void pay() {
        if (status.equals("CREATED")) status = "PAID";
        else if (status.equals("PAID")) throw new Exception("Already paid");
        // ... every status checked in EVERY method
    }
    void ship() { /* another if-else ladder on status */ }
}
```

---

## The Solution

```java
// STATE interface
interface OrderState {
    void pay(OrderContext ctx);
    void ship(OrderContext ctx);
    void cancel(OrderContext ctx);
    String getName();
}

// CONCRETE STATES — each knows its behavior + valid transitions
class CreatedState implements OrderState {
    public void pay(OrderContext ctx) { ctx.setState(new PaidState()); }
    public void ship(OrderContext ctx) { throw new IllegalStateException("Pay first"); }
    public void cancel(OrderContext ctx) { ctx.setState(new CancelledState()); }
    public String getName() { return "CREATED"; }
}

class PaidState implements OrderState {
    public void pay(OrderContext ctx) { throw new IllegalStateException("Already paid"); }
    public void ship(OrderContext ctx) { ctx.setState(new ShippedState()); }
    public void cancel(OrderContext ctx) { ctx.setState(new CancelledState()); }  // refund
    public String getName() { return "PAID"; }
}

// CONTEXT — delegates to current state
class OrderContext {
    private OrderState state = new CreatedState();
    void setState(OrderState s) { this.state = s; }
    void pay() { state.pay(this); }
    void ship() { state.ship(this); }
    String getStatus() { return state.getName(); }
}
```

---

## State vs Strategy (Critical Distinction)

Identical structure. Different behavior:

| Aspect | State | Strategy |
|---|---|---|
| Who changes it? | State transitions ITSELF | Client selects externally |
| Awareness | States know other states (transitions) | Strategies are independent |
| Lifecycle | Object moves through states over time | Algorithm chosen for a task |

**Test**: "Does the object move through these over its lifetime, each state deciding the next?" → State. "Does the client pick one for a task?" → Strategy.

---

## State: What Belongs Where

| Concern | Belongs In |
|---|---|
| What actions are valid in this state | State |
| Next state transition | State |
| Duration/config data (`getDuration()`) | State (it's state-specific data) |
| Actual timing/sleep/scheduling | Controller/orchestrator (NOT state) |

> State owns **WHAT** (behavior, next state, duration). Controller owns **WHEN** (timer, sleep).

A state should react to ONE event and transition ONCE — not run a multi-step script.

---

## Orchestration — When Needed

| System | Who Drives Transitions? | Needs Orchestrator? |
|---|---|---|
| Vending Machine | User actions (external events) | ❌ No — context + states, client triggers |
| Traffic Light | Timer (automatic) | ✅ Yes — controller with timer loop |
| Order | User/system events (pay, ship) | Partial — service triggers |
| Elevator | Requests + movement timer | ✅ Yes — dispatcher + step loop |

**Rule**: Automatic/timed advancing → needs controller. User-driven events → context + states is enough.

---

## Transitions: Decentralized vs Centralized

| Decentralized (state sets next) | Centralized (transition table) |
|---|---|
| Each state self-contained | All transitions in one place |
| Classic GoF State | State machine / table-driven |
| Adding state = new class | Adding state = update table |

---

## When to Use

| Signal | Example |
|---|---|
| Object has a lifecycle with distinct states | Order, Payment, Elevator |
| Behavior differs significantly per state | Vending machine |
| Complex transition rules | Can't go MAINTENANCE → MOVING directly |
| Nested if-else on status in many methods | The smell |

## When NOT to Use

| Signal | Use Instead |
|---|---|
| 2 states, simple toggle | Boolean flag |
| State is just data (no behavior difference) | Enum field |
| No transitions | Enum field |

---

## Real-World State

| System | States |
|---|---|
| Order | CREATED → PAID → SHIPPED → DELIVERED / CANCELLED |
| Elevator | IDLE → MOVING → DOORS_OPEN / MAINTENANCE |
| Vending Machine | IDLE → COIN_INSERTED → DISPENSING |
| TCP | CLOSED → LISTEN → ESTABLISHED → CLOSING |

---

## Resources

- [Refactoring Guru — State](https://refactoring.guru/design-patterns/state)
- [Baeldung — State](https://www.baeldung.com/java-state-design-pattern)

## Related

- [Strategy](strategy.md) — same structure, external selection vs internal transition
- [Problem-Solving Framework — pressure ⑤](../../lld-approach/problem-solving-framework.md)
