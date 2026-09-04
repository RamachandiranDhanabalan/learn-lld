package com.learn.lld;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Day 18 Practice — Text Editor with Undo/Redo (Command Pattern)
 *
 * KEY LEARNING (revisit): One class PER action, each frozen with its own data
 * and its own undo. Destructive commands capture old state during execute().
 *
 * Roles:
 * - Command: interface (execute + undo)
 * - Concrete Command: InsertCommand, DeleteCommand, ReplaceCommand
 * - Receiver: TextEditor (does the actual text work)
 * - Invoker: CommandHistory (triggers + manages undo/redo stacks)
 *
 * The trigger for Command: "need undo/redo/queue/schedule an action".
 */

// ══════════ COMMAND ══════════

interface EditorCommand {
    void execute();
    void undo();
}

// ══════════ RECEIVER ══════════

class TextEditor {
    private final StringBuilder content = new StringBuilder();

    void insert(String text, int position) { content.insert(position, text); }
    void delete(int position, int length) { content.delete(position, position + length); }
    String textAt(int position, int length) {
        return content.substring(position, position + length);
    }
    String getContent() { return content.toString(); }
}

// ══════════ CONCRETE COMMANDS ══════════

class InsertCommand implements EditorCommand {
    private final TextEditor editor;
    private final String text;
    private final int position;

    InsertCommand(TextEditor editor, String text, int position) {
        this.editor = editor;
        this.text = text;
        this.position = position;
    }

    public void execute() { editor.insert(text, position); }
    public void undo() { editor.delete(position, text.length()); }  // reverse of insert
}

class DeleteCommand implements EditorCommand {
    private final TextEditor editor;
    private final int position;
    private final int length;
    private String deletedText;  // captured during execute for undo

    DeleteCommand(TextEditor editor, int position, int length) {
        this.editor = editor;
        this.position = position;
        this.length = length;
    }

    public void execute() {
        deletedText = editor.textAt(position, length);  // save before deleting
        editor.delete(position, length);
    }
    public void undo() { editor.insert(deletedText, position); }  // restore
}

class ReplaceCommand implements EditorCommand {
    private final TextEditor editor;
    private final int position;
    private final String newText;
    private String oldText;  // captured during execute

    ReplaceCommand(TextEditor editor, int position, String newText) {
        this.editor = editor;
        this.position = position;
        this.newText = newText;
    }

    public void execute() {
        oldText = editor.textAt(position, newText.length());  // save what's there
        editor.delete(position, newText.length());
        editor.insert(newText, position);
    }
    public void undo() {
        editor.delete(position, newText.length());
        editor.insert(oldText, position);
    }
}

// ══════════ INVOKER (with undo/redo) ══════════

class CommandHistory {
    private final Deque<EditorCommand> undoStack = new ArrayDeque<>();
    private final Deque<EditorCommand> redoStack = new ArrayDeque<>();

    void execute(EditorCommand command) {
        command.execute();
        undoStack.push(command);  // push (LIFO), not add
        redoStack.clear();         // new action invalidates redo
    }

    void undo() {
        if (!undoStack.isEmpty()) {
            EditorCommand command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    void redo() {
        if (!redoStack.isEmpty()) {
            EditorCommand command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }
}

// ══════════ USAGE ══════════

class Day18Command {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
        CommandHistory history = new CommandHistory();

        history.execute(new InsertCommand(editor, "Hello", 0));
        System.out.println(editor.getContent());  // Hello

        history.execute(new InsertCommand(editor, " World", 5));
        System.out.println(editor.getContent());  // Hello World

        history.undo();
        System.out.println(editor.getContent());  // Hello

        history.redo();
        System.out.println(editor.getContent());  // Hello World

        history.execute(new DeleteCommand(editor, 0, 5));
        System.out.println(editor.getContent());  // " World"

        history.undo();
        System.out.println(editor.getContent());  // Hello World (delete undone)
    }
}



