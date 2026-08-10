---
inclusion: always
---

# Daily Learnings Format

When creating or updating files in `daily-learnings/day-XX.md`, follow this format:

## Purpose
Each daily learning file is a **standalone cheat sheet** — not a conversation summary. Anyone reading it should understand the concepts, remember key points, and reference examples without needing prior context.

## Structure

```markdown
# Day XX — [Topic Title]

## Core Concepts
- Explain each concept clearly with definitions
- Include WHY it matters, not just WHAT it is
- Use bullet points for scanability

## Key Rules / Things to Remember
- Crisp, memorable rules (like "favor composition over inheritance")
- Include mnemonics or mental models if applicable

## Examples (Java)
- Concrete code examples demonstrating the concepts
- Show both GOOD and BAD approaches with explanation

## Common Mistakes
- What beginners get wrong
- What interviewers look for you to avoid

## Interview Signals
- Exact phrases that demonstrate understanding to an interviewer
- "When asked X, say Y because Z"

## Trade-offs
- Decision matrix or table if applicable

## Quick Reference
- One-liner summaries for rapid revision

## References
- Links to topics/ folder files (e.g., `../topics/oops/README.md`)
- External resource links used for this day
```

## Rules
1. Write for a reader who has ZERO context of the conversation
2. Concepts first, then examples, then interview tips
3. Keep it dense — no filler, no "today I learned" narrative
4. Code examples must be complete enough to understand without surrounding explanation
5. Cross-reference to `topics/` folder when a concept has a dedicated page there
6. Each file should work as a standalone revision card before an interview
