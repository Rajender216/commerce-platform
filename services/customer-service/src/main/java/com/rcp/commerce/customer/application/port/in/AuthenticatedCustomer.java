package com.rcp.commerce.customer.application.port.in;

public record AuthenticatedCustomer(String subject, String email, String firstName, String lastName) {}
