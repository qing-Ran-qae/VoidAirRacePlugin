package io.github.hhn756.voidairrace.constants;

import io.github.hhn756.voidairrace.core.map.MapMeta;
import io.github.hhn756.voidairrace.infrastructure.registry.EntryCategory;
import org.bukkit.NamespacedKey;

/**
 * 插件内所有注册项类别
 * */
public class Categories {
    /**
     * 地图注册项分类，用地图 ID 作为主键
     * */
    public static final EntryCategory<MapMeta, NamespacedKey> MAP = new EntryCategory<>();
}
