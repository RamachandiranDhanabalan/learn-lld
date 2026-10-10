# Day 25 — Full Class Diagram (Parking Lot)

## Cheat Sheet

- **8 steps**: clarify scope → nouns→classes → verbs→methods → classify (Entity/VO/enum) → relationships+multiplicity → interfaces (if variation real) → aggregate roots → read back.
- **Order rule**: nouns before relationships, relationships before patterns. Patterns at step 6, not step 1.
- **Clarify scope FIRST** — state assumptions, ask. Top senior signal.
- **Not everything is an entity**: enums for fixed sets (SpotType), VOs for value-defined (Money, TimeRange).
- **Lot→Floor→Spot = composition ◆. Spot→Vehicle = aggregation ◇ (0..1). Ticket→others = association.**
- **Strategy for Fee (varies). Plain method for assignment (until a 2nd algorithm exists).** — Day 21 restraint.
- **One aggregate root** = `ParkingLot.parkVehicle()`; caller never pokes at spots.

## Critical Example — the diagram in words

```
ParkingLot ◆──1..*── ParkingFloor ◆──1..*── ParkingSpot ◇──0..1── Vehicle
Ticket ──▶ Vehicle,  Ticket ──▶ Spot,  Ticket ──▶ Payment
ParkingLot ──▶ FeeStrategy  (HourlyFee / FlatRateFee implement it)
SpotType = enum {COMPACT, LARGE, HANDICAPPED, EV}
```

## The 8-Step Procedure (apply to ANY problem)

| Step | Action | Prior day |
|------|--------|-----------|
| 1 | Clarify scope + assumptions | — |
| 2 | Nouns → candidate classes | — |
| 3 | Verbs → methods + hidden entities | framework |
| 4 | Classify Entity / VO / enum | Day 22 |
| 5 | Relationships + multiplicity | Day 23/24 |
| 6 | Interfaces where variation is real | Day 21 |
| 7 | Aggregate root(s) | Day 23 |
| 8 | Read back vs requirements | — |

## Classify Decision

| Candidate | Type | Why |
|-----------|------|-----|
| ParkingLot/Floor/Spot/Vehicle/Ticket/Payment | Entity | identity, tracked |
| SpotType, VehicleType | enum | fixed closed set (not a hierarchy) |
| Money, Fee, TimeRange | Value Object | value-defined, immutable |

## Relationship Decision

| Pair | Type | Multiplicity | Why |
|------|------|--------------|-----|
| Lot → Floor | composition ◆ | 1 / 1..* | floor is part of the lot |
| Floor → Spot | composition ◆ | 1 / 1..* | spot belongs to the floor |
| Spot → Vehicle | aggregation ◇ | 1 / 0..1 | spot holds a vehicle that exists independently |
| Ticket → Vehicle/Spot/Payment | association ──▶ | 1 / 1 | references, doesn't own |

## Interview Questions & Answers

**Q: "Design a parking lot — where do you start?"**
A: "First I clarify scope — floors, spot types, fee model, payment on exit, what's out for v1. Then I pull nouns into classes, classify each as entity, value object, or enum, draw relationships with multiplicity, and only add patterns where behaviour genuinely varies — like a FeeStrategy for pricing."

**Q: "Is SpotType a class hierarchy?"**
A: "No — it's a fixed closed set, so an enum. A class hierarchy would be over-engineering unless each spot type had genuinely different behaviour that keeps growing."

**Q: "Lot to Spot — composition or aggregation?"**
A: "Lot→Floor→Spot is composition — a spot has no meaning outside its floor and dies with the lot. But Spot→Vehicle is aggregation: the spot holds a vehicle temporarily; the vehicle exists independently and drives away. Multiplicity 0..1 — a spot has zero or one vehicle."

**Q: "Would you use Strategy for spot assignment?"**
A: "Only if the algorithm varies — nearest, load-balanced, random. For v1 picking the first free spot of the right size, a plain method is simpler. I'd extract an AssignmentStrategy when a second algorithm is actually needed."

**Q: "What's the aggregate root?"**
A: "ParkingLot. Parking is `parkingLot.parkVehicle(v)` which internally finds a floor, assigns a spot, and issues a Ticket. Callers never manipulate spots directly, so the lot keeps its invariants."

## Clues & Signals

- **Senior signal**: clarifying scope and stating assumptions before drawing.
- **Senior signal**: enum for SpotType, VO for Money — not everything an entity.
- **Senior signal**: Strategy only for fee; plain method for assignment, with the "I'd extract it when needed" caveat.
- **Senior signal**: single entry point (ParkingLot.parkVehicle), multiplicity on every line.
- **Red flag**: jumping to patterns before modeling nouns.
- **Red flag**: SpotType as a class hierarchy; everything an entity.
- **Red flag**: no aggregate root; caller mutates spots directly.
- **Red flag**: Spot→Vehicle as composition.

## Quick Links

- **Detailed topic**: [Full Class Diagram Walkthrough](../topics/uml/full-class-diagram-walkthrough.md)
- **Related**: [LLD Framework](../topics/lld-approach/problem-solving-framework.md)
- **Related**: [Class Diagram Notation](../topics/uml/class-diagram-notation.md)
- **Next**: Sequence + Activity Diagrams (Day 26)
