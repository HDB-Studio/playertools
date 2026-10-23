package com.hotcoco.playertools.command;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 指令名称校验工具。
 */
public final class CommandNames {

    private static final Pattern VALID = Pattern.compile("^[a-z0-9_-]{1,32}$");

    private CommandNames() {
    }

    public static boolean isValid(String name) {
        return name != null && VALID.matcher(name.toLowerCase(Locale.ROOT)).matches();
    }
}
