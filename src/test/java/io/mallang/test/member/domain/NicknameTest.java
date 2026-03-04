package io.mallang.test.member.domain;

import io.mallang.member.domain.Nickname;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NicknameTest {
    
    @Nested
    class 유효하지_않은_형식으로_닉네임을_생성할_수_없다 {
        
        @Test
        void 닉네임은_null_또는_공백으로_생성할_수_없다() {
            assertThatThrownBy(() -> new Nickname(null)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new Nickname("  ")).isInstanceOf(IllegalArgumentException.class);
        }
        
        @Test
        void 닉네임은_앞뒤_공백이_없어야_한다() {
            assertThatThrownBy(() -> new Nickname(" test")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new Nickname("test ")).isInstanceOf(IllegalArgumentException.class);
        }
        
        @Test
        void 닉네임은_2자_이상_20자_이하여야_한다() {
            assertThatThrownBy(() -> new Nickname("a")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new Nickname("a".repeat(21))).isInstanceOf(IllegalArgumentException.class);
        }
        
        @Test
        void 닉네임은_허용된_특수문자만_포함할_수_있다() {
            assertThatThrownBy(() -> new Nickname("#nickname")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new Nickname("@nickname")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new Nickname("!nickname")).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void 유효한_닉네임으로_생성할_수_있다() {
        assertThatCode(() -> new Nickname("홍길동")).doesNotThrowAnyException();
        assertThatCode(() -> new Nickname("test_123")).doesNotThrowAnyException();
        assertThatCode(() -> new Nickname("hong gil")).doesNotThrowAnyException();
    }
}