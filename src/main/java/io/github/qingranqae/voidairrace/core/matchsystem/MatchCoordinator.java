package io.github.qingranqae.voidairrace.core.matchsystem;

import io.github.qingranqae.voidairrace.core.result.match.CoordinatorStartMatchResult;
import io.github.qingranqae.voidairrace.core.result.match.CoordinatorStopMatchResult;
import io.github.qingranqae.voidairrace.service.config.Config;
import io.github.qingranqae.voidairrace.service.config.ObservableYamlConfiguration;
import io.github.qingranqae.voidairrace.service.config.files.FlagsKeys;
import io.github.qingranqae.voidairrace.service.config.files.PublicFiles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 比赛协调器，负责管理比赛的生命周期（开始、结束、状态查询）。
 * 单例模式，通过 {@link #getInstance()} 获取实例，必须先调用 {@link #getInstance(JavaPlugin)} 进行初始化。
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
    public static MatchCoordinator getInstance(JavaPlugin mainClass) {
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
    private final JavaPlugin mainClass;

    /**
     * 私有构造器，初始化比赛状态为 {@link MatchState#SCHEDULED}。
     *
     * @param mainClass 插件主类实例
     */
    private MatchCoordinator(JavaPlugin mainClass) {
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
     * 开始一局新比赛<br>
     * 如果当前所选地图是 bukkit 事件监听器，那么会自动向 bukkit 注册 它
     */
    public CoordinatorStartMatchResult startMatch(MatchConfig matchConfig) {
        // 检查必须条件（防止前面某阶段的检查漏条件）
        if (this.matchState != MatchState.SCHEDULED) return CoordinatorStartMatchResult.failure(
                Component.translatable("void_air_race.match.match_coordinator.start_match.invalid_match_state"));
        if (!(matchConfig.gameMap().isReady())) return CoordinatorStartMatchResult.failure(
                Component.translatable("void_air_race.match.match_coordinator.start_match.selected_not_ready"));

        // 更新状态
        this.matchState = MatchState.STARTING;

        // 设置标志，指示服务器启动时需要强制结束比赛（防止意外关闭导致状态不一致）
        setAndSaveStopFlag(true);

        // 创建比赛对象
        this.currentMatch = new Match(matchConfig, mainClass);

        // 执行比赛开始逻辑
        this.currentMatch.onStart();

        // 更新状态为进行中
        this.matchState = MatchState.IN_PROGRESS;
        return CoordinatorStartMatchResult.success();
    }

    /**
     * 结束当前进行的比赛<br>
     * 如果当前比赛所选地图是 bukkit 事件监听器，那么会自动向 bukkit 注销 它
     *
     * @param mandatory 是否强制结束。如果为 false，则只有在状态为 {@link MatchState#IN_PROGRESS} 时才能结束；
     *                  如果为 true，则无论当前状态如何都会尝试结束（但会跳过某些检查）。
     */
    public CoordinatorStopMatchResult stopMatch(boolean mandatory) {
        if ((this.matchState != MatchState.IN_PROGRESS) && !mandatory) return CoordinatorStopMatchResult.failure(
                Component.translatable("void_air_race.match.match_coordinator.stop_match.invalid_match_state")
                        .color(NamedTextColor.RED));

        // 更新状态为结束中
        this.matchState = MatchState.ENDING;

        // 设置标志，指示服务器启动时需要强制结束比赛（防止意外关闭导致状态不一致）
        setAndSaveStopFlag(true);

        try { // TODO：待修正意外重启逻辑
            // 执行比赛结束逻辑
            this.currentMatch.onOver();
        } catch (NullPointerException ignored) {}

        this.currentMatch = null;

        // 更新状态为已调度（空闲）
        this.matchState = MatchState.SCHEDULED;

        // 重置启动时强制结束的标志
        setAndSaveStopFlag(false);

        return CoordinatorStopMatchResult.success();
    }

    /**
     * 获取当前进行中比赛的实例
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
        ObservableYamlConfiguration flags = configInst.getConfig(PublicFiles.FLAGS);
        flags.set(FlagsKeys.ON_SERVER_STARTED_STOP_MATCH, newValue);
        configInst.saveOneConfig(PublicFiles.FLAGS.getFileName(), flags, 3);
    }

    public boolean matchIsRunning() {
        return this.matchState != MatchState.SCHEDULED;
    }
}