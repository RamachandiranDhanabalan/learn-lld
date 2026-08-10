# Day 01 — What is LLD? The Requirements → Design Flow

## Core Concepts

### What is Low-Level Design (LLD)?

LLD is the process of designing the internal structure of a single service or module — classes, interfaces, methods, relationships, and interactions. It answers: **"How is this one component built internally?"**

| Aspect | High-Level Design (HLD) | Low-Level Design (LLD) |
|--------|------------------------|------------------------|
| Scope | Entire system architecture | Single service / module |
| Output | Architecture diagram, service boundaries | Class diagrams, interfaces, method signatures |
| Concern | Which services, how they communicate | How one service is structured internally |
| Scale question | "Handle 1M requests/sec?" | "Add a new payment type without changing existing code?" |
| Key decision | "Use a queue between A and B" | "Use Strategy pattern for pricing algorithms" |

### What are Design Patterns?

Proven, reusable solutions to commonly recurring problems in software design. They are **not code** — they are templates/blueprints you adapt to your specific context.

### Algorithm vs Design Pattern

| Aspect | Algorithm | Design Pattern |
|--------|-----------|---------------|
| What | Precise sequence of steps to achieve a result | High-level blueprint for structuring code |
| Analogy | A recipe — follow step by step | A floor plan — adapt to your needs |
| Output | Same result every time | Implementation varies per context |
| Example | Binary Search, Dijkstra's | Strategy, Observer, State |

### Three Categories of Design Patterns

| Category | Purpose | Example |
|----------|---------|---------|
| **Creational** | Object creation with flexibility and reusability | Factory, Builder, Singleton |
| **Structural** | Organizing classes/objects into larger structures | Adapter, Decorator, Composite |
| **Behavioural** | Communication and responsibility assignment between objects | Strategy, Observer, State, Command |

---

## Key Rules / Things to Remember

1. **Problem → Pressure → Pattern** — Don't memorize patterns. Understand what design pressure leads to needing them.
2. **Find the "heart"** — Before designing everything equally, ask: "Where does the most complex behavior live?" Spend 60% of your effort there.
3. **IS-A vs HAS-A test** — "Can this thing change at runtime?" If yes → HAS-A (composition). If no, true type hierarchy → IS-A (inheritance).
4. **Domain verbs over CRUD** — push past add/remove/update. What does the system *do*? Play, skip, shuffle, retry, notify. Those verbs reveal where patterns hide.
5. **Scope before solving** — State what's IN and OUT before you design. This shows maturity.

---

## The LLD Interview Flow (45 Minutes)

```
Minutes 0–5:   REQUIREMENTS
├── Ask 3–5 clarifying questions
├── Define scope (IN and OUT)
└── Identify non-functional needs (concurrency? real-time? scale?)

Minutes 5–15:  DOMAIN MODEL
├── Entities (nouns from requirements)
├── Actions (verbs → methods)
├── Relationships + cardinality
└── Identify "the heart" — where complexity lives

Minutes 15–35: DETAILED DESIGN
├── Key interfaces and classes
├── Core method logic (pseudocode or real code)
├── Apply patterns (name them + say WHY)
└── Handle edge cases

Minutes 35–45: TRADE-OFFS + EXTENSIONS
├── What you chose and why
├── Concurrency concerns
├── "If we add X tomorrow, we just..."
└── Summarize design decisions
```

---

## Examples (Java)

### Entity Identification — Noun/Verb Technique

Given: "A user can search songs, play them, create playlists, and add to favorites"

**Nouns → Entities:**
```
User, Song, Playlist, MusicPlayer, Queue, Artist, Album
```

**Verbs → Methods:**
```
search(), play(), pause(), skip(), createPlaylist(), addToFavorites(), addToQueue()
```

**Relationships:**
```
User HAS-A MusicPlayer (1:1)
User HAS-MANY Playlists (1:N)
Playlist HAS-MANY Songs (M:N, ordered)
MusicPlayer HAS-A Queue (1:1)
Song HAS-A Artist (N:1)
```

