# Proxy Pattern

## Intent
Provide a surrogate or placeholder for another object to control access to it.

## Types
- **Virtual Proxy**: Lazy initialization (load heavy object only when needed)
- **Protection Proxy**: Access control
- **Caching Proxy**: Cache results of expensive operations
- **Logging Proxy**: Log access to the real object

## Java Example

```java
public interface DatabaseService {
    QueryResult execute(String query);
}

// Real service
public class RealDatabaseService implements DatabaseService {
    public QueryResult execute(String query) { /* actual DB call */ }
}

// Caching + Logging Proxy
public class CachingDatabaseProxy implements DatabaseService {
    private final DatabaseService realService;
    private final Map<String, QueryResult> cache = new ConcurrentHashMap<>();

    public QueryResult execute(String query) {
        if (cache.containsKey(query)) {
            log.info("Cache hit for: {}", query);
            return cache.get(query);
        }
        QueryResult result = realService.execute(query);
        cache.put(query, result);
        return result;
    }
}
```

## Real-World Java
- Spring `@Transactional` — proxy wraps your bean
- JPA lazy loading — proxy loads entity on first access
- `java.lang.reflect.Proxy` — dynamic proxy

## Resources
- [Refactoring Guru — Proxy](https://refactoring.guru/design-patterns/proxy)
