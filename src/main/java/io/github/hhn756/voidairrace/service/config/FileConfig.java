package io.github.hhn756.voidairrace.service.config;

import io.github.hhn756.voidairrace.exception.ConfigException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;

/**
 * 标记自定义配置功能
 */
public interface FileConfig {

    /**
     * 配置文件对应的枚举对象，如 {@code game_settings}
     * */
    @NonNull ConfigFile getSource();

    /**
     * 获取配置数据
     *
     * @param key 要读取的字段
     * */
    default <T> T get(@NonNull ConfigKey<T> key) {
        return get(key, null);
    }

    /**
     * 获取配置数据
     *
     * @param key 要读取的字段
     * @param def 字段为空时的返回值
     * */
    <T> T get(@NonNull ConfigKey<T> key, @Nullable T def);

    /**
     * 修改配置数据
     *
     * @param key 目标字段
     * @param value 新值
     * */
    <T> void set(@NonNull ConfigKey<T> key, @Nullable T value);

    /**
     * 将配置对象在内存中已进行过的所有修改保存到 源配置文件 中，并不在失败时重试
     *
     * @throws ConfigException 操作过程中出现问题时抛出
     * */
    default void save() throws ConfigException {
        saveTo(new File(getSource().fileName()), 0);
    };

    /**
     * 将配置对象在内存中已进行过的所有修改保存到 指定文件 中
     *
     * @param targetFile 要将配置数据保存到的文件
     * @param maxRetries 最大重试次数
     * */
    void saveTo(@NonNull File targetFile, int maxRetries) throws ConfigException;

    /**
     * 原子性地保存配置对象在内存中已进行过的所有修改（可选能力）
     *
     * @throws ConfigException 操作过程中出现问题时抛出
     * */
    default void saveAtomic() throws ConfigException {
        saveAtomic(new File(getSource().fileName()));
    }

    /**
     * 原子性地保存配置对象在内存中已进行过的所有修改到指定文件中（可选能力）
     *
     * @param targetFile 要将配置数据保存到的文件
     *
     * @throws ConfigException 操作过程中出现问题时抛出
     * */
    default void saveAtomic(@NonNull File targetFile) throws ConfigException {
        throw new ConfigException("配置 '" + getClass().getSimpleName() + "' 不支持原子保存", null);
    }

    /**
     * 重新从磁盘读取数据到配置实例中，这将完全用硬盘中的字段数据替换内存中的字段数据（可选能力）
     *
     * @throws ConfigException 操作过程中出现问题时抛出
     * */
    default void reload() throws ConfigException {
        throw new ConfigException("配置 '" + getClass().getSimpleName() + "' 不支持重新加载", null);
    };
}