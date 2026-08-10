# Trade-off Decision Matrix

## Quick Revision for Interviews

| Decision | Option A | Option B | When A Wins | When B Wins |
|----------|----------|----------|-------------|-------------|
| Inheritance vs Composition | Inheritance | Composition | True IS-A, stable hierarchy | Flexibility, multiple behaviors, runtime swap |
| Singleton vs DI | Singleton | Dependency Injection | No DI container available | Testability, flexibility (always prefer) |
| Pull vs Push (Observer) | Pull | Push | Observer needs subset of data | Simple notification, low coupling |
| Enum vs Strategy | Enum with methods | Strategy pattern | 2-3 fixed variations | 5+ variations, OCP needed |
| Sync vs Async | Synchronous | Asynchronous | Simple flow, consistency needed | Throughput, non-blocking, decoupling |
| In-memory vs External cache | In-memory (HashMap) | External (Redis) | Single instance, speed critical | Shared state, persistence, multiple instances |
| SQL vs NoSQL | Relational DB | Document/Key-Value | ACID, complex queries, joins | Schema flexibility, horizontal scale |
| Polling vs Webhooks | Polling | Webhooks/Push | Simple client, no server needed | Efficiency, real-time, reduced load |
| Optimistic vs Pessimistic lock | Optimistic | Pessimistic | Low contention, read-heavy | High contention, must-not-fail writes |
| Monolith vs Microservice | Monolith | Microservice | Early stage, small team, speed | Independent deploy, team boundaries, scale |
| Interface vs Abstract class | Interface | Abstract class | Multiple inheritance, contract only | Shared code + contract, template method |
| Checked vs Unchecked exception | Checked | Unchecked (Runtime) | Recoverable, caller must handle | Programming errors, don't force handling |
| Immutable vs Mutable | Immutable | Mutable | Thread safety, predictability | Performance (large objects), builders |

## Framework for Articulating Trade-offs

```
"I chose [X] over [Y] because:
  - In this context, [specific reason] matters more than [what Y offers]
  - If requirements change to [scenario], I'd migrate to [Y] by [how]
  - The risk of [X] is [downside], which I mitigate by [mitigation]"
```
