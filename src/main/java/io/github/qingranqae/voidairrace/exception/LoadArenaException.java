package io.github.qingranqae.voidairrace.exception;

import net.kyori.adventure.text.Component;

public class LoadArenaException extends RuntimeException implements UserFriendlyException {
    public LoadArenaException(String message, Component displayMessage) {
        super(message);
        this.displayMessage = displayMessage;
    }

    private final Component displayMessage;

    @Override
    public Component getDisplayMessage() {
        return displayMessage;
    }
}
