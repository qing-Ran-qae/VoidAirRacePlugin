package io.github.hhn756.voidairrace.core.match;

import org.jspecify.annotations.NonNull;

/**
 * 代表一个比赛组件对应的注册项，用于在注册表中记录组件
 * */
public class CompRecord {
    private final @NonNull Class<?> compClass;

    /**
     * @param compClass 此注册项所记录的组件类型
     * */
    public CompRecord(@NonNull Class<?> compClass) {
        this.compClass = compClass;
    }

    public @NonNull Class<?> getKey() {
        return compClass;
    }
}
