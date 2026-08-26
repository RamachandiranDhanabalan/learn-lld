# Day 13 — Decorator + Proxy

## Cheat Sheet

- **Decorator** = Add behavior dynamically by wrapping with SAME interface. Stackable. Avoids class explosion.
- **Proxy** = Control access by wrapping with SAME interface. Cache, protect, lazy-load, log.
- **Both** = Same structure (implement interface, wrap same type, delegate). Different INTENT.
- **Adapter** wraps DIFFERENT interface. Decorator/Proxy wrap SAME interface.
- **Decorator is stacked** (multiple layers). **Proxy is usually one layer** (transparent to client).
- **Linear classes** = N behaviors = N decorator classes. Inheritance = 2^N for N combinations.
- **Abstract base decorator** is optional — just DRYs the field + constructor.

## How to Build Decorator

```
1. Component interface (what operations exist)
2. ConcreteComponent (base behavior)
3. Decorator: implements SAME interface + wraps SAME interface (HAS-A)
4. In each method: add behavior BEFORE/AFTER, then delegate to wrapped
5. Stack: new A(new B(new C(base)))
```

## Critical Examples

### Decorator — stackable behaviors
```java
interface Sender { void send(String to, String msg); }
class EmailSender implements Sender { /* base */ }

class LoggingDecorator implements Sender {
    private final Sender wrapped;
    LoggingDecorator(Sender w) { this.wrapped = w; }
    public void send(String to, String msg) {
        log("before"); wrapped.send(to, msg); log("after");
    }
}

// Stack: new LoggingDecorator(new RetryDecorator(new EmailSender()))
```

### Proxy — controls access (transparent to client)
```java
class CachingProxy implements UserRepository {
    private final UserRepository real;
    private final Map<String, User> cache = new ConcurrentHashMap<>();

    public User findById(String id) {
        return cache.computeIfAbsent(id, real::findById);
    }
}
// Client: UserRepository repo = new CachingProxy(realRepo);  // doesn't know it's cached
```

## Decision Framework

| I need to... | Pattern |
|---|---|
| Add logging/retry/encryption AROUND calls, stackable | Decorator |
| Cache results, skip expensive work | Proxy (caching) |
| Check permissions before access | Proxy (protection) |
| Load heavy object only when first used | Proxy (lazy/virtual) |
| Combine multiple optional behaviors freely | Decorator |
| Make incompatible interface work | Adapter (NOT Decorator/Proxy) |

### Structural Patterns Comparison

| Pattern | Same Interface? | Intent |
|---|---|---|
| Adapter | ❌ Different | Translate incompatible interface |
| Decorator | ✅ Same | Add behavior (stack freely) |
| Proxy | ✅ Same | Control access (cache, auth, lazy) |
| Facade | N/A (simplifies) | Hide complex subsystem |

## Interview Questions & Answers

**Q: "What's Decorator?"**
A: "Wraps object with same interface to add behavior dynamically. Stackable — any combination without class explosion. Java I/O streams are classic example."

**Q: "What's Proxy?"**
A: "Wraps object with same interface to control access — caching, auth, lazy loading. Spring @Transactional and @Cacheable are proxy-based."

**Q: "Decorator vs Proxy?"**
A: "Same structure. Decorator adds behavior (enhance). Proxy controls access (protect, cache). Decorators stack. Proxy is usually one layer, transparent."

**Q: "Decorator vs Adapter?"**
A: "Adapter wraps a DIFFERENT interface (translates). Decorator wraps the SAME interface (adds behavior). Adapter can't stack. Decorator can."

**Q: "Why not just subclass to add behavior?"**
A: "Subclassing creates 2^N classes for N behaviors. Decorator = N classes, compose freely at runtime. Linear vs exponential."

## Clues & Signals

- **Senior signal**: "I'd use Decorator for retry + logging since they're independent and stackable"
- **Senior signal**: "Spring @Transactional is a proxy — it controls when the real method runs"
- **Red flag**: Thinking of patterns BEFORE identifying pressures (Pattern → Problem instead of Problem → Pattern)
- **Key learning**: Decorator/Proxy/Adapter have nearly identical structure. The NAME comes from INTENT.
- **Key learning Phase 2 confusion**: "I see patterns everywhere" is normal at this stage. Go back to framework: pressure FIRST, pattern follows.

## Approach Reminder

```
ALWAYS: Pressure first → Pattern follows

❌ "Is this Decorator or Proxy or Adapter?"  (pattern-first thinking)
✅ "What's the pressure?"
    → "I have a third-party SDK with different API" → Adapter
    → "I want to add logging without modifying" → Decorator
    → "I want to cache expensive calls" → Proxy
    → "Behavior varies by type at runtime" → Strategy
```

## Quick Links

- **Detailed topic**: [Decorator](../topics/design-patterns/structural/decorator.md)
- **Detailed topic**: [Proxy](../topics/design-patterns/structural/proxy.md)
- **Related**: [Adapter](../topics/design-patterns/structural/adapter.md)
- **Framework**: [Problem-Solving Framework](../topics/lld-approach/problem-solving-framework.md)
