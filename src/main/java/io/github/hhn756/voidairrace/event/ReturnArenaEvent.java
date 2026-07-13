package io.github.hhn756.voidairrace.event;

import io.github.hhn756.voidairrace.core.arena.ArenaToken;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

public class ReturnArenaEvent extends Event {
    /** Bukkit 事件处理器列表 */
    private static final HandlerList HANDLERS = new HandlerList();

    public ReturnArenaEvent(ArenaToken token) {
        this.token = token;
    }

    /**
     * 获取 Bukkit 事件处理器列表（静态方法）
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

    private final ArenaToken token;

    /**
     * 获取被归还的竞技场的借据
     *
     * @return 竞技场借据
     * */
    public ArenaToken getToken() {
        return token;
    }
}
