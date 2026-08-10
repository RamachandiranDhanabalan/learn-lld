# Complete 80-Day LLD Course Plan

## Overview

| Phase | Days | Topic | Hours/Day |
|-------|------|-------|-----------|
| 1 | 01–07 | Object-Oriented Thinking + SOLID | ~90 min |
| 2 | 08–14 | Creational + Structural Patterns | ~90 min |
| 3 | 15–21 | Behavioral Patterns | ~90 min |
| 4 | 22–28 | Domain Modeling + UML | ~90 min |
| 5 | 29–35 | Core LLD Problems | ~2 hr |
| 6 | 36–42 | Complex Domain Problems | ~2 hr |
| 7 | 43–49 | Infrastructure LLD | ~2 hr |
| 8 | 50–55 | Production-Grade LLD | ~2 hr |
| 9 | 56–60 | Java Concurrency Deep Dive | ~90 min |
| 10 | 61–70 | Senior Production Track | ~90 min |
| 11 | 71–80 | Extended Problem Bank | ~2 hr |

---

## PHASE 1 — Object-Oriented Thinking + SOLID (Days 1–7)

### Day 01 — What is LLD? Requirements → Design Flow
- The full LLD interview thinking process
- Requirements → Use Cases → Domain Model → Responsibilities → Relationships → Interfaces → Interactions → Implementation → Concurrency/Failure/Extensibility
- Exercise: Domain model for "Online Shopping Cart" (no code)

### Day 02 — Encapsulation + Abstraction
- State ownership, behavior, information hiding
- `order.markAsPaid()` vs `order.status = "PAID"`
- Exercise: Build `BankAccount` with deposit/withdraw/transfer preventing invalid states

### Day 03 — Composition vs Inheritance
- "is-a" vs "has-a" vs "uses-a"
- Fragile base class problem
- Exercise: Design `NotificationService` — first with inheritance, then redesign with composition

### Day 04 — Cohesion + Coupling + Code Smells
- God class, Feature Envy, Shotgun Surgery, Divergent Change
- Law of Demeter, Tell Don't Ask
- Exercise: Create intentionally bad `OrderService` with 6 responsibilities, then refactor

### Day 05 — Single Responsibility Principle
- "One reason to change" (not "one method")
- Exercise: Refactor Day 4's OrderService into OrderService, PaymentService, InvoiceService, NotificationService, OrderRepository

### Day 06 — Open/Closed + Liskov Substitution
- Extension without modification
- Subtypes must be substitutable
- Exercise: Replace `if (customerType == GOLD)...` with DiscountStrategy hierarchy

### Day 07 — Interface Segregation + Dependency Inversion + Weekly Mock
- Fat interface problem, interface splitting
- Constructor injection, programming to interfaces
- Weekly Mock: Design a Notification System in 45 min

---

## PHASE 2 — Creational + Structural Patterns (Days 8–14)

### Day 08 — Factory Method
- Problem: client code shouldn't know concrete classes
- Simple Factory vs Factory Method vs static factory
- Exercise: `PaymentProcessorFactory` → CreditCard, UPI, Wallet

### Day 09 — Abstract Factory
- Families of related objects
- When it's justified vs over-engineering
- Exercise: `CloudResourceFactory` for AWS/Azure families

### Day 10 — Builder
- Telescoping constructor problem
- When Builder is unnecessary (< 4 params, no optional)
- Exercise: `SearchRequest.builder().query().filters().sort().page().build()`

### Day 11 — Singleton
- Thread safety approaches (eager, lazy, double-checked, enum)
- Why Spring DI makes manual Singleton unnecessary
- Testability problems with global state

### Day 12 — Adapter
- Third-party API with incompatible interface
- Exercise: `PaymentGateway` → StripeAdapter, RazorpayAdapter, MockAdapter

### Day 13 — Decorator + Proxy
- Adding behavior without modification
- Decorator vs Proxy distinction
- Exercise: PaymentService + LoggingDecorator + MetricsDecorator + CachingProxy

