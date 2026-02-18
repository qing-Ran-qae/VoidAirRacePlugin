package io.github.qingranqae.voidairrace.event;

import io.github.qingranqae.voidairrace.corelayer.matchsystem.Match;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchStartedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
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
