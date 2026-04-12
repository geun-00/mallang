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
        log.info("Duplicate resource: {}", ex.getMessage());

        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getClientMessage());
    }

    @ExceptionHandler(InvalidValueException.class)
    public ProblemDetail handle(InvalidValueException ex) {
        log.info("Invalid value: {}", ex.getMessage());

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "요청 값이 올바르지 않습니다."
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handle(ForbiddenException ex) {
        log.info("Forbidden domain access: {}", ex.getMessage());

        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getClientMessage());
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handle(DomainException ex) {
        log.info("Domain exception. type={}, message={}",
                 ex.getClass().getSimpleName(),
                 ex.getMessage());

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "요청을 처리할 수 없습니다."
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handle(AuthenticationException ex) {
        log.info("Authentication failed. type={}, message={}",
                 ex.getClass().getSimpleName(),
                 ex.getMessage());

        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handle(AccessDeniedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }
}