### Day 14 — Facade + Composite + Weekly Review
- Facade: Simplifying complex subsystem
- Composite: Tree structures (file system)
- Exercise: In-memory file system with Directory/File using Composite
- Review: Explain all 7 patterns without notes

---

## PHASE 3 — Behavioral Patterns (Days 15–21)

### Day 15 — Strategy
- Interchangeable algorithms
- When enum/switch is simpler and better
- Exercise: `PricingStrategy` → Normal, Weekend, Festival pricing

### Day 16 — Observer
- Event notification, publisher/subscriber
- Sync Observer vs Async Event
- Exercise: Order events → EmailListener, SMSListener, AnalyticsListener

### Day 17 — State
- Object lifecycle management
- Valid transitions, invalid transition handling
- Exercise: Order states (CREATED → PAID → SHIPPED → DELIVERED + CANCELLED/REFUNDED)

### Day 18 — Command
- Encapsulating requests as objects
- Undo/redo, queue processing
- Exercise: CreateOrder, CancelOrder, RefundOrder commands + CommandQueue

### Day 19 — Chain of Responsibility
- Request pipelines, filter chains
- Exercise: Authentication → Authorization → Validation → RateLimit → BusinessLogic

### Day 20 — Template Method + Iterator
- Algorithm skeleton with customizable steps
- Strategy vs Template Method decision
- Exercise: Abstract batch job template with hook methods

### Day 21 — Pattern Selection Day
- No new pattern. Practice choosing the RIGHT (simplest) tool
- Given "We need multiple pricing algorithms" → consider if/else, enum, Strategy, config, rules engine
- Key skill: "Why not just use if/else?" answer

---

## PHASE 4 — Domain Modeling + UML (Days 22–28)

### Day 22 — Entity vs Value Object
- Entity: has identity (OrderId, CustomerId)
- Value Object: defined by value (Money, Address, DateRange)
- Exercise: Model e-commerce order with correct entity/VO classification

### Day 23 — Aggregates + Ownership + Domain Services
- Aggregate = consistency boundary
- Aggregate Root = entry point
- Domain Service = logic that doesn't belong to one entity
- Exercise: Should OrderItem be independently modifiable? Why/why not?

### Day 24 — UML Relationships + Generalization + Activity Diagrams
- Association, Aggregation, Composition (with multiplicity)
- Generalization vs Inheritance
- Activity Diagrams: decision nodes, fork/join, parallel paths
- Exercise: Activity diagram for "checkout flow"

### Day 25 — Class Diagrams
- Full class diagram for Parking Lot
- Attributes, methods, relationships, multiplicity, interfaces

### Day 26 — Sequence + Activity Diagrams
- Sequence: object interactions over time (Movie Booking flow)
- Activity: parallel paths + decisions (Payment processing)

### Day 27 — State Diagrams
- Payment lifecycle with valid/invalid transitions
- Exercise: Draw state diagram for INITIATED → PENDING → SUCCESS/FAILED → REFUND_PENDING → REFUNDED

### Day 28 — Full OOAD Exercise
- Order Management System: Requirements → Use Cases → Entities → Class Diagram → Sequence → Activity → State
- No code. Pure design exercise.

---

## PHASE 5 — Core LLD Problems (Days 29–35)

### Day 29 — Parking Lot
- Entity modeling, Composition, Factory, Strategy (pricing), Spot allocation
- Extensions: EV spots, multiple entrances, dynamic pricing

### Day 30 — Vending Machine
- State pattern, Inventory, Payment, Change calculation
- Critical: Payment succeeds but product dispensing fails

### Day 31 — Library Management System
- Repository pattern, Search, Borrow/Return, Fine calculation, Notification
- Extensions: Digital books, reservation queue

### Day 32 — ATM
- State machine, Transaction, Cash dispenser, Card validation
- Critical: Network failure mid-transaction

