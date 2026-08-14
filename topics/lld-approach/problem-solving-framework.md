# LLD Problem-Solving Framework

> Don't start with "which pattern should I use?" Start with "what pressure am I feeling?"

## The Flow

```
STEP 1: Scan for pressures → STEP 2: Apply fix → STEP 3: Validate
```

---

## STEP 1: Scan for Pressures

Check in this order. For each, ask the detection question and look for the symptoms.

### ① Encapsulation — "Can this object be in an invalid state?"

| Symptom | Example |
|---|---|
| Public fields | `account.balance = -999` |
| Setter without validation | `setStatus("BANANA")` works |
| Getter leaks mutable collection | `getItems().clear()` empties internals |
| No constructor validation | `new User()` with no email — invalid but alive |
| Sensitive data exposed | `getPassword()` returns plain text |

**Fix**: Private fields + behavior methods + constructor validation + defensive copies.

---

### ② SRP — "Does this class have multiple reasons to change?"

| Symptom | Example |
|---|---|
| Class name has "Manager", "Helper", "Util" | `UserManager` does auth + profile + email |
| Describe it with "and" multiple times | "It calculates fare AND finds drivers AND books rides" |
| Unrelated feature change risks breaking another | Changing email logic breaks payment logic |
| 500+ lines | God class |

**Fix**: Split into focused classes. Each class = one reason to change.

**Sub-technique — "What varies independently?"**: If two things change for different reasons, by different teams, at different times → they should be separate classes/interfaces.

---

### ③ Coupling — "Does this depend on concrete implementations?"

| Symptom | Example |
|---|---|
| `new ConcreteClass()` in business logic | `new StripeClient()` inside OrderService |
| Can't unit test without real DB/network | Test needs live Stripe to run |
| Changing class A forces changes in class B | Adding WhatsApp requires modifying NotificationManager |
| Imports from infrastructure in domain logic | `import com.twilio.sdk.*` in business layer |

**Fix**: Depend on interfaces. Inject implementations via constructor (DI).

---

### ④ Variation — "Is there if-else/switch that grows with new types?"

| Symptom | Example |
|---|---|
| `if (type.equals("X"))` growing | Payment: CREDIT_CARD, UPI, WALLET... keeps growing |
| Adding new type modifies existing method | New ride type → edit `bookRide()` |
| Same if-else duplicated in multiple methods | Type check repeated in calculate, validate, process |

**Fix**: Interface + one class per type. New type = new class, zero changes.

- Caller selects at runtime → **Strategy**
- System creates based on input → **Factory**

---

### ⑤ State — "Does behavior depend on object's internal lifecycle?"

| Symptom | Example |
|---|---|
| Nested `if (status == X)` in every method | Order: every method checks PLACED/SHIPPED/CANCELLED |
| Methods valid in some states, throw in others | `ship()` works when PLACED, throws when CANCELLED |
| Object transitions through lifecycle | IDLE → PLAYING → PAUSED (MusicPlayer) |
| Invalid transitions possible | DELIVERED → PLACED shouldn't happen but nothing prevents it |

**Fix**: State interface + one class per state. Each state knows valid transitions.

**State vs Strategy test**: "Does the object TRANSITION through these over its lifetime?"
- Yes (lifecycle) → State
- No (external selection) → Strategy

---

### ⑥ Wrong Hierarchy — "Is IS-A used where HAS-A should be?"

| Symptom | Example |
|---|---|
| Type can change at runtime | FreeUser upgrades to PremiumUser |
| Child throws UnsupportedOperationException | `Penguin.fly()` throws |
| Child doesn't need all parent behavior | Stack extends Vector but shouldn't expose insert-at-index |
| Need multiple behaviors, can't extend two classes | FlyingFish needs both Fish + Bird behaviors |
| Deep inheritance (3+ levels) with many overrides | Fragile base class risk |

**Fix**: Composition (HAS-A with interface fields). Apply 4-question test:
1. Truly IS-A forever? 2. Changes at runtime? 3. Needs ALL parent behavior? 4. Parent designed for extension?

---

## STEP 2: Apply the Fix

| Pressure | → Pattern/Fix |
|---|---|
| Invalid state | Encapsulation (private + behavior methods) |
| Multiple responsibilities | Split classes (SRP) |
| Concrete dependency | Interface + DI |
| Growing if-else on type | Strategy (caller picks) or Factory (system creates) |
| Behavior depends on lifecycle | State |
| Wrong IS-A relationship | Composition (HAS-A) |
| Same flow, different steps | Template Method |
| Multiple listeners react to change | Observer |
| Add behavior without modifying class | Decorator |
| Request through chain of checks | Chain of Responsibility |
| Need undo/queued operations | Command |

### Strategy vs State vs Template Method

| Feel | Pattern |
|---|---|
| External caller selects algorithm | Strategy |
| Object transitions through own lifecycle | State |
| Fixed flow, varying steps | Template Method |
| Multiple objects react to change | Observer |

---

## STEP 3: Validate

| Question | ✅ Good | ❌ Bad |
|---|---|---|
| "Add new type tomorrow?" | Add a class, nothing else changes | Modify existing if-else |
| "Test in isolation?" | Mock dependencies | Needs real DB/network |
| "Invalid state possible?" | All transitions controlled | Caller can corrupt |
| "One requirement changes — files touched?" | 1 file | 5 files (ripple) |
| "Am I over-engineering?" | 2+ variants exist | Only 1 variant (YAGNI) |

### "What If" Game (ask after every design)

- "What if we add a new type?" → 1 new class, 0 changes
- "What if this part changes implementation?" → Isolated, callers unaffected
- "What if two users do this simultaneously?" → Identify shared mutable state
- "What if this fails halfway?" → Error handling, consistency
- "What if business adds requirement X?" → Design accommodates it

---

## Quick Decision Cheat

| I feel... | Reach for... |
|---|---|
| "If-else keeps growing" | Strategy |
| "Object acts differently per lifecycle state" | State |
| "Multiple things react when X happens" | Observer |
| "Same flow, different steps" | Template Method |
| "Create different objects based on input" | Factory |
| "Add behavior without modifying class" | Decorator |
| "Can't test — too many real dependencies" | Interface + DI |
| "Class does too many things" | Split (SRP) |
| "Need to combine behaviors freely" | Composition |
| "Object needs undo" | Command |
| "Request passes through checks" | Chain of Responsibility |

---

## Worked Example: NotificationManager

**Pressures**: ① `user.name` public ② SRP (events + channels + content in one class) ③ Coupled to SMTP/Twilio ④ if-else grows per event

**What varies independently**: Event types (product team) | Message content (marketing) | Delivery channels (engineering)

**Fix**:
```java
interface NotificationChannel { void send(String to, String msg); }
interface NotificationTemplate { String getBody(User u); List<NotificationChannel> getChannels(); }

class NotificationService {
    private Map<String, NotificationTemplate> templates;
    void notify(User user, String event) {
        var t = templates.get(event);
        t.getChannels().forEach(ch -> ch.send(user.getEmail(), t.getBody(user)));
    }
}
// New event = new template class. New channel = new channel class. Zero changes.
```

**Validate**: Add event? ✅ New class. Add WhatsApp? ✅ New class. Test? ✅ Mock channels.

---

## Resources

- [LLD Approach Overview](README.md)
- [Strategy Pattern](../design-patterns/behavioural/strategy.md)
- [State Pattern](../design-patterns/behavioural/state.md)
- [SOLID Principles](../solid/README.md)
- [Composition vs Inheritance](../oops/composition-vs-inheritance.md)
