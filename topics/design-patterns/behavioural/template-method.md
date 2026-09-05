# Template Method Pattern

## Intent

Define the skeleton of an algorithm in a base class, letting subclasses override specific steps without changing the algorithm's structure.

## Problem It Solves

- Multiple classes share the SAME flow but differ in individual steps
- Avoid duplicating the overall algorithm structure
- Control the order of operations while allowing step customization

---

## Structure

```java
abstract class DataProcessor {
    // TEMPLATE METHOD — final, defines the fixed flow
    public final void process() {
        openFile();
        List<Row> data = parse();   // varies — abstract
        validate(data);             // shared
        save(data);                 // shared
        closeFile();
    }

    protected abstract List<Row> parse();  // subclass MUST implement

    private void openFile() { }    // shared steps
    private void validate(List<Row> d) { }
    private void save(List<Row> d) { }

    protected void beforeSave() { }  // HOOK — optional override
}

class CsvProcessor extends DataProcessor {
    protected List<Row> parse() { /* CSV */ }  // fills the varying step
}
```

---

## Key Characteristics

| Aspect | Description |
|---|---|
| Fixed skeleton | Base class owns the flow (order of steps) |
| `final` template method | Subclasses can't change the flow, only steps |
| Abstract steps | Subclass MUST implement (forced variation) |
| Hooks | Subclass MAY override (optional variation) |
| Inheritance-based | Uses `extends` |

---

## Hooks vs Abstract Steps

| Abstract Method | Hook |
|---|---|
| Subclass MUST implement | Subclass MAY override |
| No default | Has a default (empty or a value) |
| Forces variation | Allows optional variation |

```java
protected abstract void charge(int amount);  // MUST implement
protected void detectFraud() { }             // hook — MAY override (default: nothing)
protected boolean wantsCondiments() { return true; }  // hook controlling optional step
```

---

## Template Method vs Strategy

| Aspect | Template Method | Strategy |
|---|---|---|
| Mechanism | Inheritance (`extends`) | Composition (HAS-A) |
| Varying behavior lives | INSIDE the object (overridden method) | In a SEPARATE object (injected) |
| Relationship | IS-A | HAS-A |
| What varies | Individual STEPS in a fixed flow | The WHOLE algorithm |
| Swap at runtime? | No (fixed to subclass) | Yes (change the reference) |

**Key**: both use interfaces. The difference is WHERE the varying behavior lives — inherited (inside) vs composed (separate injected object).

---

## Combining With Other Patterns

Template Method often combines with:
- **Adapter** — a step delegates to a wrapped third-party SDK
- **Strategy** — a step could itself be a strategy (varying dimension)
- **Factory Method** — a step creates an object (the factory method IS a template step)

Example: Payment pipeline — Template Method for the flow (validate → charge → receipt), Adapter for the third-party provider, Hook for optional fraud detection.

---

## Real-World Template Method

| Where | Fixed Flow | Varying Step |
|---|---|---|
| Spring `JdbcTemplate` | connect → execute → map → close | query + row mapping |
| `HttpServlet.service()` | dispatch by HTTP method | doGet(), doPost() |
| JUnit lifecycle | setup → test → teardown | @Test body |
| Build pipeline | compile → test → package | project config |

---

## Resources

- [Refactoring Guru — Template Method](https://refactoring.guru/design-patterns/template-method)

## Related

- [Strategy](strategy.md) — composition vs inheritance
- [Factory Method](../creational/factory-method.md) — often a step in a template
