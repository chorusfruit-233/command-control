package cc.commandcontrol;

import java.time.Duration;
import java.util.List;

public final class ShellCommandResult {
    private final int exitCode;
    private final boolean timedOut;
    private final Duration elapsed;
    private final List<String> outputLines;
    private final boolean truncated;
    private final int maxOutputLines;

    public ShellCommandResult(int exitCode, boolean timedOut, Duration elapsed, List<String> outputLines, boolean truncated, int maxOutputLines) {
        this.exitCode = exitCode;
        this.timedOut = timedOut;
        this.elapsed = elapsed;
        this.outputLines = List.copyOf(outputLines);
        this.truncated = truncated;
        this.maxOutputLines = maxOutputLines;
    }

    public int exitCode() {
        return exitCode;
    }

    public boolean timedOut() {
        return timedOut;
    }

    public Duration elapsed() {
        return elapsed;
    }

    public List<String> outputLines() {
        return outputLines;
    }

    public boolean truncated() {
        return truncated;
    }

    public int maxOutputLines() {
        return maxOutputLines;
    }
}
