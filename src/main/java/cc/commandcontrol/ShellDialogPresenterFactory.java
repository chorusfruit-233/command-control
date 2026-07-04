package cc.commandcontrol;

import org.bukkit.plugin.java.JavaPlugin;

public final class ShellDialogPresenterFactory {
    private ShellDialogPresenterFactory() {
    }

    public static ShellDialogPresenter create(JavaPlugin plugin, ShellCommandSubmissionHandler submissionHandler) {
        if (!isDialogApiAvailable()) {
            return ShellDialogPresenter.UNAVAILABLE;
        }
        try {
            return new PaperShellDialogPresenter(plugin, submissionHandler);
        } catch (LinkageError error) {
            plugin.getLogger().warning("Dialog API was detected but could not be initialized: " + error.getMessage());
            return ShellDialogPresenter.UNAVAILABLE;
        }
    }

    private static boolean isDialogApiAvailable() {
        try {
            Class.forName("io.papermc.paper.dialog.Dialog", false, ShellDialogPresenterFactory.class.getClassLoader());
            Class.forName("net.kyori.adventure.dialog.DialogLike", false, ShellDialogPresenterFactory.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }
}
