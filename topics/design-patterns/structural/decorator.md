# Decorator Pattern

## Intent

Add behavior to an object **dynamically** without modifying its class or subclassing. Stack multiple behaviors freely in any combination.

## Problem It Solves

- Need to combine behaviors freely (5 options → 2^5 = 32 subclasses with inheritance)
- Want to add/remove behavior at runtime
- Avoid modifying existing classes to add new functionality

---

## Structure

```
Component (interface)           ← the contract
├── ConcreteComponent           ← base behavior (e.g., EmailSender)
├── BaseDecorator (abstract)    ← optional: DRYs the wrapping logic
    ├── DecoratorA              ← adds behavior A (e.g., Logging)
    ├── DecoratorB              ← adds behavior B (e.g., Retry)
    └── DecoratorC              ← adds behavior C (e.g., Encryption)
```

Key: Decorator implements the SAME interface as what it wraps. That enables stacking.

---

## Example

```java
interface DataSource {
    byte[] read();
    void write(byte[] data);
}

class FileDataSource implements DataSource {
    public byte[] read() { /* read from file */ }
    public void write(byte[] data) { /* write to file */ }
}

class EncryptionDecorator implements DataSource {
    private final DataSource wrapped;
    EncryptionDecorator(DataSource wrapped) { this.wrapped = wrapped; }

    public byte[] read() { return decrypt(wrapped.read()); }
    public void write(byte[] data) { wrapped.write(encrypt(data)); }
}

class CompressionDecorator implements DataSource {
    private final DataSource wrapped;
    CompressionDecorator(DataSource wrapped) { this.wrapped = wrapped; }

    public byte[] read() { return decompress(wrapped.read()); }
    public void write(byte[] data) { wrapped.write(compress(data)); }
}

// Stack freely:
DataSource source = new CompressionDecorator(
    new EncryptionDecorator(
        new FileDataSource("data.txt")
    )
);
```

---

## How to Build (Steps)

```
1. Define Component interface (what operations exist)
2. Create ConcreteComponent (base behavior)
3. Create Decorator class: implements SAME interface, wraps SAME interface via HAS-A
4. In each method: do your added behavior BEFORE or AFTER delegating to wrapped
5. Stack: new DecoratorA(new DecoratorB(new ConcreteComponent()))
```

Optional: Abstract base decorator to DRY the field + constructor across decorators.

---

## Key Characteristics

| Aspect | Description |
|---|---|
| Same interface | Decorator implements same interface as wrapped object |
| Composition | HAS-A reference to another object of same type |
| Stackable | Multiple decorators wrap each other in any order |
| Transparent | Client doesn't know how many layers exist |
| OCP | New behavior = new decorator class, no modification |
| Linear growth | N behaviors = N classes (not 2^N) |

---

## Decorator vs Inheritance

| | Decorator | Subclassing |
|---|---|---|
| Combine behaviors | Any combination at runtime | Class per combination (explosion) |
| Growth | Linear (N classes) | Exponential (2^N) |
| Runtime flexibility | ✅ Add/remove anytime | ❌ Fixed at compile time |
| Primary driver | Composability without explosion | — |

---

## When to Use

| Signal | Why Decorator |
|---|---|
| "Add logging/retry/caching to calls" | Stack on top without modifying original |
| "Combine behaviors in any order" | Each decorator is independent, composable |
| "Class explosion from combinations" | Replace inheritance tree with decorators |
| "Add behavior without modifying existing" | OCP via wrapping |

---

## Real-World Java

- `BufferedReader(new InputStreamReader(new FileInputStream(...)))` — Java I/O
- `Collections.synchronizedList(list)` — adds thread-safety
- `Collections.unmodifiableList(list)` — adds immutability
- Spring `HandlerInterceptor` — adds pre/post processing

---

## Resources

- [Refactoring Guru — Decorator](https://refactoring.guru/design-patterns/decorator)
- [Baeldung — Decorator](https://www.baeldung.com/java-decorator-pattern)

## Related

- [Proxy](proxy.md) — same structure, different intent (control access vs add behavior)
- [Adapter](adapter.md) — wraps DIFFERENT interface. Decorator wraps SAME interface.
- [Strategy](../behavioural/strategy.md) — swaps algorithm. Decorator adds to existing behavior.
