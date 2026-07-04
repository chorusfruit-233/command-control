package cc.commandcontrol;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public final class CommandControlPlugin extends JavaPlugin implements TabExecutor {
    private static final String USE_PERMISSION = "commandcontrol.use";
    private static final String ADMIN_PERMISSION = "commandcontrol.admin";
    private static final String SHELL_PERMISSION = "commandcontrol.shell";

    private AuthorizationList authorizationList = new AuthorizationList(List.of());
    private AuthorizationList shellAuthorizationList = new AuthorizationList(List.of());
    private ShellCommandSettings shellCommandSettings = ShellCommandSettings.DEFAULT;
    private final ShellCommandExecutor shellCommandExecutor = new ShellCommandExecutor();
    private final ShellTabCompleter shellTabCompleter = new ShellTabCompleter();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadAuthorizationList();
        registerCommand("cmdctl");
        registerCommand("cmdctladmin");
        registerCommand("cmdctlsh");
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
        if ("cmdctlsh".equals(commandName)) {
            return handleShellCommand(sender, args);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName().toLowerCase();
        if ("cmdctl".equals(commandName)) {
            return completeMinecraftCommand(sender, args);
        }
        if ("cmdctladmin".equals(commandName)) {
            return completeAdmin(args);
        }
        if ("cmdctlsh".equals(commandName)) {
            return completeShellCommand(sender, args);
        }
        return List.of();
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
        List<Component> output = new ArrayList<>();
        boolean submitted;
        try {
            submitted = Bukkit.dispatchCommand(
                    Bukkit.createCommandSender(output::add),
                    consoleCommand
            );
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
        for (Component outputLine : output) {
            player.sendMessage(outputLine);
        }
        return true;
    }

    private List<String> completeMinecraftCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player) || !canUseCommandControl(player)) {
            return List.of();
        }
        String commandLine = CommandNormalizer.normalize(String.join(" ", args)).orElse("");
        try {
            return Bukkit.getCommandMap().tabComplete(Bukkit.getConsoleSender(), commandLine);
        } catch (IllegalArgumentException exception) {
            return List.of();
        }
    }

    private boolean handleShellCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use /cmdctlsh.", NamedTextColor.RED));
            return true;
        }
        if (!player.isOp() || !player.hasPermission(SHELL_PERMISSION)) {
            player.sendMessage(Component.text("You must be OP and have " + SHELL_PERMISSION + " to use this command.", NamedTextColor.RED));
            return true;
        }
        if (!shellAuthorizationList.isAuthorized(player.getUniqueId(), player.getName())) {
            player.sendMessage(Component.text("You are not authorized in CommandControl's shell whitelist.", NamedTextColor.RED));
            getLogger().warning("Rejected /cmdctlsh from unauthorized player " + describe(player) + ".");
            return true;
        }

        Optional<String> normalizedCommand = ShellCommandNormalizer.normalize(String.join(" ", args));
        if (normalizedCommand.isEmpty()) {
            player.sendMessage(Component.text("Usage: /cmdctlsh <linux command>", NamedTextColor.RED));
            return true;
        }

        String shellCommand = normalizedCommand.get();
        String playerDescription = describe(player);
        ShellCommandSettings settings = shellCommandSettings;
        player.sendMessage(Component.text("Shell command started.", NamedTextColor.GRAY));
        getLogger().info("Started shell command for " + playerDescription + ": " + shellCommand);
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> executeShellCommand(player, playerDescription, shellCommand, settings));
        return true;
    }

    private List<String> completeShellCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player) || !canUseShell(player)) {
            return List.of();
        }
        return shellTabCompleter.complete(String.join(" ", args), shellCommandSettings);
    }

    private List<String> completeAdmin(String[] args) {
        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0];
            return CompletionUtils.filterByPrefix(List.of("reload"), prefix);
        }
        return List.of();
    }

    private boolean canUseCommandControl(Player player) {
        return player.isOp()
                && player.hasPermission(USE_PERMISSION)
                && authorizationList.isAuthorized(player.getUniqueId(), player.getName());
    }

    private boolean canUseShell(Player player) {
        return player.isOp()
                && player.hasPermission(SHELL_PERMISSION)
                && shellAuthorizationList.isAuthorized(player.getUniqueId(), player.getName());
    }

    private void executeShellCommand(Player player, String playerDescription, String shellCommand, ShellCommandSettings settings) {
        ShellCommandResult result;
        try {
            result = shellCommandExecutor.execute(shellCommand, settings);
        } catch (IOException exception) {
            getLogger().log(Level.SEVERE, "Shell command failed to start for " + playerDescription + ": " + shellCommand, exception);
            sendShellFailure(player, "Shell command failed to start. Check the server log.");
            return;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            getLogger().log(Level.WARNING, "Shell command interrupted for " + playerDescription + ": " + shellCommand, exception);
            sendShellFailure(player, "Shell command was interrupted. Check the server log.");
            return;
        }

        getLogger().info("Finished shell command for " + playerDescription + ": " + shellCommand
                + " (exit=" + result.exitCode()
                + ", timedOut=" + result.timedOut()
                + ", truncated=" + result.truncated()
                + ", elapsedMs=" + result.elapsed().toMillis() + ")");
        Bukkit.getScheduler().runTask(this, () -> sendShellResult(player, result));
    }

    private void sendShellFailure(Player player, String message) {
        Bukkit.getScheduler().runTask(this, () -> player.sendMessage(Component.text(message, NamedTextColor.RED)));
    }

    private void sendShellResult(Player player, ShellCommandResult result) {
        NamedTextColor statusColor = result.timedOut() || result.exitCode() != 0 ? NamedTextColor.YELLOW : NamedTextColor.GREEN;
        String status = result.timedOut()
                ? "Shell command timed out after " + result.elapsed().toMillis() + " ms."
                : "Shell command finished with exit code " + result.exitCode() + " in " + result.elapsed().toMillis() + " ms.";
        player.sendMessage(Component.text(status, statusColor));

        if (result.outputLines().isEmpty()) {
            player.sendMessage(Component.text("(no output)", NamedTextColor.DARK_GRAY));
        } else {
            for (String line : result.outputLines()) {
                player.sendMessage(Component.text(line, NamedTextColor.GRAY));
            }
        }
        if (result.truncated()) {
            player.sendMessage(Component.text("Output truncated to " + result.maxOutputLines() + " lines.", NamedTextColor.YELLOW));
        }
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
        sender.sendMessage(Component.text("CommandControl reloaded. Authorized entries: " + authorizationList.size()
                + ", shell entries: " + shellAuthorizationList.size() + ".", NamedTextColor.GREEN));
        getLogger().info("Configuration reloaded by " + sender.getName()
                + ". Authorized entries: " + authorizationList.size()
                + ", shell entries: " + shellAuthorizationList.size() + ".");
        return true;
    }

    private void reloadAuthorizationList() {
        authorizationList = loadAuthorizationList("authorized-players");
        shellAuthorizationList = loadAuthorizationList("shell-authorized-players");
        shellCommandSettings = loadShellCommandSettings();
    }

    private void registerCommand(String commandName) {
        PluginCommand command = getCommand(commandName);
        if (command == null) {
            getLogger().warning("Command is missing from plugin.yml: " + commandName);
            return;
        }
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    private AuthorizationList loadAuthorizationList(String path) {
        List<AuthorizedPlayer> players = new ArrayList<>();
        for (Map<?, ?> entry : getConfig().getMapList(path)) {
            UUID uuid = parseUuid(asString(entry.get("uuid")));
            String name = asString(entry.get("name"));
            if (uuid == null && (name == null || name.isBlank())) {
                getLogger().warning("Ignoring " + path + " entry without uuid or name.");
                continue;
            }
            players.add(new AuthorizedPlayer(uuid, name));
        }
        return new AuthorizationList(players);
    }

    private ShellCommandSettings loadShellCommandSettings() {
        String executable = getConfig().getString("shell.executable", ShellCommandSettings.DEFAULT.executable());
        String workingDirectory = getConfig().getString("shell.working-directory", ShellCommandSettings.DEFAULT.workingDirectory().toString());
        int timeoutSeconds = Math.max(1, getConfig().getInt("shell.timeout-seconds", (int) ShellCommandSettings.DEFAULT.timeout().toSeconds()));
        int maxOutputLines = Math.max(0, getConfig().getInt("shell.max-output-lines", ShellCommandSettings.DEFAULT.maxOutputLines()));
        return new ShellCommandSettings(
                executable == null || executable.isBlank() ? ShellCommandSettings.DEFAULT.executable() : executable,
                Path.of(workingDirectory == null || workingDirectory.isBlank() ? "." : workingDirectory),
                Duration.ofSeconds(timeoutSeconds),
                maxOutputLines
        );
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
