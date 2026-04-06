package io.mallang;

public class TestDataSource {

    public static String[] invalidEmailValues() {
        return new String[] {
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
        };
    }

    public static String[] validEmailValues() {
        return new String[] {
                "user@example.com",
                "test.user@example.com",
                "test+tag@example.co.uk",
                "user_name@example.org",
                "a@example.com",
                "user123@test.example.com",
                "first.last@example.com",
                "user+filter@domain.io"
        };
    }

    public static String[] invalidNicknameValues() {
        return new String[] {
                "nickname!", "nickname@", "nickname#", "nickname$", "nickname%", "nickname^",
                "nickname&", "nickname*", "nickname(", "nickname)", "nickname+", "nickname=",
                "nickname|", "nickname\\", "nickname/", "nickname?", "nickname<", "nickname>",
                "nickname~", "nickname`"
        };
    }

    public static String[] invalidPasswordLengthValues() {
        return new String[] {
                "short1@",
                "longpassword1234567890@"
        };
    }

    public static String[] invalidPasswordCompositionValues() {
        return new String[] {
                "password",
                "12345678",
                "@@@@@@@@",
                "password12",
                "password@",
                "12345678@"
        };
    }

    public static String[] invalidPasswordCharacterValues() {
        return new String[] {
                "(qwerwasd12@",
                "qwerwasd12@)",
                "qwerwasd12@★"
        };
    }

    public static String[] invalidImageUrlValues() {
        return new String[] {
                "invalid-url",
                "http://",
                "not-a-url",
                "ftp://example.com"
        };
    }

    public static String[] validImageUrlValues() {
        return new String[] {
                "http://example.com/image.jpg",
                "https://example.com/image.png"
        };
    }

    public static String[] validProductNameValues() {
        return new String[] {
                "상품명",
                "Valid Product Name 123",
                "상품명!@#$%^&*()"
        };
    }

    public static String[] invalidPhoneNumberValues() {
        return new String[] {
                null,
                "1234567890",
                "010-1111-2222",
                "010 1111 2222",
                "abcdefghijk"
        };
    }

    public static String[] invalidZipcodeValues() {
        return new String[] {
                "1234",     // 4자리
                "123456",   // 6자리
                "1234a",    // 숫자 + 문자
                "abcde"     // 문자만
        };
    }
}
