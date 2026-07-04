package cc.commandcontrol;

import java.nio.file.Path;
import java.time.Duration;

public final class ShellCommandSettings {
    public static final ShellCommandSettings DEFAULT = new ShellCommandSettings(
            "/bin/sh",
            Path.of("."),
            Duration.ofSeconds(10),
            80
    );

    private final String executable;
    private final Path workingDirectory;
    private final Duration timeout;
    private final int maxOutputLines;
    private final ShellDialogMode dialogMode;

    public ShellCommandSettings(String executable, Path workingDirectory, Duration timeout, int maxOutputLines) {
        this(executable, workingDirectory, timeout, maxOutputLines, ShellDialogMode.AUTO);
    }

    public ShellCommandSettings(String executable, Path workingDirectory, Duration timeout, int maxOutputLines, ShellDialogMode dialogMode) {
        this.executable = executable;
        this.workingDirectory = workingDirectory;
        this.timeout = timeout;
        this.maxOutputLines = maxOutputLines;
        this.dialogMode = dialogMode;
    }

    public String executable() {
        return executable;
    }

    public Path workingDirectory() {
        return workingDirectory;
    }

    public Duration timeout() {
        return timeout;
    }

    public int maxOutputLines() {
        return maxOutputLines;
    }

    public ShellDialogMode dialogMode() {
        return dialogMode;
    }
}