### Good vs Bad: IS-A vs HAS-A

```java
// ❌ BAD — User type can change at runtime, inheritance is wrong
class PremiumUser extends User { }
class FreeUser extends User { }
// Problem: User upgrades → you can't change an object's class at runtime

// ✅ GOOD — Composition, tier is an attribute that can change
class User {
    private Tier tier; // FREE, PREMIUM
    public void upgrade() { this.tier = Tier.PREMIUM; }
}
```

```java
// ❌ BAD — Song is not a Genre, it has a genre
class RockSong extends Song { }
class JazzSong extends Song { }
// Problem: What if a song is both Rock and Jazz?

// ✅ GOOD — Genre is an attribute
class Song {
    private Set<Genre> genres; // can have multiple
}
```

---

## Common Mistakes

| Mistake | Why It's Bad | Fix |
|---------|-------------|-----|
| Jump into code immediately | Shows lack of structure, miss edge cases | Always spend 5 min on requirements first |
| Treat all entities equally | Waste time on CRUD, miss the interesting parts | Find the "heart" — where complex behavior lives |
| Use IS-A for things that change | Creates rigid, unmodifiable hierarchies | Use HAS-A (composition) for anything mutable |
| Only identify CRUD verbs | Doesn't reveal design patterns or complexity | Think about domain actions: play, retry, route, match |
| Skip trade-off discussion | Biggest differentiator at senior level | Always end with: "I chose X over Y because Z" |
| Design everything in equal depth | 45 min is not enough for full coverage | Sketch the periphery, depth on the core |

---

## Interview Signals

- **When asked "How do you start an LLD problem?"** → "I start with clarifying questions to scope the problem. Then I identify entities and relationships, find where the complex behavior lives, and design that in depth with appropriate patterns."

- **When asked "How do you identify classes?"** → "Nouns from the requirements become classes. Verbs become methods. Then I look at relationships — HAS-A vs IS-A — and cardinality."

- **When asked "What's more important, code or design?"** → "Design. Interviewers evaluate your ability to structure a system for extensibility and clarity. Compiling code is secondary to demonstrating the right abstractions."

- **When asked "Why patterns?"** → "Patterns give us a shared vocabulary and proven solutions. But I never force a pattern — I apply one when I feel the design pressure that the pattern was invented to solve."

---

## Trade-offs

| Decision | Option A | Option B | Guidance |
|----------|----------|----------|----------|
| Plan first vs. code first | Spend 5 min scoping | Jump into classes | Always plan. 5 min saves 15 min of rework |
| Many small classes vs. few large | Small + focused (SRP) | Large + fewer files | Prefer small — each class = one reason to change |
| Perfect design vs. time-boxed | Complete + polished | Good enough + extensible | In interviews: "good enough + extensible" wins over "perfect but incomplete" |
| Depth vs. breadth | Design one area deeply | Cover everything lightly | Do both: sketch the whole, then depth on the "heart" |

---

## Quick Reference

- **LLD** = Internal structure of one module (classes, interfaces, methods)
- **Design Pattern** = Proven blueprint for a recurring design problem (not code)
- **3 Categories** = Creational (making objects), Structural (organizing), Behavioural (communicating)
- **Interview flow** = Requirements (5m) → Model (10m) → Design (20m) → Trade-offs (10m)
- **IS-A test** = "Can it change at runtime?" → If yes, use HAS-A
- **Find the heart** = Where does complex behavior live? Design that deeply.
- **Verbs reveal patterns** = skip, shuffle, retry, notify → these are design decisions

---

## References

- [Design Patterns Overview](../topics/design-patterns/README.md)
- [OOP Principles](../topics/oops/README.md)
- [Composition vs Inheritance](../topics/oops/composition-vs-inheritance.md)
- [Refactoring Guru — What is a Pattern?](https://refactoring.guru/design-patterns/what-is-pattern)
- [awesome-low-level-design (GitHub)](https://github.com/ashishps1/awesome-low-level-design)
