package com.learn.lld;

import java.io.File;
import java.util.*;

/**
 * Day 10 Practice Problem — Email System using Builder + Strategy + Factory
 *
 * Problem: EmailMessage had public fields, setters without validation,
 * send() logic mixed with message construction, and if-else on message type.
 *
 * Solution:
 * - Builder pattern for EmailMessage (many optional params, immutable result)
 * - Strategy pattern for email sending (TemplateEmail, HtmlEmail, TextEmail)
 * - Factory (resolver) inside service picks the right strategy based on message content
 * - Caller doesn't decide which email type — service resolves automatically
 *
 * Concepts applied: Builder, Strategy, Factory, SRP, DIP, Encapsulation, OCP
 */

// ══════════ PRODUCT — EmailMessage (Builder Pattern) ══════════

class EmailMessage {
    // All fields private final — immutable after creation
    private final String from;
    private final String to;
    private final String subject;
    private final String cc;
    private final String bcc;
    private final String bodyText;
    private final String bodyHtml;
    private final List<File> attachments;
    private final Map<String, String> headers;
    private final String replyTo;
    private final boolean isUrgent;
    private final String templateId;
    private final Map<String, Object> templateVars;

    // Private constructor — only Builder can create
    private EmailMessage(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.subject = builder.subject;
        this.cc = builder.cc;
        this.bcc = builder.bcc;
        this.bodyText = builder.bodyText;
        this.bodyHtml = builder.bodyHtml;
        this.attachments = builder.attachments != null
                ? Collections.unmodifiableList(builder.attachments) : List.of();
        this.headers = builder.headers != null
                ? Collections.unmodifiableMap(builder.headers) : Map.of();
        this.replyTo = builder.replyTo;
        this.isUrgent = builder.isUrgent;
        this.templateId = builder.templateId;
        this.templateVars = builder.templateVars != null
                ? Collections.unmodifiableMap(builder.templateVars) : Map.of();
    }

    // Getters only — no setters
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getSubject() { return subject; }
    public String getCc() { return cc; }
    public String getBcc() { return bcc; }
    public String getBodyText() { return bodyText; }
    public String getBodyHtml() { return bodyHtml; }
    public List<File> getAttachments() { return attachments; }
    public Map<String, String> getHeaders() { return headers; }
    public String getReplyTo() { return replyTo; }
    public boolean isUrgent() { return isUrgent; }
    public String getTemplateId() { return templateId; }
    public Map<String, Object> getTemplateVars() { return templateVars; }

    // Entry point for builder (required fields in params)
    public static Builder builder(String from, String to, String subject) {
        return new Builder(from, to, subject);
    }

    // ══════════ BUILDER (inner class) ══════════
    public static class Builder {
        // Required — final (set in constructor, never reassigned)
        private final String from;
        private final String to;
        private final String subject;

        // Optional — NOT final (set by fluent methods, have defaults)
        private String cc;
        private String bcc;
        private String bodyText;
        private String bodyHtml;
        private List<File> attachments;
        private Map<String, String> headers;
        private String replyTo;
        private boolean isUrgent = false;
        private String templateId;
        private Map<String, Object> templateVars;

        // Required fields enforced via constructor
        private Builder(String from, String to, String subject) {
            this.from = from;
            this.to = to;
            this.subject = subject;
        }

        // Fluent methods (return this for chaining)
        public Builder cc(String cc) { this.cc = cc; return this; }
        public Builder bcc(String bcc) { this.bcc = bcc; return this; }
        public Builder bodyText(String bodyText) { this.bodyText = bodyText; return this; }
        public Builder bodyHtml(String bodyHtml) { this.bodyHtml = bodyHtml; return this; }
        public Builder attachments(List<File> attachments) { this.attachments = attachments; return this; }
        public Builder headers(Map<String, String> headers) { this.headers = headers; return this; }
        public Builder replyTo(String replyTo) { this.replyTo = replyTo; return this; }
        public Builder urgent(boolean urgent) { this.isUrgent = urgent; return this; }
        public Builder templateId(String templateId) { this.templateId = templateId; return this; }
        public Builder templateVars(Map<String, Object> vars) { this.templateVars = vars; return this; }

        // Validation + creation — fail fast, single point of truth
        public EmailMessage build() {
            if (from == null || from.isBlank())
                throw new IllegalStateException("From is required");
            if (to == null || to.isBlank())
                throw new IllegalStateException("To is required");
            if (subject == null || subject.isBlank())
                throw new IllegalStateException("Subject is required");
            if (bodyText == null && bodyHtml == null && templateId == null)
                throw new IllegalStateException("Must have bodyText, bodyHtml, or templateId");
            if (templateId != null && (templateVars == null || templateVars.isEmpty()))
                throw new IllegalStateException("Template requires templateVars");

            return new EmailMessage(this);
        }
    }
}

