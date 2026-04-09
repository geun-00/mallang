package io.mallang.common.applicaiton.query;

import java.util.List;
import java.util.function.Function;

public record SliceResult<T>(
        List<T> items,
        boolean hasNext,
        String nextCursor
) {

    public static <T> SliceResult<T> of(
            List<T> loadedItems,
            int targetSize,
            Function<T, String> idExtractor
    ) {
        boolean hasNext = loadedItems.size() > targetSize;

        List<T> items = loadedItems.stream()
                                   .limit(targetSize)
                                   .toList();

        String nextCursor = hasNext
                ? idExtractor.apply(items.getLast())
                : null;

        return new SliceResult<>(items, hasNext, nextCursor);
    }
}
