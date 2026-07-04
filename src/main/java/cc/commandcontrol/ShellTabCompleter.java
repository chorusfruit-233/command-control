package cc.commandcontrol;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ShellTabCompleter {
    private static final int MAX_COMPLETIONS = 80;

    private final String pathEnvironment;

    public ShellTabCompleter() {
        this(System.getenv("PATH"));
    }

    ShellTabCompleter(String pathEnvironment) {
        this.pathEnvironment = pathEnvironment == null ? "" : pathEnvironment;
    }

    public List<String> complete(String commandLine, ShellCommandSettings settings) {
        ShellToken token = ShellToken.from(commandLine);
        if (token.index() == 0 && !looksLikePath(token.value())) {
            return limit(CompletionUtils.filterByPrefix(pathCommands(), token.value()));
        }
        return limit(pathCompletions(token.value(), settings.workingDirectory()));
    }

    private List<String> pathCommands() {
        Set<String> commands = new LinkedHashSet<>();
        for (String rawDirectory : pathEnvironment.split(":")) {
            if (rawDirectory.isBlank()) {
                continue;
            }
            Path directory = Path.of(rawDirectory);
            if (!Files.isDirectory(directory)) {
                continue;
            }
            try (var stream = Files.list(directory)) {
                stream.filter(Files::isExecutable)
                        .filter(Files::isRegularFile)
                        .map(path -> path.getFileName().toString())
                        .forEach(commands::add);
            } catch (IOException ignored) {
                // Ignore unreadable PATH entries.
            }
        }
        return commands.stream().sorted().toList();
    }

    private List<String> pathCompletions(String rawToken, Path workingDirectory) {
        String token = rawToken == null ? "" : rawToken;
        Path tokenPath = Path.of(token.isEmpty() ? "." : token);
        Path directory;
        String prefix;
        String displayPrefix;

        if (token.endsWith("/")) {
            directory = resolve(workingDirectory, tokenPath);
            prefix = "";
            displayPrefix = token;
        } else {
            Path parent = tokenPath.getParent();
            directory = resolve(workingDirectory, parent == null ? Path.of(".") : parent);
            Path fileName = tokenPath.getFileName();
            prefix = fileName == null ? "" : fileName.toString();
            displayPrefix = parent == null ? "" : parent + "/";
        }

        if (!Files.isDirectory(directory)) {
            return List.of();
        }

        List<String> completions = new ArrayList<>();
        try (var stream = Files.list(directory)) {
            stream.forEach(path -> {
                String fileName = path.getFileName().toString();
                if (!fileName.startsWith(prefix)) {
                    return;
                }
                String suffix = Files.isDirectory(path) ? "/" : "";
                completions.add(displayPrefix + fileName + suffix);
            });
        } catch (IOException ignored) {
            return List.of();
        }
        return completions.stream().sorted().toList();
    }

    private Path resolve(Path workingDirectory, Path path) {
        if (path.isAbsolute()) {
            return path.normalize();
        }
        return workingDirectory.resolve(path).normalize();
    }

    private List<String> limit(List<String> completions) {
        if (completions.size() <= MAX_COMPLETIONS) {
            return completions;
        }
        return completions.subList(0, MAX_COMPLETIONS);
    }

    private record ShellToken(int index, String value) {
        static ShellToken from(String commandLine) {
            if (commandLine == null || commandLine.isEmpty()) {
                return new ShellToken(0, "");
            }
            String[] parts = commandLine.split("\\s+", -1);
            int index = Math.max(0, parts.length - 1);
            return new ShellToken(index, parts[index]);
        }
    }

    private boolean looksLikePath(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        return token.contains("/") || token.startsWith(".") || token.toLowerCase(Locale.ROOT).contains("\\");
    }
}