// ══════════ STRATEGY — Email Sending (different ways to send) ══════════

interface EmailSender {
    void send(EmailMessage message);
}

class TemplateEmailSender implements EmailSender {
    @Override
    public void send(EmailMessage message) {
        // Render template with vars, then send
        String body = renderTemplate(message.getTemplateId(), message.getTemplateVars());
        doSend(message, body);
    }

    private String renderTemplate(String templateId, Map<String, Object> vars) {
        // TemplateEngine.render(templateId, vars)
        return "Rendered: " + templateId + " with " + vars;
    }

    private void doSend(EmailMessage msg, String body) {
        System.out.println("Sending template email to " + msg.getTo() + ": " + body);
    }
}

class HtmlEmailSender implements EmailSender {
    @Override
    public void send(EmailMessage message) {
        System.out.println("Sending HTML email to " + message.getTo() + ": " + message.getBodyHtml());
    }
}

class TextEmailSender implements EmailSender {
    @Override
    public void send(EmailMessage message) {
        System.out.println("Sending text email to " + message.getTo() + ": " + message.getBodyText());
    }
}

// ══════════ SERVICE — Resolves strategy internally (Factory + Strategy) ══════════

class EmailService {
    // Registry of senders (DIP — depends on interface, injected)
    private final Map<String, EmailSender> senders;

    EmailService(Map<String, EmailSender> senders) {
        this.senders = senders;
    }

    // Convenience constructor with defaults
    EmailService() {
        this.senders = Map.of(
                "TEMPLATE", new TemplateEmailSender(),
                "HTML", new HtmlEmailSender(),
                "TEXT", new TextEmailSender()
        );
    }

    public void send(EmailMessage message) {
        EmailSender sender = resolveSender(message);  // Factory: picks right strategy
        sender.send(message);                          // Strategy: executes
    }

    // Internal factory/resolver — caller doesn't need to know which sender is used
    private EmailSender resolveSender(EmailMessage message) {
        if (message.getTemplateId() != null) return senders.get("TEMPLATE");
        if (message.getBodyHtml() != null) return senders.get("HTML");
        return senders.get("TEXT");
    }
}

// ══════════ NOTIFICATION SERVICE — Uses EmailService (SRP: notification logic only) ══════════

class NotificationService {
    private final EmailService emailService;

    NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    void sendWelcome(User user) {
        EmailMessage msg = EmailMessage.builder("noreply@company.com", user.getEmail(), "Welcome!")
                .bodyText("Hi " + user.getName() + ", welcome to our platform!")
                .build();

        emailService.send(msg);  // service resolves: text email
    }

    void sendInvoice(User user, Invoice invoice) {
        EmailMessage msg = EmailMessage.builder("billing@company.com", user.getEmail(), "Invoice #" + invoice.getId())
                .templateId("invoice_template")
                .templateVars(Map.of("amount", invoice.getTotal(), "date", invoice.getDate()))
                .attachments(List.of(invoice.getPdfFile()))
                .urgent(true)
                .build();

        emailService.send(msg);  // service resolves: template email
    }

    void sendMarketingHtml(User user, String campaign) {
        EmailMessage msg = EmailMessage.builder("marketing@company.com", user.getEmail(), "Special Offer!")
                .bodyHtml("<h1>Hi " + user.getName() + "</h1><p>Check out our " + campaign + "</p>")
                .build();

        emailService.send(msg);  // service resolves: html email
    }
}

// ══════════ DOMAIN OBJECTS (encapsulated) ══════════

class User {
    private final String name;
    private final String email;

    User(String name, String email) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Valid email required");
        this.name = name;
        this.email = email;
    }

    String getName() { return name; }
    String getEmail() { return email; }
}

class Invoice {
    private final String id;
    private final String total;
    private final String date;
    private final File pdfFile;

    Invoice(String id, String total, String date, File pdfFile) {
        this.id = id;
        this.total = total;
        this.date = date;
        this.pdfFile = pdfFile;
    }

    String getId() { return id; }
    String getTotal() { return total; }
    String getDate() { return date; }
    File getPdfFile() { return pdfFile; }
}

// ══════════ USAGE ══════════

class Day10BuilderPattern {
    public static void main(String[] args) {
        // Wire up
        EmailService emailService = new EmailService();  // uses default senders
        NotificationService notifications = new NotificationService(emailService);

        // Use — caller doesn't know which email type is used
        User user = new User("Ram", "ram@example.com");
        notifications.sendWelcome(user);   // automatically uses TextEmailSender

        Invoice invoice = new Invoice("INV-001", "₹15,000", "2026-08-22", new File("invoice.pdf"));
        notifications.sendInvoice(user, invoice);  // automatically uses TemplateEmailSender

        notifications.sendMarketingHtml(user, "Summer Sale");  // automatically uses HtmlEmailSender
    }
}
