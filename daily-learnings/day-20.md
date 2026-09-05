# Day 20 — Template Method + Iterator

## Cheat Sheet

- **Template Method** = Base class defines fixed flow (final method); subclasses fill in varying steps.
- **Hook** = optional step with a default (empty or value); subclass MAY override.
- **Abstract step** = subclass MUST implement. **Hook** = subclass MAY override.
- **Template vs Strategy** = Template: behavior INSIDE object (inheritance, override). Strategy: behavior in SEPARATE object (composition, injected).
- **Iterator** = traverse without exposing internal structure. Java's Iterable/Iterator built-in.
- **Make template method `final`** so subclasses can't change the flow order.

## Critical Examples

### Template Method + Hook
```java
abstract class BeverageMaker {
    public final void make() {         // final — flow can't change
        boilWater();                    // shared
        brew();                         // abstract — MUST implement
        pourInCup();                    // shared
        if (wantsCondiments()) addCondiments();  // hook controls optional step
    }
    protected abstract void brew();     // forced variation
    protected boolean wantsCondiments() { return true; }  // HOOK — optional
}
class BlackCoffee extends Coffee {
    protected boolean wantsCondiments() { return false; }  // override hook to skip
}
```

### Iterator (usually just implement Iterable)
```java
class MyCollection implements Iterable<T> {
    public Iterator<T> iterator() {
        return new Iterator<>() {
            public boolean hasNext() { ... }
            public T next() { ... }
        };
    }
}
// Client: for (T item : myCollection) { ... }  — structure hidden
```

## Decision Framework

| Signal | Pattern |
|---|---|
| "Same flow, different steps" | Template Method |
| "Fixed order of operations, customize steps" | Template Method |
| "Some steps optional" | Hook (within Template Method) |
| "Swap the WHOLE algorithm at runtime" | Strategy (not Template) |
| "Traverse a collection uniformly" | Iterator (implement Iterable) |

### Template vs Strategy

| | Template Method | Strategy |
|---|---|---|
| Behavior lives | Inside object (override) | Separate object (injected) |
| Relationship | IS-A (extends) | HAS-A (composition) |
| Varies | Steps in fixed flow | Whole algorithm |
| Runtime swap | No | Yes |

## Interview Questions & Answers

**Q: "What's Template Method?"**
A: "Base class defines the algorithm skeleton in a final method; subclasses override specific steps. Flow is fixed, only steps vary. Inheritance-based."

**Q: "Template Method vs Strategy?"**
A: "Both use interfaces. Template: varying behavior lives INSIDE the object via inheritance/override. Strategy: varying behavior is a SEPARATE object held via composition. Template varies steps; Strategy swaps the whole algorithm."

**Q: "What's a hook?"**
A: "An optional step with a default implementation. Subclass MAY override to inject behavior at a point, but isn't forced to (unlike abstract methods)."

**Q: "Why final template method?"**
A: "So subclasses can't change the flow/order — only fill in the steps."

**Q: "What's Iterator?"**
A: "Traverse a collection without exposing internal structure. Java's Iterable/Iterator — for-each works on any collection regardless of array/list/tree internally."

## Clues & Signals

- **Senior signal**: "Fraud detection is a hook — default does nothing, only credit cards override it"
- **Senior signal**: Combining Template Method (flow) + Adapter (third-party step) + Hook (optional step)
- **Senior signal**: Separating two independent dimensions (payment method via Template subclass, provider via Adapter)
- **Red flag**: Using Template Method when you need runtime algorithm swapping (that's Strategy)
- **Key learning**: Template vs Strategy — where the varying behavior lives (inherited-inside vs composed-separate).

## Design Practice: Payment Pipeline (Multi-Pattern)

Combined 3 patterns from pressure:
- **Template Method**: process() fixed flow (validate → detectFraud → charge → record → receipt)
- **Adapter**: PaymentAdapter wraps Stripe/Razorpay third-party SDKs
- **Hook**: detectFraud() optional — only some methods override
- **DIP**: adapter injected into payment process
- **Two dimensions**: payment method (Template subclass) × provider (Adapter) — combine freely

## Quick Links

- **Detailed topic**: [Template Method](../topics/design-patterns/behavioural/template-method.md)
- **Detailed topic**: [Iterator](../topics/design-patterns/behavioural/iterator.md)
- **Related**: [Strategy](../topics/design-patterns/behavioural/strategy.md)
- **How to remember patterns**: [remembering-patterns.md](../topics/design-patterns/remembering-patterns.md)
