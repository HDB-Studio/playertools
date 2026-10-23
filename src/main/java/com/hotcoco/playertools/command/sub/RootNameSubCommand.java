package com.hotcoco.playertools.command.sub;

import com.hotcoco.playertools.PlayerToolsPlugin;
import com.hotcoco.playertools.command.CommandManager;
import com.hotcoco.playertools.command.CommandNames;
import com.hotcoco.playertools.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;

/**
 * 查看或修改根指令名称。
 */
public final class RootNameSubCommand implements SubCommand {

    private final PlayerToolsPlugin plugin;

    public RootNameSubCommand(PlayerToolsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "root";
    }

    @Override
    public String permission() {
        return "playertools.admin";
    }

    @Override
    public String description() {
        return "查看或修改根指令名称";
    }

    @Override
    public String usage() {
        return "root set <名称>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String current = plugin.commandManager().currentName();
        if (args.length == 0) {
            plugin.messages().send(sender, "root-current", "%root%", current);
            return;
        }
        if (!args[0].equalsIgnoreCase("set") || args.length < 2) {
            plugin.messages().send(sender, "root-usage", "%root%", current);
            return;
        }

        String name = args[1].toLowerCase(Locale.ROOT);
        if (!CommandNames.isValid(name)) {
            plugin.messages().send(sender, "invalid-name");
            return;
        }
        if (name.equals(current)) {
            plugin.messages().send(sender, "root-same", "%name%", name);
            return;
        }

        boolean immediate = plugin.commandManager().changeRoot(name) == CommandManager.RootChange.INSTANT;
        plugin.messages().send(sender, immediate ? "root-changed" : "root-changed-restart", "%name%", name);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String typed = args[0].toLowerCase(Locale.ROOT);
            return "set".startsWith(typed) ? List.of("set") : List.of();
        }
        return List.of();
    }
}