### Day 33 — Elevator System
- State, Command, Scheduling algorithms (SCAN, LOOK, Nearest)
- Compare scheduling approaches

### Day 34 — Elevator — Concurrency + Multiple Elevators
- Multiple elevators, concurrent button presses
- Assignment strategy, load balancing

### Day 35 — Weekly Mock
- Elevator System, 45 min, no solution lookup

---

## PHASE 6 — Complex Domain Problems (Days 36–42)

### Day 36 — Movie Ticket Booking — Domain
- Movie, Theatre, Screen, Show, Seat, Booking, Payment
- Full domain model + class diagram

### Day 37 — Movie Ticket Booking — Concurrency
- Two users book same seat simultaneously
- Optimistic locking, pessimistic locking, temporary reservation, expiration

### Day 38 — Splitwise — Split Types
- EqualSplit, ExactSplit, PercentageSplit using Strategy
- Domain modeling with User, Group, Expense, Split

### Day 39 — Splitwise — Settlement
- Debt graph, simplification algorithm, net-balance calculation
- Extension: Settle via payment provider

### Day 40 — Ride Sharing (Uber) — Domain
- Customer, Driver, Ride, Matching, Location, Vehicle
- Assignment: nearest driver, rating-based, load-balanced

### Day 41 — Ride Sharing — Strategy + State
- Dynamic pricing (surge), ETA calculation
- Ride lifecycle: REQUESTED → MATCHED → PICKUP → IN_PROGRESS → COMPLETED/CANCELLED

### Day 42 — Weekly Mock
- Random from Phase 6, 45 min

---

## PHASE 7 — Infrastructure LLD (Days 43–49)

### Day 43 — LRU Cache
- HashMap + DoublyLinkedList implementation
- get(), put(), evict()

### Day 44 — Thread-Safe Cache
- ConcurrentHashMap alone isn't enough — why?
- Add: TTL, max size, thread safety with locks

### Day 45 — Rate Limiter — Fixed + Sliding Window
- Fixed window: simple but boundary burst
- Sliding window: more accurate but more state

### Day 46 — Rate Limiter — Token + Leaky Bucket
- Token Bucket: flexible, allows bursts
- Leaky Bucket: smooth output, may delay
- Compare all 4 approaches with trade-offs

### Day 47 — Logger
- Logger, LogLevel, Appender (Console, File), Formatter
- Is Logger Singleton? Is it thread safe?

### Day 48 — Async Logger
- BlockingQueue + Producer/Consumer
- How to avoid logging blocking application threads
- ExecutorService for async dispatch

### Day 49 — Weekly Mock
- Design LRU Cache, 45 min, then add TTL + thread safety

---

## PHASE 8 — Production-Grade LLD (Days 50–55)

### Day 50 — Notification System — Design
- Email/SMS/Push channels
- Factory (channel creation), Strategy (delivery), Observer (event trigger)

### Day 51 — Notification — Reliability
- Retry with exponential backoff
- Timeout, fallback channel, Dead Letter Queue
- Sync vs async delivery trade-off

### Day 52 — Payment System — Domain
- Payment, PaymentMethod, PaymentProvider, PaymentAttempt, Refund
- States: INITIATED → PENDING → SUCCESS/FAILED → REFUND_PENDING → REFUNDED

### Day 53 — Payment — Reliability
- Idempotency keys, duplicate request handling
- Webhook processing, reconciliation
- Provider failure → retry vs fail

### Day 54 — Task Scheduler — Design
- Job, Trigger, Scheduler, Executor, RetryPolicy
- PriorityQueue + ExecutorService + Command pattern

### Day 55 — Task Scheduler — Extensions
- Priority, delayed jobs, cron expressions
- Retry with backoff, failure handling, concurrency control

---

## PHASE 9 — Java Concurrency Deep Dive (Days 56–60)

### Day 56 — Threads + ExecutorService
- Thread, Runnable, Callable, Future
- ThreadPoolExecutor configuration
- Exercise: Parallel order processing

