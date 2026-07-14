package io.github.hhn756.voidairrace.infrastructure.registry;

import org.jspecify.annotations.NonNull;

/**
 * 注册表条目接口，实现类需提供唯一主键
 *
 * @param <K> 主键类型
 */
public interface Entry<K> {
    /**
     * @return 该条目的唯一主键，对同一对象始终返回相同值
     */
    @NonNull K getKey();
}
