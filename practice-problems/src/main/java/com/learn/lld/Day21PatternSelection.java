package com.learn.lld;

import java.util.*;

/**
 * Day 21 Practice — Pattern Selection (Choosing the Right Tool, or None)
 *
 * The problem: a god-method OrderManager.placeOrder() doing 5 unrelated things
 * with a 6-param signature. The SKILL of the day is knowing where to add a
 * pattern (earned) vs where to deliberately stay simple (bait to avoid).
 *
 * DECISIONS (each tied to a force):
 *
 *  1. 6 params → parameter objects (Customer, Order).
 *     Force: readability + positional-bug safety (two Strings swappable). SRP at signature.
 *
 *  2. Discount → extract to DiscountCalculator BUT keep if/else inside it.
 *     Force to EXTRACT: SRP + reuse + testability.
 *     NO force to add Strategy: tier set is STABLE (added ~yearly). if/else is correct.
 *     (If tiers changed every sprint → Strategy. Not today.)
 *
 *  3. Payment → PaymentProcessor interface + one adapter per provider.
 *     Force: incompatible 3rd-party SDKs (Adapter) + new providers every sprint (OCP).
 *     Selecting one at runtime IS the Strategy behaviour — same interface, swapped impl.
 *     We do NOT build a separate Strategy layer on top. A Factory picks the adapter.
 *
 *  4. Persistence → OrderRepository interface (DIP: swap DB / mock in tests). SRP.
 *
 *  5. Notification → Notifier interface, EmailNotifier + SmsNotifier, call both.
 *     BAIT AVOIDED: only 2, fixed, always-sent → NO Observer. Just inject and loop.
 *     (If channels were added dynamically / conditionally → Observer. Not today.)
 *
 *  6. Logging → ONE line, no growth signal → LEAVE AS IS. No Logger framework, no Observer.
 *
 * RESULT: OrderManager becomes a thin ORCHESTRATOR. One reason to change:
 * the order-placement FLOW. Everything else moved out. That is not a pattern —
 * it is just good SRP + DIP.
 */
public class Day21PatternSelection {

    public static void main(String[] args) {
        // Wiring (in real code: Spring DI / a composition root)
        DiscountCalculator discount = new DiscountCalculator();
        OrderRepository repo = new InMemoryOrderRepository();
        PaymentProcessorFactory paymentFactory = new PaymentProcessorFactory();
        List<Notifier> notifiers = List.of(new EmailNotifier(), new SmsNotifier());

        OrderManager manager = new OrderManager(discount, repo, paymentFactory, notifiers);

        Customer customer = new Customer("user@x.com", "9876543210", Tier.GOLD);
        Order order = new Order("ORD1", 450.0);

        manager.placeOrder(order, customer, PaymentType.UPI);
    }
}

// ══════════════ 1. PARAMETER OBJECTS (kill the 6-arg signature) ══════════════

enum Tier { GOLD, SILVER, BRONZE, NONE }
enum PaymentType { UPI, CARD, WALLET }

class Customer {
    private final String email;
    private final String phone;
    private final Tier tier;

    Customer(String email, String phone, Tier tier) {
        this.email = email;
        this.phone = phone;
        this.tier = tier;
    }
    String email() { return email; }
    String phone() { return phone; }
    Tier tier() { return tier; }
}

class Order {
    private final String id;
    private final double amount;
    private double finalAmount;   // set after discount

    Order(String id, double amount) {
        this.id = id;
        this.amount = amount;
        this.finalAmount = amount;
    }
    String id() { return id; }
    double amount() { return amount; }
    double finalAmount() { return finalAmount; }
    void applyFinalAmount(double v) { this.finalAmount = v; }
}

// ══════════════ 2. DISCOUNT — extracted class, if/else INSIDE (no Strategy) ══════════════
// SRP + reuse justify the class. Stable tier set justifies keeping if/else.

