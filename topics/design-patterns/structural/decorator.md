# Decorator Pattern

## Intent
Attach additional responsibilities to an object dynamically. An alternative to subclassing for extending functionality.

## Problem It Solves
- Need to add behavior to individual objects, not the whole class
- Want to combine behaviors freely (like toppings on pizza)
- Inheritance explosion when combining features

## Java Example

```java
// Component interface
public interface DataSource {
    void writeData(String data);
    String readData();
}

// Base implementation
public class FileDataSource implements DataSource {
    private final String filename;
    public void writeData(String data) { /* write to file */ }
    public String readData() { /* read from file */ }
}

// Base decorator
public abstract class DataSourceDecorator implements DataSource {
    protected final DataSource wrappee;
    public DataSourceDecorator(DataSource source) { this.wrappee = source; }
}

// Concrete decorators
public class EncryptionDecorator extends DataSourceDecorator {
    public void writeData(String data) { wrappee.writeData(encrypt(data)); }
    public String readData() { return decrypt(wrappee.readData()); }
}

public class CompressionDecorator extends DataSourceDecorator {
    public void writeData(String data) { wrappee.writeData(compress(data)); }
    public String readData() { return decompress(wrappee.readData()); }
}

// Usage — compose behaviors freely
DataSource source = new CompressionDecorator(
    new EncryptionDecorator(
        new FileDataSource("data.txt")
    )
);
source.writeData("secret"); // compressed → encrypted → written to file
```

## Real-World Java
- `java.io` streams: `BufferedReader(new InputStreamReader(new FileInputStream(...)))`
- `Collections.synchronizedList()`, `Collections.unmodifiableList()`
- Spring `HandlerInterceptor`

## Trade-offs

| Pros | Cons |
|------|------|
| Combine behaviors without class explosion | Many small objects |
| Add/remove at runtime | Hard to remove a specific wrapper from middle |
| Single Responsibility per decorator | Order of decoration matters |

## Resources
- [Refactoring Guru — Decorator](https://refactoring.guru/design-patterns/decorator)
