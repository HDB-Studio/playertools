package com.hotcoco.playertools.command;

import com.hotcoco.playertools.PlayerToolsPlugin;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 根指令注册与运行时改名。
 * Paper 26.x 下指令必须注册到 Brigadier 命令树，客户端才会有指令提示与补全。
 */
public final class CommandManager {

    /** 根指令修改结果。 */
    public enum RootChange {
        /** 已立即写入命令树，客户端提示同步更新。 */
        INSTANT,
        /** 仅通过兜底方式生效，重启服务器后客户端提示才会更新。 */
        RESTART_REQUIRED
    }

    private static final String DEFAULT_NAME = "playertools";
    private static final String DESCRIPTION = "PlayerTools 主指令";

    private final PlayerToolsPlugin plugin;
    private final RootCommand rootCommand;
    private final RootBasicCommand basicCommand;
    private Commands registrar;
    private String currentName = DEFAULT_NAME;
    private List<String> currentAliases = List.of();
    private boolean registered;

    public CommandManager(PlayerToolsPlugin plugin) {
        this.plugin = plugin;
        this.rootCommand = new RootCommand(plugin);
        this.basicCommand = new RootBasicCommand(rootCommand);
    }

    public RootCommand rootCommand() {
        return rootCommand;
    }

    /** 当前生效的根指令名称。 */
    public String currentName() {
        return currentName;
    }

    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            if (registered) {
                return;
            }
            this.registrar = event.registrar();
            this.currentName = readName();
            this.currentAliases = readAliases();
            registrar.register(currentName, DESCRIPTION, currentAliases, basicCommand);
            this.registered = true;
        });
    }

    /**
     * 游戏内修改根指令：写入配置，并尝试立即更新命令树。
     * Paper 在命令生命周期之外禁止修改命令树，因此多数情况下需要重启才能生效。
     */
    public RootChange changeRoot(String name) {
        List<String> aliases = readAliases();
        plugin.getConfig().set("command.name", name);
        plugin.saveConfig();

        if (registered && registrar != null) {
            try {
                registrar.register(plugin.getPluginMeta(), name, DESCRIPTION, aliases, basicCommand);
                removeLabels(currentName);
                this.currentName = name;
                this.currentAliases = aliases;
                Bukkit.getOnlinePlayers().forEach(player -> player.updateCommands());
                return RootChange.INSTANT;
            } catch (RuntimeException exception) {
                plugin.getLogger().info("命令树已锁定，根指令将在重启后生效：" + exception.getMessage());
            }
        }
        return RootChange.RESTART_REQUIRED;
    }

    /** 指令树注册后不一定能在运行时注销，名称或别名改动可能要求重启服务器。 */
    public void checkReload() {
        if (registered && (!readName().equals(currentName) || !readAliases().equals(currentAliases))) {
            plugin.getLogger().warning("检测到根指令名称或别名改动，执行 /" + currentName + " root set <名称> 或在重启后生效");
        }
    }

    private void removeLabels(String label) {
        var root = registrar.getDispatcher().getRoot();
        root.getChildren().removeIf(node -> node.getName().equals(label));
        root.getChildren().removeIf(node -> node.getName().equals(plugin.getName().toLowerCase(Locale.ROOT) + ":" + label));
    }

    private String readName() {
        String name = plugin.getConfig().getString("command.name", DEFAULT_NAME);
        name = name == null ? DEFAULT_NAME : name.trim().toLowerCase(Locale.ROOT);
        if (name.isEmpty() || name.contains(" ")) {
            plugin.getLogger().warning("command.name 配置无效，已使用默认值 " + DEFAULT_NAME);
            return DEFAULT_NAME;
        }
        return name;
    }

    private List<String> readAliases() {
        return new ArrayList<>(plugin.getConfig().getStringList("command.aliases").stream()
                .filter(Objects::nonNull)
                .map(alias -> alias.trim().toLowerCase(Locale.ROOT))
                .filter(alias -> !alias.isEmpty() && !alias.contains(" "))
                .toList());
    }
}
