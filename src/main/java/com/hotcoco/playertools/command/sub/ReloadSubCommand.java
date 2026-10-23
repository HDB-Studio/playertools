package com.hotcoco.playertools.command.sub;

import com.hotcoco.playertools.PlayerToolsPlugin;
import com.hotcoco.playertools.command.SubCommand;
import org.bukkit.command.CommandSender;

/**
 * 重载配置与根指令注册。
 */
public final class ReloadSubCommand implements SubCommand {

    private final PlayerToolsPlugin plugin;

    public ReloadSubCommand(PlayerToolsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String permission() {
        return "playertools.admin";
    }

    @Override
    public String description() {
        return "重新加载配置文件";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.reload();
        plugin.messages().send(sender, "reload-success", "%root%", plugin.commandManager().currentName());
    }
}
