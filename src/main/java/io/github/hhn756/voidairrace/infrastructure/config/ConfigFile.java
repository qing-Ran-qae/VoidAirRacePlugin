package io.github.hhn756.voidairrace.infrastructure.config;

/**
 * 代表一个已定义的配置文件，包含文件名和字段列表
 *
 * @param fileName 配置文件名/路径（不含扩展名）
 * @param keys 该配置文件中定义的所有配置键
 * */
public record ConfigFile(
        String fileName,
        ConfigKey<?>[] keys
) {}
