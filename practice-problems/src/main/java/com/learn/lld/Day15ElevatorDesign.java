package com.learn.lld;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Day 15 Practice — Elevator System (Design From Scratch)
 *
 * A design-from-scratch problem (not smell-hunting). Tests: entity discovery,
 * finding "the heart", Strategy pattern, State pattern, SRP, DIP, encapsulation.
 *
 * KEY LEARNING: The "Request" entity hides inside the verb "press button".
 * Use "what flows through the system?" to catch it.
 *
 * Entities: Elevator, Request, Floor(int)
 * Services: ElevatorDispatcher (the heart), ElevatorService
 * Strategy: ElevatorRoutingStrategy (Scan/Nearest)
 * State: ElevatorStatus (State pattern deferred to Day 17 — using enum for now)
 */

// ══════════ ENUMS ══════════

enum Direction { UP, DOWN, IDLE }
enum ElevatorStatus { IDLE, MOVING, DOORS_OPEN, MAINTENANCE }
enum RequestStatus { PENDING, ASSIGNED, COMPLETED }

// ══════════ REQUEST (entity — hidden in "press button") ══════════

class ElevatorRequest {
    private final int sourceFloor;
    private final Direction direction;    // external hall request
    private Integer destinationFloor;      // set when user enters elevator
    private RequestStatus status = RequestStatus.PENDING;

    ElevatorRequest(int sourceFloor, Direction direction) {
        this.sourceFloor = sourceFloor;
        this.direction = direction;
    }

    void setDestination(int floor) { this.destinationFloor = floor; }
    void markAssigned() { this.status = RequestStatus.ASSIGNED; }
    void markCompleted() { this.status = RequestStatus.COMPLETED; }

    int getSourceFloor() { return sourceFloor; }
    Direction getDirection() { return direction; }
    Integer getDestinationFloor() { return destinationFloor; }
    RequestStatus getStatus() { return status; }
}

// ══════════ ELEVATOR (entity with behavior methods) ══════════

class Elevator {
    private final String id;
    private int currentFloor = 1;
    private Direction direction = Direction.IDLE;
    private ElevatorStatus status = ElevatorStatus.IDLE;
    private final TreeSet<Integer> targetFloors = new TreeSet<>();

    Elevator(String id) { this.id = id; }

    void addTarget(int floor) {
        targetFloors.add(floor);
        if (status == ElevatorStatus.IDLE) {
            status = ElevatorStatus.MOVING;
            direction = floor > currentFloor ? Direction.UP : Direction.DOWN;
        }
    }

    boolean isIdle() { return status == ElevatorStatus.IDLE; }
    boolean isOperational() { return status != ElevatorStatus.MAINTENANCE; }

    // "Am I heading toward this floor in the requested direction?"
    boolean canServe(int floor, Direction reqDir) {
        if (status == ElevatorStatus.MAINTENANCE) return false;
        if (isIdle()) return true;
        if (direction == Direction.UP && reqDir == Direction.UP && floor >= currentFloor) return true;
        if (direction == Direction.DOWN && reqDir == Direction.DOWN && floor <= currentFloor) return true;
        return false;
    }

    int distanceTo(int floor) { return Math.abs(currentFloor - floor); }

    String getId() { return id; }
    int getCurrentFloor() { return currentFloor; }
    ElevatorStatus getStatus() { return status; }
}

// ══════════ STRATEGY — routing (pluggable) ══════════

interface ElevatorRoutingStrategy {
    Optional<Elevator> selectElevator(List<Elevator> elevators, ElevatorRequest request);
}

class NearestElevatorStrategy implements ElevatorRoutingStrategy {
    public Optional<Elevator> selectElevator(List<Elevator> elevators, ElevatorRequest request) {
        return elevators.stream()
            .filter(e -> e.canServe(request.getSourceFloor(), request.getDirection()))
            .min(Comparator.comparingInt(e -> e.distanceTo(request.getSourceFloor())));
    }
}

class ScanStrategy implements ElevatorRoutingStrategy {
    // SCAN: prefer elevators already moving in the same direction toward the source
    public Optional<Elevator> selectElevator(List<Elevator> elevators, ElevatorRequest request) {
        return elevators.stream()
            .filter(e -> e.canServe(request.getSourceFloor(), request.getDirection()))
            .min(Comparator.comparingInt(e -> e.distanceTo(request.getSourceFloor())));
    }
}

// ══════════ ELEVATOR SERVICE (holds + filters elevators) ══════════

class ElevatorService {
    private final List<Elevator> elevators;

    ElevatorService(List<Elevator> elevators) { this.elevators = elevators; }

    List<Elevator> getOperationalElevators() {
        return elevators.stream().filter(Elevator::isOperational).collect(Collectors.toList());
    }
}

// ══════════ DISPATCHER (the heart — orchestrates) ══════════

class ElevatorDispatcher {
    private final ElevatorService elevatorService;
    private final ElevatorRoutingStrategy strategy;
    private final Queue<ElevatorRequest> pendingRequests = new LinkedList<>();

    ElevatorDispatcher(ElevatorService elevatorService, ElevatorRoutingStrategy strategy) {
        this.elevatorService = elevatorService;
        this.strategy = strategy;
    }

    Optional<Elevator> dispatch(ElevatorRequest request) {
        Optional<Elevator> chosen = strategy.selectElevator(
            elevatorService.getOperationalElevators(), request);

        chosen.ifPresentOrElse(
            elevator -> {
                elevator.addTarget(request.getSourceFloor());
                request.markAssigned();
            },
            () -> pendingRequests.add(request)  // no elevator available — queue it
        );
        return chosen;
    }

    // When a user enters and picks a destination
    void serveDestination(Elevator elevator, ElevatorRequest request, int destinationFloor) {
        request.setDestination(destinationFloor);
        elevator.addTarget(destinationFloor);
    }
}

// ══════════ USAGE ══════════

class Day15ElevatorDesign {
    public static void main(String[] args) {
        List<Elevator> elevators = List.of(
            new Elevator("A"), new Elevator("B"),
            new Elevator("C"), new Elevator("D"));

        ElevatorService service = new ElevatorService(elevators);
        ElevatorDispatcher dispatcher = new ElevatorDispatcher(service, new ScanStrategy());

        // User on floor 1 presses UP
        ElevatorRequest request = new ElevatorRequest(1, Direction.UP);
        Optional<Elevator> assigned = dispatcher.dispatch(request);

        // Elevator arrives, user enters and picks floor 3
        assigned.ifPresent(e -> {
            dispatcher.serveDestination(e, request, 3);
            System.out.println("Elevator " + e.getId() + " serving request to floor 3");
        });
    }
}
