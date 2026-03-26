package io.github.qingranqae.voidairrace.core.mapsystem;

import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import io.github.qingranqae.voidairrace.core.result.map.MapSelectedStartResult;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;

/**
 * 可作为比赛场地（即可游玩）的地图需实现此接口<br>
 * 注：每局比赛都会使用不同地图实例，如需跨对局共享数据可以使用静态属性或其他类来储存数据
 * */
public abstract class PlayableGameMap extends GameMap {
    /**
     * 检测地图是否已准备好开始游戏
     * */
    public abstract boolean isReady();

    /**
     * 在 使用此地图的比赛 开始时执行
     *
     * @return 如果返回的对象{@link MapSelectedStartResult#isSuccess()}返回{@code false}那么将会取消这次启用规则操作
     * */
    public @NonNull MapSelectedStartResult selectedStart(Match match) {
        return MapSelectedStartResult.success();
    };

    /**
     * 在 使用此地图的比赛 结束时进行
     * */
    public void selectedOver(Match match) {};

    /**
     * 获取地图允许参赛的最大队伍数量
     *
     * @return 最大队伍数量
     * */
    @Range(from = 2, to = Integer.MAX_VALUE)
    public abstract int maxTeams();
}
