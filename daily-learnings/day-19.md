# Day 19 — Chain of Responsibility

## Cheat Sheet

- **CoR** = Pass a request along a chain of handlers. Each processes, rejects, or passes on.
- **Two flavors**: Single-handler (stops when handled — loan approval) | Pipeline (all participate — logging, filters).
- **vs Decorator** = Decorator ALWAYS delegates (all run). CoR MAY stop the chain.
- **Constructor** = handler config (limit, levels). **Method param** = request data (amount, message).
- **Data-only variation** = one parameterized class, no subclasses. Override `process()` only when logic differs.
- **Sender is decoupled** — doesn't know which handler handles it.

## Critical Examples

### Single-handler flavor (stops when handled)
```java
class Approver {
    private Approver next;
    private int limit;
    void approve(int amount) {
        if (amount <= limit) approve();       // STOP — I handle it
        else if (next != null) next.approve(amount);  // escalate
        else reject();                          // chain exhausted
    }
}
```

### Pipeline flavor (all participate)
```java
abstract class LogHandler {
    private LogHandler next;
    private List<String> allowedLevels;
    void log(Log msg) {
        if (allowedLevels.contains(msg.getType())) process(msg);  // process if I care
        if (next != null) next.log(msg);  // ALWAYS pass on (pipeline)
    }
    abstract void process(Log msg);
}
```

## Decision Framework

| Signal | Flavor |
|---|---|
| "First capable handler resolves it" | Single-handler (stops) |
| "All matching handlers should process" | Pipeline (all run) |
| "Sequential checks (auth, validate, ratelimit)" | CoR pipeline |
| "Escalation (L1 → L2 → L3)" | Single-handler |
| "Add/reorder/remove steps easily" | CoR (vs hardcoded if-else) |
| "All must react at once (not sequential)" | Observer (not CoR) |

### CoR vs Decorator

| Test | → |
|---|---|
| "Do ALL links always run?" | Decorator |
| "Can one link STOP the chain?" | CoR |
| "Enhancing an object?" | Decorator |
| "Finding/routing to a handler?" | CoR |

## Interview Questions & Answers

**Q: "What's Chain of Responsibility?"**
A: "Pass a request along a chain of handlers. Each decides to process, reject, or pass on. Sender doesn't know which handler handles it. Decouples sender from receiver."

**Q: "Two flavors?"**
A: "Single-handler — request travels until one handler processes it, then stops (support escalation, loan approval). Pipeline — passes through ALL handlers, each processes or skips (logging, servlet filters)."

**Q: "CoR vs Decorator?"**
A: "Both chain and delegate. Decorator ALWAYS delegates to the next (all layers run). CoR handler MAY stop the chain. Decorator enhances; CoR routes/filters."

**Q: "Constructor vs method params in a handler?"**
A: "Constructor holds fixed config (limit, allowed levels, next). Method param holds the per-request data (amount, log message)."

**Q: "When to subclass vs one parameterized handler?"**
A: "If handlers differ only in data (denomination, limit), use one parameterized class. Subclass with overridden process() only when each handler's LOGIC differs."

## Clues & Signals

- **Senior signal**: "This is the pipeline flavor — all matching loggers process, chain never stops"
- **Senior signal**: "Handlers differ only in data here, so one parameterized class — no subclasses"
- **Red flag**: Confusing CoR with Decorator (Decorator always delegates; CoR may stop)
- **Red flag**: Using CoR when all handlers must react simultaneously (that's Observer)
- **Key learning**: The problem's nature dictates the flavor — ATM/validation = pipeline; approval/escalation = single-handler.

## Design Practice

- **Loan Approval** (single-handler): Clerk → Manager → Director. First capable approver handles it, chain stops.
- **Logging Framework** (pipeline): Console(ALL) → File(INFO+) → Email(ERROR). Message passes through all; each logs if level matches.

Both correct on first try — the CoR distinction (stop vs pipeline) clicked.

## Quick Links

- **Detailed topic**: [Chain of Responsibility](../topics/design-patterns/behavioural/chain-of-responsibility.md)
- **Related**: [Decorator](../topics/design-patterns/structural/decorator.md), [Observer](../topics/design-patterns/behavioural/observer.md)
