# Template Method Pattern

## Intent
Define the skeleton of an algorithm in a method, deferring some steps to subclasses.

## Problem It Solves
- Multiple classes share the same algorithm structure but differ in specific steps
- Avoid code duplication of the overall flow
- Control the order of operations while allowing customization

## Java Example

```java
// Abstract class with template method
public abstract class DataImportJob {

    // Template method — defines the algorithm skeleton
    public final void execute() {
        connect();
        List<Record> records = extractData();
        List<Record> transformed = transformData(records);
        loadData(transformed);
        disconnect();
        notifyCompletion();
    }

    protected abstract void connect();
    protected abstract List<Record> extractData();
    protected abstract void disconnect();

    // Hook — optional override
    protected List<Record> transformData(List<Record> records) {
        return records; // default: no transformation
    }

    protected void loadData(List<Record> records) { /* common DB insert */ }
    protected void notifyCompletion() { /* common notification */ }
}

// Concrete implementation
public class CSVImportJob extends DataImportJob {
    protected void connect() { /* open file */ }
    protected List<Record> extractData() { /* parse CSV */ }
    protected void disconnect() { /* close file */ }
}

public class APIImportJob extends DataImportJob {
    protected void connect() { /* establish HTTP connection */ }
    protected List<Record> extractData() { /* call REST API */ }
    protected void disconnect() { /* close connection */ }
    protected List<Record> transformData(List<Record> records) { /* map API response */ }
}
```

## Real-World Java
- Spring `JdbcTemplate`, `RestTemplate`
- `AbstractList.get()` → subclass implements
- Servlet `HttpServlet.service()` dispatches to `doGet()`, `doPost()`

## Template Method vs Strategy

| Template Method | Strategy |
|----------------|----------|
| Inheritance (IS-A) | Composition (HAS-A) |
| Compile-time binding | Runtime binding |
| Controls the overall flow | Controls one step |
| Fewer classes | More flexible |

## Resources
- [Refactoring Guru — Template Method](https://refactoring.guru/design-patterns/template-method)
