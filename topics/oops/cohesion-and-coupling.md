# Cohesion and Coupling

## Definition

**Cohesion** = How focused a class is on a single purpose. All fields and methods should serve the same responsibility.

**Coupling** = How dependent a class is on other classes. Classes should be able to change independently.

**Goal**: HIGH cohesion + LOW coupling.

---

## WHY It Matters

| High Cohesion + Low Coupling | Low Cohesion + High Coupling |
|---|---|
| Easy to test in isolation | Can't test without real dependencies |
| Change one thing, one file affected | Change one thing, 5 files break |
| Teams work in parallel (different files) | Merge conflicts (everyone edits same file) |
| Easy to name the class (one purpose) | Vague names like "Manager", "Helper" |
| Easy to understand what the class does | Must read 500 lines to understand |

---

## Cohesion — Detailed

### What HIGH Cohesion Looks Like

Every field is used by most methods. Every method relates to the same responsibility.

```java
// ✅ HIGH COHESION — all methods are about persistence, all use the same field
class UserRepository {
    private final Database db;

    User findById(String id) { return db.query("SELECT ... WHERE id=?", id); }
    void save(User user) { db.execute("INSERT ...", user); }
    void delete(String id) { db.execute("DELETE ... WHERE id=?", id); }
    List<User> findByRole(Role role) { return db.query("SELECT ... WHERE role=?", role); }
}
```

### What LOW Cohesion Looks Like

Methods don't share fields. Class is a grab-bag of unrelated things.

```java
// ❌ LOW COHESION — smtp never used by saveUser, db never used by sendEmail
class UserManager {
    private Database db;
    private SmtpClient smtp;
    private PdfGenerator pdf;

    void saveUser(User user) { db.save(user); }              // uses db only
    void sendWelcomeEmail(User user) { smtp.send(...); }     // uses smtp only
    void generateReport(User user) { pdf.create(...); }      // uses pdf only
    // Nothing connects these methods. They don't belong together.
}
```

### How to Detect Low Cohesion

| Signal | Detection |
|---|---|
| Method doesn't use most fields | `sendEmail()` never touches `database` field |
| Class name is vague | "Manager", "Helper", "Util", "Service" (overly generic) |
| You describe it with "and" multiple times | "It manages users AND sends emails AND generates reports" |
| Class has 10+ fields from different domains | DB + SMTP + PDF + Cache + Queue in one class |
| You can split it and neither half needs the other | Split → nothing breaks → they didn't belong together |
| 500+ lines | Usually a god class |

### The LCOM Metric (Lack of Cohesion in Methods)

Informal check: **"What percentage of methods use each field?"**

- If most methods use most fields → HIGH cohesion ✅
- If methods cluster into groups (some use field A, others use field B, never both) → LOW cohesion ❌ → split along those clusters

---

## Coupling — Detailed

### What LOW Coupling Looks Like

Depends on abstractions. Changes to one class don't ripple to others.

```java
// ✅ LOW COUPLING — depends on interfaces, injected via constructor
class OrderService {
    private final OrderRepository repository;      // interface
    private final PaymentGateway payment;          // interface
    private final NotificationService notification; // interface

    OrderService(OrderRepository repo, PaymentGateway pay, NotificationService notify) {
        this.repository = repo;
        this.payment = pay;
        this.notification = notify;
    }

    void placeOrder(Order order) {
        repository.save(order);
        payment.charge(order.getUserId(), order.getTotal());
        notification.send(order.getUser(), "Order confirmed");
    }
    // Change DB? Swap repo implementation. This class doesn't change.
    // Change payment? Swap gateway. This class doesn't change.
}
```

### What HIGH Coupling Looks Like

Depends on concrete classes, creates its own dependencies, knows internal details.

```java
// ❌ HIGH COUPLING — creates concrete deps, knows their construction details
class OrderService {
    void placeOrder(Order order) {
        MySQLConnection conn = new MySQLConnection("jdbc:mysql://localhost/db");
        conn.execute("INSERT INTO orders ...");

        StripeClient stripe = new StripeClient("sk_live_xxx");
        stripe.charges().create(order.getTotal());

        SmtpClient smtp = new SmtpClient("smtp.gmail.com", 587);
        smtp.send(order.getUser().getEmail(), "Order confirmed");
    }
    // Can't test without real MySQL, real Stripe, real Gmail.
    // Change any dependency? Modify this class.
}
```

