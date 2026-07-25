package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.application.service.CategoryNotFoundException;
import com.rcp.commerce.catalog.application.service.DuplicateSkuException;
import com.rcp.commerce.catalog.application.service.DuplicateSlugException;
import com.rcp.commerce.catalog.application.service.InvalidPaginationException;
import com.rcp.commerce.catalog.application.service.InvalidProductStatusTransitionException;
import com.rcp.commerce.catalog.application.service.ProductImageNotFoundException;
import com.rcp.commerce.catalog.application.service.ProductNotFoundException;
import com.rcp.commerce.catalog.application.service.VariantNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
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
                "Product " + exception.lookup() + " was not found.",
                request,
                List.of());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    ResponseEntity<ProblemResponse> handleCategoryNotFound(
            CategoryNotFoundException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.NOT_FOUND,
                ErrorCode.CATEGORY_NOT_FOUND,
                "Category not found",
                "Category " + exception.categoryId().value() + " was not found.",
                request,
                List.of());
    }

    @ExceptionHandler(VariantNotFoundException.class)
    ResponseEntity<ProblemResponse> handleVariantNotFound(
            VariantNotFoundException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.NOT_FOUND,
                ErrorCode.VARIANT_NOT_FOUND,
                "Variant not found",
                "Product variant " + exception.variantId().value() + " was not found.",
                request,
                List.of());
    }

    @ExceptionHandler(ProductImageNotFoundException.class)
    ResponseEntity<ProblemResponse> handleImageNotFound(
            ProductImageNotFoundException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.NOT_FOUND,
                ErrorCode.PRODUCT_IMAGE_NOT_FOUND,
                "Product image not found",
                "Product image " + exception.imageId().value() + " was not found.",
                request,
                List.of());
    }

    @ExceptionHandler(DuplicateSlugException.class)
    ResponseEntity<ProblemResponse> handleDuplicateSlug(DuplicateSlugException exception, HttpServletRequest request) {
        ErrorCode code = "Category".equals(exception.owner())
                ? ErrorCode.DUPLICATE_CATEGORY_SLUG
                : ErrorCode.DUPLICATE_PRODUCT_SLUG;
        return problem(
                HttpStatus.CONFLICT,
                code,
                exception.owner() + " slug already exists",
                exception.owner() + " slug '" + exception.slug() + "' already exists.",
                request,
                List.of());
    }

    @ExceptionHandler(DuplicateSkuException.class)
    ResponseEntity<ProblemResponse> handleDuplicateSku(DuplicateSkuException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.CONFLICT,
                ErrorCode.DUPLICATE_SKU,
                "SKU already exists",
                "SKU '" + exception.sku() + "' already exists.",
                request,
                List.of());
    }

    @ExceptionHandler(InvalidProductStatusTransitionException.class)
    ResponseEntity<ProblemResponse> handleInvalidStatusTransition(
            InvalidProductStatusTransitionException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.CONFLICT,
                ErrorCode.INVALID_PRODUCT_STATUS_TRANSITION,
                "Invalid product status transition",
                exception.getMessage(),
                request,
                List.of());
    }

    @ExceptionHandler(InvalidPaginationException.class)
    ResponseEntity<ProblemResponse> handleInvalidPagination(
            InvalidPaginationException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_PAGINATION,
                "Invalid pagination",
                exception.getMessage(),
                request,
                List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ProblemResponse> handleDataIntegrity(
            DataIntegrityViolationException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.CONFLICT,
                ErrorCode.CONFLICT,
                "Conflict",
                "The request conflicts with existing catalog data.",
                request,
                List.of());
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ProblemResponse> handleBadRequest(Exception exception, HttpServletRequest request) {
        ErrorCode code = classifyBadRequest(exception);
        return problem(
                HttpStatus.BAD_REQUEST,
                code,
                "Bad request",
                exception instanceof IllegalArgumentException
                        ? exception.getMessage()
                        : "The request could not be processed.",
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

    private static ErrorCode classifyBadRequest(Exception exception) {
        String message = exception.getMessage();
        if (message == null) {
            return ErrorCode.BAD_REQUEST;
        }
        String normalized = message.toLowerCase();
        if (normalized.contains("price")) {
            return ErrorCode.INVALID_PRICE;
        }
        if (normalized.contains("stock")) {
            return ErrorCode.INVALID_STOCK;
        }
        return ErrorCode.BAD_REQUEST;
    }
}
