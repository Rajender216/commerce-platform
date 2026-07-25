package com.rcp.commerce.customer.application.service;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException() {
        super("Customer profile was not found.");
    }
}