### Types of Coupling (weakest → strongest)

| Type | What It Means | Example | Verdict |
|---|---|---|---|
| **Message coupling** | Communicate only via method calls | `service.process(data)` | ✅ Best |
| **Data coupling** | Share simple data via parameters | `calculate(price, qty)` | ✅ Good |
| **Stamp coupling** | Share a data structure, use only part | `process(Order)` but only reads `order.total` | ⚠️ Okay |
| **Control coupling** | Pass a flag that controls behavior | `export(report, /*pdf=*/true)` | ⚠️ Avoid (use polymorphism) |
| **Content coupling** | Access another's internals directly | `order.items.clear()` (bypass encapsulation) | ❌ Terrible |
| **Common coupling** | Share global/static mutable state | `GlobalConfig.instance.timeout` | ❌ Terrible |

### How to Reduce Coupling

| Technique | What It Does |
|---|---|
| Depend on interfaces, not concrete classes | Can swap implementations without touching caller |
| Constructor injection (DI) | Dependencies are explicit and mockable |
| Law of Demeter ("don't talk to strangers") | `a.getB().getC().doThing()` → ask `a` directly |
| Event-driven (Observer) | Publisher doesn't know subscribers |
| Facade | Hide complex subsystem behind simple interface |

---

## The Relationship: Cohesion ↔ Coupling

They're inversely correlated:

```
High Cohesion → class is focused → fewer external dependencies → Low Coupling
Low Cohesion  → class does many things → needs many dependencies → High Coupling
```

**SRP is the bridge**: "A class should have only one reason to change" enforces high cohesion, which naturally reduces coupling.

---

## Code Smells That Signal Problems

| Smell | What It Signals | Fix |
|---|---|---|
| **God Class** | Low cohesion (does everything) | Split by responsibility |
| **Feature Envy** | Method uses another class's data more than its own | Move method to that class |
| **Data Clumps** | Same fields always appear together | Extract value object |
| **Long Parameter List** | Method takes 5+ params | Introduce parameter object |
| **Shotgun Surgery** | One change → edit many classes | Consolidate related logic |
| **Divergent Change** | One class changes for multiple unrelated reasons | Split class |
| **Primitive Obsession** | String/int for domain concepts (email, money, phone) | Create value objects |
| **Middle Man** | Class just delegates everything | Remove or give real logic |
| **Inappropriate Intimacy** | Two classes know too much about each other | Reduce to interface dependency |

### Value Objects — Fixing Primitive Obsession and Data Clumps

```java
// ❌ Primitives everywhere
class Student {
    String email;           // any string works? "not-an-email"?
    String street;          // always appears with city, zip
    String city;
    String zip;
}

// ✅ Value objects with validation
class Email {
    private final String value;
    Email(String value) {
        if (!value.contains("@")) throw new IllegalArgumentException("Invalid email");
        this.value = value.toLowerCase();
    }
    String getValue() { return value; }
}

class Address {
    private final String street;
    private final String city;
    private final String zip;
    // constructor validates all three, immutable
}

class Student {
    private Email email;        // can't be invalid
    private Address address;    // fields that belong together, together
}
```

---

## Where It Fits in the Design Landscape

```
GOALS (what you measure):     Cohesion (high) + Coupling (low)
                                    ↑ achieved by
PRINCIPLES (rules to follow): SRP, DIP, ISP, OCP
                                    ↑ implemented via
PATTERNS (tools):             Strategy, Factory, Observer, DI
                                    ↑ triggered by detecting
SMELLS (symptoms):            God Class, Feature Envy, Data Clumps
```

---

## Resources

- [Refactoring Guru — Code Smells](https://refactoring.guru/refactoring/smells)
- [Baeldung — Cohesion and Coupling](https://www.baeldung.com/cs/cohesion-vs-coupling)
- [Clean Code — Chapter 10 (Classes)](https://www.oreilly.com/library/view/clean-code/9780136083238/)

## Related

- [SOLID Principles](../solid/README.md) — SRP enforces cohesion, DIP reduces coupling
- [Encapsulation](encapsulation.md) — content coupling violates encapsulation
- [Abstraction](abstraction.md) — interfaces reduce coupling
- [Problem-Solving Framework](../lld-approach/problem-solving-framework.md) — pressure ② and ③
