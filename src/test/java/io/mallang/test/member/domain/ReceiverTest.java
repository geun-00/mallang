package io.mallang.test.member.domain;

import io.mallang.member.domain.Receiver;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReceiverTest {

    @Nested
    class 유효하지_않은_형식의_이름이나_전화번호로_생성할_수_없다 {

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"   "})
        void 이름은_null_또는_공백이_아니어야_한다(String invalidName) {
            assertThatThrownBy(() -> new Receiver(invalidName, "01011112222")).isInstanceOf(IllegalArgumentException.class);
        }
        
        @ParameterizedTest
        @ValueSource(strings = {"a", "aaaaaaaaaaa"})
        void 이름은_2자_이상_10자_이하여야_한다(String invalidName) {
            assertThatThrownBy(() -> new Receiver(invalidName, "01011112222")).isInstanceOf(IllegalArgumentException.class);
        }
        
        @ParameterizedTest
        @ValueSource(strings = {" hello", "hello "})
        void 이름은_앞뒤_공백이_없어야_한다(String invalidName) {
            assertThatThrownBy(() -> new Receiver(invalidName, "01011112222")).isInstanceOf(IllegalArgumentException.class);
        }
        
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"1234567890", "010-1111-2222", "010 1111 2222", "abcdefghijk"})
        void 전화번호는_010XXXXXXXX_형식이어야_한다(String invalidPhoneNumber) {
            assertThatThrownBy(() -> new Receiver("홍길동", invalidPhoneNumber)).isInstanceOf(IllegalArgumentException.class);
        }
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"홍길동", "홍 길동"})
    void 유효한_이름과_전화번호로_생성할_수_있다(String validName) {
        assertThatCode(() -> new Receiver(validName, "01011112222")).doesNotThrowAnyException();
    }
}