class DiscountCalculator {
    double discountFor(double amount, Tier tier) {
        // Closed, stable set → if/else (here: switch) is the correct, senior choice.
        switch (tier) {
            case GOLD:   return amount * 0.20;
            case SILVER: return amount * 0.10;
            case BRONZE: return amount * 0.05;
            default:     return 0.0;
        }
    }
}

// ══════════════ 3. PAYMENT — Adapter (= Strategy) + Factory to select ══════════════
// One interface. One adapter per provider. Runtime selection = Strategy, for free.

interface PaymentProcessor {                 // WHY: common interface = OCP + swappable
    void charge(double amount);
}

// Adapter: wraps an incompatible third-party SDK, maps DTOs / parses responses.
class UpiAdapter implements PaymentProcessor {
    public void charge(double amount) {
        // map to UPI SDK DTO, invoke SDK, parse SDK response back to our model
        System.out.println("Charging via UPI gateway: " + amount);
    }
}
class CardAdapter implements PaymentProcessor {
    public void charge(double amount) {
        System.out.println("Charging via Card gateway: " + amount);
    }
}
class WalletAdapter implements PaymentProcessor {
    public void charge(double amount) {
        System.out.println("Charging via Wallet gateway: " + amount);
    }
}

// Factory: the ONLY place that maps type → concrete adapter.
// Keeps the selection if/else OUT of OrderManager (SRP + OCP: add a case here only).
class PaymentProcessorFactory {
    PaymentProcessor create(PaymentType type) {
        switch (type) {
            case UPI:    return new UpiAdapter();
            case CARD:   return new CardAdapter();
            case WALLET: return new WalletAdapter();
            default: throw new IllegalArgumentException("Unknown payment type: " + type);
        }
    }
}

// ══════════════ 4. PERSISTENCE — interface (DIP) ══════════════

interface OrderRepository {                  // WHY: DIP — inject, swap real DB / mock
    void save(Order order);
}
class InMemoryOrderRepository implements OrderRepository {
    private final Map<String, Order> store = new HashMap<>();
    public void save(Order order) {
        store.put(order.id(), order);
        System.out.println("Saved order " + order.id() + " (" + order.finalAmount() + ")");
    }
}

// ══════════════ 5. NOTIFICATION — interface + 2 impls, NO Observer ══════════════
// Only 2, fixed, always-sent → inject a list and loop. Observer would be over-engineering.

interface Notifier {                         // WHY: SRP per channel + uniform call
    void notify(Customer customer, Order order);
}
class EmailNotifier implements Notifier {
    public void notify(Customer c, Order o) {
        System.out.println("Email to " + c.email() + ": Order " + o.id() + " confirmed");
    }
}
class SmsNotifier implements Notifier {
    public void notify(Customer c, Order o) {
        System.out.println("SMS to " + c.phone() + ": Order " + o.id() + " confirmed");
    }
}

// ══════════════ THE ORCHESTRATOR — thin, one reason to change (the flow) ══════════════

class OrderManager {
    private final DiscountCalculator discountCalculator;
    private final OrderRepository repository;
    private final PaymentProcessorFactory paymentFactory;
    private final List<Notifier> notifiers;

    // DIP — depend on abstractions, injected. Unit-testable with mocks.
    OrderManager(DiscountCalculator discountCalculator,
                 OrderRepository repository,
                 PaymentProcessorFactory paymentFactory,
                 List<Notifier> notifiers) {
        this.discountCalculator = discountCalculator;
        this.repository = repository;
        this.paymentFactory = paymentFactory;
        this.notifiers = notifiers;
    }

    // Fixed sequence of steps — orchestration only, no business logic inline.
    public void placeOrder(Order order, Customer customer, PaymentType paymentType) {
        double discount = discountCalculator.discountFor(order.amount(), customer.tier());
        order.applyFinalAmount(order.amount() - discount);

        PaymentProcessor processor = paymentFactory.create(paymentType);
        processor.charge(order.finalAmount());

        repository.save(order);

        for (Notifier n : notifiers) {
            n.notify(customer, order);
        }

        System.out.println("[LOG] Order " + order.id() + " placed at " + new Date());  // one line — left as-is
    }
}
