package com.rcp.commerce.catalog.application.service;

public class InvalidProductStatusTransitionException extends RuntimeException {

    public InvalidProductStatusTransitionException(String message) {
        super(message);
    }
}
