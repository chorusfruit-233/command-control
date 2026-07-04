package cc.commandcontrol;

import org.bukkit.plugin.java.JavaPlugin;

public final class ShellDialogPresenterFactory {
    private ShellDialogPresenterFactory() {
    }

    public static ShellDialogPresenter create(JavaPlugin plugin, ShellCommandSubmissionHandler submissionHandler) {
        ShellDialogMode mode = ShellDialogMode.parse(plugin.getConfig().getString("shell.dialog-mode", "auto"));
        if (mode == ShellDialogMode.DISABLED) {
            plugin.getLogger().info("Shell Dialog UI is disabled by config.");
            return ShellDialogPresenter.UNAVAILABLE;
        }
        if (!isDialogApiAvailable()) {
            return ShellDialogPresenter.UNAVAILABLE;
        }
        if (mode == ShellDialogMode.AUTO && hasKnownPacketCompatibilityRisk(plugin)) {
            plugin.getLogger().warning("Shell Dialog UI was disabled automatically because a packet-inspection plugin is present. Set shell.dialog-mode to enabled to force it.");
            return ShellDialogPresenter.UNAVAILABLE;
        }
        try {
            return new PaperShellDialogPresenter(plugin, submissionHandler);
        } catch (LinkageError error) {
            plugin.getLogger().warning("Dialog API was detected but could not be initialized: " + error.getMessage());
            return ShellDialogPresenter.UNAVAILABLE;
        }
    }

    private static boolean hasKnownPacketCompatibilityRisk(JavaPlugin plugin) {
        return plugin.getServer().getPluginManager().getPlugin("GrimAC") != null
                || plugin.getServer().getPluginManager().getPlugin("packetevents") != null
                || plugin.getServer().getPluginManager().getPlugin("PacketEvents") != null;
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
