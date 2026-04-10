package io.mallang.common.adapter.support;

import com.github.f4b6a3.ulid.UlidCreator;
import io.mallang.common.domain.port.IdGenerator;
import org.springframework.stereotype.Component;

@Component
public class UlidIdGeneratorAdapter implements IdGenerator {

    @Override
    public String nextId() {
        return UlidCreator.getMonotonicUlid().toString();
    }
}
