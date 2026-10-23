package com.hotcoco.playertools.command.custom;

import com.hotcoco.playertools.PlayerToolsPlugin;
import com.hotcoco.playertools.command.CommandNames;
import com.hotcoco.playertools.command.RootCommand;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 扫描并加载数据目录下 commands/&lt;名称&gt;/command.yml 定义的自定义子指令。
 */
public final class CustomCommandLoader {

    private final PlayerToolsPlugin plugin;
    private final File directory;

    public CustomCommandLoader(PlayerToolsPlugin plugin) {
        this.plugin = plugin;
        this.directory = new File(plugin.getDataFolder(), "commands");
    }

    /** 重新读取全部自定义子指令。 */
    public void reload() {
        RootCommand rootCommand = plugin.commandManager().rootCommand();
        rootCommand.clearCustom();

        if (!directory.isDirectory() && !directory.mkdirs()) {
            plugin.getLogger().warning("无法创建自定义指令目录：" + directory.getPath());
            return;
        }
        File[] folders = directory.listFiles(File::isDirectory);
        if (folders == null) {
            return;
        }

        int loaded = 0;
        for (File folder : folders) {
            File file = new File(folder, "command.yml");
            if (!file.isFile()) {
                continue;
            }
            CustomSubCommand subCommand = load(folder, file);
            if (subCommand == null) {
                continue;
            }
            if (rootCommand.get(subCommand.name()) != null) {
                plugin.getLogger().warning("自定义子指令 " + subCommand.name() + " 与已有指令冲突，已跳过");
                continue;
            }
            rootCommand.registerCustom(subCommand);
            loaded++;
        }
        if (loaded > 0) {
            plugin.getLogger().info("已加载 " + loaded + " 个自定义子指令");
        }
    }

    /** 创建子指令目录与配置模板，并立即加载。 */
    public boolean create(String name, String description) {
        File folder = new File(directory, name);
        if (folder.exists()) {
            return false;
        }
        if (!folder.mkdirs()) {
            return false;
        }
        File file = new File(folder, "command.yml");
        try {
            Files.writeString(file.toPath(), template(name, description), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            plugin.getLogger().warning("创建子指令配置失败：" + exception.getMessage());
            return false;
        }
        reload();
        return true;
    }

    public String relativePath(String name) {
        return "commands/" + name + "/command.yml";
    }

    private CustomSubCommand load(File folder, File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.getBoolean("enabled", true)) {
            return null;
        }
        String rawName = config.getString("name", folder.getName());
        String name = rawName == null ? folder.getName() : rawName.trim().toLowerCase(Locale.ROOT);
        if (!CommandNames.isValid(name)) {
            plugin.getLogger().warning("自定义子指令名称无效：" + name);
            return null;
        }
        String commandName = name;
        List<String> aliases = config.getStringList("aliases").stream()
                .filter(Objects::nonNull)
                .map(alias -> alias.trim().toLowerCase(Locale.ROOT))
                .filter(alias -> CommandNames.isValid(alias) && !alias.equals(commandName))
                .toList();

        String permission = config.getString("permission", "playertools.use");
        if (permission == null || permission.isBlank()) {
            permission = null;
        }

        return new CustomSubCommand(
                plugin,
                name,
                aliases,
                permission,
                config.getString("description", "自定义指令 " + name),
                config.getString("usage", name),
                config.getStringList("help"),
                config.getStringList("info"));
    }

    private String template(String name, String description) {
        String safeDescription = description.replace("\\", "\\\\").replace("\"", "\\\"");
        return String.join("\n", List.of(
                "# PlayerTools 自定义子指令",
                "# help 为执行该子指令时展示的页面，info 为执行 \"/<根指令> <名称> info\" 时展示的页面",
                "# permission 留空表示不校验权限；支持 & 颜色代码与 %root% 占位符",
                "",
                "name: \"" + name + "\"",
                "aliases: []",
                "permission: \"playertools.use\"",
                "description: \"" + safeDescription + "\"",
                "usage: \"" + name + "\"",
                "enabled: true",
                "",
                "help:",
                "  - \"&b/%root% " + name + "\"",
                "  - \"&7在这里填写该指令的帮助内容\"",
                "",
                "info:",
                "  - \"&b指令 " + name + "\"",
                "  - \"&7在这里填写该指令的信息内容\"",
                ""));
    }
}
