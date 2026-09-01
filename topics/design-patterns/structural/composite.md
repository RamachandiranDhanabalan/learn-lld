# Composite Pattern

## Intent

Treat individual objects (leaves) and groups of objects (composites) uniformly through a common interface. For tree/part-whole hierarchies.

## Problem It Solves

- Tree structure where nodes can be individual items OR groups of items
- Client shouldn't need to distinguish "is this one item or a group?"
- Recursive operations (sum, render, traverse) across the tree

---

## Part-Whole Hierarchy

A thing made up of smaller things of the SAME type, forming a tree:

```
Directory (whole)
  ├── File (part/leaf)
  ├── File (part/leaf)
  └── Directory (part — but ALSO a whole with its own parts!)
        ├── File
        └── File
```

The "whole" and "part" are the same type → a whole can contain other wholes → recursion.

---

## Example: File System

```java
// COMPONENT — common interface for leaf and composite
interface FileSystemItem {
    long getSize();
    void display(String indent);
}

// LEAF — no children
class File implements FileSystemItem {
    private final String name;
    private final long size;
    File(String name, long size) { this.name = name; this.size = size; }
    public long getSize() { return size; }
    public void display(String indent) { System.out.println(indent + name); }
}

// COMPOSITE — has children (which can be leaves OR composites)
class Directory implements FileSystemItem {
    private final String name;
    private final List<FileSystemItem> children = new ArrayList<>();

    Directory(String name) { this.name = name; }
    public void add(FileSystemItem item) { children.add(item); }

    public long getSize() {
        return children.stream().mapToLong(FileSystemItem::getSize).sum();  // recursive
    }
    public void display(String indent) {
        System.out.println(indent + name + "/");
        children.forEach(c -> c.display(indent + "  "));  // recurse
    }
}

// CLIENT — treats File and Directory the SAME
root.getSize();     // recursively sums everything
root.display("");   // prints whole tree
```

---

## Key Characteristics

| Aspect | Description |
|---|---|
| Uniform interface | Leaf and Composite implement the same interface |
| Recursive structure | Composite contains Components (which can be Composites) |
| Client simplicity | Client treats one and many the same way |
| Eliminates type-checking | No `if (isLeaf) else if (isGroup)` — polymorphism handles it |

---

## Composite Eliminates Type-Checking

```java
// ❌ BEFORE — string type + if-else, only handles fixed levels
class MenuEntry { String type; }  // "ITEM" or "SUBMENU"
if (entry.type.equals("ITEM")) { ... }
else if (entry.type.equals("SUBMENU")) { for child... }  // only 2 levels!

// ✅ AFTER — the class IS the type, polymorphism, infinite nesting
class MenuItem implements MenuEntry { }   // leaf
class MenuGroup implements MenuEntry {     // composite
    List<MenuEntry> children;  // can contain MenuItem OR MenuGroup — infinite depth
}
```

---

## When to Use

| Signal | Example |
|---|---|
| Tree/hierarchy structure | File system, org chart, UI components |
| Part-whole relationships | Menu → submenu → items |
| Recursive operations | Sum sizes, render tree, count nodes |
| Client should treat one/many uniformly | getTotalCost() on employee OR manager |

---

## Real-World Composite

| Example | Leaf | Composite |
|---|---|---|
| File system | File | Directory |
| UI framework | Button | Panel |
| Org chart | Employee | Manager (has reports) |
| Menu | MenuItem | SubMenu |
| Graphics | Shape | Group of shapes |
| HTML DOM | Text node | Element |

---

## Resources

- [Refactoring Guru — Composite](https://refactoring.guru/design-patterns/composite)

## Related

- [Facade](facade.md) — another structural pattern
- [Class Relationships](../../oops/class-relationships.md) — composition builds the tree
