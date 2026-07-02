package cc.commandcontrol;

import java.util.List;
import java.util.UUID;

public final class AuthorizationList {
    private final List<AuthorizedPlayer> authorizedPlayers;

    public AuthorizationList(List<AuthorizedPlayer> authorizedPlayers) {
        this.authorizedPlayers = List.copyOf(authorizedPlayers);
    }

    public boolean isAuthorized(UUID uuid, String name) {
        return authorizedPlayers.stream().anyMatch(player -> player.matches(uuid, name));
    }

    public int size() {
        return authorizedPlayers.size();
    }
}
