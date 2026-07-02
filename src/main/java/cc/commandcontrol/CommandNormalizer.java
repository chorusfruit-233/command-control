package cc.commandcontrol;

import java.util.Optional;

public final class CommandNormalizer {
    private CommandNormalizer() {
    }

    public static Optional<String> normalize(String input) {
        if (input == null) {
            return Optional.empty();
        }
        String normalized = input.trim();
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1).stripLeading();
        }
        if (normalized.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(normalized);
    }
}
