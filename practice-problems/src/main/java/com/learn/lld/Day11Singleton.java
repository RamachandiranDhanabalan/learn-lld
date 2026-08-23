package com.learn.lld;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Day 11 Practice Problem — Tax Calculation System
 *
 * Problem: TaxCalculationService was a broken Singleton (not thread-safe),
 * had multiple responsibilities (calculate + log + state/category/customer logic),
 * fat TaxProvider interface (LSP/ISP violation), public fields (encapsulation),
 * long parameter lists, and all tax logic in one if-else block.
 *
 * Concepts tested: Encapsulation, SRP, OCP, LSP, ISP, DIP, Singleton,
 * Strategy (multiple dimensions), Composition (nested strategies), YAGNI
 *
 * Solution: Multiple strategy dimensions (TaxRule × Category × State × CustomerType),
 * ISP split on TaxProvider, DI instead of Singleton, Invoice as encapsulated object.
 */

// ══════════ DOMAIN ══════════

class Invoice {
    private final String customerName;
    private final String email;
    private final String item;
    private final String category;
    private final String state;
    private final boolean isLuxury;
    private final String customerType;
    private final double price;
    private double tax;
    private double total;

    Invoice(String customerName, String email, String item, String category,
            String state, boolean isLuxury, String customerType, double price) {
        this.customerName = customerName;
        this.email = email;
        this.item = item;
        this.category = category;
        this.state = state;
        this.isLuxury = isLuxury;
        this.customerType = customerType;
        this.price = price;
    }

    // Getters
    public String getCustomerName() { return customerName; }
    public String getEmail() { return email; }
    public String getItem() { return item; }
    public String getCategory() { return category; }
    public String getState() { return state; }
    public boolean isLuxury() { return isLuxury; }
    public String getCustomerType() { return customerType; }
    public double getPrice() { return price; }
    public double getTax() { return tax; }
    public double getTotal() { return total; }

    // Controlled setters (only tax-related, set by service)
    void setTax(double tax) { this.tax = tax; this.total = this.price + tax; }
}

// ══════════ STRATEGY INTERFACES ══════════

// Each dimension of tax calculation is a separate strategy
interface TaxRuleStrategy {
    double applyTax(Invoice invoice);
}

interface CategoryTaxStrategy {
    double applyTax(Invoice invoice);
}

interface StateTaxStrategy {
    double applyTax(Invoice invoice);
}

interface CustomerDiscountStrategy {
    double applyDiscount(double tax, Invoice invoice);
}

// ══════════ TAX RULE STRATEGIES ══════════

class GstTaxRuleStrategy implements TaxRuleStrategy {
    private final CategoryTaxStrategy categoryStrategy;

    GstTaxRuleStrategy(CategoryTaxStrategy categoryStrategy) {
        this.categoryStrategy = categoryStrategy;
    }

    public double applyTax(Invoice invoice) {
        return categoryStrategy.applyTax(invoice);  // delegates to category
    }
}

class CessTaxRuleStrategy implements TaxRuleStrategy {
    public double applyTax(Invoice invoice) {
        return invoice.isLuxury() ? invoice.getPrice() * 0.01 : 0;
    }
}

// ══════════ CATEGORY STRATEGIES (nested inside GST) ══════════

class ElectronicsTaxStrategy implements CategoryTaxStrategy {
    public double applyTax(Invoice invoice) { return invoice.getPrice() * 0.18; }
}

class FoodTaxStrategy implements CategoryTaxStrategy {
    public double applyTax(Invoice invoice) { return invoice.getPrice() * 0.05; }
}

class ClothingTaxStrategy implements CategoryTaxStrategy {
    public double applyTax(Invoice invoice) { return invoice.getPrice() * 0.12; }
}

// ══════════ STATE STRATEGIES ══════════

class MaharashtraStateTax implements StateTaxStrategy {
    public double applyTax(Invoice invoice) { return invoice.getPrice() * 0.01; }
}

