package io.github.qingranqae.voidairrace.core.matchsystem;

import io.github.qingranqae.voidairrace.VoidAirRace;
import io.github.qingranqae.voidairrace.core.config.Config;
import io.github.qingranqae.voidairrace.core.config.ConfigFiles;
import io.github.qingranqae.voidairrace.core.config.FlagsKey;
import io.github.qingranqae.voidairrace.core.config.ObservableYamlConfiguration;
import io.github.qingranqae.voidairrace.event.MatchOverEvent;
import io.github.qingranqae.voidairrace.event.MatchStartedEvent;
import io.github.qingranqae.voidairrace.exception.InvalidMatchStateException;
import io.github.qingranqae.voidairrace.exception.MapNotPlayableException;

/**
 * 比赛协调器，负责管理比赛的生命周期（开始、结束、状态查询）。
 * 单例模式，通过 {@link #getInstance()} 获取实例，必须先调用 {@link #getInstance(VoidAirRace)} 进行初始化。
 */
public class MatchCoordinator {
    private static MatchCoordinator instance;

    /**
     * 获取协调器实例。
     *
     * @return 协调器实例
     * @throws NullPointerException 如果实例尚未初始化（未调用带参的 getInstance）
     */
    public static MatchCoordinator getInstance() {
        if (instance == null) {
            throw new NullPointerException("比赛管理器类还未初始化，无法获取实例");
        }
        return instance;
    }

    /**
     * 初始化协调器并获取实例。
     *
     * @param mainClass 插件主类实例
     * @return 协调器实例
     */
    public static MatchCoordinator getInstance(VoidAirRace mainClass) {
        if (instance == null) {
            instance = new MatchCoordinator(mainClass);
        }
        return instance;
    }

    // ------------

    /** 当前比赛的状态。 */
    private MatchState matchState;

    /** 当前正在进行的比赛对象，若无比赛则为 null。 */
    private Match currentMatch = null;

    /** 插件主类实例，用于调度任务和事件。 */
    private final VoidAirRace mainClass;

    /**
     * 私有构造器，初始化比赛状态为 {@link MatchState#SCHEDULED}。
     *
     * @param mainClass 插件主类实例
     */
    private MatchCoordinator(VoidAirRace mainClass) {
        this.matchState = MatchState.SCHEDULED;
        this.mainClass = mainClass;
    }

    /**
     * 获取当前比赛状态。
     *
     * @return 比赛状态
     */
    public MatchState getMatchState() {
        return this.matchState;
    }

    /**
     * 开始一局新比赛。
     *
     * @param matchConfig 比赛的配置信息
     * @throws InvalidMatchStateException 如果当前状态不是 {@link MatchState#SCHEDULED}（即已有比赛在进行）
     * @throws MapNotPlayableException    如果配置中的地图不可游玩
     * @throws NullPointerException       如果配置中的地图为 null
     */
    public void startMatch(MatchConfig matchConfig) throws InvalidMatchStateException, MapNotPlayableException, NullPointerException {
        // 检查必须条件
        if (this.matchState != MatchState.SCHEDULED) {
            throw new InvalidMatchStateException("比赛已在进行，无法重复开始");
        }
        if (matchConfig.gameMap() == null) {
            throw new NullPointerException("比赛地图为空，无法开始比赛");
        }

        // 更新状态
        this.matchState = MatchState.STARTING;

        // 设置标志，指示服务器启动时需要强制结束比赛（防止意外关闭导致状态不一致）
        setAndSaveStopFlag(true);

        // 创建比赛对象
        this.currentMatch = new Match(matchConfig, mainClass);

        // 调用选中地图的 selectedStart 方法
        this.currentMatch.getConfig().gameMap().selectedStart(this.currentMatch);

        // 发布比赛开始事件
        new MatchStartedEvent(this.currentMatch).callEvent();

        // 启动规则管理器
        currentMatch.getRuleManager().setup();

        // 更新状态为进行中
        this.matchState = MatchState.IN_PROGRESS;
    }

    /**
     * 结束当前进行的比赛。
     *
     * @param mandatory 是否强制结束。如果为 false，则只有在状态为 {@link MatchState#IN_PROGRESS} 时才能结束；
     *                  如果为 true，则无论当前状态如何都会尝试结束（但会跳过某些检查）。
     * @throws InvalidMatchStateException 当 mandatory 为 false 且当前状态不是 {@link MatchState#IN_PROGRESS} 时抛出
     */
    public void stopMatch(boolean mandatory) throws InvalidMatchStateException {
        if ((this.matchState != MatchState.IN_PROGRESS) && !mandatory) {
            throw new InvalidMatchStateException("当前比赛状态不是“进行中”，无法结束比赛");
        }

        // 更新状态为结束中
        this.matchState = MatchState.ENDING;

        // 设置标志，指示服务器启动时需要强制结束比赛（防止意外关闭导致状态不一致）
        setAndSaveStopFlag(true);

        try {
            // 调用选中地图的 selectedOver 方法
            this.currentMatch.getConfig().gameMap().selectedOver(this.currentMatch);

            // 关闭规则管理器
            this.currentMatch.getRuleManager().shutdown();

            // 发布比赛结束事件
            new MatchOverEvent(this.currentMatch).callEvent();

            // 销毁比赛对象
            this.currentMatch = null;
        } catch (NullPointerException ignored) {}

        // 更新状态为已调度（空闲）
        this.matchState = MatchState.SCHEDULED;

        // 重置启动时强制结束的标志
        setAndSaveStopFlag(false);
    }

    /**
     * 获取当前正在进行的比赛对象。
     *
     * @return 当前比赛对象，若无比赛则返回 null
     */
    public Match getCurrentMatch() {
        return this.currentMatch;
    }

    /**
     * 将 “标志” 配置文件中的 “当服务器启动时结束比赛” 标志设置为指定值，
     * 并保存配置文件
     * */
    private void setAndSaveStopFlag(boolean newValue) {
        Config configInst = Config.getInstance();
        ObservableYamlConfiguration flags = configInst.getConfig(ConfigFiles.FLAGS);
        flags.set(FlagsKey.ON_SERVER_STARTED_STOP_MATCH, newValue);
        configInst.saveOneConfig(ConfigFiles.FLAGS.getFileName(), flags, 3);
    }
}