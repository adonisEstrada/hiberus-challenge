package com.hiberus.challenge.infrastructure.adapter.in.rest.exception;

import com.hiberus.challenge.application.exception.DuplicatePaymentOrderException;
import com.hiberus.challenge.application.exception.PaymentOrderNotFoundException;
import com.hiberus.challenge.infrastructure.adapter.in.rest.model.ProblemDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler providing RFC 7807 Problem Details responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String PROBLEM_BASE_URL = "https://api.bank.com/problems";

    @ExceptionHandler(PaymentOrderNotFoundException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleNotFound(PaymentOrderNotFoundException ex) {
        log.error("Payment order not found: {}", ex.getMessage());

        ProblemDetail problem = createProblemDetail(
                PROBLEM_BASE_URL + "/not-found",
                "Not Found",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );

        return Mono.just(ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem));
    }

    @ExceptionHandler(DuplicatePaymentOrderException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleDuplicate(DuplicatePaymentOrderException ex) {
        log.error("Duplicate payment order: {}", ex.getMessage());

        ProblemDetail problem = createProblemDetail(
                PROBLEM_BASE_URL + "/duplicate",
                "Duplicate Payment Order",
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                null
        );

        return Mono.just(ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Validation error: {}", ex.getMessage());

        ProblemDetail problem = createProblemDetail(
                PROBLEM_BASE_URL + "/validation-error",
                "Validation Error",
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null
        );

        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem));
    }

    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleIllegalState(IllegalStateException ex) {
        log.error("Business rule violation: {}", ex.getMessage());

        ProblemDetail problem = createProblemDetail(
                PROBLEM_BASE_URL + "/business-validation",
                "Business Validation Error",
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage(),
                null
        );

        return Mono.just(ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleValidation(WebExchangeBindException ex) {
        log.error("Request validation failed: {}", ex.getMessage());

        List<ProblemDetail.ErrorsInner> errors = new ArrayList<>();
        for (FieldError error : ex.getFieldErrors()) {
            ProblemDetail.ErrorsInner errorInner = new ProblemDetail.ErrorsInner();
            errorInner.setField(error.getField());
            errorInner.setMessage(error.getDefaultMessage());
            errors.add(errorInner);
        }

        ProblemDetail problem = createProblemDetail(
                PROBLEM_BASE_URL + "/validation-error",
                "Validation Error",
                HttpStatus.BAD_REQUEST.value(),
                "Request validation failed",
                errors
        );

        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ProblemDetail>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ProblemDetail problem = createProblemDetail(
                PROBLEM_BASE_URL + "/internal-error",
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred processing your request",
                null
        );

        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem));
    }

    private ProblemDetail createProblemDetail(
            String type,
            String title,
            Integer status,
            String detail,
            List<ProblemDetail.ErrorsInner> errors) {

        ProblemDetail problem = new ProblemDetail();
        problem.setType(type);
        problem.setTitle(title);
        problem.setStatus(status);
        problem.setDetail(detail);
        problem.setTimestamp(OffsetDateTime.now().toString());
        problem.setInstance("/payment-initiation/payment-orders");

        if (errors != null && !errors.isEmpty()) {
            problem.setErrors(errors);
        }

        return problem;
    }
}
