package cc.commandcontrol;

import java.util.Optional;

public final class ShellCommandNormalizer {
    private ShellCommandNormalizer() {
    }

    public static Optional<String> normalize(String input) {
        if (input == null) {
            return Optional.empty();
        }
        String normalized = input.trim();
        if (normalized.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(normalized);
    }
}
