package com.rcp.commerce.customer.adapter.in.web;

import com.rcp.commerce.customer.application.service.AddressNotFoundException;
import com.rcp.commerce.customer.application.service.CustomerNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
class GlobalExceptionHandler {

    private final Clock clock;

    GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemResponse> handleValidation(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_FAILED,
                "Validation failed",
                "Request validation failed.",
                request,
                exception.getBindingResult().getFieldErrors().stream()
                        .map(GlobalExceptionHandler::toValidationError)
                        .toList());
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    ResponseEntity<ProblemResponse> handleCustomerNotFound(
            CustomerNotFoundException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.NOT_FOUND,
                ErrorCode.CUSTOMER_NOT_FOUND,
                "Customer not found",
                "Customer profile was not found.",
                request,
                List.of());
    }

    @ExceptionHandler(AddressNotFoundException.class)
    ResponseEntity<ProblemResponse> handleAddressNotFound(
            AddressNotFoundException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.NOT_FOUND,
                ErrorCode.ADDRESS_NOT_FOUND,
                "Address not found",
                "Shipping address " + exception.addressId().value() + " was not found.",
                request,
                List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ProblemResponse> handleConflict(
            DataIntegrityViolationException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.CONFLICT,
                ErrorCode.CONFLICT,
                "Conflict",
                "The request conflicts with existing customer data.",
                request,
                List.of());
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ProblemResponse> handleBadRequest(Exception exception, HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                ErrorCode.BAD_REQUEST,
                "Bad request",
                exception instanceof IllegalArgumentException
                        ? exception.getMessage()
                        : "The request could not be processed.",
                request,
                List.of());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ProblemResponse> handleMalformedBody(
            HttpMessageNotReadableException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                ErrorCode.BAD_REQUEST,
                "Bad request",
                "The request body could not be read.",
                request,
                List.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ProblemResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_ERROR,
                "Internal server error",
                "The request could not be processed.",
                request,
                List.of());
    }

    private ResponseEntity<ProblemResponse> problem(
            HttpStatus status,
            ErrorCode code,
            String title,
            String detail,
            HttpServletRequest request,
            List<ValidationError> validationErrors) {
        return ResponseEntity.status(status)
                .body(new ProblemResponse(
                        "https://api.rcp-commerce.local/problems/"
                                + code.name().toLowerCase().replace('_', '-'),
                        title,
                        status.value(),
                        detail,
                        request.getRequestURI(),
                        Instant.now(clock),
                        code.name(),
                        MDC.get(CorrelationIdFilter.MDC_KEY),
                        validationErrors));
    }

    private static ValidationError toValidationError(FieldError fieldError) {
        return new ValidationError(fieldError.getField(), fieldError.getDefaultMessage());
    }
}
