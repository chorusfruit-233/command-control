package cc.commandcontrol;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class AuthorizationListTest {
    @Test
    void authorizesByUuid() {
        UUID uuid = UUID.randomUUID();
        AuthorizationList authorizationList = new AuthorizationList(List.of(
                new AuthorizedPlayer(uuid, "someone-else")
        ));

        assertTrue(authorizationList.isAuthorized(uuid, "player"));
    }

    @Test
    void authorizesByNameIgnoringCase() {
        AuthorizationList authorizationList = new AuthorizationList(List.of(
                new AuthorizedPlayer(null, "TrustedPlayer")
        ));

        assertTrue(authorizationList.isAuthorized(UUID.randomUUID(), "trustedplayer"));
    }

    @Test
    void rejectsUnknownPlayer() {
        AuthorizationList authorizationList = new AuthorizationList(List.of(
                new AuthorizedPlayer(UUID.randomUUID(), "TrustedPlayer")
        ));

        assertFalse(authorizationList.isAuthorized(UUID.randomUUID(), "OtherPlayer"));
    }
}
