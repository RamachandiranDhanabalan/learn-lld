# Singleton Pattern

## Intent
Ensure a class has exactly one instance and provide a global access point to it.

## Problem It Solves
- Shared resource that must not be duplicated (connection pool, configuration, registry)
- Global access without passing references everywhere

## Java Implementations

### Thread-Safe (Enum — Recommended by Joshua Bloch)
```java
public enum AppConfig {
    INSTANCE;

    private final Properties properties = new Properties();

    AppConfig() { /* load properties */ }

    public String get(String key) { return properties.getProperty(key); }
}
```

### Double-Checked Locking (Classic)
```java
public class ConnectionPool {
    private static volatile ConnectionPool instance;

    private ConnectionPool() { /* initialize pool */ }

    public static ConnectionPool getInstance() {
        if (instance == null) {
            synchronized (ConnectionPool.class) {
                if (instance == null) {
                    instance = new ConnectionPool();
                }
            }
        }
        return instance;
    }
}
```

## Why You Should Prefer DI Over Singleton

| Singleton | Dependency Injection |
|-----------|---------------------|
| Hidden dependency | Explicit dependency |
| Hard to test (global state) | Easy to mock |
| Tight coupling | Loose coupling |
| Concurrency issues | Container-managed lifecycle |

In Spring, `@Scope("singleton")` is the default — you get single-instance behavior WITHOUT the Singleton pattern's downsides.

## When Singleton is Still Valid
- Framework-level infrastructure (not application code)
- Pre-Spring/DI environments
- Enum-based constants

## Interview Signal
"I'd use Spring's singleton scope for application-level singletons because it gives me the same guarantee with testability. The GoF Singleton is really only needed when you have no DI container."

## Resources
- [Refactoring Guru — Singleton](https://refactoring.guru/design-patterns/singleton)
- [Effective Java Item 3](https://www.oreilly.com/library/view/effective-java/9780134686097/)
