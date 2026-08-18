# Day 05 — Single Responsibility Principle (SRP)

## Cheat Sheet

- **SRP** = "A class should have only one reason to change" = one actor/stakeholder
- **Actor test** = "Who would request a change to this method?" — different actors → split
- **SRP = High Cohesion as a principle** — same idea, different framing
- **Don't over-split** — CRUD repo with 5 methods is ONE responsibility (persistence)
- **Orchestrator should be thin** — just coordinates, no business logic itself
- **Middle Man smell** = you split too far (1-method classes that just delegate)

## Critical Examples

### The actor test (one example to remember)
```java
// ❌ Three actors → three responsibilities
class Employee {
    BigDecimal calculatePay()  { }  // Accounting changes this
    String generateReport()    { }  // Reporting changes this
    void save()                { }  // DBA changes this
}
// Fix: PayCalculator, ReportGenerator, EmployeeRepository
```

### When NOT to split (equally important)
```java
// ✅ This is ONE responsibility — don't split further
class UserRepository {
    User findById(String id) { ... }
    void save(User user) { ... }
    void delete(String id) { ... }
}
// All methods use same field (db), same actor (DBA), same purpose (persistence)
```

## Decision Framework

| Signal | Action |
|---|---|
| Different methods change for different actors | Split along actor boundaries |
| All methods use same fields, same actor | Keep together — already SRP compliant |
| Adding feature risks breaking unrelated code | Split — responsibilities tangled |
| Splitting creates 1-method delegating classes | Don't split — Middle Man smell |
| Class is 500+ lines, vague name | Likely God Class — split |

### Borderline Cases — The Right Answer

> "I'd start together. If different teams start owning different methods, or the methods grow complex, I'd split along actor boundaries. SRP is about knowing WHEN to split, not preemptively splitting everything."

## Interview Questions & Answers

**Q: "What's SRP?"**
A: "A class should have one reason to change — one actor/stakeholder. It's the principle form of high cohesion."

**Q: "How do you identify an SRP violation?"**
A: "I ask 'who would request a change to this method?' If different methods serve different teams, the class has multiple responsibilities."

**Q: "Isn't many small classes bad?"**
A: "Over-splitting is bad — creates Middle Man classes. SRP doesn't mean one method per class. A CRUD repo with 5 methods is one responsibility. I only split when I see DIFFERENT REASONS to change."

**Q: "SRP vs cohesion?"**
A: "Same goal. Cohesion = all elements serve one purpose (metric). SRP = one reason to change (principle). High cohesion = SRP satisfied."

## Clues & Signals

- **Senior signal**: "This is borderline — I'd keep it together until the team grows and different people own different parts"
- **Senior signal**: Distinguishing between thin orchestrator (good) vs God class (bad)
- **Red flag**: Splitting a CRUD repo into FindService + SaveService + DeleteService
- **Red flag**: Class named "Manager" or "Helper" with 20 methods
- **Key learning**: State pattern ≠ just changing a status field. Only use when BEHAVIOR differs per state.

## Trade-offs

| Keep Together | Split |
|---|---|
| All methods share fields + actor | Different actors own different methods |
| Simple CRUD, no business logic | Complex logic per method |
| Splitting creates Middle Man | Each split class has real responsibility |
| Small team, one person owns all | Large team, different owners |

## Quick Links

- **Detailed topic**: [Single Responsibility Principle](../topics/solid/single-responsibility.md)
- **Related**: [Cohesion and Coupling](../topics/oops/cohesion-and-coupling.md)
- **Related**: [Problem-Solving Framework](../topics/lld-approach/problem-solving-framework.md)
