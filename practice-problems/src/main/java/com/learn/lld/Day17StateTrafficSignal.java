package com.learn.lld;

import java.util.*;

/**
 * Day 17 Practice — Traffic Signal System (Design From Scratch)
 *
 * Tests: State pattern, orchestration boundary (state owns WHAT, controller owns WHEN),
 * "only one GREEN" invariant, emergency mode.
 *
 * KEY LEARNING:
 * - State owns duration (data) + next transition. Controller owns the actual timer.
 * - A state handles ONE transition per event — not a multi-step script.
 * - Automatic/timed systems need a controller (unlike user-driven vending machine).
 */

// ══════════ STATE (signal color) ══════════

interface SignalState {
    void next(TrafficSignal signal);  // advance to next state (ONE transition)
    int getDurationSeconds();          // state owns its duration (data)
    String getColor();
}

class GreenState implements SignalState {
    public void next(TrafficSignal signal) { signal.setState(new YellowState()); }
    public int getDurationSeconds() { return 30; }
    public String getColor() { return "GREEN"; }
}

class YellowState implements SignalState {
    public void next(TrafficSignal signal) { signal.setState(new RedState()); }
    public int getDurationSeconds() { return 5; }
    public String getColor() { return "YELLOW"; }
}

class RedState implements SignalState {
    public void next(TrafficSignal signal) { signal.setState(new GreenState()); }
    public int getDurationSeconds() { return 35; }
    public String getColor() { return "RED"; }
}

// ══════════ SIGNAL (context) ══════════

enum Direction { NORTH, EAST, SOUTH, WEST }

class TrafficSignal {
    private final Direction direction;
    private SignalState state;

    TrafficSignal(Direction direction) {
        this.direction = direction;
        this.state = new RedState();  // all start RED
    }

    void setState(SignalState state) { this.state = state; }
    void next() { state.next(this); }

    Direction getDirection() { return direction; }
    SignalState getState() { return state; }
    String getColor() { return state.getColor(); }
}

// ══════════ CONTROLLER (orchestrator — owns the WHEN) ══════════

class TrafficController {
    private final List<TrafficSignal> signals;
    private int activeIndex = 0;
    private boolean emergency = false;
    private boolean running = false;

    TrafficController(List<TrafficSignal> signals) {
        this.signals = signals;
    }

    // Drives the cycle: only ONE direction green at a time, rotating
    void run() {
        running = true;
        while (running && !emergency) {
            TrafficSignal active = signals.get(activeIndex);

            // Enforce "only one GREEN" — all others RED
            signals.forEach(s -> s.setState(new RedState()));
            active.setState(new GreenState());
            waitFor(active.getState().getDurationSeconds());  // controller does the waiting

            active.next();  // GREEN → YELLOW
            waitFor(active.getState().getDurationSeconds());

            active.next();  // YELLOW → RED

            activeIndex = (activeIndex + 1) % signals.size();  // rotate to next direction
        }
    }

    // Emergency: all signals RED immediately
    void emergencyStop() {
        emergency = true;
        signals.forEach(s -> s.setState(new RedState()));
        System.out.println("EMERGENCY: All signals RED");
    }

    void resume() {
        emergency = false;
        run();
    }

    void stop() { running = false; }

    private void waitFor(int seconds) {
        // Controller owns timing. In real code: scheduler.schedule(...) or Thread.sleep.
        // States never sleep — keeps them testable.
        System.out.println("Waiting " + seconds + "s...");
    }
}

// ══════════ USAGE ══════════

class Day17StateTrafficSignal {
    public static void main(String[] args) {
        List<TrafficSignal> signals = List.of(
            new TrafficSignal(Direction.NORTH),
            new TrafficSignal(Direction.EAST),
            new TrafficSignal(Direction.SOUTH),
            new TrafficSignal(Direction.WEST));

        TrafficController controller = new TrafficController(signals);
        // controller.run();  // would loop — omitted in demo

        // Emergency override
        controller.emergencyStop();
        signals.forEach(s ->
            System.out.println(s.getDirection() + ": " + s.getColor()));
    }
}
