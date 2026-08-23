# Day 11 — Singleton Pattern (+ Why DI Replaces It)

## Cheat Sheet

- **Singleton** = One instance, global access. Use Enum (JVM-guaranteed, reflection-proof, serialization-safe).
- **Problems** = Hidden dependency, hard to test, global state, SRP violation, tight coupling.
- **DI replaces Singleton** — same guarantee (one instance) + explicit deps + testable + mockable.
- **Enum vs Eager** = Same idea (one static final instance). Enum adds reflection/serialization protection.
- **volatile in double-checked** = Prevents other threads seeing half-constructed object (JVM reorders steps).
- **Bill Pugh** = Inner class loaded only when accessed → lazy. JVM class loading = thread-safe.
- **When still use** = No DI available, JVM-level resource, enum constants.
- **Enum + DI are mutually exclusive** — pick one. In Spring: always DI.

## Critical Examples

### Enum Singleton (recommended when no DI)
```java
public enum AppConfig {
    INSTANCE;
    private final Properties props = new Properties();
    public String get(String key) { return props.getProperty(key); }
}
// Usage: AppConfig.INSTANCE.get("db.host")
// No getInstance() needed — enum constant is already public static final
```

### Why DI is better (same guarantee, testable)
```java
// ❌ Singleton — hidden dependency, can't mock
class OrderService {
    void process() { Logger.getInstance().log("..."); }  // who is Logger? Can't see from constructor
}

// ✅ DI — explicit, testable
class OrderService {
    private final Logger logger;
    OrderService(Logger logger) { this.logger = logger; }  // visible, mockable
}
```

## How to Implement (Step-by-Step)

```
If DI available (Spring):
   → Don't use Singleton pattern. Use @Service (default singleton scope).

If no DI:
   → Use Enum (simplest, safest)
   → Use Bill Pugh if you need lazy + can't use enum
   → Know double-checked for interviews (volatile + synchronized)
```

## Decision Framework

| Question | → Use |
|---|---|
| "Do I have Spring/DI?" | Don't use Singleton pattern. Use @Service + constructor injection |
| "No DI, need one instance?" | Enum Singleton |
| "Need lazy init + can't use enum?" | Bill Pugh (static inner class) |
| "Interview asks for thread-safe impl?" | Double-checked locking (explain volatile) |

## Interview Questions & Answers

**Q: "What's Singleton?"**
A: "One instance, global access. Enum is best Java implementation — thread-safe, serialization-safe, reflection-proof."

**Q: "Problem with Singleton?"**
A: "Hidden dependency (invisible from constructor), hard to test (can't mock without mockStatic), global mutable state, SRP violation (manages own lifecycle). DI gives same guarantee without these problems."

**Q: "Thread-safe implementation?"**
A: "Enum (JVM-guaranteed). Or Bill Pugh (lazy, class-loading is thread-safe). Or double-checked locking (volatile prevents partial construction visibility, double-check prevents duplicate creation)."

**Q: "Why volatile in double-checked?"**
A: "JVM can reorder: assign reference BEFORE construction finishes. Without volatile, another thread sees non-null instance that's half-constructed."

**Q: "When Singleton over DI?"**
A: "Almost never in Spring apps. Only pre-DI environments or JVM-level resources. In application code, always DI."

**Q: "How to break Singleton?"**
A: "Reflection (setAccessible), serialization (deserialize = new instance), cloning, multiple classloaders. Enum is immune to first three."

## Clues & Signals

- **Senior signal**: "I'd use DI here — Singleton gives the same guarantee with more problems"
- **Senior signal**: "Enum is immune to reflection/serialization attacks"
- **Senior signal**: "Even pre-Spring, I'd do manual constructor injection and just hold the singleton instance at the wiring layer"
- **Red flag**: Using Singleton in a Spring app (container already handles single-instance)
- **Red flag**: `getInstance()` calls scattered in business logic (hidden dependency)
- **Key insight**: Singleton doesn't prevent others from creating the wrapped resource (e.g., ExecutorService). It's a convention, not enforcement.

## Trade-offs

| Decision | GoF Singleton | DI Singleton Scope |
|---|---|---|
| One instance | ✅ | ✅ |
| Testable | ❌ (mockStatic needed) | ✅ (mock via constructor) |
| Explicit deps | ❌ (hidden getInstance) | ✅ (constructor) |
| Thread safety | Manual | Container handles |
| When | No DI available | Always prefer |

## Quick Links

- **Detailed topic**: [Singleton Pattern](../topics/design-patterns/creational/singleton.md)
- **Related**: [Dependency Inversion](../topics/solid/dependency-inversion.md)
- **Related**: [Factory Method](../topics/design-patterns/creational/factory-method.md)
