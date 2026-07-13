package io.github.hhn756.voidairrace.constants;

import org.bukkit.NamespacedKey;

/**
 * 插件在各处使用的命名空间值
 * */
public class Namespace {
    private Namespace() {}

    /** 蛇形风格、字符串形式的命名空间值 */
    public static final String str = "void_air_race";

    /**
     * @param key 指定键（资源名称/路径）
     *
     * @return 一个使用本插件命名空间和指定资源键的{@link NamespacedKey}
     * */
    public static NamespacedKey of(String key) {
        return new NamespacedKey(str, key);
    }
}
