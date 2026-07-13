package io.github.hhn756.voidairrace.service.config;

import org.jspecify.annotations.NonNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 代表一个已定义的配置键，包含路径和类型
 *
 * @param <T> 字段类型，用于让读读取对象的方法实现静态类型安全
 */
public class ConfigKey<T> {
    /**
     * 键路径
     * */
    private final String path;
    /**
     * 缓存提取出来的完整泛型类型
     */
    private final Type type;

    /**
     * 必须通过{@code new TypeToken<字段类型>(字段实际键路径){}}的形式实例化
     *
     * @param path 获取配置项在YAML文件中的路径（例如 {@code player_data}）
     * @param <T> 键（字段）的值类型
     * */
    protected <T> ConfigKey(@NonNull String path) {
        this.path = path;

        // 1. 获取当前对象（匿名子类）的带有泛型信息的直接父类
        Type superClass = getClass().getGenericSuperclass();

        // 2. 确保父类是一个带有泛型参数的类型 (ParameterizedType)
        if (superClass instanceof ParameterizedType pType) {
            // 3. 提取父类泛型参数列表中的第一个参数
            this.type = pType.getActualTypeArguments()[0];
        } else {
            throw new IllegalArgumentException(
                    "读取 TypeToken 中记录的字段类型失败"
            );
        }
    }

    /**
     * 获取键对象对应的物理键路径
     * */
    public String path() {
        return path;
    }

    /**
     * 获取字段的预期类型
     * */
    public Type type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigKey<?> configKey = (ConfigKey<?>) o;
        return Objects.equals(path, configKey.path)
                && Objects.equals(type, configKey.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, type);
    }
}
