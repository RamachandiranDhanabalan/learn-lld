# UML Diagrams for LLD

## Diagrams You Need for Interviews

### 1. Class Diagram
- Shows classes, attributes, methods, and relationships
- Relationships: Association, Aggregation, Composition, Inheritance, Implementation
- Cardinality: 1:1, 1:N, M:N

### 2. Sequence Diagram
- Shows object interactions over time (method calls)
- Read top-to-bottom (time flows down)
- Key elements: lifelines, messages, return values, loops, alt blocks

### 3. State Diagram
- Shows lifecycle of an entity (e.g., Order: CREATED → PAID → SHIPPED → DELIVERED)
- States, transitions, guards (conditions), actions

### 4. Activity Diagram
- Flowchart-style showing workflow, decision points, parallel paths
- Fork/Join for concurrency, Diamond for decisions
- Useful for complex business processes

### 5. Use Case Diagram
- Actors (stick figures) and use cases (ovals)
- Shows WHAT the system does, not HOW
- Good for initial requirements gathering

## Relationships Quick Reference

| Symbol | Meaning | Example |
|--------|---------|---------|
| ——→ | Association | Student → Course |
| ◇——→ | Aggregation (weak HAS-A) | Department ◇→ Employee |
| ◆——→ | Composition (strong HAS-A) | House ◆→ Room |
| △——→ | Inheritance (IS-A) | Dog △→ Animal |
| ----▷ | Implementation | ArrayList ----▷ List |

## Resources

- [PlantUML](https://plantuml.com/) — text-based UML diagrams
- [draw.io](https://app.diagrams.net/) — free diagramming tool
- [UML Quick Reference](https://www.uml-diagrams.org/)

## Related

- [Domain Modeling](../domain-modeling/README.md)
- [Examples](examples/)
