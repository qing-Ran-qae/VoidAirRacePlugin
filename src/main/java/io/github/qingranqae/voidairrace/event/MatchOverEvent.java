package io.github.qingranqae.voidairrace.event;

import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * 当一局比赛结束时触发的事件。
 * 该事件由 {@link io.github.qingranqae.voidairrace.core.matchsystem.MatchCoordinator} 在调用 {@code stopMatch} 方法后发布。
 * 监听此事件可以执行赛后清理、奖励发放、数据统计等操作。
 */
public class MatchOverEvent extends Event {
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

    /** 刚刚结束的比赛 */
    private final Match match;
    /** 玩家排名，索引越大的玩家排名越低/后 */
    private final List<Player> tops;

    /**
     * 构造一个比赛结束事件。
     *
     * @param match 结束的比赛实例
     */
    public MatchOverEvent(Match match, List<Player> tops) {
        this.match = match;
        this.tops = tops;
    }

    /**
     * 获取结束的比赛对象。
     *
     * @return 比赛实例
     */
    public Match getMatch() {
        return match;
    }

    /**
     * 获取玩家排名，索引越大的玩家排名越低/后
     * */
    public List<Player> getTops() {
        return tops;
    }
}