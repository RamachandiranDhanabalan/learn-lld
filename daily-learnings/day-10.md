# Day 10 — Builder Pattern

## Cheat Sheet

- **Builder** = Construct complex objects step-by-step. Fluent API. Immutable result.
- **Telescoping constructors** = Multiple constructors growing longer (Builder fixes this)
- **Target fields** = `private final` (immutable after creation)
- **Builder fields** = NOT final for optional params (reassigned by fluent methods). Final only for required (set in constructor).
- **Target constructor** = `private` (only Builder can create — no bypass)
- **Validation** = in `build()` method — single point, fail fast
- **vs Factory** = Factory decides WHICH type. Builder decides HOW to configure one type.
- **vs Constructor** = Constructor for 1-3 required simple params. Builder for 4+ with optionals.
- **Lombok @Builder** = for DTOs/config. Manual builder when custom validation needed in `build()`.

## Critical Examples

### The pattern (minimal to remember)
```java
class Config {
    private final String host;        // immutable
    private final int port;           // immutable
    private final int timeout;        // immutable

    private Config(Builder b) { this.host = b.host; ... }  // private constructor

    public static Builder builder(String host, int port) { return new Builder(host, port); }

    public static class Builder {
        private final String host;     // required — final (set in constructor)
        private final int port;        // required — final
        private int timeout = 30000;   // optional — NOT final (has default)

        Builder(String host, int port) { this.host = host; this.port = port; }
        public Builder timeout(int t) { this.timeout = t; return this; }
        public Config build() {
            if (port < 1 || port > 65535) throw new IllegalStateException("Invalid port");
            return new Config(this);
        }
    }
}
// Usage: Config.builder("localhost", 5432).timeout(5000).build();
```

### Key insight: validate at creation, not at behavior
```java
// ❌ Validate when sending (too late — object already exists in bad state)
void send() { if (from == null) throw ...; }

// ✅ Validate in build() — object can never be invalid
public EmailMessage build() {
    if (from == null) throw new IllegalStateException("From required");
    return new EmailMessage(this);
}
```

## How to Implement Builder (Step-by-Step)

```
Step 1: Create the TARGET class
   → All fields: private final
   → Constructor: private, takes Builder as parameter
   → Getters only, NO setters

Step 2: Create static inner Builder class
   → Required fields: final (set in Builder constructor)
   → Optional fields: NOT final, have sensible defaults
   → Builder constructor takes required params only

Step 3: Add fluent setter methods in Builder
   → Each returns `this` (enables chaining)
   → One method per optional field

Step 4: Add build() method
   → Validates all fields (required not null, ranges, combinations)
   → Throws IllegalStateException if invalid
   → Returns new TargetClass(this)

Step 5: Add static entry point on target class
   → public static Builder builder(requiredParams) { return new Builder(requiredParams); }
```

## Decision Framework

| Question | → Use |
|---|---|
| "4+ params, some optional?" | Builder |
| "Object should be frozen after creation?" | Builder (private final + no setters) |
| "Need defaults + override only what matters?" | Builder |
| "1-3 required params, simple?" | Constructor |
| "Type varies based on input?" | Factory (not Builder) |
| "Team uses Lombok, no custom validation?" | @Builder annotation |

### GoF Builder vs Modern Builder

| | GoF (diagram with Director) | Modern (what we use) |
|---|---|---|
| Builder interface? | Yes (multiple impls) | No (one inner class) |
| Director? | Yes (controls order) | No (caller chains freely) |
| When | Same steps → different products | Many params → one product |
| In Java? | Rarely | Everywhere |

## Interview Questions & Answers

**Q: "What's Builder pattern?"**
A: "Constructs complex objects step-by-step with fluent API. Separates construction from representation. Required params in builder constructor, optional with defaults, validation in build(), immutable result."

**Q: "When Builder vs constructor?"**
A: "Constructor for 1-3 required simple params. Builder for 4+, many optional, need defaults, or want validation + immutability."

**Q: "Builder vs Factory?"**
A: "Factory decides WHICH type to create. Builder decides HOW to configure one specific type. Factory returns different classes, Builder always returns same class configured differently."

**Q: "How enforce required fields?"**
A: "Put them in Builder's constructor — compile-time enforcement. build() validates combinations."

**Q: "Why private final on the target?"**
A: "private = encapsulation. final = immutable. The whole point of Builder is to produce a safe, frozen object. No one can modify it after build()."

## Clues & Signals

- **Senior signal**: "Validation should happen at build(), not at usage — fail fast, object can never be invalid"
- **Senior signal**: "I'd use Lombok @Builder for this DTO — no custom validation needed"
- **Senior signal**: Knowing Builder fields are NOT final (except required) — common mistake
- **Red flag**: Builder that produces mutable object (defeats the purpose)
- **Red flag**: Using Builder for 2-param object (over-engineering)
- **Key learning**: GoF Builder (with Director + interface) is different from Modern Builder. Know both, use Modern 99%.

## Trade-offs

| Decision | Builder | Constructor |
|---|---|---|
| 4+ params | ✅ Readable | ❌ "What's param 5?" |
| Many optional | ✅ Defaults + override | ❌ Telescoping constructors |
| Immutability | ✅ private final + build() | ⚠️ Possible but awkward |
| Validation | ✅ Single point in build() | Scattered in setters |
| Boilerplate | ❌ More code (or use Lombok) | ✅ Less code |
| Simple objects | ❌ Over-engineering | ✅ Just use new |

## Quick Links

- **Detailed topic**: [Builder Pattern](../topics/design-patterns/creational/builder.md)
- **Related**: [Factory Method](../topics/design-patterns/creational/factory-method.md)
- **Related**: [Encapsulation](../topics/oops/encapsulation.md)
