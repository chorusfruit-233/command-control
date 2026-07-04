package cc.commandcontrol;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShellCommandPresentationFormatterTest {
    @Test
    void formatsSuccessfulResult() {
        ShellCommandPresentation presentation = ShellCommandPresentationFormatter.format(
                "echo hello",
                new ShellCommandResult(0, false, Duration.ofMillis(25), List.of("hello"), false, 80)
        );

        assertEquals("Shell Command Complete", presentation.title());
        assertFalse(presentation.warning());
        assertTrue(presentation.lines().contains("Command: echo hello"));
        assertTrue(presentation.lines().contains("Status: exit code 0 in 25 ms"));
        assertTrue(presentation.lines().contains("hello"));
    }

    @Test
    void formatsTimeoutAsWarning() {
        ShellCommandPresentation presentation = ShellCommandPresentationFormatter.format(
                "sleep 30",
                new ShellCommandResult(-1, true, Duration.ofSeconds(10), List.of(), false, 80)
        );

        assertEquals("Shell Command Timed Out", presentation.title());
        assertTrue(presentation.warning());
        assertTrue(presentation.lines().contains("Status: timed out after 10000 ms"));
        assertTrue(presentation.lines().contains("(no output)"));
    }

    @Test
    void reportsTruncatedOutput() {
        ShellCommandPresentation presentation = ShellCommandPresentationFormatter.format(
                "yes",
                new ShellCommandResult(0, false, Duration.ofMillis(5), List.of("a", "b"), true, 2)
        );

        assertTrue(presentation.lines().contains("Output (truncated to 2 lines):"));
    }

    @Test
    void capsDialogBodyText() {
        ShellCommandPresentation presentation = new ShellCommandPresentation(
                "title",
                List.of("1234567890", "abcdefghijklmnopqrstuvwxyz"),
                false
        );

        String body = presentation.bodyText(30);

        assertEquals(30, body.length());
        assertTrue(body.endsWith("[Dialog text truncated]"));
    }
}
