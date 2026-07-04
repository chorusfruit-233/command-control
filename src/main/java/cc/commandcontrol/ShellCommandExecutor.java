package cc.commandcontrol;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public final class ShellCommandExecutor {
    public ShellCommandResult execute(String command, ShellCommandSettings settings) throws IOException, InterruptedException {
        Instant startedAt = Instant.now();
        ProcessBuilder processBuilder = new ProcessBuilder(settings.executable(), "-lc", command);
        processBuilder.directory(settings.workingDirectory().toFile());
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        CompletableFuture<String> outputFuture = CompletableFuture.supplyAsync(() -> readOutput(process));
        boolean completed = process.waitFor(settings.timeout().toMillis(), TimeUnit.MILLISECONDS);
        if (!completed) {
            process.destroy();
            if (!process.waitFor(1, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                process.waitFor();
            }
        }

        String rawOutput = waitForOutput(outputFuture);
        Duration elapsed = Duration.between(startedAt, Instant.now());
        ShellCommandOutputLimiter.LimitedOutput output = ShellCommandOutputLimiter.limit(rawOutput, settings.maxOutputLines());
        return new ShellCommandResult(completed ? process.exitValue() : -1, !completed, elapsed, output.lines(), output.truncated(), settings.maxOutputLines());
    }

    private String readOutput(Process process) {
        try {
            byte[] outputBytes = process.getInputStream().readAllBytes();
            return new String(outputBytes, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            return "[CommandControl failed to read process output: " + exception.getMessage() + "]";
        }
    }

    private String waitForOutput(CompletableFuture<String> outputFuture) throws InterruptedException {
        try {
            return outputFuture.get();
        } catch (ExecutionException exception) {
            return "[CommandControl failed to collect process output: " + exception.getCause().getMessage() + "]";
        }
    }
}
