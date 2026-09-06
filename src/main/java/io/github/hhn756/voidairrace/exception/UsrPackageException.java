package io.github.hhn756.voidairrace.exception;

import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;

/**
 * 用户包相关操作会抛出的异常
 * */
public class UsrPackageException extends RuntimeException implements UserFriendlyException {
    public UsrPackageException(@Nullable String message, @Nullable Component userMessage) {
        super(message);
        this.userMessage = userMessage;
    }

    private final Component userMessage;

    @Override
    public @Nullable Component getUserMessage() {
        return userMessage;
    }
}
