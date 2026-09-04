# Command Pattern

## Intent

Encapsulate a request as an object. This lets you store, queue, log, schedule, and undo actions — because the action becomes a first-class object you can manipulate.

## The One-Sentence Idea

> Instead of calling a method directly, wrap that method call inside an object you can hold, store, and manipulate.

```java
light.turnOn();                        // direct call — fleeting, can't store/undo
Command cmd = new LightOnCommand(light);  // now it's an OBJECT — store/queue/undo it
```

---

## The Four Roles

| Role | Responsibility | Example |
|---|---|---|
| **Command** | Interface with `execute()` (+ often `undo()`) | `Command` |
| **Concrete Command** | ONE action, frozen with its data + knows its undo | `InsertCommand` |
| **Receiver** | Does the actual work | `TextEditor` |
| **Invoker** | Triggers commands, holds history | `CommandHistory` |

---

## Key Insight: One Class Per Action

Each command = ONE specific action, frozen with its data:

```java
interface Command {
    void execute();
    void undo();
}

// Receiver
class TextEditor {
    private StringBuilder content = new StringBuilder();
    void insert(String text, int pos) { content.insert(pos, text); }
    void delete(int pos, int len) { content.delete(pos, pos + len); }
    String getContent() { return content.toString(); }
}

// ONE command per action — holds its own data, knows its own undo
class InsertCommand implements Command {
    private final TextEditor editor;
    private final String text;
    private final int position;

    InsertCommand(TextEditor editor, String text, int position) {
        this.editor = editor; this.text = text; this.position = position;
    }
    public void execute() { editor.insert(text, position); }
    public void undo() { editor.delete(position, text.length()); }  // reverse of insert
}

// Destructive commands capture old state during execute (to restore on undo)
class DeleteCommand implements Command {
    private final TextEditor editor;
    private final int position;
    private final int length;
    private String deletedText;  // captured for undo

    public void execute() {
        deletedText = editor.getContent().substring(position, position + length);
        editor.delete(position, length);
    }
    public void undo() { editor.insert(deletedText, position); }
}
```

---

## Invoker with Undo/Redo

```java
class CommandHistory {
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    void execute(Command cmd) {
        cmd.execute();
        undoStack.push(cmd);   // push, not add (LIFO)
        redoStack.clear();
    }
    void undo() {
        if (!undoStack.isEmpty()) {
            Command cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
        }
    }
    void redo() {
        if (!redoStack.isEmpty()) {
            Command cmd = redoStack.pop();
            cmd.execute();
            undoStack.push(cmd);
        }
    }
}
```

---

## Why "Action as Object" Enables Everything

All capabilities work because the action is now storable in a data structure:

| Capability | Data Structure | How |
|---|---|---|
| Undo | Stack | Pop + call `undo()` |
| Queue | Queue | Add now, execute later |
| Log | List | Store history, replay |
| Schedule | Scheduler | Hand to timer, run at future time |
| Macro | List of commands | Execute all as one |

You cannot do these with a direct method call — only with an object.

---

## The Interface — execute() Always, Rest Optional

| Method | When |
|---|---|
| `execute()` | Always (the essence) |
| `undo()` | If undo/redo needed |
| `getDescription()` | If logging/audit |
| `getScheduledTime()` | If scheduling |

Java's `Runnable` is a Command with just `run()` (execute, no undo).

---

## Command vs Strategy

| Aspect | Command | Strategy |
|---|---|---|
| Purpose | Encapsulate a REQUEST/ACTION | Encapsulate an ALGORITHM |
| Focus | WHAT to do + how to reverse | HOW to do one thing |
| Has undo? | Often | No |
| Holds receiver? | Yes | No |
| Queued/logged? | Common | Rare |

---

## When to Use — The Trigger

> **"Do I need to undo/redo, queue, log, schedule, or replay an action?" → Command.**

If NOT → probably Strategy or State. Don't use Command for simple one-off actions with no undo.

---

## Interview Frequency

★★★☆☆ Situational. Expected for: text editor undo/redo, job schedulers, transaction rollback. Less common than Strategy/Observer/State.

---

## Real-World Command

| Where | Command For |
|---|---|
| Text editor | Each edit (undo/redo) |
| Job scheduler | Each job |
| Transaction rollback | Compensating commands |
| GUI buttons | Button click → command |
| `Runnable` | Command without undo |
| Message queue | Message = command to process |

---

## Resources

- [Refactoring Guru — Command](https://refactoring.guru/design-patterns/command)
- [Baeldung — Command](https://www.baeldung.com/java-command-pattern)

## Related

- [Strategy](strategy.md) — algorithm vs action
- [Chain of Responsibility](chain-of-responsibility.md) — Day 19
