package cc.commandcontrol;

import java.util.ArrayList;
import java.util.List;

public final class ShellCommandPresentationFormatter {
    private ShellCommandPresentationFormatter() {
    }

    public static ShellCommandPresentation format(String command, ShellCommandResult result) {
        boolean warning = result.timedOut() || result.exitCode() != 0;
        String title;
        if (result.timedOut()) {
            title = "Shell Command Timed Out";
        } else if (result.exitCode() == 0) {
            title = "Shell Command Complete";
        } else {
            title = "Shell Command Failed";
        }

        List<String> lines = new ArrayList<>();
        lines.add("Command: " + command);
        if (result.timedOut()) {
            lines.add("Status: timed out after " + result.elapsed().toMillis() + " ms");
        } else {
            lines.add("Status: exit code " + result.exitCode() + " in " + result.elapsed().toMillis() + " ms");
        }
        lines.add("");
        lines.add(result.truncated() ? "Output (truncated to " + result.maxOutputLines() + " lines):" : "Output:");
        if (result.outputLines().isEmpty()) {
            lines.add("(no output)");
        } else {
            lines.addAll(result.outputLines());
        }
        return new ShellCommandPresentation(title, lines, warning);
    }
}
