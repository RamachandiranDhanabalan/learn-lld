# Day 24 — Movie Booking (Reference Solution)

Validated class + activity diagrams. Open Markdown preview (Cmd+Shift+V) to render.

---

## Part A — Class Diagram

Relationship decisions:

| Relationship | Type | Multiplicity | Why |
|--------------|------|--------------|-----|
| MovieShow → Screen | association `-->` | `*` show / `1` screen | show references its screen; screen is independent, hosts many shows |
| MovieShow → Seat | **composition** `*--` | `1` show / `1..*` seats | a seat entry is meaningless without its show; owned + dies with it |
| Booking → Customer | association `-->` | `*` bookings / `1` customer | customer is a referenced peer aggregate |
| Booking → Seat | **aggregation** `o--` | `1` booking / `1..*` seats | booking *reserves* seats it does NOT own — seats belong to the show |
| Booking → Payment | association `-->` | `1` / `1` | booking uses one payment |
| Card/Wallet → Payment | **realization** `<\|..` | — | implements the interface (dashed triangle) |

Key insight: the same **Seat** is *composed by* MovieShow (owner) but only *aggregated by* Booking
(reserved, not owned). A child can be composed by exactly one parent; other references are aggregation/association.

```mermaid
classDiagram
    class MovieShow {
        -showId: String
        -startTime: LocalDateTime
    }
    class Screen {
        -screenId: String
    }
    class Seat {
        -seatNumber: String
    }
    class Booking {
        -bookingId: String
    }
    class Customer {
        -customerId: String
    }
    class Payment {
        <<interface>>
        +pay(amount) void
    }
    class CardPayment
    class WalletPayment

    MovieShow "*" --> "1" Screen : runs in
    MovieShow "1" *-- "1..*" Seat : has
    Booking "*" --> "1" Customer : made by
    Booking "1" o-- "1..*" Seat : reserves
    Booking "1" --> "1" Payment : uses
    Payment <|.. CardPayment
    Payment <|.. WalletPayment
```

Mermaid reminders:
- Composition = `*--`, Aggregation = `o--` (letters — Mermaid draws the diamond).
- Multiplicity in quotes, outside the arrow: `A "1" *-- "*" B`.
- Realization (implements) = `<|..` (dashed); Generalization (extends) = `<|--` (solid).

---

## Part B — Activity Diagram (flowchart form)

```mermaid
flowchart TD
    Start([Start]) --> Select[Select seats]
    Select --> Check{Seats available?}
    Check -->|No| Err[Show error]
    Err --> End1([End])
    Check -->|Yes| Hold[Hold seats]
    Hold --> Pay[Process payment]
    Pay --> PayOk{Payment success?}
    PayOk -->|No| Release[Release held seats]
    Release --> End2([End])

    PayOk -->|Yes| Fork[/ fork /]
    Fork --> Confirm[Confirm booking]
    Fork --> Email[Send email]
    Fork --> SMS[Send SMS]
    Confirm --> Join[/ join /]
    Email --> Join
    SMS --> Join
    Join --> Ticket[Show ticket]
    Ticket --> End3([End])
```

## Part B — same flow with REAL UML fork/join bars (stateDiagram-v2)

```mermaid
stateDiagram-v2
    [*] --> SelectSeats
    SelectSeats --> CheckAvailability
    CheckAvailability --> ShowError: seats taken
    CheckAvailability --> HoldSeats: seats free
    HoldSeats --> ProcessPayment
    ProcessPayment --> ReleaseSeats: payment failed
    ProcessPayment --> fork1: payment success
    ReleaseSeats --> [*]
    ShowError --> [*]

    state fork1 <<fork>>
    fork1 --> ConfirmBooking
    fork1 --> SendEmail
    fork1 --> SendSMS

    state join1 <<join>>
    ConfirmBooking --> join1
    SendEmail --> join1
    SendSMS --> join1
    join1 --> ShowTicket
    ShowTicket --> [*]
```

Decision (`{...}` / labeled transitions) = choose ONE path (OR).
Fork/Join (`<<fork>>`/`<<join>>`) = run ALL paths in parallel (AND), join waits for all.
