# Encapsulation

## Definition

Bundling state (data) with the **behavior (methods) that governs** that state, and restricting direct access to internal data from outside.

**It is NOT**: "Make fields private and add getters/setters."
**It IS**: "Expose behavior methods that enforce business rules on the data."

---

## WHY It Matters

| Benefit | Explanation |
|---------|-------------|
| **Data hiding** | Object's internal state is not exposed to the outside world |
| **Controlled access** | Any modification happens via methods that ensure valid state always |
| **Maintainability** | Internal representation can change without breaking callers |
| **Security** | No one can corrupt the system state from outside |
| **Invariant protection** | Object can never exist in an invalid state |

---

## How It's Achieved in Java

| Mechanism | What It Does |
|-----------|-------------|
| `private` fields | Hide state from outside |
| Public behavior methods | Validate before modifying state |
| `final` fields | Prevent reassignment after construction |
| Defensive copies | Prevent external mutation of internal collections |
| Immutable objects | Thread-safe, predictable, no setters |
| Constructor validation | Object can never be created in invalid state |

---

## Examples

### Level 1: No Encapsulation (Public Fields)

```java
// ❌ Anyone can corrupt the state
public class BankAccount {
    public double balance;    // set to -999? sure.
    public String status;     // set to "BANANA"? why not.
}

// Usage — chaos
account.balance = -500;  // no rules, no validation
account.status = "INVALID_JUNK";
```

### Level 2: False Encapsulation (Private + Getter/Setter)

```java
// ❌ Looks encapsulated but ISN'T — setters bypass all rules
public class BankAccount {
    private double balance;

    public double getBalance() { return balance; }
    public void setBalance(double b) { this.balance = b; }  // no validation!
}

// Usage — still chaos, just with more typing
account.setBalance(-500);  // allowed! no business rules enforced
```

### Level 3: True Encapsulation (Behavior Methods)

```java
// ✅ State can only change through controlled behavior
public class BankAccount {
    private BigDecimal balance;
    private AccountStatus status;

    public BankAccount(BigDecimal initialDeposit) {
        if (initialDeposit.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Initial deposit cannot be negative");
        this.balance = initialDeposit;
        this.status = AccountStatus.ACTIVE;
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be positive");
        if (status != AccountStatus.ACTIVE)
            throw new IllegalStateException("Account is not active");
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(this.balance) > 0)
            throw new InsufficientFundsException();
        if (status != AccountStatus.ACTIVE)
            throw new IllegalStateException("Account is not active");
        this.balance = this.balance.subtract(amount);
    }

    public BigDecimal getBalance() { return this.balance; }  // read-only
    // NO setBalance() — balance changes ONLY via deposit/withdraw
}
```

**Key differences from Level 2:**
- No `setBalance()` — balance only changes through business operations
- Constructor validates — object can never start invalid
- Every method checks preconditions — status, amount validity
- `BigDecimal` not `double` — money must be exact

---

## Common Violations

### Violation 1: Leaking Mutable Internals

```java
// ❌ Caller can mutate your internal list
public class Order {
    private List<Item> items = new ArrayList<>();

    public List<Item> getItems() { return items; }  // ← THE LEAK
}

// Attack:
order.getItems().clear();  // caller just emptied your order!
order.getItems().add(null);  // caller added garbage
```

**Fix — Defensive Copy:**

```java
// ✅ Read-only view — caller can't mutate
public class Order {
    private final List<Item> items = new ArrayList<>();

    public void addItem(Item item) {
        if (item == null) throw new IllegalArgumentException("Item cannot be null");
        items.add(item);
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);  // safe read-only view
    }
}
```

### Violation 2: Exposing Sensitive Data

```java
// ❌ Password exposed in plain text
public class User {
    private String password;
    public String getPassword() { return password; }  // NEVER do this
}

// ✅ Don't expose, or expose only a check
public class User {
    private String passwordHash;
    public boolean checkPassword(String input) {
        return BCrypt.matches(input, passwordHash);
    }
    // No getter for password at all
}
```

### Violation 3: Lombok @Data on Entities

```java
// ❌ @Data generates getter + setter for EVERY field
@Data
public class Order {
    private Long id;
    private OrderStatus status;
    private List<Item> items;
    // Lombok generates setStatus(), setItems() — breaking encapsulation!
}

// ✅ Use @Getter only (no setters), or manual methods
@Getter
public class Order {
    private Long id;
    private OrderStatus status;
    private List<Item> items;

    public void cancel() {
        if (status == OrderStatus.SHIPPED)
            throw new IllegalStateException("Cannot cancel shipped order");
        this.status = OrderStatus.CANCELLED;
    }
}
```

---

## Encapsulation in Practice (Spring Boot)

| Where | How |
|-------|-----|
| `@Entity` classes | Private fields, JPA manages state, behavior methods enforce transitions |
| DTOs | Use `@Value` (Lombok) for immutability — no setters |
| Configuration | `@ConfigurationProperties` with `@ConstructorBinding` — immutable config |
| Service layer | Internal state (caches, counters) are private, exposed only via methods |

---

## The Mental Test

Before exposing any method, ask:

> "Can a caller put my object into an invalid state using this method?"

- If yes → don't expose it, or add validation
- If it's a getter → does it return a mutable reference? → use defensive copy

---

## Resources

- [Oracle Java Tutorial — Objects](https://docs.oracle.com/javase/tutorial/java/concepts/object.html)
- [Effective Java Item 15 — Minimize Accessibility](https://www.oreilly.com/library/view/effective-java/9780134686097/)
- [Effective Java Item 17 — Minimize Mutability](https://www.oreilly.com/library/view/effective-java/9780134686097/)

## Related

- [Abstraction](abstraction.md)
- [OOP Overview](README.md)
- [SOLID — Single Responsibility](../solid/README.md)
