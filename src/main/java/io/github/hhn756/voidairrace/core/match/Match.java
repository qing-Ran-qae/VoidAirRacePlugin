package io.github.hhn756.voidairrace.core.match;

import io.github.hhn756.voidairrace.VoidAirRace;
import io.github.hhn756.voidairrace.constants.TranslateKeys;
import io.github.hhn756.voidairrace.core.match.componentbase.*;
import io.github.hhn756.voidairrace.event.MatchOverEvent;
import io.github.hhn756.voidairrace.event.MatchStartedEvent;
import io.github.hhn756.voidairrace.infrastructure.config.Config;
import io.github.hhn756.voidairrace.infrastructure.config.ConfigDefinition;
import io.github.hhn756.voidairrace.infrastructure.config.ConfigKey;
import io.github.hhn756.voidairrace.infrastructure.config.YamlConfig;
import io.github.hhn756.voidairrace.infrastructure.config.files.PublicFiles;
import io.github.hhn756.voidairrace.result.base.OperationResult;
import io.github.hhn756.voidairrace.result.base.ValueResult;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 代表一局正在进行或已安排的比赛<br>
 * 包含比赛的配置、所用的组件等数据
 */
public class Match {
    /** 标记此赛实例是否已进行过游戏，防止复用实例 */
    private boolean used = false;

    /** 比赛状态 */
    private MatchState state = MatchState.SCHEDULED;

    /** 比赛所用的配置 */
    private final MatchConfig config;

    /** 比赛记录 */
    private static final ConfigDefinition recordFile = new ConfigDefinition(
            PublicFiles.TEMP_DIR + "match_record",
            new ConfigKey<?>[0]
    );
    private YamlConfig recordInst;

    /**
     * 构造一场新的比赛（不会自动开始）
     *
     * @param config 指定新比赛所用配置
     */
    private Match(@NonNull MatchConfig config) {
        this.config = config;
    }

    /**
     * 构造一场新的比赛（不会自动开始）
     *
     * @param config 指定新比赛所用配置
     */
    public static CreateMatchResult create(@NonNull MatchConfig config) {
        // 防止重复使用配置实例
        if (config.isUsed()) return CreateMatchResult.failure(
                Component.translatable(TranslateKeys.Match.CREATE_CONFIG_IS_USED)
        );
        config.use();

        return CreateMatchResult.success(new Match(config));
    }

    // ---------- 开始比赛 ----------

    /**
     * 使比赛开始
     *
     * @param args 传递给所有组件的参数。组件收到的参数可以为{@code null}（即传入列表不包含对应组件的参数）<br>
     *             此方法会将每个参数对象传递给其{@link CustomData#getSource()}返回类型的组件
     * */
    public @NonNull StartResult start(@Nullable CustomData... args) {
        // 如果比赛已开始
        if (state != MatchState.SCHEDULED) return StartResult.failure(
                Component.translatable(TranslateKeys.Match.START_INVALID_STATE));
        // 如果比赛实例已使用过
        if (used) return StartResult.failure(
                Component.translatable(TranslateKeys.Match.START_INSTANCE_IS_USED));

        // 标记使用
        used = true;

        // 更新状态
        state = MatchState.IN_PROGRESS;

        // 创建记录
        createRecord();

        // 安装组件
        InstallComponentsResult installComponentsResult = installComponents(args);
        if (!installComponentsResult.isSuccess()) {
            return StartResult.failure(
                    installComponentsResult.getDisplayMessage() // 安装组件方法失败时必返回消息
            );
        }
        Map<DataKey<?>, CustomData> startContext = installComponentsResult.getValue();

        new MatchStartedEvent(this, startContext).callEvent();
        return StartResult.success();
    }

    /**
     * 内部方法，开始比赛流程的一部分。用于创建比赛记录
     * */
    private void createRecord() {
        recordInst = Config.getInstance().getYmlConfig(recordFile);
    }

    /**
     * 内部方法，开始比赛流程的一部分。用于安装比赛组件
     *
     * @param args 传递给各组件的开始参数
     * */
    InstallComponentsResult installComponents(@Nullable CustomData... args) {
        // 开始上下文
        Map<DataKey<?>, CustomData> startContext = new HashMap<>();
        // 参数映射
        HashMap<Class<? extends MatchComp>, CustomData> argMap = new HashMap<>();
        if (args != null) {
            for (CustomData arg : args) {
                if (arg != null) { // 过滤 null 参数
                    argMap.put(arg.getSource(), arg);
                }
            }
        }

        // 按安装优先级降序排序（优先级高的先安装）
        List<? extends StartableComp<?, ?>> sortedComponents = config.getAllComponents().values().stream()
                .filter(comp -> comp instanceof StartableComp<?,?>)
                .map(comp -> (StartableComp<?,?>) comp)
                .sorted((a, b)
                        -> Integer.compare(b.getInstallPriority(), a.getInstallPriority()))
                .toList();

        List<StartableComp<?, ?>> installed = new ArrayList<>();
        for (StartableComp<?, ?> component : sortedComponents) {
            CustomData arg = argMap.get(component.getClass());
            boolean success = installComponentSafely(component, arg, installed, startContext);
            if (!success) {
                // 安装失败后逆安装顺序卸载已安装的组件
                rollback(installed, this);
                return InstallComponentsResult.failure(
                        Component.translatable(TranslateKeys.Match.START_INSTALL_FAILED));
            }
        }

        return InstallComponentsResult.success(startContext);
    }

