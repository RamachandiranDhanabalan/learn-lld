package com.learn.lld;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Day 22 Practice — Hotel Booking domain model (Entity vs Value Object)
 *
 * The original class was one god-object with 12 public fields, a broken equals(),
 * no hashCode, double money, mutable java.util.Date, and direct state mutation.
 *
 * FIXES (each tied to a concept):
 *
 *  ENTITY vs VALUE OBJECT
 *   - HotelBooking → ENTITY. Identity = bookingId. equals/hashCode on ID ONLY.
 *     (The original compared guest+dates+price → two distinct bookings collapse. Bug.)
 *   - Guest → ENTITY (extracted from loose guestName/guestEmail). Identity = email/id.
 *   - Money → VALUE OBJECT. Immutable, integer paise, equality by value. (was double → rounding)
 *   - Address → VALUE OBJECT. Immutable, equality by all fields.
 *   - DateRange → VALUE OBJECT. Immutable, wraps checkIn/checkOut, owns nights()/overlaps().
 *
 *  ENCAPSULATION (Day 2)
 *   - No public fields. Private + accessors.
 *   - amenities returned as unmodifiable copy (was: external code did b.amenities.add()).
 *   - status changes via cancel() with a transition guard (was: b.status = "CANCELLED").
 *
 *  TYPE SAFETY
 *   - BookingStatus enum (was: raw "CONFIRMED"/"CANCELLED" strings).
 *   - LocalDate (immutable) instead of java.util.Date (mutable, legacy).
 *
 *  PATTERN-SELECTION RESTRAINT (Day 21)
 *   - No Strategy/Factory/Observer added. Nothing here needs them. Just correct modeling.
 */
public class Day22EntityValueObject {

    public static void main(String[] args) {
        Guest alice = new Guest("g-alice", "Alice", "a@x.com");
        Money nightly = Money.ofRupees(2999, Currency.INR);
        Address room = new Address("12 MG Rd", "Bangalore", "560001");
        DateRange stay = new DateRange(LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 13));

        HotelBooking booking = new HotelBooking("BK1", alice, nightly, room, stay);
        booking.addAmenity("WiFi");

        System.out.println("Total: " + booking.total());        // 3 nights * 2999
        booking.cancel();
        System.out.println("Status: " + booking.status());

        // Entity equality: same ID = same booking, regardless of other fields
        HotelBooking sameId = new HotelBooking("BK1",
                new Guest("g-bob", "Bob", "b@x.com"),
                Money.ofRupees(100, Currency.INR), room, stay);
        System.out.println("Equal by ID: " + booking.equals(sameId));  // true

        // Value equality: two Money with same value are interchangeable
        System.out.println("Money equal: " + Money.ofRupees(2999, Currency.INR)
                .equals(Money.ofRupees(2999, Currency.INR)));          // true
    }
}

// ═══════════════ VALUE OBJECTS (immutable, equality by value) ═══════════════

enum Currency { INR, USD, EUR }

final class Money {
    private final long paise;          // integer minor units — NO double rounding
    private final Currency currency;

    private Money(long paise, Currency currency) {
        if (paise < 0) throw new IllegalArgumentException("money cannot be negative");
        this.paise = paise;
        this.currency = Objects.requireNonNull(currency);
    }

    static Money ofRupees(long rupees, Currency currency) {
        return new Money(rupees * 100, currency);
    }

    Money times(long factor) {          // returns NEW instance — no mutation
        return new Money(paise * factor, currency);
    }

    @Override public boolean equals(Object o) {
        return (o instanceof Money m) && paise == m.paise && currency == m.currency;
    }
    @Override public int hashCode() { return Objects.hash(paise, currency); }
    @Override public String toString() { return currency + " " + (paise / 100.0); }
}

final class Address {
    private final String street, city, zip;

    Address(String street, String city, String zip) {
        this.street = Objects.requireNonNull(street);
        this.city   = Objects.requireNonNull(city);
        this.zip    = Objects.requireNonNull(zip);
    }

    @Override public boolean equals(Object o) {
        return (o instanceof Address a)
            && street.equals(a.street) && city.equals(a.city) && zip.equals(a.zip);
    }
    @Override public int hashCode() { return Objects.hash(street, city, zip); }
}

final class DateRange {
    private final LocalDate start, end;

    DateRange(LocalDate start, LocalDate end) {
        if (!end.isAfter(start)) throw new IllegalArgumentException("end must be after start");
        this.start = start;         // LocalDate is itself immutable — safe to store
        this.end = end;
    }

    long nights() { return ChronoUnit.DAYS.between(start, end); }

    boolean overlaps(DateRange other) {
        return start.isBefore(other.end) && other.start.isBefore(end);
    }

    @Override public boolean equals(Object o) {
        return (o instanceof DateRange d) && start.equals(d.start) && end.equals(d.end);
    }
    @Override public int hashCode() { return Objects.hash(start, end); }
}

// ═══════════════ ENTITIES (identity, equality by ID) ═══════════════

class Guest {
    private final String id;      // identity
    private String name;          // can change; not part of identity
    private String email;

    Guest(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    String name() { return name; }
    void rename(String newName) { this.name = newName; }   // still the same guest

    @Override public boolean equals(Object o) {
        return (o instanceof Guest g) && id.equals(g.id);   // ID ONLY
    }
    @Override public int hashCode() { return id.hashCode(); }
}

enum BookingStatus { CONFIRMED, CANCELLED, CHECKED_OUT }

class HotelBooking {
    private final String bookingId;        // identity
    private final Guest guest;
    private final Money pricePerNight;
    private final Address room;
    private final DateRange stay;
    private final List<String> amenities = new ArrayList<>();
    private BookingStatus status;          // mutable lifecycle state

    HotelBooking(String bookingId, Guest guest, Money pricePerNight,
                 Address room, DateRange stay) {
        this.bookingId = Objects.requireNonNull(bookingId);
        this.guest = Objects.requireNonNull(guest);
        this.pricePerNight = Objects.requireNonNull(pricePerNight);
        this.room = Objects.requireNonNull(room);
        this.stay = Objects.requireNonNull(stay);
        this.status = BookingStatus.CONFIRMED;
    }

    Money total() { return pricePerNight.times(stay.nights()); }  // VO behaviour, no float math

    void addAmenity(String a) { amenities.add(a); }              // controlled mutation
    List<String> amenities() { return Collections.unmodifiableList(amenities); } // no leak

    BookingStatus status() { return status; }

    void cancel() {                                              // transition guard, not b.status=...
        if (status == BookingStatus.CHECKED_OUT)
            throw new IllegalStateException("cannot cancel a checked-out booking");
        this.status = BookingStatus.CANCELLED;
    }

    @Override public boolean equals(Object o) {
        return (o instanceof HotelBooking b) && bookingId.equals(b.bookingId);  // ID ONLY
    }
    @Override public int hashCode() { return bookingId.hashCode(); }
}