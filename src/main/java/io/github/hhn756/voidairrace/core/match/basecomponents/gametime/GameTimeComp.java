package io.github.hhn756.voidairrace.core.match.basecomponents.gametime;

import io.github.hhn756.voidairrace.VoidAirRace;
import io.github.hhn756.voidairrace.constants.TranslateKeys;
import io.github.hhn756.voidairrace.core.match.ComponentPriority;
import io.github.hhn756.voidairrace.core.match.DataKey;
import io.github.hhn756.voidairrace.core.match.Match;
import io.github.hhn756.voidairrace.core.match.MatchConfig;
import io.github.hhn756.voidairrace.core.match.componentbase.*;
import io.github.hhn756.voidairrace.event.MatchStatusChangedEvent;
import io.github.hhn756.voidairrace.infrastructure.config.Config;
import io.github.hhn756.voidairrace.infrastructure.config.files.GameSettingKeys;
import io.github.hhn756.voidairrace.infrastructure.config.files.PublicFiles;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * 游戏时间管理组件：维护比赛剩余时间，提供时间变更事件
 */
public class GameTimeComp extends MatchComp
        implements StartableComp<GameTimeComp.GameTimeSA, CustomData>,
        EndableComp<CustomData, CustomData>,
        ConfigurableComp<GameTimeComp.GameTimeECFG, GameTimeComp.GameTimeConfig> {

    // ==================== 组件内部状态 ====================

    private int remaining; // tick
    private Match match;
    /**
     * 剩余游戏时间是否随tick减少
     * */
    private boolean timeFlow = true;
    private BukkitTask tickTask;

    // ==================== ConfigurableComp 实现 ====================

    public static final DataKey<GameTimeConfig> CONFIG_KEY = DataKey.of(GameTimeComp.class, GameTimeConfig.class);

    @Override
    public DataKey<GameTimeConfig> getConfigKey() {
        return CONFIG_KEY;
    }

    @Override
    public @NonNull CustomConfigResult<GameTimeConfig> createCustomConfig(@Nullable GameTimeECFG expected) {
        int duration = 12000; // 默认10分钟（20ticks/sec * 60sec * 10 = 12000）;
        if (expected != null) {
            // 尝试读取预期配置
            duration = expected.expectedDuration();
        } else {
            // 尝试读取配置文件
            Integer configValue = Config.getInstance()
                    .getYmlConfig(PublicFiles.GAME_SETTINGS)
                    .get(GameSettingKeys.MATCH_DURATION,null);
            if (configValue != null) {
                duration = configValue;
            }
        }

        return CustomConfigResult.success(new GameTimeConfig(duration));
    }

    @Override
    public @NonNull DefaultConfigResult<GameTimeConfig> createDefaultConfig() {
        return DefaultConfigResult.success(new GameTimeConfig(12000));
    }

    public MatchConfig.@NonNull ValidationConfigResult validateConfig(@NonNull GameTimeConfig config) {
        if (config.duration() <= 0) {
            return MatchConfig.ValidationConfigResult.failure(
                    Component.translatable(TranslateKeys.BaseComponents.GAME_TIME_COMP_INVALID_DURATION)
            );
        }
        return MatchConfig.ValidationConfigResult.success();
    }

    // ==================== StartableComp 实现 ====================

    @Override
    public StartableComp.@NonNull InstallResult<CustomData> install(@NonNull Match match, @Nullable GameTimeSA startArg) {
        this.match = match;
        GameTimeConfig config = match.getConfig().getData(GameTimeComp.CONFIG_KEY);
        if (config == null) {
            return new InstallResult<>(
                    false, Component.translatable(TranslateKeys.BaseComponents.GAME_TIME_COMP_NO_CONFIG),
                    null
            );
        }

        int initialTime = config.duration();
        if (startArg != null && startArg.initialRemaining() > 0) {
            initialTime = startArg.initialRemaining();
        }
        this.remaining = initialTime;

        tickTask = Bukkit.getScheduler().runTaskTimer(
                VoidAirRace.getInstance(),
                () -> {
                    if (timeFlow && remaining > 0) {
                        setRemaining(remaining - 1);
                    }
                },
                0L,
                1L
        );

        return InstallResult.success(new GameTimeSC(initialTime));
    }

    @Override
    public @Range(from = 0, to = Integer.MAX_VALUE) int getInstallPriority() {
        return ComponentPriority.HIGH.getValue();
    }

    // ==================== 公共 API ====================

    /**
     * @return 现在距离比赛结束的tick数
     * */
    public int getRemaining() {
        return remaining;
    }

    /**
     * 使比赛在指定tick后结束
     *
     * @param n 指定tick数
     * */
    public void setRemaining(@Range(from = 0, to = Integer.MAX_VALUE) int n) {
        int old = this.remaining;
        this.remaining = n;
        if (old != n && match != null) {
            new MatchStatusChangedEvent(match).callEvent();
        }
    }

    /**
     * @return 剩余游戏时间是否随tick减少
     * */
    public boolean isTimeFlow() {
        return timeFlow;
    }

    /**
     * 设置剩余游戏时间是否随tick减少
     *
     * @param newValue {@code true}减少；{@code false}不减少v
     * */
    public void setTimeFlow(boolean newValue) {
        this.timeFlow = newValue;
    }

    @Override
    public @NonNull ComponentUninstallResult<CustomData> uninstall(
            @NonNull Match match,
            @Nullable CustomData endArg) {
        tickTask.cancel();

        return new ComponentUninstallResult<>(false, null, null);
    }

    /**
     * @param duration 初始比赛时间（可由其他部分动态修改），比赛将在指定 tick 后结束
     */
    public static record GameTimeConfig(int duration) implements CustomData {
    
        @Override
        public @NonNull Class<? extends MatchComp> getSource() {
            return GameTimeComp.class;
        }
    }

    /**
     * @param expectedDuration tick
     */
    public static record GameTimeECFG(int expectedDuration) implements CustomData {
    
        @Override
        public @NonNull Class<? extends MatchComp> getSource() {
            return GameTimeComp.class;
        }
    }

    /**
     * @param initialRemaining 可以包含初始剩余时间的覆盖值
     */
    public static record GameTimeSA(int initialRemaining) implements CustomData {
    
        @Override
        public @NonNull Class<? extends MatchComp> getSource() {
            return GameTimeComp.class;
        }
    }

    public static record GameTimeSC(int startTime) implements CustomData {
    
        @Override
        public @NonNull Class<? extends MatchComp> getSource() {
            return GameTimeComp.class;
        }
    }
}
