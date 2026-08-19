# Interface Segregation Principle (ISP)

## Definition

> "Clients should not be forced to depend on interfaces they don't use."

**Plain English**: Don't create fat interfaces. If a class only needs 2 of 8 methods, it shouldn't be forced to implement the other 6.

**ISP is SRP applied to interfaces**: SRP says "class does one thing." ISP says "interface defines one capability."

---

## The Problem ISP Solves

```java
// ❌ FAT INTERFACE — forces implementers to deal with methods they don't need
interface Worker {
    void work();
    void eat();
    void sleep();
    void attendMeeting();
}

class Robot implements Worker {
    public void work() { /* works ✅ */ }
    public void eat() { /* ❌ no-op — robots don't eat */ }
    public void sleep() { /* ❌ no-op */ }
    public void attendMeeting() { /* ❌ no-op */ }
}
```

**Problems:**
- Robot has 3 meaningless methods (noise, confusion)
- If `eat()` signature changes, Robot must update too
- Caller might call `robot.eat()` expecting behavior (LSP violation)

---

## The Fix — Split into Focused Interfaces

```java
// ✅ Each interface = one capability
interface Workable { void work(); }
interface Feedable { void eat(); }
interface Restable { void sleep(); }

class Developer implements Workable, Feedable, Restable { /* all three */ }
class Robot implements Workable { /* only what it can do */ }
```

---

## ISP Detection Signals

| Signal | What It Means |
|---|---|
| Empty/no-op method implementations | Interface forced unwanted methods |
| `throw new UnsupportedOperationException()` | Interface too broad (also LSP violation) |
| Interface has 8+ methods | Likely multiple responsibilities |
| Client uses 2 of 7 methods from a dependency | Depends on too broad an interface |
| Change to one method forces recompilation of unrelated classes | Fat interface couples unrelated clients |

---

## ISP + LSP Connection

LSP violations are FIXED by applying ISP:

```
LSP violation: "Penguin can't fly but Bird interface promises fly()"
    ↓ fix via ISP
Split: Bird (eat) + Flyable (fly)
Penguin implements Bird only — no broken promise
```

---

## Real-World Example: Repository Interface

```java
// ❌ Fat — not every data source supports everything
interface DataSource {
    List<Record> read(String query);
    void write(Record record);
    void delete(String id);
    void beginTransaction();
    void commit();
    void rollback();
    void clearCache();
    void reindex();
}

class ReadOnlyCache implements DataSource {
    public List<Record> read(String query) { /* ✅ */ }
    public void write(Record r) { throw new UnsupportedOperationException(); }  // ❌
    public void delete(String id) { throw new UnsupportedOperationException(); } // ❌
    // 5 more useless methods...
}

// ✅ Segregated
interface Readable { List<Record> read(String query); }
interface Writable { void write(Record record); void delete(String id); }
interface Transactional { void begin(); void commit(); void rollback(); }

class ReadOnlyCache implements Readable { /* only reads */ }
class MySQLDatabase implements Readable, Writable, Transactional { /* full */ }
```

---

## When NOT to Split (Over-Segregation)

| Don't Split When... | Why |
|---|---|
| ALL implementers use ALL methods | Interface is already focused |
| Interface has 2-3 related methods | Splitting creates single-method interfaces (noise) |
| Methods are always called together | They represent one atomic capability |

```java
// ✅ Don't split this — read/write are one capability (CRUD)
interface Repository<T> {
    T findById(String id);
    List<T> findAll();
    void save(T entity);
    void delete(String id);
}
// All repos implement all 4. They belong together.
```

---

## Resources

- [Baeldung — Interface Segregation](https://www.baeldung.com/java-interface-segregation)
- [Refactoring Guru — SOLID](https://refactoring.guru/design-patterns)

## Related

- [Liskov Substitution](liskov-substitution.md) — ISP fixes LSP violations
- [Single Responsibility](single-responsibility.md) — ISP is SRP for interfaces
- [Dependency Inversion](dependency-inversion.md) — ISP shapes interfaces that DIP depends on
