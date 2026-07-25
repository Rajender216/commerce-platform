package com.rcp.commerce.customer.domain;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

public record CustomerProfile(
        CustomerId id,
        String identitySubject,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        Instant createdAt,
        Instant updatedAt,
        long version) {

    public static final int SUBJECT_MAX_LENGTH = 128;
    public static final int EMAIL_MAX_LENGTH = 320;
    public static final int NAME_MAX_LENGTH = 120;
    public static final int PHONE_MAX_LENGTH = 40;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public CustomerProfile {
        Objects.requireNonNull(id, "id must not be null");
        identitySubject = required(identitySubject, "Identity subject is required.");
        email = required(email, "Email is required.").toLowerCase();
        firstName = required(firstName, "First name is required.");
        lastName = required(lastName, "Last name is required.");
        phoneNumber = optional(phoneNumber);
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        max(identitySubject, SUBJECT_MAX_LENGTH, "Identity subject");
        max(email, EMAIL_MAX_LENGTH, "Email");
        max(firstName, NAME_MAX_LENGTH, "First name");
        max(lastName, NAME_MAX_LENGTH, "Last name");
        max(phoneNumber, PHONE_MAX_LENGTH, "Phone number");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email must be valid.");
        }
        if (version < 0) {
            throw new IllegalArgumentException("version must not be negative.");
        }
    }

    public static CustomerProfile create(
            String identitySubject, String email, String firstName, String lastName, String phoneNumber, Clock clock) {
        Instant now = Instant.now(clock);
        return new CustomerProfile(
                CustomerId.newId(), identitySubject, email, firstName, lastName, phoneNumber, now, now, 0);
    }

    public CustomerProfile update(String firstName, String lastName, String phoneNumber, Clock clock) {
        return new CustomerProfile(
                id, identitySubject, email, firstName, lastName, phoneNumber, createdAt, Instant.now(clock), version);
    }

    private static String required(String value, String message) {
        String normalized = optional(value);
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private static String optional(String value) {
        return Objects.requireNonNullElse(value, "").trim();
    }

    private static void max(String value, int maxLength, String field) {
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must be at most " + maxLength + " characters.");
        }
    }
}
