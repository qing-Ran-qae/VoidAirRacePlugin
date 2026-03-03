package io.github.qingranqae.voidairrace.core.config;

import io.github.qingranqae.voidairrace.event.ConfigFieldChangeEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 支持 {@link ConfigKey} 并能在配置值变更时自动发布 {@link ConfigFieldChangeEvent} 的 YAML 配置实现。
 * 继承自 Bukkit 的 {@link YamlConfiguration}，添加了键注册和事件触发功能。
 */
public class ObservableYamlConfiguration extends YamlConfiguration {

    /** 当前配置所属的配置文件枚举。 */
    private final ConfigFiles file;

    /** 从路径到对应 {@link ConfigKey} 的映射，用于事件触发时识别哪个配置项被修改。 */
    private final Map<String, ConfigKey> keyMap = new HashMap<>();

    /**
     * 构造一个可观察的 YAML 配置对象。
     *
     * @param file 配置文件枚举，用于事件发布时标识来源文件
     */
    public ObservableYamlConfiguration(@NotNull ConfigFiles file) {
        this.file = file;
    }

    /**
     * 注册一个 {@link ConfigKey}，使其路径能被事件系统识别。
     *
     * @param key 要注册的配置键
     */
    public void registerConfigKey(@NotNull ConfigKey key) {
        keyMap.put(key.getPath(), key);
    }

    /**
     * 批量注册多个 {@link ConfigKey}。
     *
     * @param keys 要注册的配置键数组
     */
    public void registerConfigKeys(@NotNull ConfigKey[] keys) {
        for (ConfigKey key : keys) {
            registerConfigKey(key);
        }
    }

    // ========== 重写 set 方法以触发事件 ==========

    /**
     * 设置指定路径的值，如果值发生变化且该路径已注册 {@link ConfigKey}，则触发 {@link ConfigFieldChangeEvent}。
     *
     * @param path     配置项路径
     * @param newValue 新值
     */
    @Override
    public void set(@NotNull String path, Object newValue) {
        Object oldValue = get(path);
        super.set(path, newValue);

        if (!Objects.equals(oldValue, newValue)) {
            ConfigKey key = keyMap.get(path);
            if (key != null) {
                new ConfigFieldChangeEvent(file, key, oldValue, newValue).callEvent();
            }
        }
    }

    // ----- 基于 ConfigKey 的基础操作 -----

    /**
     * 获取指定配置键的值（返回 Object）。
     *
     * @param key 配置键
     * @return 值对象
     */
    public Object get(@NotNull ConfigKey key) {
        return get(key.getPath());
    }

    /**
     * 设置指定配置键的值。
     *
     * @param key   配置键
     * @param value 新值
     */
    public void set(@NotNull ConfigKey key, @Nullable Object value) {
        set(key.getPath(), value);
    }

    /**
     * 检查配置键是否存在。
     *
     * @param key 配置键
     * @return 如果存在则返回 true
     */
    public boolean contains(@NotNull ConfigKey key) {
        return contains(key.getPath());
    }

    /**
     * 检查配置键是否存在，可选择深度检查（考虑子路径）。
     *
     * @param key  配置键
     * @param deep 是否深度检查
     * @return 如果存在则返回 true
     */
    public boolean contains(@NotNull ConfigKey key, boolean deep) {
        return contains(key.getPath(), deep);
    }

    // ----- 类型安全的 get 方法（基于 ConfigKey）-----

    /**
     * 获取字符串值。
     *
     * @param key 配置键
     * @return 字符串值，若不存在则返回 null
     */
    public String getString(@NotNull ConfigKey key) {
        return getString(key.getPath());
    }

    /**
     * 获取字符串值，若不存在则返回默认值。
     *
     * @param key 配置键
     * @param def 默认值
     * @return 字符串值或默认值
     */
    public String getString(@NotNull ConfigKey key, @Nullable String def) {
        return getString(key.getPath(), def);
    }

    /**
     * 获取整数值。
     *
     * @param key 配置键
     * @return 整数值，若不存在则返回 0
     */
    public int getInt(@NotNull ConfigKey key) {
        return getInt(key.getPath());
    }

    /**
     * 获取整数值，若不存在则返回默认值。
     *
     * @param key 配置键
     * @param def 默认值
     * @return 整数值或默认值
     */
    public int getInt(@NotNull ConfigKey key, int def) {
        return getInt(key.getPath(), def);
    }

    /**
     * 获取布尔值。
     *
     * @param key 配置键
     * @return 布尔值，若不存在则返回 false
     */
    public boolean getBoolean(@NotNull ConfigKey key) {
        return getBoolean(key.getPath());
    }

    /**
     * 获取布尔值，若不存在则返回默认值。
     *
     * @param key 配置键
     * @param def 默认值
     * @return 布尔值或默认值
     */
    public boolean getBoolean(@NotNull ConfigKey key, boolean def) {
        return getBoolean(key.getPath(), def);
    }

    /**
     * 获取双精度浮点值。
     *
     * @param key 配置键
     * @return 双精度值，若不存在则返回 0.0
     */
    public double getDouble(@NotNull ConfigKey key) {
        return getDouble(key.getPath());
    }

    /**
     * 获取双精度浮点值，若不存在则返回默认值。
     *
     * @param key 配置键
     * @param def 默认值
     * @return 双精度值或默认值
     */
    public double getDouble(@NotNull ConfigKey key, double def) {
        return getDouble(key.getPath(), def);
    }

    /**
     * 获取长整数值。
     *
     * @param key 配置键
     * @return 长整数值，若不存在则返回 0L
     */
    public long getLong(@NotNull ConfigKey key) {
        return getLong(key.getPath());
    }

    /**
     * 获取长整数值，若不存在则返回默认值。
     *
     * @param key 配置键
     * @param def 默认值
     * @return 长整数值或默认值
     */
    public long getLong(@NotNull ConfigKey key, long def) {
        return getLong(key.getPath(), def);
    }

    /**
     * 获取列表值。
     *
     * @param key 配置键
     * @return 列表对象，若不存在则返回 null
     */
    public List<?> getList(@NotNull ConfigKey key) {
        return getList(key.getPath());
    }

    /**
     * 获取列表值，若不存在则返回默认列表。
     *
     * @param key 配置键
     * @param def 默认列表
     * @return 列表对象或默认列表
     */
    public List<?> getList(@NotNull ConfigKey key, @Nullable List<?> def) {
        return getList(key.getPath(), def);
    }

    /**
     * 获取字符串列表。
     *
     * @param key 配置键
     * @return 字符串列表，若不存在或类型不匹配则返回空列表
     */
    public List<String> getStringList(@NotNull ConfigKey key) {
        return getStringList(key.getPath());
    }

    /**
     * 获取整数列表。
     *
     * @param key 配置键
     * @return 整数列表，若不存在或类型不匹配则返回空列表
     */
    public List<Integer> getIntegerList(@NotNull ConfigKey key) {
        return getIntegerList(key.getPath());
    }
}