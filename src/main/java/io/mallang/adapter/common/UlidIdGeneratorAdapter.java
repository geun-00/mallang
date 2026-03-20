package io.mallang.adapter.common;

import com.github.f4b6a3.ulid.UlidCreator;
import io.mallang.domain.common.IdGenerator;
import org.springframework.stereotype.Component;

@Component
public class UlidIdGeneratorAdapter implements IdGenerator {

    @Override
    public String nextId() {
        return UlidCreator.getMonotonicUlid().toString();
    }
}
