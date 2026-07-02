package cc.commandcontrol;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public final class CommandControlPlugin extends JavaPlugin {
    private static final String USE_PERMISSION = "commandcontrol.use";
    private static final String ADMIN_PERMISSION = "commandcontrol.admin";

    private AuthorizationList authorizationList = new AuthorizationList(List.of());

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadAuthorizationList();
        getLogger().info("Loaded " + authorizationList.size() + " authorized player entries.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName().toLowerCase();
        if ("cmdctl".equals(commandName)) {
            return handleCommandControl(sender, args);
        }
        if ("cmdctladmin".equals(commandName)) {
            return handleAdmin(sender, args);
        }
        return false;
    }

    private boolean handleCommandControl(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use /cmdctl.", NamedTextColor.RED));
            return true;
        }
        if (!player.isOp() || !player.hasPermission(USE_PERMISSION)) {
            player.sendMessage(Component.text("You must be OP and have " + USE_PERMISSION + " to use this command.", NamedTextColor.RED));
            return true;
        }
        if (!authorizationList.isAuthorized(player.getUniqueId(), player.getName())) {
            player.sendMessage(Component.text("You are not authorized in CommandControl's whitelist.", NamedTextColor.RED));
            getLogger().warning("Rejected /cmdctl from unauthorized player " + describe(player) + ".");
            return true;
        }

        Optional<String> normalizedCommand = CommandNormalizer.normalize(String.join(" ", args));
        if (normalizedCommand.isEmpty()) {
            player.sendMessage(Component.text("Usage: /cmdctl <command>", NamedTextColor.RED));
            return true;
        }

        String consoleCommand = normalizedCommand.get();
        boolean submitted;
        try {
            submitted = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand);
        } catch (RuntimeException exception) {
            getLogger().log(Level.SEVERE, "Console command failed for " + describe(player) + ": " + consoleCommand, exception);
            player.sendMessage(Component.text("Command failed before it could be submitted. Check the server log.", NamedTextColor.RED));
            return true;
        }

        getLogger().info("Submitted console command for " + describe(player) + ": " + consoleCommand + " (accepted=" + submitted + ")");
        if (submitted) {
            player.sendMessage(Component.text("Command submitted as console.", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("Command was submitted, but no command handler accepted it.", NamedTextColor.YELLOW));
        }
        return true;
    }

    private boolean handleAdmin(CommandSender sender, String[] args) {
        if (!sender.isOp() && !sender.hasPermission(ADMIN_PERMISSION)) {
            sender.sendMessage(Component.text("You must be OP or have " + ADMIN_PERMISSION + " to use this command.", NamedTextColor.RED));
            return true;
        }
        if (args.length != 1 || !"reload".equalsIgnoreCase(args[0])) {
            sender.sendMessage(Component.text("Usage: /cmdctladmin reload", NamedTextColor.RED));
            return true;
        }

        reloadConfig();
        reloadAuthorizationList();
        sender.sendMessage(Component.text("CommandControl reloaded. Authorized entries: " + authorizationList.size() + ".", NamedTextColor.GREEN));
        getLogger().info("Configuration reloaded by " + sender.getName() + ". Authorized entries: " + authorizationList.size() + ".");
        return true;
    }

    private void reloadAuthorizationList() {
        List<AuthorizedPlayer> players = new ArrayList<>();
        for (Map<?, ?> entry : getConfig().getMapList("authorized-players")) {
            UUID uuid = parseUuid(asString(entry.get("uuid")));
            String name = asString(entry.get("name"));
            if (uuid == null && (name == null || name.isBlank())) {
                getLogger().warning("Ignoring authorized-players entry without uuid or name.");
                continue;
            }
            players.add(new AuthorizedPlayer(uuid, name));
        }
        authorizationList = new AuthorizationList(players);
    }

    private String asString(Object value) {
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    private UUID parseUuid(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException exception) {
            getLogger().warning("Ignoring invalid authorized player UUID: " + value);
            return null;
        }
    }

    private String describe(Player player) {
        return player.getName() + " (" + player.getUniqueId() + ")";
    }
}
