package io.mallang.common.domain.port;

import java.time.LocalDateTime;

public interface ClockHolder {
    LocalDateTime now();
}
