package io.github.hhn756.voidairrace.core.map;

import io.github.hhn756.voidairrace.core.match.Match;
import io.github.hhn756.voidairrace.core.result.base.OperationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;

/**
 * 可作为比赛场地（即可游玩）的地图的基类<br>
 * 注：每局比赛都会使用不同地图实例，如需跨对局共享数据可以使用静态属性或其他类来储存数据
 * */
public abstract class PlayableGameMap extends GameMap {
    /**
     * 检测地图是否已准备好开始游戏
     * */
    public abstract boolean isReady();

    /**
     * 在 使用此地图的比赛 开始时执行
     * 如果返回的对象{@link StartResult#isSuccess()}返回{@code false}那么会导致地图组件启用失败和比赛开始失败
     * */
    public @NonNull StartResult start(@NonNull Match match) {
        return StartResult.success();
    }

    /**
     * 在 使用此地图的比赛 结束时进行
     * */
    public @NonNull OverResult over(@NonNull Match match) {
        return OverResult.success();
    };

    /**
     * 获取地图允许参赛的最大队伍数量
     *
     * @return 最大队伍数量
     * */
    @Range(from = 1, to = Integer.MAX_VALUE)
    public abstract int maxTeams();

    // ------ 结果类型 ------

    public static class StartResult extends OperationResult {
        public StartResult(boolean success, @Nullable Component displayMessage) {
            super(success, displayMessage);
        }

        public static StartResult success() {
            return new StartResult(true, null);
        }

        public static StartResult failure(Component displayMessage) {
            return new StartResult(false, displayMessage);
        }
    }

    public static class OverResult extends OperationResult {
        public OverResult(boolean success, @Nullable Component displayMessage) {
            super(success, displayMessage);
        }

        public static OverResult success() {
            return new OverResult(true, null);
        }

        public static OverResult failure(Component displayMessage) {
            return new OverResult(false, displayMessage);
        }
    }
}
