package io.github.qingranqae.voidairrace.event;

import io.github.qingranqae.voidairrace.service.config.files.ConfigFiles;
import io.github.qingranqae.voidairrace.service.config.files.ConfigKeys;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * 当配置文件中的某个字段值发生变更时触发的事件。
 * 该事件由 {@link io.github.qingranqae.voidairrace.service.config.ObservableYamlConfiguration} 在调用 {@code set} 方法且值发生变化时发布。
 * 监听此事件可以实时响应配置更改，例如动态调整游戏行为。
 */
public class ConfigFieldChangeEvent extends Event {

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
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /** 发生变更的配置文件。 */
    private final ConfigFiles file;

    /** 发生变更的配置字段。 */
    private final ConfigKeys field;

    /** 变更前的值。 */
    private final Object oldValue;

    /** 变更后的值。 */
    private final Object newValue;

    /**
     * 构造一个配置字段变更事件。
     *
     * @param file     发生变更的配置文件
     * @param field    发生变更的字段
     * @param oldValue 旧值
     * @param newValue 新值
     */
    public ConfigFieldChangeEvent(ConfigFiles file, ConfigKeys field, Object oldValue, Object newValue) {
        this.file = file;
        this.field = field;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * 获取发生变更的配置字段。
     *
     * @return 配置字段
     */
    public ConfigKeys getField() {
        return field;
    }

    /**
     * 获取字段变更前的值，并自动转换为指定类型。
     *
     * @param expectedType 期望的类型 Class
     * @param <T>          期望的类型
     * @return 旧值（已转型）
     * @throws ClassCastException 如果实际类型与期望类型不兼容
     */
    public <T> T getOldValue(Class<T> expectedType) throws ClassCastException {
        return expectedType.cast(oldValue);
    }

    /**
     * 获取字段变更后的值，并自动转换为指定类型。
     *
     * @param expectedType 期望的类型 Class
     * @param <T>          期望的类型
     * @return 新值（已转型）
     * @throws ClassCastException 如果实际类型与期望类型不兼容
     */
    public <T> T getNewValue(Class<T> expectedType) throws ClassCastException {
        return expectedType.cast(newValue);
    }

    /**
     * 获取发生变更的配置文件。
     *
     * @return 配置文件枚举
     */
    public ConfigFiles getFile() {
        return file;
    }
}