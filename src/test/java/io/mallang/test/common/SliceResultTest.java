package io.mallang.test.common;

import io.mallang.annotations.CustomDisplayName;
import io.mallang.common.application.query.SliceResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@CustomDisplayName
@DisplayName("SliceResult")
class SliceResultTest {

    @Test
    void targetSize보다_많이_조회되면_다음_커서를_반환한다() {
        SliceResult<Item> result = SliceResult.of(
                List.of(new Item("item-1"), new Item("item-2"), new Item("item-3")),
                2,
                Item::id
        );

        assertThat(result.items()).extracting(Item::id)
                                  .containsExactly("item-1", "item-2");
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isEqualTo("item-2");
    }

    @Test
    void targetSize_이하로_조회되면_다음_커서를_반환하지_않는다() {
        SliceResult<Item> result = SliceResult.of(
                List.of(new Item("item-1"), new Item("item-2")),
                2,
                Item::id
        );

        assertThat(result.items()).extracting(Item::id)
                                  .containsExactly("item-1", "item-2");
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void 빈_목록이면_다음_커서를_반환하지_않는다() {
        SliceResult<Item> result = SliceResult.of(
                List.of(),
                2,
                Item::id
        );

        assertThat(result.items()).isEmpty();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursor()).isNull();
    }

    private record Item(String id) {
    }
}
