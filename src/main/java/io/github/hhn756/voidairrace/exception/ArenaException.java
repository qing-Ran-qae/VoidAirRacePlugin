package io.github.hhn756.voidairrace.exception;

import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;

/**
 * 竞技场相关操作会抛出的异常
 * */
public class ArenaException extends RuntimeException implements UserFriendlyException {
    public ArenaException(@Nullable String message, @Nullable Component displayMessage) {
        super(message);
        this.displayMessage = displayMessage;
    }

    private final @Nullable Component displayMessage;

    @Override
    public @Nullable Component getDisplayMessage() {
        return displayMessage;
    }
}