# LLD Course — Session Prompt

Copy-paste this into any new AI chat session (ChatGPT, Claude, Kiro, Gemini, etc.) to instantly restore context.

---

## The Prompt

```
I'm following a structured 80-day LLD (Low-Level System Design) interview preparation course. I'm a Java backend engineer with 9+ years of experience (Spring Boot, AWS, PostgreSQL, batch processing).

**Course format**: When I say "Day N", give me a complete lesson with:
- 🎯 Objective
- 📚 Exact resources (links, no searching)
- 🧠 Concepts with Java examples
- 💡 Worked example
- 💻 Coding exercise
- 🏢 Production connection
- 🎤 Interview questions
- ⚖️ Trade-offs
- ✅ End-of-day checkpoint

**Core principles**:
1. Problem → Pressure → Pattern (WHY, not memorization)
2. Appropriate simplicity > over-engineering
3. Always discuss: concurrency, failures, extensibility
4. Trade-offs: What I chose / Why / Alternative / When it breaks
5. Ratio: 25% reading, 60% implement, 15% review

**Problem Template (Day 29+)**: Requirements → Use Cases → Domain Model → Class Diagram → Sequence/Activity Diagrams → State Diagram → Patterns Applied (+ why) → Concurrency/Failures → Extensibility → Trade-offs

**Curriculum**:
- Days 01–07: OOP + SOLID
- Days 08–14: Creational + Structural Patterns
- Days 15–21: Behavioral Patterns
- Days 22–28: Domain Modeling + UML
- Days 29–35: Core LLD (Parking Lot, Vending Machine, Library, ATM, Elevator)
- Days 36–42: Complex (Movie Booking, Splitwise, Ride Sharing/Uber)
- Days 43–49: Infrastructure (LRU Cache, Rate Limiter, Logger)
- Days 50–55: Production (Notification, Payment, Task Scheduler)
- Days 56–60: Java Concurrency (Threads, Locks, CompletableFuture)
- Days 61–70: Senior Track (API Design, DB Locking, Circuit Breaker, Events, Testability)
- Days 71–80: Extended (Amazon Locker, Meeting Scheduler, Car Rental, E-commerce, Stack Overflow, Social Network, Stock Brokerage, Restaurant, Airline, Final Mock)

**Resources** (fixed, don't make me search):
- Patterns: refactoring.guru/design-patterns
- Java OOP: docs.oracle.com/javase/tutorial/java/concepts/
- Concurrency: docs.oracle.com/javase/tutorial/essential/concurrency/
- Problems: github.com/ashishps1/awesome-low-level-design
- Java: baeldung.com
- Enterprise: martinfowler.com/eaaCatalog/

I'm on **Day [X]**. Deliver that lesson.
```

---

## Additional Prompts

### For Code Review
```
Review my LLD implementation as a senior interviewer would. Point out:
- SOLID violations
- Missing patterns or over-engineering
- Concurrency issues
- Extensibility problems
- Trade-offs I didn't consider
```

### For Mock Interview
```
Run a 45-minute LLD mock interview with me. 
Problem: [PROBLEM NAME]
Act as the interviewer. Ask clarifying questions when I make assumptions.
Push back on my design decisions. Ask follow-up extensions.
At the end, score me on: domain modeling, patterns, concurrency, extensibility, trade-offs.
```

### For Quick Recap
```
Give me a quick recap of what I should know after completing Days [X] to [Y] of my LLD course.
Focus on: key concepts, patterns learned, and what I should be able to explain without notes.
```

### For Trade-off Discussion
```
I chose [DECISION] in my design. Play devil's advocate:
- Why might this be wrong?
- What alternative would you suggest?
- Under what conditions does my choice break?
- How would I migrate if requirements change?
```
