package cc.commandcontrol;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class AuthorizedPlayer {
    private final UUID uuid;
    private final String name;

    public AuthorizedPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = normalizeName(name);
    }

    public Optional<UUID> uuid() {
        return Optional.ofNullable(uuid);
    }

    public Optional<String> name() {
        return Optional.ofNullable(name);
    }

    public boolean matches(UUID candidateUuid, String candidateName) {
        if (uuid != null && uuid.equals(candidateUuid)) {
            return true;
        }
        String normalizedCandidateName = normalizeName(candidateName);
        return name != null && name.equals(normalizedCandidateName);
    }

    private static String normalizeName(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AuthorizedPlayer that)) {
            return false;
        }
        return Objects.equals(uuid, that.uuid) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name);
    }
}
