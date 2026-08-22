# Day 09 — Abstract Factory Pattern

## Cheat Sheet

- **Abstract Factory** = Creates families of related objects that MUST be compatible. One factory per family.
- **Key constraint** = Mixing products from different families is a bug. Factory prevents it.
- **vs Simple Factory** = Simple creates ONE type. Abstract creates a FAMILY.
- **vs Factory Method** = Factory Method has ONE creation step in a flow. Abstract Factory has MULTIPLE create methods, caller owns flow.
- **Weakness** = Adding new product to all families is painful (modify all factories). Adding new family is easy.
- **ISP on factory** = If not all families support a product, split the factory interface.
- **Use interface** (not abstract class) for the factory — caller owns the flow.

## Critical Example

```java
// Abstract Factory — guarantees consistent family
interface UIFactory {
    Button createButton();
    TextField createTextField();
}
class MaterialFactory implements UIFactory {
    public Button createButton() { return new MaterialButton(); }
    public TextField createTextField() { return new MaterialTextField(); }
}
class IOSFactory implements UIFactory {
    public Button createButton() { return new IOSButton(); }
    public TextField createTextField() { return new IOSTextField(); }
}

// Client — gets consistent family, doesn't know which
class LoginPage {
    private final UIFactory ui;
    LoginPage(UIFactory ui) { this.ui = ui; }
    void render() {
        ui.createButton();     // guaranteed same family
        ui.createTextField();  // guaranteed same family
    }
}
```

## How to Approach (The Grid Technique)

Before coding, spend 3 minutes drawing this:

```
Step 1: List PRODUCTS (interfaces) → these become COLUMNS
Step 2: List FAMILIES (groups) → these become ROWS
Step 3: Fill the grid

              | ProductA     | ProductB     | ProductC     |
   Family1    | ConcreteA1   | ConcreteB1   |      -       |
   Family2    | ConcreteA2   | ConcreteB2   | ConcreteC2   |

Key formula:
   Rows (families)    = number of CONCRETE FACTORY classes
   Columns (products) = number of CREATE METHODS in the factory interface
   Cells with "-"     = signal to split factory interface (ISP)

Step 4: Determine FACTORY INTERFACES
   → Columns present in ALL rows → one factory interface with those create methods
   → Columns with "-" in some rows → separate interface (ISP)

Step 5: Determine CLIENTS
   → Who needs what? Depends only on factory interfaces they require.
```

### Applied to MediaPlayer Problem:

```
              | AudioDecoder | VideoDecoder | AudioOutput | VideoOutput |
   MP3        | Mp3Decoder   |      -       | Speaker     |      -      |
   FLAC       | FlacDecoder  |      -       | Speaker     |      -      |
   MP4        | Mp4Decoder   | Mp4Decoder   | Speaker     | Screen      |

Factories:
   AudioMediaFactory (createDecoder + createAudioOutput)  → all implement
   VideoMediaFactory (createVideoDecoder + createVideoOutput) → only MP4

Clients:
   AudioPlayer depends on AudioMediaFactory
   VideoPlayer depends on AudioMediaFactory + VideoMediaFactory
```

## Decision Framework

| Question | → Use |
|---|---|
| "Multiple objects must be from same family?" | Abstract Factory |
| "Only one object type varies?" | Simple Factory or Factory Method |
| "Not all families support all products?" | ISP — split factory interface |
| "I have a fixed flow with one varying creation step?" | Factory Method (not Abstract Factory) |
| "Caller wants objects back to use however they want?" | Abstract Factory (caller owns flow) |

### All Three Factories Compared

| | Simple Factory | Factory Method | Abstract Factory |
|---|---|---|---|
| Creates | ONE type | ONE type | FAMILY of types |
| Who decides | Factory method (switch) | Subclass override | Factory implementation |
| Who owns flow | Caller | Base class | Caller |
| OCP | Pragmatic (switch) | True (new subclass) | True (new factory) |
| When | Type depends on input | Creation step varies in a flow | Family consistency required |

## Interview Questions & Answers

**Q: "What's Abstract Factory?"**
A: "Creates families of related objects that must be compatible. One factory per family. Mixing products from different families is prevented by the factory structure."

**Q: "When Abstract Factory vs Simple Factory?"**
A: "Simple: one product varies. Abstract: MULTIPLE products must come from same family. If mixing is a bug, Abstract Factory."

**Q: "Downside?"**
A: "Adding new product to all families = modify all factory interfaces + all implementations. Adding new family = easy, just one new class."

**Q: "What if a family doesn't support a product?"**
A: "Apply ISP to the factory — split into core factory + optional capability interfaces. Compiler enforces which clients can use which factories."

**Q: "Abstract Factory vs Factory Method?"**
A: "Factory Method = one creation step inside a flow (base class owns it). Abstract Factory = multiple related create methods (caller owns the flow). Factory Method uses abstract class, Abstract Factory uses interface."

## Clues & Signals

- **Senior signal**: Drawing the Products × Families grid before coding
- **Senior signal**: Applying ISP when not all families support all products
- **Senior signal**: "Abstract Factory is a collection of Factory Methods grouped by family"
- **Red flag**: One massive factory interface that forces no-ops in some families
- **Red flag**: Mixing products from different families (the bug Abstract Factory prevents)
- **Key learning**: Use interface (not abstract class) for Abstract Factory — caller controls the flow

## Trade-offs

| Decision | Use Abstract Factory | Don't Use |
|---|---|---|
| Products MUST be same family | ✅ | |
| Only one product varies | | ✅ Simple Factory |
| Adding new families often | ✅ Easy | |
| Adding new products often | ❌ Painful | Reconsider design |
| Products can mix freely | | ✅ No family constraint |

## Quick Links

- **Detailed topic**: [Abstract Factory](../topics/design-patterns/creational/abstract-factory.md)
- **Related**: [Factory Method](../topics/design-patterns/creational/factory-method.md)
- **Related**: [ISP](../topics/solid/interface-segregation.md)
- **Related**: [DIP](../topics/solid/dependency-inversion.md)
