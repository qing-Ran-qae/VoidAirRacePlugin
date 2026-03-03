package io.github.qingranqae.voidairrace.event;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

public class PlayerStateChangedEvent extends Event {
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

    private final NamespacedKey oldState;
    private final NamespacedKey newState;

    PlayerStateChangedEvent(NamespacedKey oldState, NamespacedKey newState) {
        this.oldState = oldState;
        this.newState = newState;
    }

    /**
     * 获取状态变更前的旧状态
     *
     * @return 旧状态
     * */
    public NamespacedKey getOldState() {
        return oldState;
    }

    /**
     * 获取状态变更后的新状态
     *
     * @return 新状态
     * */
    public NamespacedKey getNewState() {
        return newState;
    }
}
