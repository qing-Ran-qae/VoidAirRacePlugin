package io.github.hhn756.voidairrace.exception;

import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;

/**
 * 配置相关的非受检异常<br>
 * 配置的加载、创建、读取、修改等操作失败时抛出
 */
public class ConfigException extends RuntimeException implements UserFriendlyException {

    private final Component displayMessage;

    public ConfigException(String message, Component displayMessage) {
        super(message);
        this.displayMessage = displayMessage;
    }

    public ConfigException(String message, Throwable cause, Component displayMessage) {
        super(message, cause);
        this.displayMessage = displayMessage;
    }

    @Override
    public @Nullable Component getDisplayMessage() {
        return displayMessage;
    }
}