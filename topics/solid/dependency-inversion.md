# Dependency Inversion Principle (DIP)

## Definition

> "High-level modules should not depend on low-level modules. Both should depend on abstractions."

**Plain English**: Business logic depends on INTERFACES, not on MySQL, Stripe, or SMTP directly.

**DIP is the principle. DI (Dependency Injection) is the mechanism that implements it.**

---

## The Problem DIP Solves

```java
// ❌ High-level (OrderService) depends on low-level (MySQL, Stripe)
class OrderService {
    private MySQLOrderRepository repo = new MySQLOrderRepository();
    private StripePaymentClient stripe = new StripePaymentClient();

    void placeOrder(Order order) {
        repo.save(order);           // coupled to MySQL
        stripe.charge(order.total); // coupled to Stripe
    }
}
// Can't test without real MySQL and Stripe.
// Can't swap to Postgres or Razorpay without modifying this class.
```

---

## The Fix — Depend on Abstractions, Inject Implementations

```java
// ✅ Depends on interfaces, implementations injected
interface OrderRepository { void save(Order order); }
interface PaymentGateway { void charge(BigDecimal amount); }

class OrderService {
    private final OrderRepository repo;
    private final PaymentGateway payment;

    OrderService(OrderRepository repo, PaymentGateway payment) {
        this.repo = repo;
        this.payment = payment;
    }

    void placeOrder(Order order) {
        repo.save(order);
        payment.charge(order.getTotal());
    }
}
// Swap DB? New repo implementation, inject it. OrderService unchanged.
// Test? Mock both. No real systems needed.
```

---

## The Dependency Arrow Flip

```
WITHOUT DIP:
  OrderService ──depends on──→ MySQLOrderRepository
  (high-level)                  (low-level detail)

WITH DIP:
  OrderService ──depends on──→ OrderRepository (interface)
                                      ↑ implements
  MySQLOrderRepository ──────────────┘
```

Both high-level AND low-level depend on the abstraction. The low-level module's arrow FLIPS — it now points UP toward the abstraction.

---

## DIP Detection Signals

| Signal | What It Means |
|---|---|
| `new ConcreteClass()` in business logic | Directly coupled to implementation |
| Import from infrastructure package in domain | Domain knows about DB/network details |
| Can't unit test without real external system | No abstraction to mock |
| Changing provider requires modifying business class | Not inverted |
| Constructor creates its own dependencies | No inversion |

---

## DIP in Spring Boot

```java
// Spring implements DIP via DI container:
@Service
class OrderService {
    private final OrderRepository repo;  // interface

    OrderService(OrderRepository repo) {  // Spring injects implementation
        this.repo = repo;
    }
}

@Repository
class JpaOrderRepository implements OrderRepository { /* Hibernate */ }
// Swap to Mongo? New class, new @Repository. OrderService unchanged.
// Test? @MockBean OrderRepository.
```

---

## When NOT to Apply DIP

| Situation | Why Skip | Example |
|---|---|---|
| Internal utility, never swapped | No benefit to abstracting | `StringUtils`, `MathHelper` |
| Standard library classes | You won't swap ArrayList | `new ArrayList<>()`, `new HashMap<>()` |
| Value objects / DTOs | Data holders, no polymorphism | `new Address(street, city)` |
| Simple scripts, throwaway code | Overhead > benefit | Migration script |

**Apply DIP When:**
- External systems (DB, payment, email, queue)
- Anything you mock in tests
- Behavior that might be swapped (providers, strategies)
- Cross-team boundaries

**Quick test**: "Will I ever need a second implementation, OR mock this in tests?" → Yes to either → Apply DIP.

---

## DIP vs DI — The Distinction

| Term | What It Is |
|---|---|
| **DIP** | The PRINCIPLE — "depend on abstractions" |
| **DI** | The MECHANISM — "pass dependencies via constructor" |
| **IoC Container** | The TOOL — Spring/Guice that automates DI |

DI without DIP is possible (inject concrete classes) but misses the point.
DIP without DI is possible (manually create and pass interfaces) but tedious.

---

## Resources

- [Baeldung — Dependency Inversion](https://www.baeldung.com/java-dependency-inversion-principle)
- [Martin Fowler — Inversion of Control](https://martinfowler.com/articles/injection.html)

## Related

- [Interface Segregation](interface-segregation.md) — ISP shapes the interfaces DIP depends on
- [Open/Closed Principle](open-closed.md) — DIP enables OCP (abstraction allows new implementations)
- [Problem-Solving Framework — pressure ③](../lld-approach/problem-solving-framework.md)
