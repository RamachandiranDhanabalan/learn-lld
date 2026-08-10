# Composite Pattern

## Intent
Compose objects into tree structures to represent part-whole hierarchies. Let clients treat individual objects and compositions uniformly.

## Problem It Solves
- Tree-structured data (file systems, org charts, UI components)
- Client shouldn't care if it's dealing with a leaf or a group

## Java Example

```java
// Component
public interface FileSystemItem {
    String getName();
    long getSize();
    void display(String indent);
}

// Leaf
public class File implements FileSystemItem {
    private final String name;
    private final long size;

    public long getSize() { return size; }
    public void display(String indent) { System.out.println(indent + name + " (" + size + " bytes)"); }
}

// Composite
public class Directory implements FileSystemItem {
    private final String name;
    private final List<FileSystemItem> children = new ArrayList<>();

    public void add(FileSystemItem item) { children.add(item); }
    public void remove(FileSystemItem item) { children.remove(item); }

    public long getSize() {
        return children.stream().mapToLong(FileSystemItem::getSize).sum();
    }

    public void display(String indent) {
        System.out.println(indent + name + "/");
        children.forEach(c -> c.display(indent + "  "));
    }
}
```

## When to Use
- In-memory file system (common interview question)
- Menu/submenu structures
- Organization hierarchy
- UI component trees

## Resources
- [Refactoring Guru — Composite](https://refactoring.guru/design-patterns/composite)
