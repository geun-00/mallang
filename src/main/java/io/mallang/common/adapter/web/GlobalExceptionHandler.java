package io.mallang.common.adapter.web;

import io.mallang.common.domain.exception.DomainException;
import io.mallang.common.domain.exception.DomainNotFoundException;
import io.mallang.common.domain.exception.DuplicateException;
import io.mallang.common.domain.exception.ForbiddenException;
import io.mallang.common.domain.exception.InvalidValueException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DomainNotFoundException.class)
    public ProblemDetail handle(DomainNotFoundException ex) {
        log.info("Domain not found: {}", ex.getMessage());

        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getClientMessage());
    }

    @ExceptionHandler(DuplicateException.class)
    public ProblemDetail handle(DuplicateException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidValueException.class)
    public ProblemDetail handle(InvalidValueException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handle(DomainException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handle(ForbiddenException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handle(AuthenticationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handle(AccessDeniedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }
}
