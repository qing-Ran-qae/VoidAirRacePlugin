package io.github.qingranqae.voidairrace.match;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

public class MatchStartedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLERS;
    }

    private final Match match;

    public MatchStartedEvent(Match match) {
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }
}
