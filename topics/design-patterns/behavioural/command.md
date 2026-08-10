# Command Pattern

## Intent
Encapsulate a request as an object, letting you parameterize clients with different requests, queue requests, log them, and support undo.

## Problem It Solves
- Decouple the invoker (button, scheduler) from the action
- Support undo/redo
- Queue, log, or schedule operations
- Support macro commands (batch of commands)

## Java Example

```java
// Command interface
public interface Command {
    void execute();
    void undo();
}

// Receiver
public class TextEditor {
    private StringBuilder content = new StringBuilder();
    public void insert(String text, int pos) { content.insert(pos, text); }
    public void delete(int pos, int length) { content.delete(pos, pos + length); }
}

// Concrete command
public class InsertTextCommand implements Command {
    private final TextEditor editor;
    private final String text;
    private final int position;

    public void execute() { editor.insert(text, position); }
    public void undo() { editor.delete(position, text.length()); }
}

// Invoker
public class CommandHistory {
    private final Deque<Command> history = new ArrayDeque<>();

    public void execute(Command cmd) {
        cmd.execute();
        history.push(cmd);
    }

    public void undo() {
        if (!history.isEmpty()) {
            history.pop().undo();
        }
    }
}
```

## When to Use
- Undo/redo functionality
- Task schedulers (each task is a command)
- Transaction logging
- Macro recording

## Resources
- [Refactoring Guru — Command](https://refactoring.guru/design-patterns/command)
