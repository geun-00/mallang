package io.mallang;

import io.mallang.domain.common.ClockHolder;
import io.mallang.domain.common.IdGenerator;
import io.mallang.member.domain.PasswordEncoder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.mallang.fixtures.MemberFixture.generatePasswordEncoder;

@TestConfiguration
public class TestConfig {

    @Bean
    public IdGenerator idGenerator() {
        return () -> UUID.randomUUID().toString();
    }

    @Bean
    public ClockHolder clockHolder() {
        return LocalDateTime::now;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return generatePasswordEncoder();
    }
}
