package cc.commandcontrol;

import java.util.Locale;

public enum ShellDialogMode {
    AUTO,
    ENABLED,
    DISABLED;

    public static ShellDialogMode parse(String value) {
        if (value == null || value.isBlank()) {
            return AUTO;
        }
        try {
            return ShellDialogMode.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return AUTO;
        }
    }
}
