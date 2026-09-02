package com.learn.lld;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import sun.misc.Signal;

/**
 * Day 16 Practice — Chat Application (Design From Scratch)
 *
 * Tests: Observer pattern, Strategy (join policy), entity discovery,
 * relationships (aggregation vs composition), SRP, DIP, encapsulation.
 *
 * KEY PATTERNS:
 * - Observer: ChatRoom is the subject. Listeners notified on sendMessage().
 * - Strategy: JoinPolicy (PublicJoinPolicy, PrivateJoinPolicy) for room access.
 *
 * KEY LEARNING: Keep entities clean. Use dedicated observer classes, not User entity.
 */

// ══════════ ENTITIES ══════════

class User {
    private final String id;
    private final String name;

    User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    String getId() { return id; }
    String getName() { return name; }

    @Override
    public boolean equals(Object o) { return o instanceof User u && id.equals(u.id); }

    @Override
    public int hashCode() { return id.hashCode(); }
}

class Message {
    private final String id;
    private final User sender;
    private final String text;
    private final Instant timestamp;

    Message(User sender, String text) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.text = text;
        this.timestamp = Instant.now();
    }

    User getSender() { return sender; }
    String getText() { return text; }
    Instant getTimestamp() { return timestamp; }
}

// ══════════ OBSERVER ══════════

interface ChatRoomListener {
    void onMessage(ChatRoom room, Message message);
}

// Delivers messages to users (excludes sender)
class MessageDeliveryListener implements ChatRoomListener {
    public void onMessage(ChatRoom room, Message message) {
        room.getMembers().stream()
            .filter(u -> !u.equals(message.getSender()))
            .forEach(u -> System.out.println(
                "[" + room.getName() + "] " + u.getName() + " received: " + message.getText()));
    }
}

// Audit logs every message
class AuditLogListener implements ChatRoomListener {
    public void onMessage(ChatRoom room, Message message) {
        System.out.println("[AUDIT] " + message.getSender().getName()
            + " in " + room.getName() + ": " + message.getText());
    }
}

// ══════════ STRATEGY — Join Policy (PUBLIC vs PRIVATE) ══════════

interface JoinPolicy {
    boolean canJoin(User user, ChatRoom room);
}

class PublicJoinPolicy implements JoinPolicy {
    public boolean canJoin(User user, ChatRoom room) { return true; }
}

class PrivateJoinPolicy implements JoinPolicy {
    private final Set<String> invitedUserIds;

    PrivateJoinPolicy(Set<String> invitedUserIds) {
        this.invitedUserIds = invitedUserIds;
    }

    public boolean canJoin(User user, ChatRoom room) {
        return invitedUserIds.contains(user.getId());
    }
}

// ══════════ CHAT ROOM (Subject — Observer pattern) ══════════

class ChatRoom {
    private final String id;
    private final String name;
    private final User owner;
    private final JoinPolicy joinPolicy;
    private final Set<User> members = new LinkedHashSet<>();
    private final List<Message> messages = new ArrayList<>();
    private final List<ChatRoomListener> listeners = new ArrayList<>();

    ChatRoom(String name, User owner, JoinPolicy joinPolicy) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.owner = owner;
        this.joinPolicy = joinPolicy;
        this.members.add(owner);
    }

    // Observer management
    void subscribe(ChatRoomListener listener) { listeners.add(listener); }
    void unsubscribe(ChatRoomListener listener) { listeners.remove(listener); }

    // Join with access control (Strategy)
    void join(User user) {
        if (!joinPolicy.canJoin(user, this)) {
            throw new IllegalStateException("User not allowed to join this room");
        }
        members.add(user);
    }

    void leave(User user) { members.remove(user); }

    // Send message — stored + observers notified
    void sendMessage(User sender, String text) {
        if (!members.contains(sender)) throw new IllegalStateException("Not a member");
        Message msg = new Message(sender, text);
        messages.add(msg);
        listeners.forEach(l -> l.onMessage(this, msg));
    }

    // Defensive returns
    Set<User> getMembers() { return Collections.unmodifiableSet(members); }
    List<Message> getMessages() { return Collections.unmodifiableList(messages); }
    String getName() { return name; }
}

