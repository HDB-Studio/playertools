package com.hotcoco.playertools.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.Collection;

/**
 * 把根指令接入 Paper 的 Brigadier 命令树，客户端才能收到指令提示与补全。
 */
final class RootBasicCommand implements BasicCommand {

    private static final String ROOT_PERMISSION = "playertools.use";

    private final RootCommand rootCommand;

    RootBasicCommand(RootCommand rootCommand) {
        this.rootCommand = rootCommand;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        rootCommand.dispatch(source.getSender(), args);
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        return rootCommand.suggest(source.getSender(), args);
    }

    @Override
    public String permission() {
        return ROOT_PERMISSION;
    }
}
