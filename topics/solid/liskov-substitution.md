# Liskov Substitution Principle (LSP)

## Definition

> "Subtypes must be substitutable for their base types without altering the correctness of the program."

**Plain English**: If caller code works with a parent type, it must work EXACTLY the same with ANY child type. No surprises.

---

## WHY It Matters

Without LSP:
- Callers need `instanceof` checks (defeats polymorphism)
- OCP fails — new subtypes break existing code instead of extending safely
- You can't trust the interface contract

---

## The One-Sentence Test

> **"If your child class makes the caller add a special `if` check, you've violated LSP."**

---

## How to Detect LSP Violations

| Signal | Example |
|---|---|
| Child throws `UnsupportedOperationException` | `Penguin.fly()` throws |
| Caller needs `instanceof` to handle a specific child | `if (bird instanceof Penguin)` |
| Child changes the meaning of a parent method | `Square.setWidth()` also sets height |
| Child strengthens preconditions (rejects valid input) | Parent accepts 0, child only accepts > 10 |
| Child weakens postconditions (returns unexpected result) | Parent guarantees sorted, child returns unsorted |

---

## Classic Violations and Fixes

### Rectangle/Square

```java
// ❌ Square breaks Rectangle's contract — setWidth/setHeight should be independent
class Square extends Rectangle {
    void setWidth(int w) { this.width = w; this.height = w; }  // changes both!
}

void testRectangle(Rectangle r) {
    r.setWidth(5); r.setHeight(3);
    assert r.area() == 15;  // FAILS with Square — returns 9
}
```

**Fix**: Don't make Square extend Rectangle. Use `Shape` interface with `area()`.

### Bird/Penguin

```java
// ❌ Penguin can't fulfill Bird's promise to fly
class Penguin extends Bird {
    void fly() { throw new UnsupportedOperationException(); }
}
```

**Fix**: Separate capabilities into interfaces:
```java
interface Bird { void eat(); }
interface Flyable { void fly(); }
class Sparrow implements Bird, Flyable { }
class Penguin implements Bird { }  // doesn't promise to fly
```

### Collection/ImmutableList

```java
// ❌ ImmutableList can't fulfill Collection's promise to add
class ImmutableList implements Collection {
    void add(Object o) { throw new UnsupportedOperationException(); }
}
```

**Fix**: Separate ReadableCollection from MutableCollection.

---

## The Pattern of LSP Violations

Every violation has the same root cause:

> **The parent interface promised too much. A child can't keep all the promises.**

The fix is always: **narrow the interface** — only promise what ALL implementations can deliver. (This leads to Interface Segregation Principle.)

---

## LSP Rules (What Subtypes Must NOT Do)

| Rule | Meaning |
|---|---|
| Don't strengthen preconditions | If parent accepts any positive number, child can't reject < 10 |
| Don't weaken postconditions | If parent guarantees non-null return, child can't return null |
| Don't throw unexpected exceptions | If parent never throws, child can't throw |
| Don't change semantics | If `setWidth` sets width only, child can't also set height |
| Honor invariants | If parent is immutable, child must be too |

---

## How OCP and LSP Work Together

```
OCP: "Extend via new classes" → gives you the mechanism
LSP: "New class must honor the contract" → ensures the mechanism works

OCP without LSP = you add subtypes that break callers
LSP without OCP = subtypes are correct but you still modify code
Together = new subtype works seamlessly with existing code
```

---

## LSP vs Other Principles

| Principle | Question |
|---|---|
| SRP | "Does this class do too much?" |
| OCP | "Can I add new types without modifying existing code?" |
| **LSP** | "When I add a new type, does it ACTUALLY work with existing caller code?" |

---

## Resources

- [Baeldung — Liskov Substitution](https://www.baeldung.com/java-liskov-substitution-principle)
- [Effective Java — Item 18 (Composition over Inheritance)](https://www.oreilly.com/library/view/effective-java/9780134686097/)

## Related

- [Open/Closed Principle](open-closed.md) — LSP ensures OCP extensions are valid
- [Interface Segregation](interface-segregation.md) — fix for LSP violations (narrow the interface)
- [Composition vs Inheritance](../oops/composition-vs-inheritance.md) — composition avoids many LSP issues
