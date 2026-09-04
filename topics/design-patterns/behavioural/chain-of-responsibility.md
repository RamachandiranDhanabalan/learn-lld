# Chain of Responsibility Pattern

## Intent

Pass a request along a chain of handlers. Each handler decides to process it, reject/stop, or pass it to the next. The sender doesn't know which handler will handle it.

## Problem It Solves

- A request needs multiple checks/handlers in sequence
- Each check is independent and may pass, reject, or escalate
- You want to add/remove/reorder handlers without changing existing code

---

## Two Flavors

### 1. Single Handler (classic) — stops when handled

One handler fully handles the request, others just pass along.

```java
// Loan approval: first capable approver approves it, chain stops
class LoanApprovalHandler {
    private LoanApprovalHandler next;
    private int allowedLimit;
    private String role;

    void approve(int amount) {
        if (amount <= allowedLimit) {
            System.out.println(role + " approved");  // STOP
        } else if (next != null) {
            next.approve(amount);  // escalate
        } else {
            System.out.println("Rejected");  // chain exhausted
        }
    }
}
// Clerk(5k) → Manager(50k) → Director(500k)
```

### 2. Pipeline — all handlers participate

Request passes through ALL handlers. Each decides to process or skip.

```java
// Logging: message passes through all loggers
abstract class LogHandler {
    private LogHandler next;
    private List<String> allowedLevels;

    void log(Log msg) {
        if (allowedLevels.contains(msg.getType())) {
            process(msg);  // this handler cares — process
        }
        if (next != null) {
            next.log(msg);  // ALWAYS pass to next (pipeline)
        }
    }
    abstract void process(Log msg);
}
// ConsoleLogger(ALL) → FileLogger(INFO+) → EmailLogger(ERROR)
```

---

## CoR vs Decorator (The Cousin)

| | Decorator | CoR |
|---|---|---|
| Always delegates? | YES — always calls next | NO — may STOP the chain |
| Purpose | ADD behavior (all layers run) | FIND handler or FILTER (may stop) |
| Test | Do ALL links always run? → Decorator | Can one STOP? → CoR |

---

## Constructor vs Method Params

| Value | Where | Why |
|---|---|---|
| Handler config (limit, role, levels) | Constructor | Fixed for the handler's lifetime |
| Next handler | Constructor or setNext | Chain structure, set up once |
| Request data (amount, log message) | Method parameter | Changes every call |

> Constructor = what defines this handler. Method param = what varies per request.

---

## When Handlers Differ Only in Data (Not Logic)

If all handlers have identical logic and only differ in data (denomination, limit) → one parameterized class, no subclasses:

```java
CashHandler atm = new CashHandler(2000);
atm.setNext(new CashHandler(500));
// No Rs2000Handler, Rs500Handler classes needed
```

Override `process()` only when each handler does genuinely DIFFERENT work (auth vs validation vs rate-limit).

---

## When to Use CoR

| Signal | Example |
|---|---|
| Sequential checks/validations | Auth → RateLimit → Validate |
| Escalation | L1 → L2 → L3 support |
| Approval levels | Clerk → Manager → Director |
| Filtering pipeline | Servlet filters, middleware |

## When NOT to Use

| Signal | Use Instead |
|---|---|
| Simple fixed sequence, never changes | if-else is fine |
| All observers must react simultaneously | Observer (not sequential CoR) |
| Need undo/queue | Command |

---

## Real-World CoR

| Where | Chain |
|---|---|
| Servlet Filters | Auth → CORS → Logging |
| Spring Security | Filter chain |
| Middleware (Express) | Request processing |
| Approval workflow | Employee → Manager → VP |
| ATM dispensing | ₹2000 → ₹500 → ₹100 |
| Logging levels | Console → File → Email |
| Exception handling | Try handlers until one catches |

---

## Resources

- [Refactoring Guru — CoR](https://refactoring.guru/design-patterns/chain-of-responsibility)

## Related

- [Decorator](../structural/decorator.md) — same structure, always delegates (vs may stop)
- [Observer](observer.md) — all react simultaneously (vs sequential chain)
- [Command](command.md) — one action (vs chain of handlers)
