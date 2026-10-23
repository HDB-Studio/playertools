package com.hotcoco.playertools;

import com.hotcoco.playertools.command.CommandManager;
import com.hotcoco.playertools.command.custom.CustomCommandLoader;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * PlayerTools 主类：面向 Folia 26.2 的扩展型基础插件。
 */
public final class PlayerToolsPlugin extends JavaPlugin {

    private Messages messages;
    private CommandManager commandManager;
    private CustomCommandLoader customCommands;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.messages = new Messages(this);
        this.commandManager = new CommandManager(this);
        this.commandManager.register();
        this.customCommands = new CustomCommandLoader(this);
        this.customCommands.reload();
        getLogger().info("已启用，根指令：/" + commandManager.currentName());
    }

    public Messages messages() {
        return messages;
    }

    public CommandManager commandManager() {
        return commandManager;
    }

    public CustomCommandLoader customCommands() {
        return customCommands;
    }

    /** 重载配置与自定义子指令。 */
    public void reload() {
        reloadConfig();
        messages.reload();
        commandManager.checkReload();
        customCommands.reload();
        getLogger().info("配置已重载，根指令：/" + commandManager.currentName());
    }
}
