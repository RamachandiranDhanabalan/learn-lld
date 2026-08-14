# Class Relationships

## The Spectrum (Loosest → Tightest)

```
Association → Aggregation → Composition → Inheritance
(weakest coupling)                        (strongest coupling)
```

| Relationship | Coupling | Lifetime | Ownership | UML Symbol |
|---|---|---|---|---|
| Association | Loose | Independent | None | ——→ (arrow) |
| Aggregation | Medium | Independent | Shared/weak | ◇——→ (empty diamond) |
| Composition | Tight | Dependent | Exclusive/strong | ◆——→ (filled diamond) |
| Inheritance | Tightest | Same | IS-A | △——→ (triangle) |

---

## Association — "uses" or "knows about"

One class uses or interacts with another, but neither owns the other. They exist completely independently.

**Characteristics:**
- Objects have no ownership relationship
- Both can exist independently
- Relationship can be temporary (method parameter, local variable)
- Can be bidirectional or unidirectional

```java
// Teacher USES Classroom (association — neither owns the other)
class Teacher {
    void teach(Classroom room) {   // uses it temporarily via parameter
        room.openProjector();
    }
    // Teacher doesn't store/own Classroom. Classroom doesn't store/own Teacher.
}

// Customer KNOWS about a Product (association — browsing, no ownership)
class Customer {
    void browse(Product product) {
        System.out.println(product.getName());
    }
}
```

**Real-world analogy**: A driver uses a car (rental). Driver doesn't own the car. Car doesn't own the driver. They interact temporarily.

---

## Aggregation — "has" (weak, shared, independent)

One class contains a reference to another, but the contained object has its own independent lifecycle. It can exist without the parent and can be shared.

**Characteristics:**
- Parent HAS-A child (weak)
- Child is created OUTSIDE, passed in
- Child survives if parent is destroyed
- Same child can belong to multiple parents
- Parent does NOT control child's lifecycle

```java
// Department HAS Employees (aggregation — employees exist independently)
class Department {
    private List<Employee> employees = new ArrayList<>();

    void addEmployee(Employee emp) {
        employees.add(emp);  // emp was created elsewhere, passed in
    }

    void dissolve() {
        employees.clear();
        // Employees still exist! They get reassigned, not destroyed.
    }
}

// Team HAS Players (aggregation — players exist outside the team)
class Team {
    private List<Player> players;

    Team(List<Player> players) {
        this.players = players;  // passed in from outside
    }
    // Player plays for national team AND club team simultaneously
}

// Playlist HAS Songs (aggregation — songs exist in many playlists)
class Playlist {
    private List<Song> songs = new ArrayList<>();

    void addSong(Song song) { songs.add(song); }
    // Deleting playlist doesn't delete the song from Spotify
}
```

**Real-world analogy**: A library has books. Remove the library, books still exist (donated elsewhere). Same book can be in multiple reading lists.

---

## Composition — "owns" (strong, exclusive, dependent)

One class contains another, and the contained object cannot exist without the parent. Parent controls the child's entire lifecycle.

**Characteristics:**
- Parent HAS-A child (strong)
- Child is created INSIDE the parent
- Child dies when parent dies
- Child belongs to ONLY one parent (exclusive)
- Parent CONTROLS child's lifecycle (create, modify, destroy)

```java
// Order OWNS LineItems (composition — items don't exist without the order)
class Order {
    private final List<LineItem> items = new ArrayList<>();

    void addItem(String productId, int qty, BigDecimal price) {
        items.add(new LineItem(productId, qty, price));  // created inside
    }
    // Delete order → all line items are gone. "3x iPhone at ₹80k" is meaningless alone.
}

// House OWNS Rooms (composition — room doesn't exist without a house)
class House {
    private final List<Room> rooms;

    House(int numRooms) {
        rooms = IntStream.range(0, numRooms)
            .mapToObj(i -> new Room())  // created inside
            .collect(toList());
    }
    // Demolish house → rooms are destroyed
}

// Invoice OWNS LineEntries (composition)
class Invoice {
    private final List<LineEntry> entries = new ArrayList<>();
    private final LocalDate issueDate = LocalDate.now();  // created inside

    void addEntry(String desc, BigDecimal amount) {
        entries.add(new LineEntry(desc, amount));  // created here, owned here
    }
}
```

**Real-world analogy**: A human body has a heart. Destroy the body, heart dies. Heart doesn't transfer to another body. Heart was "created" with the body.

---

## How to Decide: The Lifetime Test

