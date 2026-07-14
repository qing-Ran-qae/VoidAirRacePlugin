package io.github.hhn756.voidairrace.exception;

import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;

/**
 * 注册表相关操作抛出的异常
 * */
public class RegistryException extends RuntimeException implements UserFriendlyException {
    public RegistryException(@Nullable String message, @Nullable Component userMessage) {
        super(message);
        this.userMessage = userMessage;
    }

    private final Component userMessage;

    @Override
    public @Nullable Component getUserMessage() {
        return userMessage;
    }
}
