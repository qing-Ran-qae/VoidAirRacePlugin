package io.github.qingranqae.voidairrace.pluginevent;

import io.github.qingranqae.voidairrace.VoidAirRace;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

/**
 * 插件禁用时触发一次
 * */
public class PluginDisableEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLERS;
    }

    private final VoidAirRace mainClass;

    /**
     * 构造器
     *
     * @param mainClass 应传入插件主类实例
     * */
    public PluginDisableEvent(VoidAirRace mainClass) {
        this.mainClass = mainClass;
    }

    /**
     * 获取插件主类实例
     * */
    public VoidAirRace getMainClass() {
        return mainClass;
    }
}