> **"If I delete the parent, does the child make sense on its own?"**

| Answer | Relationship | Example |
|---|---|---|
| "Don't even store it — just use it" | Association | Teacher uses Classroom |
| "Yes, child lives on" | Aggregation | Department → Employee (employee gets reassigned) |
| "No, child is meaningless" | Composition | Order → LineItem ("3x iPhone at ₹80k" alone means nothing) |

> **"Can the same child belong to multiple parents?"**

| Answer | Relationship |
|---|---|
| Yes (shared reference) | Aggregation |
| No (exclusive ownership) | Composition |

---

## Implementation Differences in Java

```java
// ASSOCIATION — temporary interaction (method parameter or local variable)
class Driver {
    void drive(Car car) { car.start(); }   // no field, just uses it
}

// AGGREGATION — stored but not owned (passed in via constructor/method)
class Department {
    private List<Employee> employees;

    void addEmployee(Employee emp) {
        employees.add(emp);   // created OUTSIDE, passed IN
    }
}

// COMPOSITION — stored and owned (created INSIDE)
class Order {
    private final List<LineItem> items = new ArrayList<>();

    void addItem(String product, int qty, BigDecimal price) {
        items.add(new LineItem(product, qty, price));  // created HERE
    }
}
```

**Key code signal:**
- `new X()` inside the class → likely Composition (parent creates it)
- `X` passed via constructor/method → likely Aggregation (someone else created it)
- `X` only in method parameter (not stored) → Association

---

## Database Analogy

| Relationship | DB Equivalent |
|---|---|
| Association | Two tables with no FK (or soft reference) |
| Aggregation | FK without CASCADE DELETE (child survives) |
| Composition | FK with ON DELETE CASCADE (child dies with parent) |

---

## Quick Classification Exercise

| # | Relationship | Type | Reasoning |
|---|---|---|---|
| 1 | School → Teacher | Aggregation | Teacher exists independently, can work at another school |
| 2 | Order → ShippingAddress | Composition | That specific address instance belongs to that order, meaningless alone |
| 3 | Logger → FileSystem | Association | Logger uses FileSystem to write, doesn't own or contain it |
| 4 | ChatRoom → Message | Composition | Messages belong to that room, don't float between rooms |
| 5 | Hospital → Patient | Aggregation | Patient exists independently, can go to another hospital |
| 6 | Invoice → LineItem | Composition | "₹500 for consulting" is meaningless without that specific invoice |
| 7 | Playlist → Song | Aggregation | Song exists in Spotify regardless, shared across playlists |
| 8 | Car → GPS Navigator | Aggregation* | GPS is stored as a component but has independent lifecycle — can be swapped or reused |

*#8 is debatable: if GPS is an external service the car queries → Association. If it's an installed component stored as a field → Aggregation. State your reasoning in interviews.

---

## More Examples for Intuition

| Parent → Child | Type | Why |
|---|---|---|
| University → Student | Aggregation | Students graduate and exist independently |
| Car → Engine | Composition* | Engine manufactured for this specific car |
| Garage → Car | Aggregation | Cars drive away, garage remains |
| Email → Attachment | Composition | Attachment is meaningless without that email |
| Playlist → Song | Aggregation | Song exists in Spotify regardless of playlists |
| Order → PaymentTransaction | Composition | That specific transaction belongs to that order only |
| Company → Employee | Aggregation | Employees leave and join other companies |
| ChatRoom → Message | Composition | Messages don't float between chat rooms |
| School → Classroom | Composition | Classroom is part of that school physically |
| Course → Student | Aggregation | Students take multiple courses independently |

*Car → Engine is debatable: if engines are swappable/reusable parts, it's aggregation. If built specifically for one car, it's composition. This shows that the choice is sometimes a **design decision** based on your domain.

---

## Common Mistakes

| Mistake | Fix |
|---|---|
| Using composition when child should be shared | If the same object appears in multiple parents → Aggregation |
| Using aggregation when child is meaningless alone | If child has no identity outside parent → Composition |
| Confusing association with aggregation | Association = temporary use. Aggregation = stored reference with independent life |
| Overusing inheritance where aggregation fits | "Car HAS-A Engine" not "Car IS-A Engine" |

---

## Resources

- [UML Diagrams](../uml/README.md)
- [Composition vs Inheritance](composition-vs-inheritance.md)

## Related

- [OOP Overview](README.md)
- [Encapsulation](encapsulation.md) — how to protect composed/aggregated objects
