package cc.commandcontrol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShellCommandOutputLimiterTest {
    @Test
    void keepsOutputUnderLimit() {
        ShellCommandOutputLimiter.LimitedOutput output = ShellCommandOutputLimiter.limit("one\ntwo", 2);

        assertEquals(2, output.lines().size());
        assertFalse(output.truncated());
    }

    @Test
    void truncatesOutputOverLimit() {
        ShellCommandOutputLimiter.LimitedOutput output = ShellCommandOutputLimiter.limit("one\ntwo\nthree", 2);

        assertEquals(2, output.lines().size());
        assertEquals("one", output.lines().get(0));
        assertEquals("two", output.lines().get(1));
        assertTrue(output.truncated());
    }

    @Test
    void marksNonEmptyOutputAsTruncatedWhenLimitIsZero() {
        ShellCommandOutputLimiter.LimitedOutput output = ShellCommandOutputLimiter.limit("one", 0);

        assertTrue(output.lines().isEmpty());
        assertTrue(output.truncated());
    }
}
