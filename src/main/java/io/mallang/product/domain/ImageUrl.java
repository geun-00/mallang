package io.mallang.product.domain;

import java.net.URI;
import java.net.URISyntaxException;

public record ImageUrl(String value) {

    public ImageUrl {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("이미지 URL은 null이거나 빈 문자열일 수 없습니다.");
        }
        try {
            java.net.URI uri = new URI(value);
            String scheme = uri.getScheme();

            if (!"http".equals(scheme) && !"https".equals(scheme)) {
                throw new IllegalArgumentException("이미지 URL은 http 또는 https 여야 합니다.");
            }
            if (uri.getHost() == null) {
                throw new IllegalArgumentException("이미지 URL에 유효한 호스트가 없습니다.");
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("유효한 URL 형식이 아닙니다: " + value, e);
        }
    }
}
