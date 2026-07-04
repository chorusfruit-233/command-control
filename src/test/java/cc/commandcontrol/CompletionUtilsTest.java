package cc.commandcontrol;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class CompletionUtilsTest {
    @Test
    void filtersAndSortsByPrefix() {
        assertEquals(
                List.of("reload", "restart"),
                CompletionUtils.filterByPrefix(List.of("stop", "restart", "reload"), "re")
        );
    }
}
