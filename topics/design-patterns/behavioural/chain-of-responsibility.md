# Chain of Responsibility Pattern

## Intent
Pass a request along a chain of handlers. Each handler decides to process or pass to the next.

## Problem It Solves
- Multiple objects might handle a request, and the handler isn't known in advance
- Avoid coupling the sender to a specific receiver
- Dynamic ordering of handlers

## Java Example

```java
// Handler interface
public abstract class ValidationHandler {
    private ValidationHandler next;

    public ValidationHandler setNext(ValidationHandler next) {
        this.next = next;
        return next;
    }

    public ValidationResult validate(Request request) {
        ValidationResult result = doValidate(request);
        if (!result.isValid()) return result;
        if (next != null) return next.validate(request);
        return ValidationResult.valid();
    }

    protected abstract ValidationResult doValidate(Request request);
}

// Concrete handlers
public class AuthenticationHandler extends ValidationHandler {
    protected ValidationResult doValidate(Request request) {
        if (request.getToken() == null) return ValidationResult.fail("No auth token");
        return ValidationResult.valid();
    }
}

public class RateLimitHandler extends ValidationHandler {
    protected ValidationResult doValidate(Request request) {
        if (isRateLimited(request.getClientId())) return ValidationResult.fail("Rate limited");
        return ValidationResult.valid();
    }
}

public class InputSanitizationHandler extends ValidationHandler {
    protected ValidationResult doValidate(Request request) {
        if (containsSQLInjection(request.getBody())) return ValidationResult.fail("Unsafe input");
        return ValidationResult.valid();
    }
}

// Usage
ValidationHandler chain = new AuthenticationHandler();
chain.setNext(new RateLimitHandler())
     .setNext(new InputSanitizationHandler());

ValidationResult result = chain.validate(request);
```

## Real-World Java
- Servlet Filters / Spring `HandlerInterceptor`
- Exception handler chains
- Approval workflows (manager → director → VP)

## Resources
- [Refactoring Guru — Chain of Responsibility](https://refactoring.guru/design-patterns/chain-of-responsibility)
