package cc.commandcontrol;

import java.util.ArrayList;
import java.util.List;

public final class ShellCommandOutputLimiter {
    private ShellCommandOutputLimiter() {
    }

    public static LimitedOutput limit(String output, int maxLines) {
        if (maxLines <= 0 || output == null || output.isEmpty()) {
            return new LimitedOutput(List.of(), output != null && !output.isEmpty());
        }

        String[] splitLines = output.split("\\R");
        List<String> lines = new ArrayList<>();
        boolean truncated = false;
        for (String line : splitLines) {
            if (lines.size() >= maxLines) {
                truncated = true;
                break;
            }
            lines.add(line);
        }
        return new LimitedOutput(lines, truncated);
    }

    public record LimitedOutput(List<String> lines, boolean truncated) {
        public LimitedOutput {
            lines = List.copyOf(lines);
        }
    }
}
