# Proxy Pattern

## Intent

Control **access** to an object by wrapping it with same interface. Add a layer of indirection for caching, protection, lazy loading, or logging.

## Problem It Solves

- Expensive object creation — defer until actually needed (lazy/virtual proxy)
- Access control — check permissions before allowing operation (protection proxy)
- Expensive operations — cache results to avoid repeated calls (caching proxy)
- Auditing — log all access to an object (logging proxy)

---

## Types of Proxy

| Type | Purpose | Example |
|---|---|---|
| Virtual (Lazy) | Create expensive object only when needed | Load image on first display |
| Protection | Check permissions before access | Admin-only operations |
| Caching | Cache results of expensive operations | Cache DB queries |
| Logging | Log all method calls | Audit trail |
| Remote | Represent object in another JVM/server | RMI stub |

---

## Example: Caching Proxy

```java
interface UserRepository {
    User findById(String id);
}

class DatabaseUserRepository implements UserRepository {
    public User findById(String id) { /* expensive DB call */ }
}

class CachingUserProxy implements UserRepository {
    private final UserRepository realRepo;
    private final Map<String, User> cache = new ConcurrentHashMap<>();

    CachingUserProxy(UserRepository realRepo) { this.realRepo = realRepo; }

    public User findById(String id) {
        return cache.computeIfAbsent(id, realRepo::findById);  // cache hit → skip DB
    }
}

// Client — doesn't know it's cached
UserRepository repo = new CachingUserProxy(new DatabaseUserRepository());
repo.findById("123");  // first call → DB. Second call → cache.
```

---

## Proxy vs Decorator

| Aspect | Proxy | Decorator |
|---|---|---|
| Intent | **Control access** (protect, cache, lazy) | **Add behavior** (enhance) |
| Stacking | Usually one layer | Multiple layers stacked |
| Client awareness | Transparent (client doesn't know) | Client often builds the chain |

Both wrap same interface + delegate. The difference is purely intent.

---

## Real-World Java

- Spring `@Transactional` — proxy wraps bean, adds transaction management
- Spring `@Cacheable` — proxy checks cache before calling real method
- JPA lazy loading — proxy loads entity on first field access
- `java.lang.reflect.Proxy` — dynamic proxy for interfaces

---

## Resources

- [Refactoring Guru — Proxy](https://refactoring.guru/design-patterns/proxy)

## Related

- [Decorator](decorator.md) — same structure, adds behavior (vs controls access)
- [Adapter](adapter.md) — wraps DIFFERENT interface (vs same interface)
