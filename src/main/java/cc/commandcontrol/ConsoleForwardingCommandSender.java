package cc.commandcontrol;

import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;

final class ConsoleForwardingCommandSender implements ProxiedCommandSender {
    private final Player caller;
    private final ConsoleCommandSender console;

    ConsoleForwardingCommandSender(Player caller, ConsoleCommandSender console) {
        this.caller = caller;
        this.console = console;
    }

    @Override
    public CommandSender getCaller() {
        return caller;
    }

    @Override
    public CommandSender getCallee() {
        return console;
    }

    @Override
    public void sendMessage(String message) {
        caller.sendMessage(message);
    }

    @Override
    public void sendMessage(String... messages) {
        caller.sendMessage(messages);
    }

    @Override
    public void sendMessage(UUID sender, String message) {
        caller.sendMessage(sender, message);
    }

    @Override
    public void sendMessage(UUID sender, String... messages) {
        caller.sendMessage(sender, messages);
    }

    @Override
    public Server getServer() {
        return console.getServer();
    }

    @Override
    public String getName() {
        return console.getName();
    }

    @Override
    public Component name() {
        return console.name();
    }

    @Override
    public Spigot spigot() {
        return caller.spigot();
    }

    @Override
    public boolean isPermissionSet(String name) {
        return console.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return console.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String name) {
        return console.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return console.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return console.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return console.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return console.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return console.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        console.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        console.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return console.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return console.isOp();
    }

    @Override
    public void setOp(boolean value) {
        console.setOp(value);
    }
}
