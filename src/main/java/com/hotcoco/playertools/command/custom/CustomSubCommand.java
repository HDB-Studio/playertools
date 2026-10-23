package com.hotcoco.playertools.command.custom;

import com.hotcoco.playertools.PlayerToolsPlugin;
import com.hotcoco.playertools.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;

/**
 * 由 commands/&lt;名称&gt;/command.yml 配置出来的子指令。
 * 默认展示 help 页面，执行 info 时展示 info 页面。
 */
public final class CustomSubCommand implements SubCommand {

    private final PlayerToolsPlugin plugin;
    private final String name;
    private final List<String> aliases;
    private final String permission;
    private final String description;
    private final String usage;
    private final List<String> helpLines;
    private final List<String> infoLines;

    public CustomSubCommand(PlayerToolsPlugin plugin, String name, List<String> aliases, String permission,
                            String description, String usage, List<String> helpLines, List<String> infoLines) {
        this.plugin = plugin;
        this.name = name;
        this.aliases = aliases;
        this.permission = permission;
        this.description = description;
        this.usage = usage;
        this.helpLines = helpLines;
        this.infoLines = infoLines;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<String> aliases() {
        return aliases;
    }

    @Override
    public String permission() {
        return permission;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String usage() {
        return usage;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        boolean info = args.length > 0 && args[0].equalsIgnoreCase("info");
        List<String> lines = info ? infoLines : helpLines;
        if (lines.isEmpty()) {
            plugin.messages().send(sender, info ? "custom-no-info" : "custom-no-help");
            return;
        }
        String root = plugin.commandManager().currentName();
        for (String line : lines) {
            sender.sendMessage(plugin.messages().color(line.replace("%root%", root)));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length > 1) {
            return List.of();
        }
        String typed = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
        return "info".startsWith(typed) ? List.of("info") : List.of();
    }
}
