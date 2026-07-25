package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.application.service.ProductNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        List<ValidationError> validationErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(GlobalExceptionHandler::toValidationError)
                .toList();
        return problem(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_FAILED,
                "Validation failed",
                "Request validation failed.",
                request,
                validationErrors);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    ResponseEntity<ProblemResponse> handleProductNotFound(
            ProductNotFoundException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.NOT_FOUND,
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product not found",
                "Product " + exception.productId().value() + " was not found.",
                request,
                List.of());
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ProblemResponse> handleBadRequest(Exception exception, HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                ErrorCode.BAD_REQUEST,
                "Bad request",
                "The request could not be processed.",
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
        ProblemResponse response = new ProblemResponse(
                "https://api.rcp-commerce.local/problems/"
                        + code.name().toLowerCase().replace('_', '-'),
                title,
                status.value(),
                detail,
                request.getRequestURI(),
                Instant.now(clock),
                code.name(),
                MDC.get(CorrelationIdFilter.MDC_KEY),
                validationErrors);
        return ResponseEntity.status(status).body(response);
    }

    private static ValidationError toValidationError(FieldError fieldError) {
        return new ValidationError(fieldError.getField(), fieldError.getDefaultMessage());
    }
}
