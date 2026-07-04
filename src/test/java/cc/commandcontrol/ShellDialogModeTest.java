package cc.commandcontrol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ShellDialogModeTest {
    @Test
    void parsesKnownModesIgnoringCase() {
        assertEquals(ShellDialogMode.AUTO, ShellDialogMode.parse("auto"));
        assertEquals(ShellDialogMode.ENABLED, ShellDialogMode.parse("ENABLED"));
        assertEquals(ShellDialogMode.DISABLED, ShellDialogMode.parse("disabled"));
    }

    @Test
    void defaultsUnknownModeToAuto() {
        assertEquals(ShellDialogMode.AUTO, ShellDialogMode.parse("something-else"));
        assertEquals(ShellDialogMode.AUTO, ShellDialogMode.parse(null));
    }
}
