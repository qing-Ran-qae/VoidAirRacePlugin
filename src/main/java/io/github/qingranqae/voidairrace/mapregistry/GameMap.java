package io.github.qingranqae.voidairrace.mapregistry;

import io.github.qingranqae.voidairrace.match.Match;
import net.kyori.adventure.text.Component;

public interface GameMap {
    /**
     * 获取地图标识符
     *
     * 此方法返回值应始终固定且不应和其他地图重复
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
     * 检查地图是否可作为比赛场地
     *
     * @return 地图是否可作为比赛场地
     * */
    boolean isPlayable();

    /**
     * 在 使用此地图的比赛 开始时执行
     * */
    default void selectedStart(Match match) {};

    /**
     * 在 使用此地图的比赛 结束时进行
     * */
    default void selectedOver(Match match) {};
}
