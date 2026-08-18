# Single Responsibility Principle (SRP)

## Definition

**Version 1**: "A class should have only one reason to change."

**Version 2 (Uncle Bob's clarification)**: "A class should have only one actor/stakeholder it serves."

Version 2 is more useful — it means: one class should only change because of ONE team/person's requests.

---

## WHY It Matters

| With SRP | Without SRP |
|---|---|
| Change one thing → one file modified | Change one thing → 5 files break |
| Teams work in parallel (different classes) | Merge conflicts (same file) |
| Easy to test in isolation | Must test everything together |
| Easy to name (one purpose) | Vague names ("Manager", "Helper") |
| Safe to modify (isolated risk) | Change accounting logic → risk breaking email logic |

---

## The "Actor" Test

For each method, ask: **"Who would request a change to this?"**

```java
// ❌ THREE actors → THREE responsibilities → SPLIT
class Employee {
    BigDecimal calculatePay()    { ... }  // Accounting team changes this
    String generateReport()      { ... }  // Reporting team changes this
    void save()                  { ... }  // DBA changes this
}

// ✅ One actor per class
class PayCalculator {
    BigDecimal calculatePay(Employee emp) { ... }  // only accounting
}
class EmployeeReportGenerator {
    String generate(Employee emp) { ... }  // only reporting
}
class EmployeeRepository {
    void save(Employee emp) { ... }  // only DBA
}
```

---

## How to Identify SRP Violations

| Signal | What It Means |
|---|---|
| "Changes when X **OR** when Y changes" | Two responsibilities |
| Methods cluster into groups that don't share fields | Two classes living in one |
| Class name needs "And" to describe | Multiple purposes |
| Multiple stakeholders request changes to same class | Multiple actors |
| Adding a feature risks breaking unrelated things | Tangled responsibilities |
| Class has 500+ lines | Likely God Class |

---

## SRP Is Cohesion as a Principle

| Concept | What It Is | Framing |
|---|---|---|
| High Cohesion | All methods/fields serve one purpose | Metric (measurable) |
| SRP | One reason to change | Principle (rule) |

Same idea, different framing. If cohesion is high, SRP is satisfied.

---

## Levels of SRP

| Level | Means | Example |
|---|---|---|
| Method | One method does one thing | `calculateTotal()` doesn't also send emails |
| Class | One class has one responsibility | `OrderService` doesn't also generate reports |
| Module/Package | One module serves one domain | `com.payment` doesn't contain notification logic |
| Microservice | One service owns one bounded context | Payment service doesn't own user profiles |

---

## When NOT to Split (Over-Engineering)

| Don't Split When... | Why |
|---|---|
| All methods use the same fields | Already cohesive — one responsibility |
| Only one stakeholder ever changes it | One reason to change = SRP satisfied |
| Splitting creates 1-method classes that just delegate | Middle Man smell |
| It's simple CRUD (no business logic) | Repository with find/save/delete = ONE responsibility: persistence |

```java
// ✅ This does NOT violate SRP — all methods serve "user persistence"
class UserRepository {
    User findById(String id) { ... }
    List<User> findByRole(Role r) { ... }
    void save(User user) { ... }
    void delete(String id) { ... }
}
// One field (db), one purpose, one actor (DBA). Don't split further.
```

---

## Worked Example: Invoice Service

```java
// ❌ FOUR actors → four responsibilities in one class
class InvoiceService {
    private Database db;
    private PdfEngine pdf;
    private SmtpClient smtp;
    private TaxRateProvider taxRates;

    Invoice createInvoice(Order order) {
        BigDecimal tax = order.getTotal().multiply(taxRates.getRate(order.getRegion()));
        byte[] pdfBytes = pdf.generate(order, tax);
        db.execute("INSERT INTO invoices ...");
        smtp.send(order.getEmail(), "Invoice", pdfBytes);
        return new Invoice(order, tax, pdfBytes);
    }
}
```

**Split:**

```java
// Tax calculation — changes when tax laws change (finance team)
class TaxCalculator {
    private TaxRateProvider rates;
    BigDecimal calculate(Order order) {
        return order.getTotal().multiply(rates.getRate(order.getRegion()));
    }
}

// PDF generation — changes when invoice template changes (design team)
class InvoicePdfGenerator {
    private PdfEngine pdf;
    byte[] generate(Order order, BigDecimal tax) { return pdf.render(order, tax); }
}

// Persistence — changes when schema changes (DBA)
class InvoiceRepository {
    private Database db;
    void save(Invoice invoice) { db.execute("INSERT ..."); }
}

// Email delivery — changes when provider/template changes (marketing/ops)
class InvoiceEmailService {
    private SmtpClient smtp;
    void send(Invoice invoice) { smtp.send(invoice.getEmail(), "Invoice", invoice.getPdf()); }
}

// Orchestrator — thin, only coordinates. Changes only if the FLOW changes.
class InvoiceService {
    private TaxCalculator tax;
    private InvoicePdfGenerator pdfGen;
    private InvoiceRepository repo;
    private InvoiceEmailService email;

    Invoice createInvoice(Order order) {
        BigDecimal taxAmount = tax.calculate(order);
        byte[] pdf = pdfGen.generate(order, taxAmount);
        Invoice invoice = new Invoice(order, taxAmount, pdf);
        repo.save(invoice);
        email.send(invoice);
        return invoice;
    }
}
```

---

## Borderline Cases

SRP isn't always black and white. The split depends on team structure:

### AuthenticationService — Keep Together or Split?

```java
class AuthenticationService {
    void register(...) { }
    String login(...) { }
    void resetPassword(...) { }
}
```

| Situation | Decision |
|---|---|
| Small team, one person owns all auth | Keep together — one actor |
| Large org, registration = product team, login = security team | Split — different actors |
| Methods are 5 lines each | Keep — splitting creates Middle Man |
| Methods are 50+ lines with complex logic | Split — clear separation value |

**Interview answer**: "I'd start together. If different teams start owning different flows, or the methods grow complex, I'd split along actor boundaries."

---

## SRP in Spring Boot

| Layer | Responsibility | Example |
|---|---|---|
| Controller | HTTP mapping only | No business logic, no DB calls |
| Service | One business workflow | `OrderService` ≠ `NotificationService` |
| Repository | Data access for one entity | `findById`, `save`, `delete` |
| Validator | Validation rules only | `OrderValidator.validate(order)` |
| Mapper | DTO ↔ Entity conversion only | `OrderMapper.toDto(order)` |
| EventListener | React to one event type | `@EventListener OrderPlacedEvent` |

**Anti-pattern**: `@Service` with 20 methods spanning 3 features → split into 3 services.

---

## Common Mistakes

| Mistake | Why It's Wrong | Fix |
|---|---|---|
| "SRP = one method per class" | No — one RESPONSIBILITY per class (can have many related methods) | Group methods that change together |
| Split CRUD into 4 classes | Over-engineering — all CRUD serves one purpose (persistence) | Keep together |
| Orchestrator with complex logic | Orchestrator should be thin (just coordinates) | Move logic into the services it calls |
| Keeping everything together "for simplicity" | Simplicity now = pain later when things change independently | Split when you see different actors |

---

## Resources

- [Baeldung — SRP in Java](https://www.baeldung.com/java-single-responsibility-principle)
- [Clean Architecture — Chapter 7 (SRP)](https://www.oreilly.com/library/view/clean-architecture/9780134494272/)
- [Refactoring Guru — Code Smells](https://refactoring.guru/refactoring/smells)

## Related

- [Cohesion and Coupling](../oops/cohesion-and-coupling.md) — SRP is cohesion as a principle
- [Open/Closed Principle](open-closed.md) — next SOLID principle
- [Problem-Solving Framework — pressure ②](../lld-approach/problem-solving-framework.md)
