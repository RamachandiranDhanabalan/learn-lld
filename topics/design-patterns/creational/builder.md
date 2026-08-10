# Builder Pattern

## Intent
Construct complex objects step-by-step. Separate construction from representation.

## Problem It Solves
- Constructor with too many parameters (telescoping constructor)
- Some parameters are optional
- Object needs to be immutable after construction

## Java Example

```java
public class HttpRequest {
    private final String url;
    private final String method;
    private final Map<String, String> headers;
    private final String body;
    private final int timeout;

    private HttpRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = Map.copyOf(builder.headers);
        this.body = builder.body;
        this.timeout = builder.timeout;
    }

    public static class Builder {
        private final String url;    // required
        private String method = "GET";
        private Map<String, String> headers = new HashMap<>();
        private String body;
        private int timeout = 30_000;

        public Builder(String url) { this.url = url; }

        public Builder method(String method) { this.method = method; return this; }
        public Builder header(String key, String value) { headers.put(key, value); return this; }
        public Builder body(String body) { this.body = body; return this; }
        public Builder timeout(int ms) { this.timeout = ms; return this; }

        public HttpRequest build() {
            // validation here
            return new HttpRequest(this);
        }
    }
}

// Usage
HttpRequest req = new HttpRequest.Builder("https://api.example.com/users")
    .method("POST")
    .header("Content-Type", "application/json")
    .body("{\"name\": \"Ram\"}")
    .timeout(5000)
    .build();
```

## When to Use
- Object has many optional parameters
- You want immutable objects
- Construction process needs validation
- You want readable construction (fluent API)

## Trade-offs

| Pros | Cons |
|------|------|
| Readable construction code | More verbose than simple constructor |
| Immutable objects | Extra inner class |
| Validation in build() | |
| Optional params without null | |

## Real-World Java
- `StringBuilder`, `Stream.Builder`
- Lombok `@Builder`
- Spring `UriComponentsBuilder`

## Resources
- [Refactoring Guru — Builder](https://refactoring.guru/design-patterns/builder)
- [Effective Java Item 2](https://www.oreilly.com/library/view/effective-java/9780134686097/)
