package cc.commandcontrol;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShellTabCompleterTest {
    @TempDir
    Path tempDir;

    @Test
    void completesCommandFromPath() throws Exception {
        Path bin = Files.createDirectory(tempDir.resolve("bin"));
        Path command = Files.createFile(bin.resolve("echotest"));
        command.toFile().setExecutable(true);

        ShellTabCompleter completer = new ShellTabCompleter(bin.toString());

        assertEquals(List.of("echotest"), completer.complete("echo", settings(tempDir)));
    }

    @Test
    void completesRelativeFilePath() throws Exception {
        Files.createFile(tempDir.resolve("server.properties"));
        Files.createDirectory(tempDir.resolve("plugins"));

        ShellTabCompleter completer = new ShellTabCompleter("");
        List<String> completions = completer.complete("cat se", settings(tempDir));

        assertEquals(List.of("server.properties"), completions);
    }

    @Test
    void appendsSlashToDirectoryCompletion() throws Exception {
        Files.createDirectory(tempDir.resolve("plugins"));

        ShellTabCompleter completer = new ShellTabCompleter("");

        assertTrue(completer.complete("ls pl", settings(tempDir)).contains("plugins/"));
    }

    private ShellCommandSettings settings(Path workingDirectory) {
        return new ShellCommandSettings("/bin/sh", workingDirectory, Duration.ofSeconds(1), 10);
    }
}