### Day 57 — Synchronization + Locks
- synchronized, volatile, Lock, ReentrantLock, ReadWriteLock
- Exercise: Thread-safe counter, reader-writer problem

### Day 58 — Concurrent Collections
- ConcurrentHashMap, BlockingQueue, AtomicInteger, AtomicReference
- Exercise: Producer/Consumer implementation

### Day 59 — CompletableFuture
- thenApply, thenCompose, thenCombine, exceptionally, handle
- Exercise: Order → Inventory → Payment → Notification with parallelism

### Day 60 — Concurrency Interview Questions
- Race condition, deadlock, starvation, livelock
- Visibility, atomicity, happens-before
- Lock contention, thread pool exhaustion, backpressure

---

## PHASE 10 — Senior Production Track (Days 61–70)

### Day 61 — REST API Design + Idempotency
- HTTP semantics, proper status codes, request/response DTOs
- Idempotency keys: `POST /payments` with `Idempotency-Key: abc123`

### Day 62 — Database Transactions + Locking
- ACID properties, isolation levels
- Transaction boundaries, propagation in Spring

### Day 63 — Optimistic vs Pessimistic Locking
- Version columns (`@Version`), `SELECT FOR UPDATE`
- When to use each, contention scenarios

### Day 64 — Cache Consistency Patterns
- Cache-aside, write-through, write-behind, read-through
- Invalidation strategies, thundering herd

### Day 65 — Retry + Exponential Backoff + Jitter
- RetryPolicy, BackoffStrategy (fixed, exponential, decorrelated jitter)
- When to stop retrying, circuit breaker integration

### Day 66 — Circuit Breaker + Bulkhead
- States: CLOSED → OPEN → HALF_OPEN
- Bulkhead: thread pool isolation, semaphore isolation

### Day 67 — Event-Driven Design
- Internal event bus, ApplicationEventPublisher (Spring)
- Domain events, loose coupling within a service

### Day 68 — Domain Events + Saga Pattern
- Choreography vs Orchestration
- Compensation / rollback logic

### Day 69 — Designing for Testability
- DI, interfaces, test doubles (mock, stub, fake, spy)
- Exercise: Refactor a tightly coupled class for testability

### Day 70 — Senior-Level Mock Interview
- Unknown problem, 45 min, full production-grade discussion

---

## PHASE 11 — Extended Problem Bank (Days 71–80)

### Day 71 — Amazon Locker Service
- State machine (available → reserved → occupied → available)
- Locker assignment by package size, expiration

### Day 72 — Meeting Scheduler / Calendar System
- Time slot management, conflict detection, room allocation
- Multi-participant availability checking

### Day 73 — Car Rental System
- Vehicle inventory, reservation lifecycle, pricing
- Return handling, damage assessment

### Day 74 — Amazon Online Shopping (Full E-commerce)
- Catalog, Cart, Order, Payment, Shipping, Returns
- Full end-to-end with all patterns

### Day 75 — Stack Overflow
- Question, Answer, Comment, Vote, Reputation, Tag, Search
- Reputation system design, moderation

### Day 76 — Social Network (Facebook/LinkedIn)
- User, Connection/Friend, Post, Feed, Notification
- Feed generation algorithm, friend graph

### Day 77 — Online Stock Brokerage
- Account, Portfolio, Order (Market/Limit/Stop), Trade
- Real-time market data, order matching

### Day 78 — Restaurant Management
- Table, Reservation, Order, MenuItem, Kitchen Queue, Bill
- Kitchen order prioritization

### Day 79 — Airline Management
- Flight, Booking, Seat, Passenger, Boarding, Cancellation
- Overbooking strategy, seat assignment

### Day 80 — Final Unknown-Problem Mock Interview
- Truly random problem (pick from: Insurance Claim, Music Streaming, Gym Membership, Subscription Box, Online Exam)
- 45 min, full senior-level discussion
