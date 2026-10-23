package com.hotcoco.playertools;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 统一读取 config.yml 中的中文提示文本。
 */
public final class Messages {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    private final PlayerToolsPlugin plugin;
    private final FileConfiguration defaults;
    private Component prefix;

    public Messages(PlayerToolsPlugin plugin) {
        this.plugin = plugin;
        this.defaults = loadBundledDefaults();
        reload();
    }

    public void reload() {
        FileConfiguration config = plugin.getConfig();
        this.prefix = color(config.getString("messages.prefix", defaults.getString("messages.prefix", "")));
    }

    public Component color(String text) {
        return SERIALIZER.deserialize(text);
    }

    /** 读取原始文本（不含前缀），支持 %key% 占位符替换。 */
    public String raw(String key, String... replacements) {
        String path = "messages." + key;
        String text = plugin.getConfig().getString(path);
        if (text == null) {
            text = defaults.getString(path, key);
        }
        if (text == null) {
            text = key;
        }
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            text = text.replace(replacements[i], replacements[i + 1]);
        }
        return text;
    }

    /** 读取文本并转换为组件（不含前缀）。 */
    public Component message(String key, String... replacements) {
        return color(raw(key, replacements));
    }

    /** 读取文本并加上前缀后发送给指令来源。 */
    public void send(CommandSender sender, String key, String... replacements) {
        sender.sendMessage(prefix.append(message(key, replacements)));
    }

    /** 新版本新增的提示项在旧配置里可能缺失，这里以插件内置配置兜底。 */
    private FileConfiguration loadBundledDefaults() {
        try (InputStream stream = plugin.getResource("config.yml")) {
            if (stream == null) {
                return new YamlConfiguration();
            }
            return YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
        } catch (IOException exception) {
            plugin.getLogger().warning("读取内置配置失败：" + exception.getMessage());
            return new YamlConfiguration();
        }
    }
}
