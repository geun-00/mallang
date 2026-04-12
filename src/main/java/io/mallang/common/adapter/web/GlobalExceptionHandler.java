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

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DomainNotFoundException.class)
    public ProblemDetail handle(DomainNotFoundException ex) {
        log.info("Domain not found: {}", ex.getMessage());

        return problemDetail(NOT_FOUND, ex.getClientMessage());
    }

    @ExceptionHandler(DuplicateException.class)
    public ProblemDetail handle(DuplicateException ex) {
        log.info("Duplicate resource: {}", ex.getMessage());

        return problemDetail(CONFLICT, ex.getClientMessage());
    }

    @ExceptionHandler(InvalidValueException.class)
    public ProblemDetail handle(InvalidValueException ex) {
        log.info("Invalid value: {}", ex.getMessage());

        return problemDetail(BAD_REQUEST, ex.getClientMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handle(ForbiddenException ex) {
        log.info("Forbidden domain access: {}", ex.getMessage());

        return problemDetail(FORBIDDEN, ex.getClientMessage());
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handle(DomainException ex) {
        log.info("Domain exception. type={}, message={}", ex.getClass().getSimpleName(), ex.getMessage());

        return problemDetail(BAD_REQUEST, ex.getClientMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handle(AuthenticationException ex) {
        log.info("Authentication failed. type={}, message={}", ex.getClass().getSimpleName(), ex.getMessage());

        return problemDetail(UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handle(AccessDeniedException ex) {
        log.info("Access denied in application. type={}, message={}", ex.getClass().getSimpleName(), ex.getMessage());

        return problemDetail(FORBIDDEN, "접근 권한이 없습니다.");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handle(Exception ex) {
        log.error("Unexpected exception", ex);

        return problemDetail(INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
    }

    private ProblemDetail problemDetail(HttpStatus status, String detail) {
        return ProblemDetail.forStatusAndDetail(status, detail);
    }
}
