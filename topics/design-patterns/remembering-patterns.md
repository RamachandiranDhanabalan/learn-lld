# How to Remember Patterns (Without Memorizing Diagrams)

## The Core Idea

Don't memorize class diagrams. Memorize **the trigger** (when to use it). Then **derive the structure from the purpose** — every field and method exists because the purpose physically requires it.

---

## Two Things to Memorize Per Pattern

```
1. TRIGGER — what pressure signals this pattern?
2. PURPOSE — one sentence
```

The structure regenerates itself from the purpose.

---

## The Trigger Table (Memorize This, Not Diagrams)

| Pattern | Trigger (the pressure) |
|---|---|
| Factory | "Which object to create depends on input" |
| Builder | "Too many optional params" |
| Singleton | "Exactly one instance needed" |
| Adapter | "Incompatible third-party interface" |
| Decorator | "Stack behaviors freely" |
| Proxy | "Control access — cache/auth/lazy" |
| Facade | "Simplify many subsystems" |
| Composite | "Tree — treat leaf & group the same" |
| Strategy | "Swap algorithm at runtime" |
| Observer | "Notify many when one changes" |
| State | "Behavior changes by lifecycle state" |
| Command | "Undo / queue / schedule an action" |
| Chain of Responsibility | "Chain of handlers, each may handle or pass" |
| Template Method | "Same flow, different steps" |

---

## Derive the Structure From the Purpose

Every structural detail answers: **"What does the PURPOSE physically require?"**

| Pattern | Purpose | Therefore it MUST... |
|---|---|---|
| CoR | Pass to the next handler | Hold a `next` reference (same type) |
| Strategy | Swap at runtime | Have a `setStrategy()` (only if runtime swap needed) |
| Observer | Listeners come and go | Have `subscribe()`/`unsubscribe()` + a listener list |
| Decorator | Wrap + delegate | Hold the wrapped object (same interface, to stack) |
| State | Transition to next state | Context has `setState()`; states create the next |
| Command | Undo later | Store the data needed to reverse (capture in execute) |
| Factory | Return the right type | A method with the type-selection logic |
| Builder | Optional params + immutable | Fluent setters returning `this` + `build()` |
| Composite | Recurse over tree | Composite holds `List<Component>`, iterates children |

---

## The Reasoning Method (Regenerate, Don't Recall)

When writing a pattern, narrate WHY each field/method exists:

```java
class ChainHandler {
    private ChainHandler next;  // WHY: purpose is "pass to next" → must hold it

    void handle(Request r) {
        if (canHandle(r)) process(r);        // WHY: I might handle it
        else if (next != null) next.handle(r);  // WHY: I might pass it on
    }
}
```

```java
class PaymentContext {
    private PaymentStrategy strategy;         // WHY: holds the swappable algorithm
    void setStrategy(PaymentStrategy s) { }   // WHY: purpose is runtime swap → need setter
}
```

```java
class Subject {
    private List<Observer> observers;         // WHY: must track who's listening
    void subscribe(Observer o) { }            // WHY: listeners join
    void unsubscribe(Observer o) { }          // WHY: listeners leave
    void notifyAll() { }                      // WHY: purpose is "notify many"
}
```

If you can explain WHY each line exists (traced from purpose), you never memorize structure — you regenerate it every time.

---

## Most Patterns Share ONE Skeleton

```
Strategy, State, Observer, CoR, Command, Adapter, Decorator, Proxy
   → ALL are: interface + concrete implementations + a holder/context
```

You're memorizing ~14 TRIGGERS, not 14 diagrams. The skeleton is the same; the intent differs.

---

## The Habit That Builds This

For every pattern you write in practice, add a WHY comment on each field/method:

```java
private Strategy strategy;    // WHY: holds swappable algorithm
void setStrategy(Strategy s)  // WHY: runtime swap needed
```

After ~10 problems, the "why" becomes automatic. Then you derive structure from purpose every time — no memorization.

---

## In an Interview

You won't draw UML from memory. You will:
1. Recognize the pressure ("behavior varies by type")
2. Name the pattern ("Strategy")
3. Write interface + implementations + context (the skeleton you know from practice)
4. Explain WHY each part exists (from purpose)

That's how seniors work — reason from intent, not recall diagrams.

---

## Related

- [Problem-Solving Framework](../lld-approach/problem-solving-framework.md)
- [Design Patterns Overview](README.md)
