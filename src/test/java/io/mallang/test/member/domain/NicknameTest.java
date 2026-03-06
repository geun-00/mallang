package io.mallang.test.member.domain;

import io.mallang.member.domain.Nickname;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NicknameTest {
    
    @Nested
    class 유효하지_않은_형식으로_닉네임을_생성할_수_없다 {

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"   "})
        void 닉네임은_null_또는_공백으로_생성할_수_없다(String invalidNickname) {
            assertThatThrownBy(() -> new Nickname(invalidNickname)).isInstanceOf(IllegalArgumentException.class);
        }
        
        @ParameterizedTest
        @ValueSource(strings = {" test", "test "})
        void 닉네임은_앞뒤_공백이_없어야_한다(String invalidNickname) {
            assertThatThrownBy(() -> new Nickname(invalidNickname)).isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"1", "111111111111111111111"})
        void 닉네임은_2자_이상_20자_이하여야_한다(String invalidNickname) {
            assertThatThrownBy(() -> new Nickname(invalidNickname)).isInstanceOf(IllegalArgumentException.class);
        }
        
        @ParameterizedTest
        @ValueSource(strings = {
                "nickname!", "nickname@", "nickname#", "nickname$", "nickname%", "nickname^",
                "nickname&", "nickname*", "nickname(", "nickname)", "nickname+", "nickname=",
                "nickname|", "nickname\\", "nickname/", "nickname?", "nickname<", "nickname>",
                "nickname~", "nickname`"
        })
        void 닉네임은_허용된_특수문자만_포함할_수_있다(String invalidNickname) {
            assertThatThrownBy(() -> new Nickname(invalidNickname)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"홍길동", "test_123", "hong gil"})
    void 유효한_닉네임으로_생성할_수_있다(String validNickname) {
        assertThatCode(() -> new Nickname(validNickname)).doesNotThrowAnyException();
    }
}