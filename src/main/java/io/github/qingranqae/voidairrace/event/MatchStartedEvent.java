package io.github.qingranqae.voidairrace.event;

import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

/**
 * 当一局比赛开始时触发的事件。
 * 该事件由 {@link io.github.qingranqae.voidairrace.core.matchsystem.MatchCoordinator} 在调用 {@code startMatch} 方法后发布。
 * 监听此事件可以执行赛前准备、广播通知、初始化规则等操作。
 */
public class MatchStartedEvent extends Event {
    /** Bukkit 事件处理器列表。 */
    private static final HandlerList HANDLERS = new HandlerList();

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

    /** 开始的比赛对象。 */
    private final Match match;

    /**
     * 构造一个比赛开始事件。
     *
     * @param match 开始的比赛实例
     */
    public MatchStartedEvent(Match match) {
        this.match = match;
    }

    /**
     * 获取开始的比赛对象。
     *
     * @return 比赛实例
     */
    public Match getMatch() {
        return match;
    }
}