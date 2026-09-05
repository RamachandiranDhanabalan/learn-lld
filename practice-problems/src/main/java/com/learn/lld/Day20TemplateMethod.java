package com.learn.lld;

/**
 * Day 20 Practice — Payment Processing Pipeline (Multi-Pattern)
 *
 * Combines THREE patterns from pressure:
 * - Template Method: process() fixed flow, subclasses fill charge()
 * - Adapter: PaymentAdapter wraps third-party SDKs (Stripe, Razorpay)
 * - Hook: detectFraud() optional — only some payment methods override
 * - DIP: adapter injected into payment process
 *
 * Two independent dimensions:
 *   Payment METHOD (Template subclass) × Payment PROVIDER (Adapter)
 *   new CreditCardPayment(new StripeAdapter()) — mix any method + provider
 */

// ══════════ TEMPLATE METHOD — fixed payment flow ══════════

abstract class PaymentProcess {
    protected final PaymentAdapter adapter;  // DIP — injected provider

    PaymentProcess(PaymentAdapter adapter) {
        this.adapter = adapter;
    }

    // Template method — final, defines the fixed flow
    public final void process(int amount) {
        validate(amount);
        detectFraud(amount);       // hook
        charge(amount);            // varies by payment method
        recordTransaction(amount);
        sendReceipt();
    }

    // Shared steps (same for all)
    protected void validate(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Invalid amount");
        System.out.println("Validated ₹" + amount);
    }
    protected void recordTransaction(int amount) {
        System.out.println("Transaction recorded: ₹" + amount);
    }
    protected void sendReceipt() {
        System.out.println("Receipt sent");
    }

    // Hook — default does nothing; subclass MAY override
    protected void detectFraud(int amount) { }

    // Abstract step — subclass MUST implement (varies by method)
    protected abstract void charge(int amount);
}

// ══════════ CONCRETE PAYMENT METHODS (Template subclasses) ══════════

class CreditCardPayment extends PaymentProcess {
    CreditCardPayment(PaymentAdapter adapter) { super(adapter); }

    protected void charge(int amount) {
        System.out.println("Credit card charge:");
        adapter.makePayment(amount);
    }

    @Override
    protected void detectFraud(int amount) {  // credit cards override the hook
        System.out.println("Running fraud detection for ₹" + amount);
    }
}

class UpiPayment extends PaymentProcess {
    UpiPayment(PaymentAdapter adapter) { super(adapter); }

    protected void charge(int amount) {
        System.out.println("UPI charge:");
        adapter.makePayment(amount);
    }
    // No fraud detection override → uses default (no check)
}

class WalletPayment extends PaymentProcess {
    WalletPayment(PaymentAdapter adapter) { super(adapter); }

    protected void charge(int amount) {
        System.out.println("Wallet charge:");
        adapter.makePayment(amount);
    }
}

// ══════════ ADAPTER — wrap third-party providers ══════════

interface PaymentAdapter {
    void makePayment(int amount);
}

class StripeAdapter implements PaymentAdapter {
    // private final StripeSDK stripe;  // wrap the real SDK
    public void makePayment(int amount) {
        // translate our call → Stripe's API (cents, DTOs, etc.)
        System.out.println("  [Stripe] charged " + (amount * 100) + " cents");
    }
}

class RazorpayAdapter implements PaymentAdapter {
    // private final RazorpayClient razorpay;
    public void makePayment(int amount) {
        // translate our call → Razorpay's API
        System.out.println("  [Razorpay] charged ₹" + amount);
    }
}

// ══════════ USAGE ══════════

class Day20TemplateMethod {
    public static void main(String[] args) {
        // Mix any payment method with any provider
        PaymentProcess creditViaStripe = new CreditCardPayment(new StripeAdapter());
        creditViaStripe.process(1000);  // includes fraud detection (hook overridden)

        System.out.println("---");

        PaymentProcess upiViaRazorpay = new UpiPayment(new RazorpayAdapter());
        upiViaRazorpay.process(500);   // no fraud detection (hook default)
    }
}
