package com.learn.lld;

import java.util.*;

/**
 * Day 23 Practice — Shopping Cart / Checkout (Aggregates, Ownership, Domain Services)
 *
 * Original: public fields everywhere, hand-maintained totalAmount, embedded Customer
 * object, all logic in CheckoutService (anemic Cart), double money, "GOLD" strings.
 *
 * FIXES (each tied to a concept):
 *
 *  AGGREGATES / OWNERSHIP (Day 23)
 *   - Cart = AGGREGATE ROOT. Owns CartLines (composition). Only entry point.
 *   - CartLine = internal; no lifecycle without Cart. lines list is private; exposed as copy.
 *   - Customer = a DIFFERENT aggregate → Cart holds CustomerId, not the Customer object.
 *   - INVARIANT "total = sum of line subtotals" enforced by DERIVING total(), not storing it.
 *     (Original stored totalAmount → drifts the moment a 2nd mutator touches lines.)
 *   - Loyalty points belong to Customer's aggregate → updated via CustomerService, not Cart.
 *
 *  DOMAIN vs APPLICATION service
 *   - CheckoutService orchestrates across aggregates (cart + customer); each aggregate
 *     still guards its own invariant (Cart owns total; Customer owns loyalty points).
 *
 *  ENTITY vs VALUE OBJECT (Day 22)
 *   - Money = immutable VALUE OBJECT, integer paise (NO double → no percentage drift).
 *   - Cart / Customer = ENTITIES (identity, equals by ID).
 *
 *  ENCAPSULATION (Day 2) — no public fields; internal list never leaked (copy on read).
 *  DIP (Day 7) — CheckoutService depends on injected CustomerService abstraction.
 *  PATTERN RESTRAINT (Day 21) — discount stays if/else (tiers stable). No Strategy.
 */
public class Day23Aggregates {

    public static void main(String[] args) {
        CustomerRepository customers = new InMemoryCustomerRepository();
        customers.save(new Customer("C1", "Alice", CustomerTier.GOLD));

        CheckoutService checkout = new CheckoutService(customers);

        Cart cart = new Cart("CART1", "C1");
        cart.addLine("P-iphone", 1, Money.ofRupees(80000));
        cart.addLine("P-case", 2, Money.ofRupees(500));

        System.out.println("Cart total: " + cart.total());   // 81000
        checkout.checkout(cart);
        System.out.println("Cart status: " + cart.status());
        System.out.println("Alice points: " + customers.byId("C1").loyaltyPoints());
    }
}

// ═══════════ VALUE OBJECT — Money (immutable, integer paise, no double) ═══════════

final class Money {
    private final long paise;

    private Money(long paise) {
        if (paise < 0) throw new IllegalArgumentException("money cannot be negative");
        this.paise = paise;
    }
    static Money ofRupees(long rupees) { return new Money(rupees * 100); }
    static Money zero() { return new Money(0); }

    Money add(Money o)        { return new Money(this.paise + o.paise); }
    Money times(long factor)  { return new Money(this.paise * factor); }
    Money percentage(int pct) { return new Money(this.paise * pct / 100); }  // integer math, no drift
    Money minus(Money o)      { return new Money(this.paise - o.paise); }
    long toRupees()           { return paise / 100; }

    @Override public boolean equals(Object o) { return (o instanceof Money m) && paise == m.paise; }
    @Override public int hashCode() { return Long.hashCode(paise); }
    @Override public String toString() { return "INR " + (paise / 100.0); }
}

// ═══════════ CART AGGREGATE (root = Cart, owns CartLine) ═══════════

enum CartStatus { ACTIVE, CHECKED_OUT }

class Cart {                                   // AGGREGATE ROOT
    private final String cartId;               // identity
    private final String customerId;           // OTHER aggregate → by ID
    private final List<CartLine> lines = new ArrayList<>();   // owned, hidden
    private CartStatus status = CartStatus.ACTIVE;

    Cart(String cartId, String customerId) {
        this.cartId = cartId;
        this.customerId = customerId;
    }

    void addLine(String productId, int qty, Money unitPrice) {   // ONLY way to add
        if (status != CartStatus.ACTIVE)
            throw new IllegalStateException("cannot modify a checked-out cart");
        if (qty <= 0) throw new IllegalArgumentException("qty must be positive");
        lines.add(new CartLine(productId, qty, unitPrice));
    }

    Money total() {                             // DERIVED — invariant can never drift
        return lines.stream().map(CartLine::subtotal).reduce(Money.zero(), Money::add);
    }

    void markCheckedOut() { this.status = CartStatus.CHECKED_OUT; }

    String customerId() { return customerId; }
    CartStatus status()  { return status; }
    List<CartLine> lines() { return List.copyOf(lines); }   // read-only view, no leak

    @Override public boolean equals(Object o) { return (o instanceof Cart c) && cartId.equals(c.cartId); }
    @Override public int hashCode() { return cartId.hashCode(); }
}

class CartLine {                                // internal to the Cart aggregate
    private final String productId;
    private final int quantity;
    private final Money unitPrice;

    CartLine(String productId, int quantity, Money unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    Money subtotal() { return unitPrice.times(quantity); }
}

// ═══════════ CUSTOMER AGGREGATE (separate root) ═══════════

enum CustomerTier { GOLD, SILVER, REGULAR }

class Customer {                                // AGGREGATE ROOT (its own aggregate)
    private final String id;                    // identity
    private String name;
    private CustomerTier tier;
    private int loyaltyPoints;

    Customer(String id, String name, CustomerTier tier) {
        this.id = id; this.name = name; this.tier = tier;
    }

    CustomerTier tier() { return tier; }
    int loyaltyPoints() { return loyaltyPoints; }
    void addLoyaltyPoints(int pts) {            // Customer guards its OWN state
        if (pts < 0) throw new IllegalArgumentException("points cannot be negative");
        this.loyaltyPoints += pts;
    }
    String id() { return id; }
}

interface CustomerRepository {                  // DIP — abstraction, injected
    Customer byId(String id);
    void save(Customer c);
}
class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<String, Customer> store = new HashMap<>();
    public Customer byId(String id) { return store.get(id); }
    public void save(Customer c) { store.put(c.id(), c); }
}

// ═══════════ CHECKOUT — coordinates two aggregates ═══════════

class CheckoutService {
    private final CustomerRepository customers;

    CheckoutService(CustomerRepository customers) { this.customers = customers; }

    // Discount stays if/else — tiers are stable (Day 21 restraint). Money math, no double.
    private Money payableFor(Cart cart) {
        Customer customer = customers.byId(cart.customerId());
        Money total = cart.total();
        Money discount = switch (customer.tier()) {
            case GOLD   -> total.percentage(20);
            case SILVER -> total.percentage(10);
            case REGULAR -> Money.zero();
        };
        return total.minus(discount);
    }

    void checkout(Cart cart) {
        Money payable = payableFor(cart);
        System.out.println("Charging " + payable);

        // loyalty points belong to the Customer aggregate — mutate it through its own root
        Customer customer = customers.byId(cart.customerId());
        customer.addLoyaltyPoints((int) (payable.toRupees() / 100));
        customers.save(customer);

        cart.markCheckedOut();      // Cart guards its own status transition
    }
}