class KarnatakaStateTax implements StateTaxStrategy {
    public double applyTax(Invoice invoice) { return invoice.getPrice() * 0.005; }
}

class NoStateTax implements StateTaxStrategy {
    public double applyTax(Invoice invoice) { return 0; }
}

// ══════════ CUSTOMER DISCOUNT STRATEGIES ══════════

class WholesaleDiscount implements CustomerDiscountStrategy {
    public double applyDiscount(double tax, Invoice invoice) { return tax * 0.9; }
}

class GovernmentDiscount implements CustomerDiscountStrategy {
    public double applyDiscount(double tax, Invoice invoice) { return 0; }
}

class NoDiscount implements CustomerDiscountStrategy {
    public double applyDiscount(double tax, Invoice invoice) { return tax; }
}

// ══════════ TAX CALCULATION SERVICE (orchestrator) ══════════

class TaxCalculationService {
    private final List<TaxRuleStrategy> taxRules;
    private final StateTaxStrategy stateTax;
    private final CustomerDiscountStrategy customerDiscount;

    TaxCalculationService(List<TaxRuleStrategy> taxRules,
                          StateTaxStrategy stateTax,
                          CustomerDiscountStrategy customerDiscount) {
        this.taxRules = taxRules;
        this.stateTax = stateTax;
        this.customerDiscount = customerDiscount;
    }

    public double calculate(Invoice invoice) {
        double tax = 0;

        // Apply all tax rules (GST + CESS + any future rules)
        for (TaxRuleStrategy rule : taxRules) {
            tax += rule.applyTax(invoice);
        }

        // Apply state tax
        tax += stateTax.applyTax(invoice);

        // Apply customer discount
        tax = customerDiscount.applyDiscount(tax, invoice);

        return tax;
    }
}

// ══════════ INVOICE SERVICE ══════════

class InvoiceService {
    private final TaxCalculationService taxService;

    InvoiceService(TaxCalculationService taxService) {
        this.taxService = taxService;
    }

    public Invoice createInvoice(Invoice invoice) {
        double tax = taxService.calculate(invoice);
        invoice.setTax(tax);
        return invoice;
    }
}

// ══════════ ISP FIX — Split fat TaxProvider interface ══════════

interface TaxProvider {
    double calculate(double price);
    String getProviderName();
}

interface Reportable {
    void exportReport(String format);
}

interface Notifiable {
    void sendNotification(String channel, String message);
}

interface Loggable {
    void log(String message);
}

// GSTProvider only implements what it can do — no UnsupportedOperationException
class GSTProvider implements TaxProvider, Loggable {
    public double calculate(double price) { return price * 0.18; }
    public String getProviderName() { return "GST"; }
    public void log(String message) { System.out.println("[GST] " + message); }
    // Does NOT implement Reportable or Notifiable — no broken promises (LSP safe)
}

// ══════════ USAGE ══════════

class Day11Singleton {
    public static void main(String[] args) {
        // Wire up strategies (Maharashtra, Electronics, Wholesale customer)
        CategoryTaxStrategy categoryTax = new ElectronicsTaxStrategy();
        TaxRuleStrategy gst = new GstTaxRuleStrategy(categoryTax);
        TaxRuleStrategy cess = new CessTaxRuleStrategy();
        StateTaxStrategy stateTax = new MaharashtraStateTax();
        CustomerDiscountStrategy discount = new WholesaleDiscount();

        TaxCalculationService taxService = new TaxCalculationService(
                List.of(gst, cess), stateTax, discount);

        InvoiceService invoiceService = new InvoiceService(taxService);

        // Create invoice
        Invoice invoice = new Invoice(
                "Ram", "ram@example.com", "Laptop",
                "ELECTRONICS", "MAHARASHTRA", true, "WHOLESALE", 100000.0);

        Invoice result = invoiceService.createInvoice(invoice);
        System.out.println("Price: " + result.getPrice());
        System.out.println("Tax: " + result.getTax());
        System.out.println("Total: " + result.getTotal());
    }
}
