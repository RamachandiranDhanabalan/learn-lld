package com.learn.lld;

import java.util.List;

/**
 * Day 19 Practice — Chain of Responsibility (both flavors)
 *
 * FLAVOR 1 (single-handler): Loan Approval — first capable approver handles it, chain stops.
 * FLAVOR 2 (pipeline): Logging Framework — message passes through ALL handlers, each logs if level matches.
 *
 * KEY LEARNING:
 * - Single-handler: stops when handled (escalation, approval).
 * - Pipeline: all participate (logging, filters).
 * - Constructor = handler config. Method param = per-request data.
 * - Data-only variation → one parameterized class. Override process() only when logic differs.
 */

// ══════════ FLAVOR 1: Loan Approval (single-handler, stops when handled) ══════════

class LoanApprovalHandler {
    private LoanApprovalHandler next;
    private final int allowedLimit;   // constructor config (fixed)
    private final String role;

    LoanApprovalHandler(int allowedLimit, String role) {
        this.allowedLimit = allowedLimit;
        this.role = role;
    }

    void setNext(LoanApprovalHandler next) { this.next = next; }

    void approve(int amount) {  // method param (per-request)
        if (amount <= allowedLimit) {
            System.out.println("Approved by " + role + " (₹" + amount + ")");
            // STOP — handled
        } else if (next != null) {
            next.approve(amount);  // escalate
        } else {
            System.out.println("Rejected — ₹" + amount + " exceeds all limits");
        }
    }
}

// ══════════ FLAVOR 2: Logging Framework (pipeline, all participate) ══════════

class Log {
    private final String level;
    private final String message;

    Log(String level, String message) {
        this.level = level;
        this.message = message;
    }

    String getLevel() { return level; }
    String getMessage() { return message; }
}

abstract class LogHandler {
    private LogHandler next;
    private final List<String> allowedLevels;  // constructor config

    LogHandler(List<String> allowedLevels) {
        this.allowedLevels = allowedLevels;
    }

    void setNext(LogHandler next) { this.next = next; }

    void log(Log message) {
        if (allowedLevels.contains(message.getLevel())) {
            process(message);  // this handler cares — process
        }
        if (next != null) {
            next.log(message);  // ALWAYS pass on (pipeline — never stops)
        }
    }

    protected abstract void process(Log message);
}

class ConsoleLogger extends LogHandler {
    ConsoleLogger() { super(List.of("DEBUG", "INFO", "WARNING", "ERROR")); }
    protected void process(Log msg) {
        System.out.println("[CONSOLE] " + msg.getLevel() + ": " + msg.getMessage());
    }
}

class FileLogger extends LogHandler {
    FileLogger() { super(List.of("INFO", "WARNING", "ERROR")); }
    protected void process(Log msg) {
        System.out.println("[FILE] " + msg.getLevel() + ": " + msg.getMessage());
    }
}

class EmailLogger extends LogHandler {
    EmailLogger() { super(List.of("ERROR")); }
    protected void process(Log msg) {
        System.out.println("[EMAIL] " + msg.getLevel() + ": " + msg.getMessage());
    }
}

// ══════════ USAGE ══════════

class Day19ChainOfResponsibility {
    public static void main(String[] args) {
        // FLAVOR 1: Loan approval (single-handler)
        LoanApprovalHandler clerk = new LoanApprovalHandler(5_000, "Clerk");
        LoanApprovalHandler manager = new LoanApprovalHandler(50_000, "Manager");
        LoanApprovalHandler director = new LoanApprovalHandler(500_000, "Director");
        clerk.setNext(manager);
        manager.setNext(director);

        clerk.approve(3_000);    // Clerk approves (stops)
        clerk.approve(40_000);   // escalates to Manager
        clerk.approve(600_000);  // exceeds all → rejected

        System.out.println("---");

        // FLAVOR 2: Logging (pipeline — all matching handlers process)
        LogHandler console = new ConsoleLogger();
        LogHandler file = new FileLogger();
        LogHandler email = new EmailLogger();
        console.setNext(file);
        file.setNext(email);

        console.log(new Log("DEBUG", "debug msg"));   // only console
        console.log(new Log("INFO", "info msg"));      // console + file
        console.log(new Log("ERROR", "error msg"));    // console + file + email
    }
}
