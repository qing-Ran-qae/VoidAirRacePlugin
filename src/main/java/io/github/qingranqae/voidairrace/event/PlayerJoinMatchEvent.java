package io.github.qingranqae.voidairrace.event;

import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

/**
 * 玩家 加入 比赛时触发<br>
 * 注：比赛开始时不会为所有参赛者都触发一次此事件
 * */
public class PlayerJoinMatchEvent extends Event {
    /** Bukkit 事件处理器列表。 */
    private static final HandlerList HANDLERS = new HandlerList();

    public PlayerJoinMatchEvent(Player player, Match match) {
        this.player = player;
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

    private final Player player;
    private final Match match;

    /**
     * 获取加入比赛的玩家
     * */
    public Player getPlayer() {
        return player;
    }

    /**
     * 获取玩家加入的比赛
     * */
    public Match getMatch() {
        return match;
    }
}
