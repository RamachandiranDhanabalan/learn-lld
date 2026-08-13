# LLD — What It Is and How to Approach It

## What is Low-Level Design?

LLD is the process of designing the **internal structure** of a single service or module — classes, interfaces, methods, relationships, and interactions.

It answers: **"How is this one component built internally?"**

---

## HLD vs LLD

| Aspect | High-Level Design (HLD) | Low-Level Design (LLD) |
|--------|------------------------|------------------------|
| Scope | Entire system architecture | Single service / module |
| Output | Architecture diagram, service boundaries | Class diagrams, interfaces, method signatures |
| Concern | Which services, how they communicate | How one service is structured internally |
| Scale question | "Handle 1M requests/sec?" | "Add a new payment type without changing existing code?" |
| Key decision | "Use a queue between A and B" | "Use Strategy pattern for pricing algorithms" |
| Interviewer expects | Service decomposition, data flow | Clean class hierarchy, patterns named, trade-offs |

---

## What Interviewers Evaluate (9+ YOE)

| Criteria | What They Look For | Weight |
|----------|-------------------|--------|
| Requirements Gathering | Do you ask clarifying questions or jump in? | High |
| Entity Identification | Correct nouns → classes, verbs → methods | High |
| Relationship Modeling | HAS-A vs IS-A used correctly | High |
| Pattern Recognition | Name the pattern AND explain WHY it fits | Very High |
| Extensibility | "If tomorrow we need X, what changes?" | Very High |
| Concurrency Awareness | "What if two users do this simultaneously?" | Medium-High |
| Trade-off Articulation | "I chose X over Y because..." | Very High |

---

## The 45-Minute Interview Flow

```
Minutes 0–5:   REQUIREMENTS
├── Ask 3–5 clarifying questions
│   • Functional: What features?
│   • Non-functional: Concurrent? Real-time? Scale?
│   • Scope: What's IN and OUT?
├── State scope summary out loud
└── Identify "the heart" — where complexity lives

Minutes 5–15:  DOMAIN MODEL
├── Entities: nouns from requirements → classes
├── Actions: verbs → methods
├── Relationships + cardinality (HAS-A, IS-A, 1:N, M:N)
└── Sketch class diagram (whiteboard or verbal)

Minutes 15–35: DETAILED DESIGN
├── Write key interfaces and classes
├── Implement core method logic
├── Apply patterns (name them explicitly + WHY)
├── Handle edge cases
└── Focus 60% effort on "the heart"

Minutes 35–45: TRADE-OFFS + EXTENSIONS
├── What you chose and why
├── Alternative approaches and when they'd be better
├── Concurrency concerns
├── "If we add feature X tomorrow, we just..."
└── Summarize design decisions
```

---

## The Noun/Verb Technique

Given a problem statement, extract:

- **Nouns** → Candidate classes/entities
- **Verbs** → Candidate methods
- **Adjectives/states** → Candidate enums or attributes
- **Relationships** → HAS-A, IS-A, M:N

Example: "A user can search songs, play them, create playlists, and add to favorites"

```
Nouns    → User, Song, Playlist, MusicPlayer, Queue, Artist
Verbs    → search(), play(), pause(), skip(), createPlaylist(), addToFavorites()
States   → PLAYING, PAUSED, IDLE (playback states)
Relations → User HAS-A MusicPlayer, Playlist HAS-MANY Songs
```

---

## Finding "The Heart"

Before designing everything equally, ask: **"Where does the most complex behavior live?"**

| System | The Heart (design deeply) | The Periphery (sketch lightly) |
|--------|--------------------------|-------------------------------|
| Spotify | MusicPlayer (state + queue) | Playlist CRUD, User profile |
| Parking Lot | Parking strategy + spot allocation | Payment, Entry/Exit gates |
| Elevator | Scheduling algorithm + state machine | Floor buttons, Door open/close |
| Splitwise | Debt simplification graph | User CRUD, Group management |

Spend 60% of your design time on the heart. Sketch the rest.

---

## Design Patterns — Categories

| Category | Purpose | Common Interview Patterns |
|----------|---------|--------------------------|
| **Creational** | Object creation with flexibility | Factory, Builder, Singleton |
| **Structural** | Organizing classes into larger structures | Adapter, Decorator, Composite |
| **Behavioural** | Communication between objects | Strategy, Observer, State, Command, Chain of Responsibility |

**Key insight**: Patterns are NOT code to memorize. They are solutions to **design pressures**:
- "I need to swap algorithms at runtime" → Strategy
- "Object behavior changes with state" → State
- "Notify multiple dependents of a change" → Observer
- "I need to add behavior without modifying existing classes" → Decorator

---

## Algorithm vs Design Pattern

| Aspect | Algorithm | Design Pattern |
|--------|-----------|---------------|
| What | Precise steps to achieve a result | Blueprint for structuring code |
| Analogy | A recipe — follow step by step | A floor plan — adapt to your needs |
| Output | Same result every time | Implementation varies per context |
| Example | Binary Search, BFS | Strategy, Observer, State |

---

## Clarifying Questions — What Good Looks Like

**Low value** (don't waste time): "What language should I use?" "Should I write tests?"

**High value** (shapes your design):
- "Is this single-user or concurrent?" → determines locking strategy
- "Can this change at runtime?" → composition vs inheritance
- "Do we need to support multiple types of X?" → strategy or factory
- "Should actions be undoable?" → command pattern
- "Who needs to know when X happens?" → observer pattern
- "What are the states and valid transitions?" → state pattern

---

## Resources

- [Refactoring Guru — What is a Pattern?](https://refactoring.guru/design-patterns/what-is-pattern)
- [awesome-low-level-design (GitHub)](https://github.com/ashishps1/awesome-low-level-design)
- [Design Patterns Overview](../design-patterns/README.md)

## Related

- [OOP Principles](../oops/README.md)
- [SOLID Principles](../solid/README.md)
- [UML Diagrams](../uml/README.md)
