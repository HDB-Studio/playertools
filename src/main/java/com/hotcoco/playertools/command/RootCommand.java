package com.hotcoco.playertools.command;

import com.hotcoco.playertools.PlayerToolsPlugin;
import com.hotcoco.playertools.command.sub.CreateSubCommand;
import com.hotcoco.playertools.command.sub.HelpSubCommand;
import com.hotcoco.playertools.command.sub.InfoSubCommand;
import com.hotcoco.playertools.command.sub.ReloadSubCommand;
import com.hotcoco.playertools.command.sub.RootNameSubCommand;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 根指令：负责子指令注册、分发以及参数补全。
 */
public final class RootCommand {

    private final PlayerToolsPlugin plugin;
    private final Map<String, SubCommand> subCommands = new LinkedHashMap<>();
    private final Set<String> customKeys = new LinkedHashSet<>();

    public RootCommand(PlayerToolsPlugin plugin) {
        this.plugin = plugin;
        register(new HelpSubCommand(this));
        register(new ReloadSubCommand(plugin));
        register(new InfoSubCommand(plugin));
        register(new RootNameSubCommand(plugin));
        register(new CreateSubCommand(plugin));
    }

    /** 注册子指令，返回占用的全部名称（含别名）。 */
    public Set<String> register(SubCommand subCommand) {
        Set<String> keys = new LinkedHashSet<>();
        String name = subCommand.name().toLowerCase(Locale.ROOT);
        subCommands.put(name, subCommand);
        keys.add(name);
        for (String alias : subCommand.aliases()) {
            String key = alias.toLowerCase(Locale.ROOT);
            subCommands.putIfAbsent(key, subCommand);
            keys.add(key);
        }
        return keys;
    }

    /** 注册来自 commands 目录的自定义子指令。 */
    public void registerCustom(SubCommand subCommand) {
        customKeys.addAll(register(subCommand));
    }

    /** 移除已加载的自定义子指令，用于重载。 */
    public void clearCustom() {
        for (String key : customKeys) {
            subCommands.remove(key);
        }
        customKeys.clear();
    }

    public PlayerToolsPlugin plugin() {
        return plugin;
    }

    public SubCommand get(String name) {
        return subCommands.get(name.toLowerCase(Locale.ROOT));
    }

    /** 全部子指令（去重，保留注册顺序）。 */
    public Collection<SubCommand> subCommands() {
        return new LinkedHashSet<>(subCommands.values());
    }

    /** 执行根指令，args 不包含根指令名称。 */
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 0) {
            get("help").execute(sender, new String[0]);
            return;
        }

        SubCommand subCommand = get(args[0]);
        if (subCommand == null) {
            plugin.messages().send(sender, "unknown-subcommand",
                    "%sub%", args[0], "%root%", plugin.commandManager().currentName());
            return;
        }
        if (subCommand.permission() != null && !sender.hasPermission(subCommand.permission())) {
            plugin.messages().send(sender, "no-permission");
            return;
        }

        subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    /** 根指令参数补全，args 不包含根指令名称。 */
    public List<String> suggest(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            String typed = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
            return subCommands().stream()
                    .filter(sub -> sub.permission() == null || sender.hasPermission(sub.permission()))
                    .map(SubCommand::name)
                    .filter(name -> name.startsWith(typed))
                    .sorted()
                    .toList();
        }

        SubCommand subCommand = get(args[0]);
        if (subCommand == null) {
            return List.of();
        }
        return subCommand.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
    }
}
