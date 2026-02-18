package io.github.qingranqae.voidairrace.corelayer.matchsystem;

import io.github.qingranqae.voidairrace.event.MatchOverEvent;
import io.github.qingranqae.voidairrace.event.MatchStartedEvent;
import io.github.qingranqae.voidairrace.exception.map.MapNotPlayableException;
import io.github.qingranqae.voidairrace.exception.match.InvalidMatchStateException;

/**
 * 管理比赛进行状态
 * */
public class MatchCoordinator {
    private static MatchCoordinator instance;

    public static MatchCoordinator getInstance() {
        if (instance == null) { instance = new MatchCoordinator(); }
        return instance;
    }

    private MatchState matchState;
    private Match currentMatch;

    private MatchCoordinator() {
        this.matchState = MatchState.SCHEDULED;
    }

    public MatchState getMatchState() {
        return this.matchState;
    }

    /**
     * 开始一局比赛
     *
     * @param matchConfig 这局比赛所使用的配置
     * */
    public void startMatch(MatchConfig matchConfig) throws InvalidMatchStateException, MapNotPlayableException {
        if (this.matchState != MatchState.SCHEDULED) {
            throw new InvalidMatchStateException("比赛已在进行，无法重复开始");
        }
        if (matchConfig.gameMap() == null) {
            throw new NullPointerException("比赛地图为空，无法开始比赛");
        }
        if (!matchConfig.gameMap().isPlayable()) {
            throw new MapNotPlayableException("选中的地图不可游玩，因此无法用作比赛地图");
        }

        // 更新状态
        this.matchState = MatchState.STARTING;

        // 创建 比赛对象
        this.currentMatch = new Match(matchConfig);

        // 调用选中地图 开始 方法
        this.currentMatch.getConfig().gameMap().selectedStart(this.currentMatch);

        // 发布事件
        new MatchStartedEvent(this.currentMatch).callEvent();

        // 更新状态
        this.matchState = MatchState.IN_PROGRESS;
    }

    /**
     * 结束当前比赛
     *
     * @param mandatory 是否要无视当前比赛状态强行结束
     * */
    public void stopMatch(boolean mandatory) throws InvalidMatchStateException {
        if (this.matchState != MatchState.IN_PROGRESS && !mandatory) {
            throw new InvalidMatchStateException("当前比赛状态不是“进行中”，无法结束比赛");
        }

        // 更新状态
        this.matchState = MatchState.ENDING;

        // 调用选中地图 结束 方法
        this.currentMatch.getConfig().gameMap().selectedOver(this.currentMatch);

        // 发布事件
        new MatchOverEvent(this.currentMatch).callEvent();

        // 销毁 比赛对象
        this.currentMatch = null;

        // 更新状态
        this.matchState = MatchState.SCHEDULED;
    }

    public Match getCurrentMatch() {
        return this.currentMatch;
    }
}