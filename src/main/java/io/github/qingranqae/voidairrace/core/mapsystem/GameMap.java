package io.github.qingranqae.voidairrace.core.mapsystem;

import net.kyori.adventure.text.Component;

/**
 * 游戏地图必须实现此接口<br>
 * 用于获取地图基本信息和控制地图初始化
 * */
public interface GameMap {
    /**
     * 获取地图标识符<br>
     * 此方法返回值应始终固定且和其他地图重复
     *
     * @return 地图标识符
     * */
    String getId();

    /**
     * 获取地图显示名称
     *
     * @return 地图的显示名称
     * */
    Component getDisplayName();

    /**
     * 获取地图描述文本
     *
     * @return 地图的描述文本
     * */
    Component getDescription();

    /**
     * 插件首次启用或执行重新初始化命令时执行
     * */
    default void init() {}

    /**
     * 在重新初始化前执行一次<br>
     * 注：需通过地图初始化器重初始化，否则此方法不会被自动钓鱼
     * */
    default void unInit() {}
}
