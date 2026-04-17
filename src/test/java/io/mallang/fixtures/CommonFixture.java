package io.mallang.fixtures;

import io.mallang.common.domain.port.ClockHolder;
import io.mallang.common.domain.port.IdGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommonFixture {

    public static IdGenerator generateIdGenerator() {
        return () -> UUID.randomUUID().toString();
    }

    public static ClockHolder generateClockHolder() {
        return () -> LocalDateTime.of(2024, 1, 1, 0, 0, 0);
    }
}
