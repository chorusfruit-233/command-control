package cc.commandcontrol;

import java.util.List;

public final class ShellCommandPresentation {
    public static final int DEFAULT_DIALOG_BODY_LIMIT = 6000;

    private final String title;
    private final List<String> lines;
    private final boolean warning;

    public ShellCommandPresentation(String title, List<String> lines, boolean warning) {
        this.title = title;
        this.lines = List.copyOf(lines);
        this.warning = warning;
    }

    public String title() {
        return title;
    }

    public List<String> lines() {
        return lines;
    }

    public boolean warning() {
        return warning;
    }

    public String bodyText() {
        return bodyText(DEFAULT_DIALOG_BODY_LIMIT);
    }

    public String bodyText(int maxCharacters) {
        String body = String.join("\n", lines);
        if (maxCharacters <= 0 || body.length() <= maxCharacters) {
            return body;
        }
        String marker = "\n[Dialog text truncated]";
        if (marker.length() >= maxCharacters) {
            return marker.substring(0, maxCharacters);
        }
        int end = Math.max(0, maxCharacters - marker.length());
        return body.substring(0, end) + marker;
    }
}
