package cc.commandcontrol;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShellCommandExecutorTest {
    @Test
    void executesShellCommandAndCapturesOutput() throws Exception {
        ShellCommandExecutor executor = new ShellCommandExecutor();
        ShellCommandResult result = executor.execute(
                "echo hello",
                new ShellCommandSettings("/bin/sh", Path.of("."), Duration.ofSeconds(5), 10)
        );

        assertFalse(result.timedOut());
        assertEquals(0, result.exitCode());
        assertEquals("hello", result.outputLines().get(0));
    }

    @Test
    void timesOutLongRunningCommand() throws Exception {
        ShellCommandExecutor executor = new ShellCommandExecutor();
        ShellCommandResult result = executor.execute(
                "sleep 2",
                new ShellCommandSettings("/bin/sh", Path.of("."), Duration.ofMillis(100), 10)
        );

        assertTrue(result.timedOut());
        assertEquals(-1, result.exitCode());
    }

    @Test
    void handlesLargeOutputWithoutBlocking() throws Exception {
        ShellCommandExecutor executor = new ShellCommandExecutor();
        ShellCommandResult result = executor.execute(
                "yes output | head -n 10000",
                new ShellCommandSettings("/bin/sh", Path.of("."), Duration.ofSeconds(5), 5)
        );

        assertFalse(result.timedOut());
        assertEquals(0, result.exitCode());
        assertEquals(5, result.outputLines().size());
        assertTrue(result.truncated());
    }
}
