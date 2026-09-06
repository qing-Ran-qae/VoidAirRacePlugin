package io.github.hhn756.voidairrace.core.match;

import io.github.hhn756.voidairrace.constants.TranslateKeys;
import io.github.hhn756.voidairrace.infrastructure.config.Config;
import io.github.hhn756.voidairrace.infrastructure.config.YamlConfig;
import io.github.hhn756.voidairrace.infrastructure.config.files.FlagsKeys;
import io.github.hhn756.voidairrace.infrastructure.config.files.PublicFiles;
import io.github.hhn756.voidairrace.result.base.OperationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

/**
 * 比赛协调器，负责管理比赛的进行，即{@link Match}实例（创建、开始游戏、结束游戏、状态查询）<br>
 * 单例模式，通过 {@link #getInstance()} 获取实例
 */
public class MatchCoordinator {
    private static MatchCoordinator instance;

    static void load() {
        instance = new MatchCoordinator();
    }

    static void unload() {
        instance = null;
    }

    /**
     * 获取协调器实例
     *
     * @return 协调器实例
     * @throws IllegalStateException 如果实例尚未初始化（未调用带参的 getInstance）
     */
    public static MatchCoordinator getInstance() throws NullPointerException {
        if (instance == null) throw new NullPointerException("比赛协调器实例不存在");
        return instance;
    }

    // -----------

    /** 当前正在进行的比赛，若无比赛则为 null */
    private @Nullable Match currentMatch = null;

    private MatchCoordinator() {}

    /**
     * 获取当前比赛状态
     *
     * @return 比赛状态
     */
    public @NonNull MatchState getMatchState() {
        if (currentMatch == null) return MatchState.SCHEDULED;
        return this.currentMatch.getState();
    }

    /**
     * 开始一局新比赛
     *
     * @param matchConfig 比赛所使用的配置
     */
    public StartResult startMatch(@Nullable MatchConfig matchConfig) {
        if (matchIsRunning()) {
            return StartResult.failure(Component.translatable(
                    TranslateKeys.Match.MATCH_COORDINATOR_START_MATCH_REPEAT_START
            ));
        }

        // 设置标志，指示服务器启动时需要强制结束比赛（防止意外关闭导致状态不一致）
        setAndSaveStopFlag(true);

        // 默认参数值
        if (matchConfig == null) {
            MatchConfig.DefaultConfigResult defaultConfigResult = MatchConfig.createDefault();
            matchConfig = defaultConfigResult.getValue();
            if (!defaultConfigResult.isSuccess() || matchConfig == null) {
                return StartResult.failure(
                        defaultConfigResult.getDisplayMessage() == null
                                ? Component.translatable(TranslateKeys.Match.START_START_FAILED_UNKNOWN_REASONS)
                                : Component.translatable(TranslateKeys.Match.START_START_FAILED_SPECIFIED_REASONS)
                                  .arguments(defaultConfigResult.getDisplayMessage())
                );
            }
        }

        // 创建比赛对象
        Match.CreateMatchResult createMatchResult = Match.create(matchConfig);
        Match match = createMatchResult.getValue();
        if (!createMatchResult.isSuccess() || match == null) {
            Component msg = createMatchResult.getDisplayMessage() == null
                    ? Component.translatable(TranslateKeys.Match.START_START_FAILED_UNKNOWN_REASONS)
                    : Component.translatable(
                    TranslateKeys.Match.START_START_FAILED_SPECIFIED_REASONS)
                      .arguments(createMatchResult.getDisplayMessage());
            return StartResult.failure(msg);
        }
        currentMatch = match;

        // 执行比赛开始逻辑
        Match.StartResult startMatchResult = currentMatch.start();
        if (!startMatchResult.isSuccess()) {
            Component msg = startMatchResult.getDisplayMessage() == null
                    ? Component.translatable(TranslateKeys.Match.MATCH_COORDINATOR_START_MATCH_FAILURE_UNKNOWN_CAUSE)
                    : Component.translatable(TranslateKeys.Match.MATCH_COORDINATOR_START_MATCH_FAILURE_SPECIFIED_CAUSE)
                      .arguments(startMatchResult.getDisplayMessage());
            return MatchCoordinator.StartResult.failure(msg);
        }
        return MatchCoordinator.StartResult.success();
    }

    /**
     * 使用默认参数开始比赛
     *
     * @see MatchCoordinator#startMatch(MatchConfig)
     */
    public StartResult startMatch() {
        return startMatch(null);
    }

    /**
     * 结束当前进行的比赛<br>
     * 如果当前比赛所选地图是 Bukkit 事件监听器，那么会自动向 Bukkit 注销 它
     */
    public StopResult stopMatch() {
        if (this.getMatchState() != MatchState.IN_PROGRESS) {
            return StopResult.failure(
                    Component.translatable(TranslateKeys.Match.MATCH_COORDINATOR_STOP_MATCH_INVALID_MATCH_STATE)
            );
        }

        // 设置标志，指示服务器启动时需要强制结束比赛（防止意外关闭导致状态不一致）
        setAndSaveStopFlag(true);

        // 执行比赛结束逻辑
        if (currentMatch != null) { // 这里理论上不会失败
            this.currentMatch.stop();
        }

        this.currentMatch = null;

        // 重置启动时强制结束的标志
        setAndSaveStopFlag(false);

        return StopResult.success();
    }

    /**
     * 获取当前进行中比赛的实例
     *
     * @return 当前比赛对象，若无比赛则返回 null
     */
    public @Nullable Match getCurrentMatch() {
        return this.currentMatch;
    }

    /**
     * 将 “标志” 配置文件中的 “当服务器启动时结束比赛” 标志设置为指定值，并保存配置文件
     * */
    private void setAndSaveStopFlag(boolean newValue) {
        Config configInst = Config.getInstance();
        YamlConfig flags = configInst.getYmlConfig(PublicFiles.FLAGS);
        flags.set(FlagsKeys.MATCH_ABORTED, newValue);
        configInst.save(flags, 3);
    }

    /**
     * 检查比赛是否正在进行<br>
     * 具体来说：如果此比赛的当前状态不为 {@link MatchState#SCHEDULED} 则返回 {@code true}，否则返回 {@code false}
     *
     * @return 比赛目前是否正在进行
     * */
    public boolean matchIsRunning() {
        return this.getMatchState() != MatchState.SCHEDULED;
    }

    // ------ 结果类型 ------

    public static final class StartResult extends OperationResult {
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

    public static final class StopResult extends OperationResult {
        public StopResult(boolean success, @Nullable Component displayMessage) {
            super(success, displayMessage);
        }

        public static StopResult success() {
            return new StopResult(true, null);
        }

        public static StopResult failure(Component displayMessage) {
            return new StopResult(false, displayMessage);
        }
    }
}
