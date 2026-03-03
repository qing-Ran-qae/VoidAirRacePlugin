package io.github.qingranqae.voidairrace.core.mapsystem;

import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import org.jetbrains.annotations.Range;

/**
 * 可作为比赛场地（即可游玩）的地图需实现此接口<br>
 * 注：每局比赛都会使用不同地图实例，如需跨对局共享数据可以使用静态属性或其他类来储存数据
 * */
public interface PlayableGameMap extends GameMap {
    /**
     * 检测地图是否已准备好开始游戏
     * */
    boolean isReady();

    /**
     * 在 使用此地图的比赛 开始时执行
     * */
    void selectedStart(Match match);

    /**
     * 在 使用此地图的比赛 结束时进行
     * */
    void selectedOver(Match match);

    /**
     * 获取地图允许参赛的最大队伍数量
     *
     * @return 最大队伍数量
     * */
    @Range(from = 2, to = Integer.MAX_VALUE)
    int maxTeamsNumber();
}