    /**
     * 内部方法。尝试安装一个指定组件
     *
     * @param startable 要安装的组件
     *
     * @return 组件是否安装成功
     * */
    @SuppressWarnings("unchecked")
    private boolean installComponentSafely(
            StartableComp<?, ?> startable,
            CustomData arg,
            List<StartableComp<?, ?>> installed,
            Map<DataKey<?>, CustomData> startContext) {

        StartableComp.InstallResult<?> result = null;
        Exception thrownException = null;
        try {
            @SuppressWarnings({"rawtypes", "unchecked"})
            StartableComp.InstallResult<?> tmp = ((StartableComp) startable).install(this, arg);
            result = tmp;
        } catch (Exception e) {
            thrownException = e;
        }

        // 检查是否失败（失败结果或抛出异常）
        boolean failed = thrownException != null || !result.isSuccess();
        if (failed) {
            // 安装失败，回滚并传递失败原因或错误信息
            rollbackOne(startable, arg, result, thrownException);
            return false;
        }

        // 安装成功
        installed.add(startable);
        CustomData ctx = result.getStartContext();
        if (ctx != null) {
            startContext.put(startable.getSCK(), ctx);
        }
        return true;
    }

    /**
     * 内部方法，用于在组件加载失败后捕获{@link StartableComp}类型组件的开始参数类型以执行回滚操作
     * */
    @SuppressWarnings("unchecked")
    private <SA extends CustomData, SC extends CustomData>void rollbackOne(
            StartableComp<SA, SC> startable,
            CustomData installArg,
            StartableComp.InstallResult<?> installResult,
            Exception exceptionOfInstall) {
        startable.rollback(
                this,
                (SA) installArg,
                (StartableComp.InstallResult<SC>) installResult,
                exceptionOfInstall
        );
    }

    /**
     * 内部方法，用于在开始游戏加载组件失败后逆向卸载已安装的组件
     * */
    private void rollback(List<StartableComp<?, ?>> installed, Match match) {
        // 按安装的相反顺序卸载
        for (int i = installed.size() - 1; i >= 0; i--) {
            StartableComp<?, ?> startableComp = installed.get(i);
            try {
                // 调用正常的 uninstall 进行清理（不关心返回值，忽略失败）
                if (startableComp instanceof EndableComp<?,?> endableComp) {
                    endableComp.uninstall(match, null);
                }
            } catch (Exception e) {
                if (startableComp instanceof MatchComp matchComp) {
                    // 记录日志，继续执行
                    VoidAirRace.getInstance().getLogger().warning(
                            "回滚时卸载组件 '"
                                    + matchComp.getMeta().names()
                                    + "' 失败：" + e.getMessage());
                }
            }
        }
    }

    // ---------- 结束比赛 ----------

    /**
     * 使比赛结束
     *
     * @param args 传递给所有组件的结束参数。组件收到的参数可以为 {@code null}（即传入列表不包含对应组件的参数）。
     *             此方法会将每个参数对象传递给其 {@link CustomData#getSource()} 返回类型的组件
     */
    public @NonNull StopResult stop(@Nullable CustomData... args) {
        // 如果未开始
        if (state != MatchState.IN_PROGRESS) return StopResult.failure(
                Component.translatable(TranslateKeys.Match.STOP_INVALID_STATE));

        // 结束上下文
        Map<DataKey<?>, CustomData> endContext = new HashMap<>();

        // 参数映射
        HashMap<Class<? extends MatchComp>, CustomData> argMap = new HashMap<>();
        if (args != null) {
            for (CustomData arg : args) {
                if (arg != null) {
                    argMap.put(arg.getSource(), arg);
                }
            }
        }

        // 按卸载优先级降序排序（优先级高的先卸载）
        List<? extends EndableComp<?, ?>> sortedComponents = config.getAllComponents().values().stream()
                .filter(comp -> comp instanceof EndableComp<?, ?>)
                .map(comp -> (EndableComp<?, ?>) comp)
                .sorted((a, b)
                        -> Integer.compare(b.getUninstallPriority(), a.getUninstallPriority()))
                .toList();

        for (EndableComp<?, ?> endable : sortedComponents) {
            uninstallOne(endable, argMap, endContext);
            // 卸载失败也继续执行，不阻塞其他组件卸载
        }

        state = MatchState.SCHEDULED;
        new MatchOverEvent(this, endContext).callEvent();
        return StopResult.success();
    }

