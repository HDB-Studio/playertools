package com.hotcoco.playertools.command.sub;

import com.hotcoco.playertools.PlayerToolsPlugin;
import com.hotcoco.playertools.command.CommandNames;
import com.hotcoco.playertools.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 创建自定义子指令，生成 commands/&lt;名称&gt;/command.yml 并立即注册。
 */
public final class CreateSubCommand implements SubCommand {

    private final PlayerToolsPlugin plugin;

    public CreateSubCommand(PlayerToolsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "create";
    }

    @Override
    public List<String> aliases() {
        return List.of("new");
    }

    @Override
    public String permission() {
        return "playertools.admin";
    }

    @Override
    public String description() {
        return "创建自定义子指令";
    }

    @Override
    public String usage() {
        return "create <名称> [描述]";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String root = plugin.commandManager().currentName();
        if (args.length == 0) {
            plugin.messages().send(sender, "create-usage", "%root%", root);
            return;
        }

        String name = args[0].toLowerCase(Locale.ROOT);
        if (!CommandNames.isValid(name)) {
            plugin.messages().send(sender, "invalid-name");
            return;
        }
        if (plugin.commandManager().rootCommand().get(name) != null) {
            plugin.messages().send(sender, "create-exists", "%name%", name);
            return;
        }

        String description = args.length > 1
                ? String.join(" ", Arrays.copyOfRange(args, 1, args.length))
                : "自定义指令 " + name;
        if (!plugin.customCommands().create(name, description)) {
            plugin.messages().send(sender, "create-exists", "%name%", name);
            return;
        }
        plugin.messages().send(sender, "create-success",
                "%name%", name,
                "%path%", plugin.customCommands().relativePath(name));
    }
}
