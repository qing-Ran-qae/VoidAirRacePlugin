package io.github.hhn756.voidairrace.infrastructure.config;

import org.jspecify.annotations.NonNull;

/**
 * 代表一个已定义的配置文件，包含文件名和字段列表
 *
 * @param filePath 配置文件名/路径（不含扩展名）
 * @param keys     该配置文件中定义的所有配置键
 * */
public record ConfigDefinition(
        @NonNull String filePath,
        @NonNull ConfigKey<?>[] keys
) {}
