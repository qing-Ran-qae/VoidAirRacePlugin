package io.github.qingranqae.voidairrace.event;

import io.github.qingranqae.voidairrace.corelayer.matchsystem.Match;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

public class MatchOverEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLERS;
    }

    private final Match match;

    public MatchOverEvent(Match match) {
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }
}