    /**
     * 内部方法，用于在比赛结束时卸载单个组件
    */
    @SuppressWarnings("unchecked")
    private <EA extends CustomData, EC extends CustomData> void uninstallOne(
            EndableComp<EA, EC> component,
            HashMap<Class<? extends MatchComp>, CustomData> argMap,
            Map<DataKey<?>, CustomData> endContext
    ) {
        Logger logger = VoidAirRace.getInstance().getLogger();
        // 获取该组件对应的结束参数（可能为 null）
        CustomData arg = argMap.get(component.getClass());
        EndableComp.ComponentUninstallResult<?> result = null;
        Component compName = null;
        // 一定满足条件
        if (component instanceof MatchComp matchComp) {
            compName = matchComp.getMeta().mainName();
        }

        try {
            // 直接调用 uninstall，不处理过程中的问题（组件自行记录日志或处理）
            result = component.uninstall(this, (EA) arg);
            if (!result.isSuccess()) {
                logger.warning(
                        "'卸载比赛组件 '"
                        + compName
                        + "' 失败"
                );
            }
        } catch (Exception e) {
            logger.warning(
                    "卸载比赛组件 '"
                            + compName
                            + "' 时发生异常：" + e.getMessage());
        }
        if (result != null && result.isSuccess()) {
            CustomData ctx = result.getEndContext();
            if (ctx != null) {
                DataKey<?> key = component.getECK();
                endContext.put(key, ctx);
            }
        }
    }

    // ---------- API ----------

    /**
     * 获取比赛所用的配置
     *
     * @return 比赛配置对象
     */
    public MatchConfig getConfig() {
        return config;
    }

    /**
     * 获取比赛中的比赛组件实例
     *
     * @see MatchConfig#getComp(Class)
     * */
    public <C extends MatchComp> @NonNull C getComp(@NonNull Class<C> componentClass) {
        return getConfig().getComp(componentClass);
    }

    /**
     * 获取比赛配置中的数据
     *
     * @see MatchConfig#getData(ConfigurableComp)
     * */
    public <K extends CustomData> K getConfigData(ConfigurableComp<?, K> component) {
        return config.getData(component.getConfigKey());
    }
    
    /**
     * 获取比赛配置中的数据
     * 
     * @see MatchConfig#getData(DataKey)
     * */
    public @Nullable <K extends CustomData> K getConfigData(DataKey<K> key) {
        return config.getData(key);
    }

    /**
     * @return 当前比赛状态
     *
     * @see MatchState
     * */
    public MatchState getState() {
        return state;
    }

    /**
     * 获取比赛记录的指定部分
     *
     * @param module 指定模块
     *
     * @return 指定模块的记录段
     * */
    public ConfigurationSection getRecord(@NonNull NamespacedKey module) {
        String key = module.getNamespace()
                + "___"
                + module.getKey();
        if (!recordInst.contains(key)) return recordInst.createSection(key);
        return recordInst.getConfigurationSection(key);
    }

    /**
     * 持久化内存中的比赛记录
     * */
    public void saveRecord() {
        Config.getInstance().save(recordInst);
    }

    // ------ 结果类型 ------

    /**
     * 开始比赛的结果
     *
     * @see Match#start(CustomData...)
     * */
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

    /**
     * 结束比赛的结果
     *
     * @see Match#stop(CustomData...)
     * */
    public static class StopResult extends OperationResult {

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

    /**
     * 构造比赛实例的结果
     *
     * @see Match#create(MatchConfig)
     * */
    public static class CreateMatchResult extends ValueResult<Match> {
        public CreateMatchResult(boolean success, @Nullable Component displayMessage, @Nullable Match match) {
            super(success, displayMessage, match);
        }

        public static CreateMatchResult success(@NonNull Match match) {
            return new CreateMatchResult(true, null, match);
        }

        public static CreateMatchResult failure(Component displayMessage) {
            return new CreateMatchResult(false, displayMessage, null);
        }
    }

    /**
     * 安装组件的结果
     *
     * @see Match#installComponents(CustomData...)
     * */
    private static class InstallComponentsResult extends ValueResult<Map<DataKey<?>, CustomData>> {
        public InstallComponentsResult(boolean success, @Nullable Component displayMessage, @Nullable Map<DataKey<?>, CustomData> ctx) {
            super(success, displayMessage, ctx);
        }

        public static InstallComponentsResult success(@NonNull Map<DataKey<?>, CustomData> match) {
            return new InstallComponentsResult(true, null, match);
        }

        public static InstallComponentsResult failure(Component displayMessage) {
            return new InstallComponentsResult(false, displayMessage, null);
        }
    }
}
