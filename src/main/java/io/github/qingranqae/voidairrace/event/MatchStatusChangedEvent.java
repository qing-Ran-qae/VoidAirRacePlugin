package io.github.qingranqae.voidairrace.event;

import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

/**
 * 比赛状态发生变化时触发一次<br>
 * 注：开始和结束不会触发此事件，但有另外的事件类型用于检测开始和结束比赛
 * */
public class MatchStatusChangedEvent extends Event {
    /** Bukkit 事件处理器列表。 */
    private static final HandlerList HANDLERS = new HandlerList();

    public MatchStatusChangedEvent(Match match) {
        this.match = match;
    }

    /**
     * 获取 Bukkit 事件处理器列表（静态方法）。
     *
     * @return 处理器列表
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /** 发生状态变化的比赛 */
    private final Match match;

    /**
     * 获取发生状态变化的比赛
     *
     * @return 比赛对象
     * */
    public Match getMatch() {
        return match;
    }
}
