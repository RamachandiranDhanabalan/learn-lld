# Object-Oriented Programming Principles

## Core Pillars

### 1. Encapsulation
- Bundling data + methods that operate on it into a single unit (class)
- Controlling access via access modifiers (private, protected, public)
- Exposing behavior, hiding state

### 2. Abstraction
- Hiding implementation complexity behind a simple interface
- Focus on WHAT an object does, not HOW it does it
- Abstract classes vs Interfaces

### 3. Inheritance
- IS-A relationship — child class extends parent
- Code reuse via hierarchy
- **When to avoid**: Fragile base class problem, tight coupling

### 4. Polymorphism
- Same interface, different implementations
- Compile-time (method overloading) vs Runtime (method overriding)
- Enables the Open/Closed principle

## Key Concepts

- **Cohesion**: How focused a class is on a single responsibility (HIGH is good)
- **Coupling**: How dependent classes are on each other (LOW is good)
- **Generalization**: Extracting common behavior into a parent (bottom-up)
- **Specialization**: Adding specific behavior in a child (top-down)

## Resources

- [Oracle Java OOP Tutorial](https://docs.oracle.com/javase/tutorial/java/concepts/)
- [Refactoring Guru — OOP Basics](https://refactoring.guru/refactoring/smells)

## Related

- [Composition vs Inheritance](composition-vs-inheritance.md)
- [SOLID Principles](../solid/README.md)
- [Examples](examples/)
