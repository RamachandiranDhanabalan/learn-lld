# SOLID Principles

## Overview

SOLID is a set of 5 design principles that make software more maintainable, flexible, and testable.

---

## S — Single Responsibility Principle (SRP)

> A class should have only one reason to change.

**Violation**: A `UserService` that handles validation, persistence, AND email sending.
**Fix**: Split into `UserValidator`, `UserRepository`, `EmailService`.

**Interview signal**: "If I need to change how emails are sent, I shouldn't risk breaking user persistence."

---

## O — Open/Closed Principle (OCP)

> Open for extension, closed for modification.

**Violation**: A `PaymentProcessor` with if-else for each payment type.
**Fix**: `PaymentStrategy` interface with `CreditCardPayment`, `UPIPayment` implementations.

**Interview signal**: "Adding a new payment method should be adding a class, not modifying existing code."

---

## L — Liskov Substitution Principle (LSP)

> Subtypes must be substitutable for their base types without breaking behavior.

**Violation**: `Square extends Rectangle` where `setWidth()` also sets height.
**Fix**: Separate classes, or use a `Shape` interface with `area()`.

**Interview signal**: "If substituting a subclass breaks the caller's expectations, the hierarchy is wrong."

---

## I — Interface Segregation Principle (ISP)

> Clients should not be forced to depend on interfaces they don't use.

**Violation**: A `Worker` interface with `work()`, `eat()`, `sleep()` — robots don't eat.
**Fix**: Split into `Workable`, `Feedable`, `Restable`.

**Interview signal**: "Fat interfaces force implementers to provide no-op methods, which is a design smell."

---

## D — Dependency Inversion Principle (DIP)

> High-level modules should depend on abstractions, not low-level modules.

**Violation**: `OrderService` directly instantiates `MySQLOrderRepository`.
**Fix**: `OrderService` depends on `OrderRepository` interface; inject the implementation.

**Interview signal**: "This is why Spring uses DI — my service doesn't know or care if it's MySQL, Postgres, or a mock."

---

## Resources

- [Refactoring Guru — SOLID](https://refactoring.guru/refactoring/smells)
- [Uncle Bob's Clean Code Blog](https://blog.cleancoder.com)
- [Baeldung — SOLID in Java](https://www.baeldung.com/solid-principles)

## Related

- [OOP Principles](../oops/README.md)
- [Design Patterns](../design-patterns/README.md) — patterns that enforce SOLID
- [Examples](examples/)
