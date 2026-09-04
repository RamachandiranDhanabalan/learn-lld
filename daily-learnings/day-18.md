# Day 18 — Command Pattern

> ⚠️ REVISIT: This pattern felt confusing. See REVISIT.md — reinforce at Day 54-55 (Task Scheduler).

## Cheat Sheet

- **Command** = Encapsulate a request/action as an OBJECT. Enables undo, queue, log, schedule.
- **The mental shift**: instead of calling `light.turnOn()`, wrap it in `new LightOnCommand(light)` — now it's a storable object.
- **One class PER action** (InsertCommand, DeleteCommand) — each frozen with its own data + own undo.
- **4 roles**: Command (interface), Concrete Command (action+data), Receiver (does work), Invoker (triggers + history).
- **execute() always. undo() if needed.** Everything else optional.
- **Destructive commands capture old state during execute()** (so undo can restore).
- **The trigger**: "undo/redo/queue/schedule/replay an action?" → Command.

## Critical Example

```java
interface Command { void execute(); void undo(); }

// Receiver does the work
class TextEditor {
    void insert(String text, int pos) { ... }
    void delete(int pos, int len) { ... }
}

// ONE command per action, frozen with data, knows own undo
class InsertCommand implements Command {
    private final TextEditor editor;
    private final String text;
    private final int position;
    // constructor stores all three

    public void execute() { editor.insert(text, position); }
    public void undo() { editor.delete(position, text.length()); }  // reverse
}

// Invoker manages history
class CommandHistory {
    private Deque<Command> undoStack = new ArrayDeque<>();
    void execute(Command c) { c.execute(); undoStack.push(c); }
    void undo() { if (!undoStack.isEmpty()) undoStack.pop().undo(); }
}
```

## Why Action-as-Object Enables Everything

| Capability | How |
|---|---|
| Undo | Store commands in stack, pop + undo() |
| Queue | Store in queue, execute later |
| Log | Store in list, replay |
| Schedule | Hand to scheduler, run at future time |
| Macro | List of commands executed as one |

All work because the action is a storable OBJECT (not a fleeting method call).

## Decision Framework

| Signal | Pattern |
|---|---|
| "Need undo/redo" | Command |
| "Queue/schedule actions" | Command |
| "Log/replay actions" | Command |
| "Decouple invoker from receiver" | Command |
| "Simple one-off action, no undo" | Just call the method (Command = overkill) |
| "Swap algorithm" | Strategy (not Command) |

## Interview Questions & Answers

**Q: "What's Command?"**
A: "Encapsulate a request as an object. Binds a receiver + action. Enables undo, queue, log, schedule — because the action is now a storable object."

**Q: "The 4 roles?"**
A: "Command (interface, execute/undo), Concrete Command (one action + its data), Receiver (does the work), Invoker (triggers + holds history)."

**Q: "How does undo work?"**
A: "Each command knows its reversal via undo(). Invoker keeps a stack. Undo pops the last command and calls undo(). Destructive commands capture old state during execute() to restore it."

**Q: "Command vs Strategy?"**
A: "Command = action/request (with undo, queuing). Strategy = algorithm. Command binds a receiver and often supports undo; Strategy doesn't."

**Q: "When Command?"**
A: "When I need undo/redo, queuing, scheduling, or logging of actions. The trigger keyword is 'undo' or 'schedule'. Otherwise it's overkill."

## Clues & Signals

- **Senior signal**: "This needs undo, so I'd model each action as a Command that knows how to reverse itself"
- **Senior signal**: "Destructive commands must capture the old state during execute to restore on undo"
- **Red flag**: One command class with insert/delete/replace methods (that's NOT Command — each action needs its own class)
- **Red flag**: Using Command for a simple action with no undo/queue/schedule need
- **Key learning**: Java's Runnable is a Command with just execute() (no undo)

## Personal Note (Revisit)

This pattern has more moving parts than Strategy/Observer/State. The "one class per action, each frozen with data" idea is the crux. Interview frequency is moderate (★★★☆☆) — expected for editors, schedulers, rollback. Master the TRIGGER (undo/queue/schedule) and the structure; reinforce with Task Scheduler on Day 54-55.

## Quick Links

- **Detailed topic**: [Command Pattern](../topics/design-patterns/behavioural/command.md)
- **Revisit tracker**: [REVISIT.md](../REVISIT.md)
- **Related**: [Strategy](../topics/design-patterns/behavioural/strategy.md)
