package io.mallang.test.member.domain;

import io.mallang.member.domain.Receiver;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReceiverTest {

    @Nested
    class 유효하지_않은_형식의_이름이나_전화번호로_생성할_수_없다 {
        
        @Test
        void 이름은_null_또는_공백이_아니어야_한다() {
            assertThatThrownBy(() -> new Receiver(null, "01011112222")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new Receiver("  ", "01011112222")).isInstanceOf(IllegalArgumentException.class);
        }
        
        @Test
        void 이름은_2자_이상_10자_이하여야_한다() {
            assertThatThrownBy(() -> new Receiver("a", "01011112222")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new Receiver("a".repeat(11), "01011112222")).isInstanceOf(IllegalArgumentException.class);
        }
        
        @Test
        void 이름은_앞뒤_공백이_없어야_한다() {
            assertThatThrownBy(() -> new Receiver(" hello", "01011112222")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new Receiver("hello ", "01011112222")).isInstanceOf(IllegalArgumentException.class);
        }
        
        @Test
        void 전화번호는_010XXXXXXXX_형식이어야_한다() {
            assertThatThrownBy(() -> new Receiver("홍길동", null)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new Receiver("홍길동", "1234567890")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new Receiver("홍길동", "010-1111-2222")).isInstanceOf(IllegalArgumentException.class);
        }
    }
    
    @Test
    void 유효한_이름과_전화번호로_생성할_수_있다() {
        assertThatCode(() -> new Receiver("홍길동", "01011112222")).doesNotThrowAnyException();
        assertThatCode(() -> new Receiver("홍 길동", "01011112222")).doesNotThrowAnyException();
    }
}