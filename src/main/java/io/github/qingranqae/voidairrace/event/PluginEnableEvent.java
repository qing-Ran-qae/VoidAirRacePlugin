package io.github.qingranqae.voidairrace.event;

import io.github.qingranqae.voidairrace.VoidAirRace;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

/**
 * 当插件启用时触发的事件。
 * 该事件由 {@link VoidAirRace#onEnable()} 在插件启动流程末尾发布。
 * 监听此事件可以执行需要在插件完全加载后进行的初始化操作，例如注册命令、加载配置、初始化地图等。
 * 此事件为各模块提供了统一的启动入口，避免在 {@code onEnable} 中直接耦合大量初始化代码。
 */
public class PluginEnableEvent extends Event {
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

    /** 插件主类实例。 */
    private final VoidAirRace mainClass;

    /**
     * 构造一个插件启用事件。
     *
     * @param mainClass 插件主类实例（即触发事件的插件）
     */
    public PluginEnableEvent(VoidAirRace mainClass) {
        this.mainClass = mainClass;
    }

    /**
     * 获取插件主类实例。
     *
     * @return 插件主类
     */
    public VoidAirRace getMainClass() {
        return mainClass;
    }
}