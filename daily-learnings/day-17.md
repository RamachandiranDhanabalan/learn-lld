# Day 17 — State Pattern

## Cheat Sheet

- **State** = Object's behavior changes by internal state. Each state = a class. States manage own transitions.
- **Eliminates** nested `if (status == X)` scattered across methods.
- **State vs Strategy** = SAME structure. State transitions itself (internal). Strategy chosen externally (client).
- **State owns WHAT** (valid actions, next state, duration data). **Controller owns WHEN** (timer, sleep).
- **A state reacts to ONE event, transitions ONCE** — don't run multi-step scripts inside a state.
- **Orchestration needed?** Automatic/timed advancing (traffic light) → controller. User-driven (vending) → context + states, client triggers.
- **Transitions**: decentralized (state sets next, GoF) OR centralized (transition table).

## Critical Example

```java
interface OrderState {
    void pay(OrderContext ctx);
    void ship(OrderContext ctx);
}
class CreatedState implements OrderState {
    public void pay(OrderContext ctx) { ctx.setState(new PaidState()); }  // self-transition
    public void ship(OrderContext ctx) { throw new IllegalStateException("Pay first"); }
}
class OrderContext {
    private OrderState state = new CreatedState();
    void setState(OrderState s) { this.state = s; }
    void pay() { state.pay(this); }  // delegate to current state
}
```

## Decision Framework

| Signal | → |
|---|---|
| "Object has lifecycle with states + transition rules" | State |
| "Nested if-else on status field in many methods" | State (the smell) |
| "Object transitions itself over time" | State (not Strategy) |
| "Client picks the algorithm" | Strategy (not State) |
| "Only 2 states, simple toggle" | Boolean flag (not State) |
| "Automatic timed advancing?" | Add a controller/orchestrator |
| "User-driven events?" | Context + states, client triggers directly |

### What Belongs Where

| Concern | State | Controller |
|---|---|---|
| Valid actions per state | ✅ | |
| Next transition | ✅ | |
| Duration data (getDuration) | ✅ | |
| Actual sleep/timer/scheduling | | ✅ |

## Interview Questions & Answers

**Q: "What's State pattern?"**
A: "Object behaves differently by internal state. Each state is a class defining behavior for all actions and managing its own transitions. Eliminates nested if-else on a status field."

**Q: "State vs Strategy?"**
A: "Identical structure. State transitions itself internally and states know each other. Strategy is chosen externally and strategies are independent. State = lifecycle, Strategy = algorithm choice."

**Q: "When State?"**
A: "Lifecycle with distinct states, behavior differs per state, transition rules exist. The smell: nested if-else on a status field across methods."

**Q: "Where does timing/waiting go?"**
A: "State owns the duration (data via getDuration). Controller owns the actual timer/sleep. Keeps states testable — no real sleeps inside states."

**Q: "State pattern downside?"**
A: "Many classes (one per state). States can couple to each other for transitions. Overkill for simple 2-state toggles."

## Clues & Signals

- **Senior signal**: "State owns duration as data; the controller does the actual waiting — keeps states testable"
- **Senior signal**: "This is user-driven, so no orchestrator needed — the client triggers each transition"
- **Senior signal**: "Traffic light needs a controller because it advances on a timer; vending machine doesn't because the user drives it"
- **Red flag**: A state that runs a multi-step script (sleep + transition + sleep + transition)
- **Red flag**: State pattern for a simple boolean toggle
- **Key learning**: A state handles ONE transition per event. Multi-step cycles belong in the controller.

## Design Practice: Traffic Signal + Vending Machine

- **Vending Machine** (user-driven): IDLE → COIN_INSERTED → DISPENSING. No orchestrator — client triggers each action.
- **Traffic Light** (timer-driven): GREEN → YELLOW → RED, rotating directions. Needs a TrafficController with timer loop. States expose getDuration(); controller does the waiting.

Key lesson: Automatic advancing needs a controller. User-driven needs only context + states.

## Quick Links

- **Detailed topic**: [State Pattern](../topics/design-patterns/behavioural/state.md)
- **Related**: [Strategy](../topics/design-patterns/behavioural/strategy.md)
- **Framework**: [pressure ⑤](../topics/lld-approach/problem-solving-framework.md)
