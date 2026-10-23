package com.hotcoco.playertools.command.sub;

import com.hotcoco.playertools.PlayerToolsPlugin;
import com.hotcoco.playertools.command.RootCommand;
import com.hotcoco.playertools.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * 帮助子指令。
 */
public final class HelpSubCommand implements SubCommand {

    private final RootCommand rootCommand;

    public HelpSubCommand(RootCommand rootCommand) {
        this.rootCommand = rootCommand;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public List<String> aliases() {
        return List.of("?");
    }

    @Override
    public String description() {
        return "查看指令帮助";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        PlayerToolsPlugin plugin = rootCommand.plugin();
        String root = plugin.commandManager().currentName();
        sender.sendMessage(plugin.messages().message("help-header"));
        for (SubCommand subCommand : rootCommand.subCommands()) {
            if (subCommand.permission() != null && !sender.hasPermission(subCommand.permission())) {
                continue;
            }
            sender.sendMessage(plugin.messages().message("help-entry",
                    "%root%", root,
                    "%usage%", subCommand.usage(),
                    "%description%", subCommand.description()));
        }
        sender.sendMessage(plugin.messages().message("help-footer"));
    }
}
