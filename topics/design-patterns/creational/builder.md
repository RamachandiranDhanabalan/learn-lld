# Builder Pattern

## Intent

Construct complex objects step-by-step with a fluent API. Separates construction (the Builder) from representation (the final object). Gives you readable creation, optional params with defaults, validation, and an immutable result.

## Problem It Solves

- **Telescoping constructors**: Multiple constructors growing longer with each optional param
- **JavaBean setters**: Object can be in incomplete/invalid state between setter calls
- **Unreadable creation**: `new X(a, b, c, d, e, f)` — what does each param mean?
- **Missing validation**: No single point to validate all fields together

---

## When to Use

| Signal | Why Builder |
|---|---|
| 4+ constructor parameters | Readability degrades with positional args |
| Many optional parameters | Can't have 20 constructor overloads |
| Object should be immutable after creation | Builder creates, then object is frozen |
| Complex validation across fields | `build()` checks field combinations |
| You want sensible defaults | Builder provides them, caller overrides what they need |

## When NOT to Use

| Signal | Use Instead |
|---|---|
| 1-3 params, all required | Simple constructor |
| Mutable object (entity that changes) | Constructor + behavior methods |
| Simple value objects | Constructor or static factory |

---

## Implementation (Modern/Effective Java Builder)

```java
public class HttpRequest {
    // All fields immutable (private final)
    private final String url;
    private final String method;
    private final String contentType;
    private final String body;
    private final int timeout;
    private final boolean followRedirects;

    // Private constructor — only Builder can create
    private HttpRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.contentType = builder.contentType;
        this.body = builder.body;
        this.timeout = builder.timeout;
        this.followRedirects = builder.followRedirects;
    }

    // Getters only — no setters (immutable)
    public String getUrl() { return url; }
    public String getMethod() { return method; }

    // Entry point
    public static Builder builder(String url) { return new Builder(url); }

    public static class Builder {
        // Required — final, set in constructor
        private final String url;

        // Optional — NOT final, have defaults
        private String method = "GET";
        private String contentType = "application/json";
        private String body;
        private int timeout = 30_000;
        private boolean followRedirects = true;

        // Required params in builder constructor
        public Builder(String url) { this.url = url; }

        // Fluent setters (return this)
        public Builder method(String m) { this.method = m; return this; }
        public Builder contentType(String ct) { this.contentType = ct; return this; }
        public Builder body(String b) { this.body = b; return this; }
        public Builder timeout(int ms) { this.timeout = ms; return this; }
        public Builder followRedirects(boolean f) { this.followRedirects = f; return this; }

        // Validation + creation
        public HttpRequest build() {
            if (url == null || url.isBlank())
                throw new IllegalStateException("URL is required");
            if (timeout < 0)
                throw new IllegalStateException("Timeout must be positive");
            if (method.equals("POST") && body == null)
                throw new IllegalStateException("POST requires a body");
            return new HttpRequest(this);
        }
    }
}

// Usage
HttpRequest req = HttpRequest.builder("https://api.example.com/users")
    .method("POST")
    .body("{\"name\":\"Ram\"}")
    .timeout(5000)
    .build();
```

---

## Key Rules

| Rule | Why |
|---|---|
| Target object fields are `private final` | Immutable after creation |
| Target constructor is `private` | Only Builder can create (no bypass) |
| Required fields go in Builder's constructor | Enforced at compile time |
| Optional fields have defaults in Builder | Caller sets only what they need |
| Builder fields are NOT final (except required) | They get reassigned by fluent methods |
| Validation happens in `build()` | Single point of truth, fail fast |
| Fluent methods return `this` | Enables chaining |

---

## GoF Builder vs Modern Builder

| Aspect | GoF (textbook/diagram) | Modern (Effective Java) |
|---|---|---|
| Builder interface? | ✅ Multiple builder implementations | ❌ One inner class |
| Director? | ✅ Controls build step ORDER | ❌ Caller chains freely |
| Purpose | Same steps → different products | Many params → readable construction |
| Used in Java? | Rarely | Everywhere (Lombok, OkHttp, Spring) |
| When | Multiple representations (PDF, HTML, Markdown) | One product, many optional params |

---

## Builder vs Constructor vs Factory

| Approach | When |
|---|---|
| **Constructor** | Few params (1-3), all required, simple |
| **Factory** | Type varies (which class to create) |
| **Builder** | Many params, optional, validation, immutable result |

---

## Lombok @Builder

```java
@Builder
@Getter
public class SearchQuery {
    private final String keyword;
    @Builder.Default private int page = 1;
    @Builder.Default private int pageSize = 20;
    private String sortBy;
}
// Usage: SearchQuery.builder().keyword("java").page(2).build();
```

Use Lombok for DTOs/config. Use manual builder when `build()` needs custom validation.

---

## Real-World Java

| Library | Builder Usage |
|---|---|
| `StringBuilder` | Builds strings step-by-step |
| `OkHttp Request.Builder` | HTTP request construction |
| `Lombok @Builder` | Auto-generated builders |
| Spring `UriComponentsBuilder` | URI with query params |
| `Notification.Builder` (Android) | Complex notifications |

---

## Resources

- [Refactoring Guru — Builder](https://refactoring.guru/design-patterns/builder)
- [Effective Java Item 2](https://www.oreilly.com/library/view/effective-java/9780134686097/)
- [Baeldung — Builder Pattern](https://www.baeldung.com/creational-design-patterns#builder)

## Related

- [Factory Method](factory-method.md) — decides which TYPE. Builder decides HOW to configure.
- [Encapsulation](../../oops/encapsulation.md) — Builder produces encapsulated, immutable objects
