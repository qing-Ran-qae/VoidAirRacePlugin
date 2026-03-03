package io.github.qingranqae.voidairrace.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

public class PlayerInitEvent extends Event {
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

    /**
     * 被初始化的玩家
     * */
    private final Player player;

    public PlayerInitEvent(Player player) {
        this.player = player;
    }

    /**
     * 获取被初始化的玩家
     * */
    public Player getPlayer() {
        return player;
    }
}
