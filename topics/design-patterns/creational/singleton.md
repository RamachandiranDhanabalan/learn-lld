# Singleton Pattern

## Intent

Ensure exactly **one instance** of a class exists and provide a **global access point** to it.

## Problem It Solves

- Shared resource that must not be duplicated (connection pool, thread pool, config)
- Expensive object that should be reused (cache, registry)
- Coordination point (logger, event bus)

---

## Java Implementations

### 1. Enum (Recommended — Effective Java Item 3)

```java
public enum AppConfig {
    INSTANCE;  // single instance, JVM-guaranteed

    private final Properties properties = new Properties();

    AppConfig() { properties.setProperty("db.host", "localhost"); }

    public String get(String key) { return properties.getProperty(key); }
}
// Usage: AppConfig.INSTANCE.get("db.host")
```

**Why best:** Thread-safe (JVM guarantee). Serialization-safe. Reflection-proof. Simplest code.

### 2. Double-Checked Locking (Classic Interview Answer)

```java
public class ConnectionPool {
    private static volatile ConnectionPool instance;  // volatile prevents partial construction visibility

    private ConnectionPool() { /* init pool */ }

    public static ConnectionPool getInstance() {
        if (instance == null) {                     // first check — avoid synchronization if exists
            synchronized (ConnectionPool.class) {   // lock only when needed
                if (instance == null) {             // second check — inside lock
                    instance = new ConnectionPool();
                }
            }
        }
        return instance;
    }
}
```

**Why `volatile`:** Without it, JVM can reorder: assign reference BEFORE construction completes → other threads see half-constructed object.

**Why double-check:** First avoids lock in common case. Second prevents two threads both creating.

### 3. Bill Pugh (Static Inner Class — Lazy + Thread-Safe)

```java
public class Registry {
    private Registry() {}

    private static class Holder {
        private static final Registry INSTANCE = new Registry();
        // Loaded only when Holder class is accessed (lazy)
        // JVM guarantees class loading is thread-safe
    }

    public static Registry getInstance() { return Holder.INSTANCE; }
}
```

**Why it works:** JVM loads inner class only when first accessed (`getInstance()` call). Not when outer class is loaded.

### 4. Eager (Simplest, Not Lazy)

```java
public class Logger {
    private static final Logger INSTANCE = new Logger();  // created at class load
    private Logger() {}
    public static Logger getInstance() { return INSTANCE; }
}
```

---

## Comparison

| Approach | Thread-Safe | Lazy | Reflection-Proof | Serialization-Safe |
|---|---|---|---|---|
| Enum | ✅ | ❌ (eager) | ✅ | ✅ |
| Double-Checked | ✅ | ✅ | ❌ | ❌ |
| Bill Pugh | ✅ | ✅ | ❌ | ❌ |
| Eager | ✅ | ❌ | ❌ | ❌ |

---

## Problems with Singleton (Why It's Controversial)

| Problem | Explanation |
|---|---|
| Hidden dependency | `getInstance()` called inside methods — invisible from constructor |
| Hard to test | Can't mock easily (global state shared across tests) |
| Global mutable state | All threads share it — needs synchronization |
| Tight coupling | Every caller coupled to the Singleton class |
| Violates SRP | Class manages own lifecycle + its actual responsibility |
| mockStatic required | Tests need special tools (fragile, not parallel-safe) |

---

## Why DI Replaces Singleton

| Singleton | DI (Spring @Service) |
|---|---|
| Class manages own lifecycle | Container manages lifecycle |
| Hidden dependency (getInstance) | Explicit (constructor injection) |
| Hard to mock | Easy (@MockBean) |
| One-per-JVM (rigid) | One-per-context (flexible) |
| Manual thread-safety | Container handles |

```java
// Spring — same single-instance guarantee, none of the problems
@Service  // default scope = singleton
class CacheService { ... }

@Service
class OrderService {
    private final CacheService cache;  // explicit, testable, mockable
    OrderService(CacheService cache) { this.cache = cache; }
}
```

---

## When Singleton IS Still Valid

| Scenario | Why |
|---|---|
| No DI container | Pre-Spring environments |
| JVM-level resource | Shutdown hooks, security manager |
| Enum constants | Fixed instances (AppConfig.INSTANCE) |
| Framework internals | Not application code |

---

## How to Break a Singleton

| Attack | How | Enum Immune? |
|---|---|---|
| Reflection | `constructor.setAccessible(true)` | ✅ JVM prevents |
| Serialization | Deserialize creates new instance | ✅ JVM prevents |
| Cloning | `clone()` creates copy | ✅ Enums can't clone |
| Multiple classloaders | Each classloader has own static space | ❌ Even enum affected |

---

## Multiple Classloaders Problem

Each classloader loads its own copy of the class → separate static fields → separate instances.

Happens in: App servers (Tomcat), OSGi, plugin systems, hot-reload tools.

Fix: Put singleton in shared parent classloader, or use DI (container is per-app anyway).

---

## Resources

- [Refactoring Guru — Singleton](https://refactoring.guru/design-patterns/singleton)
- [Effective Java Item 3](https://www.oreilly.com/library/view/effective-java/9780134686097/)
- [Baeldung — Singleton](https://www.baeldung.com/java-singleton)

## Related

- [Dependency Inversion](../../solid/dependency-inversion.md) — DI replaces Singleton in modern apps
- [Factory Method](factory-method.md) — Factory can be singleton-scoped
