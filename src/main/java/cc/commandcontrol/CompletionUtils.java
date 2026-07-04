package cc.commandcontrol;

import java.util.List;

public final class CompletionUtils {
    private CompletionUtils() {
    }

    public static List<String> filterByPrefix(List<String> candidates, String prefix) {
        String effectivePrefix = prefix == null ? "" : prefix;
        return candidates.stream()
                .filter(candidate -> candidate.startsWith(effectivePrefix))
                .sorted()
                .toList();
    }
}
