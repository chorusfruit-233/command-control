package cc.commandcontrol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class CommandNormalizerTest {
    @Test
    void acceptsCommandWithoutSlash() {
        assertEquals("say hi", CommandNormalizer.normalize("say hi").orElseThrow());
    }

    @Test
    void removesLeadingSlash() {
        assertEquals("say hi", CommandNormalizer.normalize("/say hi").orElseThrow());
    }

    @Test
    void removesRepeatedLeadingSlashes() {
        assertEquals("say hi", CommandNormalizer.normalize("  // say hi").orElseThrow());
    }

    @Test
    void rejectsBlankCommand() {
        assertTrue(CommandNormalizer.normalize("   /   ").isEmpty());
    }
}
