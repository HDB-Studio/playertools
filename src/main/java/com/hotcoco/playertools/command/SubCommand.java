package com.hotcoco.playertools.command;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * 子指令接口。新增功能时实现该接口并在根指令中注册即可。
 */
public interface SubCommand {

    /** 子指令名称，例如 help。 */
    String name();

    /** 子指令别名。 */
    default List<String> aliases() {
        return List.of();
    }

    /** 所需权限，null 表示无需额外权限。 */
    default String permission() {
        return null;
    }

    /** 帮助信息中的简介。 */
    default String description() {
        return "";
    }

    /** 帮助信息中的用法（不含根指令前缀）。 */
    default String usage() {
        return name();
    }

    /** 执行子指令，args 为根指令之后的参数。 */
    void execute(CommandSender sender, String[] args);

    /** 补全子指令参数。 */
    default List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
