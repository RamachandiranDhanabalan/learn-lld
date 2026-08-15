---
inclusion: always
---

# Content Strategy — Topics vs Daily Learnings

## Two Layers — Different Purposes

### Layer 1: `topics/` — Detailed Knowledge Base (the textbook)

**Purpose**: Comprehensive reference documents. Anyone should understand the concept fully by reading only this file.

**File naming**: One file per concept, named clearly:
- `topics/oops/encapsulation.md`
- `topics/oops/abstraction.md`
- `topics/oops/composition-vs-inheritance.md`
- `topics/design-patterns/behavioural/strategy.md`
- `topics/lld-approach/README.md`

**Folder README.md**: Lightweight overview that links to individual topic files. NOT a dump of all content.

**Structure per topic file**:
```markdown
# [Topic Name]

## Definition
- Clear, concise definition
- What it IS vs what it is NOT

## WHY It Matters
- Benefits (table format preferred)

## Detailed Explanation
- Full walkthrough with nuances
- Multiple levels of examples (bad → better → best)

## Examples (Java)
- Complete, runnable code (not snippets)
- Show both ❌ BAD and ✅ GOOD approaches
- Include comments explaining WHY each is bad/good

## Common Violations / Mistakes
- What goes wrong in practice
- How to fix each violation

## When to Use / When NOT to Use
- Decision criteria
- Trade-offs table

## Resources
- External links (Refactoring Guru, Baeldung, books)

## Related
- Cross-links to other topic files
```

**Rules for topics/**:
1. Write for someone learning the concept for the FIRST time
2. Be thorough — edge cases, nuances, gotchas, multiple examples
3. Code examples must be complete enough to understand without context
4. One concept per file — don't combine unrelated concepts
5. Update topic files as new learnings add depth over time
6. Folder README.md is an index, not the content itself

---

### Layer 2: `daily-learnings/day-XX.md` — Cheat Sheet (the revision card)

**Purpose**: Rapid revision before an interview. Dense, scannable, interview-focused. NOT a conversation log. NOT a duplicate of topics/.

**Structure**:
```markdown
# Day XX — [Topic Title]

## Cheat Sheet
- Crisp 1-line definitions
- Key rules / mnemonics (bullet points)

## Critical Examples
- The ONE code example that explains everything (minimal, memorable)
- Good vs Bad side-by-side (smallest possible code)

## Decision Framework
- When to use X vs Y (table or flowchart)
- Questions to ask yourself

## Interview Questions & Answers
- Q: "exact interviewer question"
- A: "exact answer you'd give" (2-3 sentences max, includes WHY)

## Clues & Signals
- What interviewers look for
- Senior signals to demonstrate
- Red flags to avoid

## Trade-offs (table format)

## Quick Links
- Links to detailed topic files in topics/
```

**Rules for daily-learnings/**:
1. Max ~2 pages of mental reading — keep it SCANNABLE
2. Don't duplicate topic content — reference it via links
3. Every file MUST have Interview Q&A section
4. Examples should be the MINIMAL code to demonstrate the point
5. Write for someone revising at 11pm before an interview
6. NO narrative ("today I learned", "we discussed") — just facts

---

## End-of-Day Practice Problem

After each day's lesson and cheat sheet creation, give a **cumulative practice problem** that:

1. Is a piece of code or a small design problem (not a full LLD like Parking Lot)
2. Requires applying ALL concepts learned so far (not just today's topic)
3. Gets progressively harder as days progress
4. **Do NOT hint which concepts/days/topics to apply** — present the problem cold, like an interview
5. After their answer, validate and score against all applicable concepts

Format:
- Present the problem code / scenario with zero hints
- Ask: "Analyze this. What's wrong? How would you fix it?"
- After their answer, validate, score, and connect back to relevant concepts
- Show the ideal solution

This builds muscle memory — the learner must independently recognize which concepts apply without being told.

---

## How They Connect

| When learning a new day | What happens |
|------------------------|--------------|
| During the lesson | Full explanation is written to `topics/<area>/<concept>.md` |
| After the lesson | Cheat sheet is written to `daily-learnings/day-XX.md` |
| Cheat sheet links to | `../topics/<area>/<concept>.md` for deep details |

**Example for Day 2 (Encapsulation + Abstraction):**
- `topics/oops/encapsulation.md` — full reference (violations, fixes, all examples)
- `topics/oops/abstraction.md` — full reference (interface vs abstract, YAGNI, polymorphism)
- `daily-learnings/day-02.md` — cheat sheet with 1 critical example each, interview Q&A, links to both topic files

---

## Folder Structure Convention

```
topics/
├── lld-approach/
│   └── README.md              ← Day 1 topic
├── oops/
│   ├── README.md              ← Index linking to files below
│   ├── encapsulation.md       ← Day 2 topic
│   ├── abstraction.md         ← Day 2 topic
│   └── composition-vs-inheritance.md  ← Day 3 topic
├── solid/
│   ├── README.md              ← Index
│   ├── single-responsibility.md
│   ├── open-closed.md
│   └── ...
├── design-patterns/
│   ├── creational/
│   ├── structural/
│   └── behavioural/
├── domain-modeling/
├── concurrency/
└── production-patterns/
```

**Naming rules:**
- Use kebab-case: `composition-vs-inheritance.md`, not `compositionVsInheritance.md`
- One concept per file, named after the concept
- README.md in folders is an INDEX, not content
- Create new folders under `topics/` when a new category emerges

---

## When Updating Existing Topic Files

As the course progresses, earlier topic files may get enriched:
- Day 15 (Strategy pattern) might add examples to `topics/oops/composition-vs-inheritance.md`
- Day 56 (Concurrency) might add thread-safety notes to `topics/oops/encapsulation.md`

This is expected and encouraged — topics/ is a living knowledge base, not frozen after first creation.
