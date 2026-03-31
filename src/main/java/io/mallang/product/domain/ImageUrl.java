package io.mallang.product.domain;

import io.mallang.domain.common.exception.InvalidValueException;

import java.net.URI;
import java.net.URISyntaxException;

public record ImageUrl(String value) {

    public ImageUrl {
        if (value == null || value.isBlank()) {
            throw new InvalidValueException("이미지 URL은 null이거나 빈 문자열일 수 없습니다.");
        }
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();

            if (!"http".equals(scheme) && !"https".equals(scheme)) {
                throw new InvalidValueException("이미지 URL은 http 또는 https 여야 합니다.");
            }
            if (uri.getHost() == null) {
                throw new InvalidValueException("이미지 URL에 유효한 호스트가 없습니다.");
            }
        } catch (URISyntaxException e) {
            throw new InvalidValueException("유효한 URL 형식이 아닙니다: " + value);
        }
    }
}
