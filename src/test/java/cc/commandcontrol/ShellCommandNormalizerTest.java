package cc.commandcontrol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShellCommandNormalizerTest {
    @Test
    void preservesLeadingSlash() {
        assertEquals("/usr/bin/id", ShellCommandNormalizer.normalize("/usr/bin/id").orElseThrow());
    }

    @Test
    void trimsCommand() {
        assertEquals("echo hello", ShellCommandNormalizer.normalize("  echo hello  ").orElseThrow());
    }

    @Test
    void rejectsBlankCommand() {
        assertTrue(ShellCommandNormalizer.normalize("   ").isEmpty());
    }
}
