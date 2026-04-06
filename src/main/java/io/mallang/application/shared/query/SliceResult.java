package io.mallang.application.shared.query;

import java.util.List;

public record SliceResult<T>(
        List<T> items,
        boolean hasNext,
        String nextCursor
) {
}
