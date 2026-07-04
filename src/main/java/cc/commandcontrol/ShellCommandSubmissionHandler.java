package cc.commandcontrol;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface ShellCommandSubmissionHandler {
    void submit(Player player, String command);
}