// ══════════ SERVICE ══════════

class ChatService {
    private final Map<String, ChatRoom> rooms = new HashMap<>();

    ChatRoom createPublicRoom(String name, User owner) {
        ChatRoom room = new ChatRoom(name, owner, new PublicJoinPolicy());
        rooms.put(name, room);
        return room;
    }

    ChatRoom createPrivateRoom(String name, User owner, Set<String> invitedIds) {
        ChatRoom room = new ChatRoom(name, owner, new PrivateJoinPolicy(invitedIds));
        rooms.put(name, room);
        return room;
    }
}

// ══════════ USAGE ══════════

class Day16ChatApplication {
    public static void main(String[] args) {
        User alice = new User("1", "Alice");
        User bob = new User("2", "Bob");
        User charlie = new User("3", "Charlie");

        ChatService service = new ChatService();
        ChatRoom general = service.createPublicRoom("General", alice);

        // Subscribe observers
        general.subscribe(new MessageDeliveryListener());
        general.subscribe(new AuditLogListener());

        // Users join
        general.join(bob);
        general.join(charlie);

        // Alice sends — Bob and Charlie receive, Alice doesn't
        general.sendMessage(alice, "Hello everyone!");
        // Output:
        // [General] Bob received: Hello everyone!
        // [General] Charlie received: Hello everyone!
        // [AUDIT] Alice in General: Hello everyone!
    }
}


class TrafficSystemClient {
    public void main(String[] args) {
        // Switch on the traffic system
    }
}

enum Signal {
    NORTH(),
    EAST(),
    SOUTH(),
    WEST();

    private SignalState state;
    private Signal nextSignal;

    Signal() {
        this.state = new RedState();
    }

    static {
        NORTH.setNextSignal(EAST);
        EAST.setNextSignal(SOUTH);
        SOUTH.setNextSignal(WEST);
        WEST.setNextSignal(NORTH);
    }

    public void setNextSignal(Signal next) {
        this.nextSignal = next;
    }

    public Signal getNextSignal() {
        return this.nextSignal;
    }

    public void setState(SignalState state) {
        this.state = state;
    }

    public SignalState getState() {
        return this.state;
    }
}

interface SignalState {
    void setYellow(Signal signal);
    void setGreen(Signal signal);
    void setRed(Signal signal);
}

class RedState implements SignalState {

    @Override
    public void setYellow(Signal signal) {
        System.out.print("can't go to yellow");
    }

    @Override
     public void setGreen(Signal signal) {
        // logics to implement how long RED stays
        signal.setState(new GreenState());
        // Ensure other signals are set to RED state
        // Wait logic for 30 s
        signal.getState().setYellow(signal);
    }

    @Override
    public void setRed(Signal signal) {
        System.out.print("Already at yellow");
    }
}

class GreenState implements SignalState {

    @Override
    public void setYellow(Signal signal) {
        signal.setState(new YellowState());
        // Wait logic for 5 s
        signal.getState().setRed(signal);
    }

    @Override
     public void setGreen(Signal signal) {
        System.out.print("Already at Green");
    }

    @Override
    public void setRed(Signal signal) {
        System.out.print("Can't set to RED directly");
    }
}

class YellowState implements SignalState {

    @Override
    public void setYellow(Signal signal) {
        System.out.print("Already at Yello");
    }

    @Override
     public void setGreen(Signal signal) {
        System.out.print("Can't set to Green directly");
    }

    @Override
    public void setRed(Signal signal) {
        signal.setState(new RedState());
    }
}

class TrafficSystem {
    private Signal currentSignal;
    private boolean stopTrafficSystem;

    TrafficSystem() {
        this.currentSignal = Signal.NORTH;
        this.stopTrafficSystem = false;
    }

    public void autoDivertTraffic() {
       while(!stopTrafficSystem) {
            currentSignal.getState().setGreen(currentSignal);
            // above will complete the entire life cycle (Utill Red)

            this.currentSignal = currentSignal.getNextSignal();
       }
    }

    public void emergencyStop() {
        // Set all signals state to ReadState irrespective of other state
    }

    public void stopTrafficSystem() {
        this.stopTrafficSystem = true;
    }
}

