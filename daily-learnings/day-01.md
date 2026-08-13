# Day 01 — What is LLD? Requirements → Design Flow

## Cheat Sheet

- **LLD** = Internal structure of one module (classes, interfaces, methods, relationships)
- **HLD** = System-wide architecture (services, databases, queues, communication)
- **Design Pattern** = Proven blueprint for a recurring design problem (NOT code)
- **3 Categories** = Creational (making objects) | Structural (organizing) | Behavioural (communicating)
- **Algorithm vs Pattern** = Algorithm = same steps, same result. Pattern = structural guidance, implementation varies.

## Critical Examples

### The 45-min Interview Flow
```
0–5 min:  Requirements → clarify, scope, find "the heart"
5–15 min: Domain model → nouns=classes, verbs=methods, relationships
15–35 min: Design → interfaces, classes, patterns (NAME them + WHY)
35–45 min: Trade-offs → what I chose, why, alternatives, extensibility
```

### Noun/Verb Technique (one example to remember)
```
"User can search songs, play them, create playlists, add to favorites"

Nouns  → User, Song, Playlist, MusicPlayer, Queue
Verbs  → search(), play(), pause(), skip(), addToFavorites()
States → PLAYING, PAUSED, IDLE
```

## Decision Framework

> **"Where does the most complex behavior live?"** → Design that deeply. Sketch the rest.

| System | The Heart | The Periphery |
|--------|-----------|---------------|
| Spotify | MusicPlayer (state + queue) | Playlist CRUD |
| Parking Lot | Spot allocation strategy | Payment |
| Elevator | Scheduling + state machine | Door logic |

## Interview Questions & Answers

**Q: "What's the difference between HLD and LLD?"**
A: "HLD = system-level architecture (which services, how they talk). LLD = internal structure of one service (classes, interfaces, patterns)."

**Q: "How do you start an LLD problem?"**
A: "Clarifying questions first — scope the problem, identify constraints. Then entities via noun/verb technique, find the heart, design that deeply."

**Q: "What's the first thing you do?"**
A: "Ask clarifying questions. Scope is everything. 5 minutes of requirements saves 15 minutes of rework."

**Q: "Code or design — what's more important?"**
A: "Design. Structure, extensibility, and trade-offs > compiling code."

## Clues & Signals

- **Senior signal**: Ask questions that SHAPE the design — "Is it concurrent?" "Can it change at runtime?" "Do we need undo?"
- **Red flag**: Jumping straight into code without asking questions
- **Red flag**: Designing all entities with equal depth (shows no prioritization)
- **Senior signal**: Explicitly stating scope — "X is in, Y is out for now"

## Trade-offs

| Decision | A | B | Guidance |
|----------|---|---|----------|
| Plan vs code immediately | Plan first | Jump in | Always plan. 5 min saves 15 min |
| Depth vs breadth | One area deep | Everything light | Both: sketch all, depth on heart |
| Perfect vs time-boxed | Complete + polished | Good enough + extensible | "Good enough + extensible" wins in interviews |

## Quick Links

- **Detailed topic**: [LLD Approach](../topics/lld-approach/README.md)
- **Design Patterns catalog**: [topics/design-patterns/](../topics/design-patterns/README.md)
- **OOP Principles**: [topics/oops/](../topics/oops/README.md)
