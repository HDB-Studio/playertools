package com.hotcoco.playertools.command.sub;

import com.hotcoco.playertools.PlayerToolsPlugin;
import com.hotcoco.playertools.Messages;
import com.hotcoco.playertools.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * 查看插件与服务端运行环境信息。
 */
public final class InfoSubCommand implements SubCommand {

    private final PlayerToolsPlugin plugin;

    public InfoSubCommand(PlayerToolsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "info";
    }

    @Override
    public String description() {
        return "查看插件与运行环境信息";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messages messages = plugin.messages();
        sender.sendMessage(messages.color("&bPlayerTools &7v" + plugin.getPluginMeta().getVersion()));
        sender.sendMessage(messages.color("&7服务端：&f" + Bukkit.getName() + " " + Bukkit.getBukkitVersion()));
        sender.sendMessage(messages.color("&7Java：&f" + System.getProperty("java.version")));
        sender.sendMessage(messages.color("&7根指令：&f/" + plugin.commandManager().currentName()));
    }
}
