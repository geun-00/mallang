package io.mallang.test.member.domain;

import io.mallang.member.domain.Email;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {
    
    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "invalid",
            "@example.com",
            "invalid@",
            "invalid @example.com",
            "invalid@example .com",
            "invalid..@example.com",
            "invalid@.example.com",
            "invalid@example.com.",
            "invalid@@example.com",
            "invalid@exam ple.com",
            "invalid@example",
            "invalid@.com"
    })
    void 유효하지_않은_형식으로_생성할_수_없다(String invalidEmail) {
        assertThatThrownBy(() -> new Email(invalidEmail))
                 .isInstanceOf(IllegalArgumentException.class);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
            "user@example.com",
            "test.user@example.com",
            "test+tag@example.co.uk",
            "user_name@example.org",
            "a@example.com",
            "user123@test.example.com",
            "first.last@example.com",
            "user+filter@domain.io"
    })
    void 유효한_이메일_형식으로_생성할_수_있다(String validEmail) {
        assertThatCode(() -> new Email(validEmail)).doesNotThrowAnyException();
    }
}