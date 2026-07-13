package io.github.hhn756.voidairrace.core.match;

import io.github.hhn756.voidairrace.constants.TranslateKeys;
import io.github.hhn756.voidairrace.core.match.componentbase.ConfigurableComp;
import io.github.hhn756.voidairrace.core.match.componentbase.CustomData;
import io.github.hhn756.voidairrace.core.match.componentbase.DataKey;
import io.github.hhn756.voidairrace.core.match.componentbase.MatchComp;
import io.github.hhn756.voidairrace.core.result.base.OperationResult;
import io.github.hhn756.voidairrace.core.result.base.ValueResult;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 比赛的配置，决定组件行为
 */
public class MatchConfig {
    /**
     * 记录此配置加载的所有组件
     * */
    private final Map<Class<? extends MatchComp>, MatchComp> components = new HashMap<>();

    /**
     * 记录所有组件添加的自定义数据。值为组件添加的自定义数据，键为添加对应数据的组件
     * */
    private final Map<DataKey<?>, CustomData> customData = new HashMap<>();

    /**
     * 标记配置实例是否已被某局比赛使用过，防止重复使用配置实例
     * */
    private boolean used = false;

    /**
     * 创建一个新比赛配置实例
     *
     * @param expectationsConfigs 传递给各组件的用于表示调用者预期参数内容的对象
     *
     * @return 创建的配置实例
     * */
    public static @NonNull CreateConfigResult create(CustomData... expectationsConfigs) {
        MatchConfig config = new MatchConfig();
        ConfigInitResult initResult = config.init(ConfigDataSource.COMMON, expectationsConfigs);
        if (!initResult.isSuccess()) {
            Component msg = initResult.getDisplayMessage() == null
                    ? Component.translatable(TranslateKeys.Match.MatchConfig.CreateConfig.FAILURE_UNKNOWN_CAUSE)
                    : Component.translatable(TranslateKeys.Match.MatchConfig.CreateConfig.FAILURE_SPECIFIED_CAUSE)
                      .arguments(initResult.getDisplayMessage());

            return CreateConfigResult.failure(msg);
        }
        return CreateConfigResult.success(config);
    }

    /**
     * 根据当前系统状态创建一个比赛配置实例
     *
     * @return 创建的配置实例
     * */
    public static @NonNull DefaultConfigResult createDefault() {
        MatchConfig config = new MatchConfig();
        ConfigInitResult initResult = config.init(ConfigDataSource.DEFAULT);
        if (!initResult.isSuccess()) {
            Component msg = initResult.getDisplayMessage() == null
                    ? Component.translatable(TranslateKeys.Match.MatchConfig.CreateDefaultConfig.FAILURE_UNKNOWN_CAUSE)
                    : Component.translatable(TranslateKeys.Match.MatchConfig.CreateDefaultConfig.FAILURE_SPECIFIED_CAUSE)
                      .arguments(initResult.getDisplayMessage());
            return DefaultConfigResult.failure(msg);
        }
        return DefaultConfigResult.success(config);
    }

    private MatchConfig() {}

    /**
     * 初始化比赛配置实例<br>
     * 使用独立初始化方法以传递失败信号和消息<br>
     *
     * @param dataSource 配置对象中的自定义数据通过调用组件的指定数据源方法获得
     * @param expectationsConfigs 传递给各组件的用于表示调用者预期参数内容的对象
     * */
    private @NonNull ConfigInitResult init(
            @NonNull ConfigDataSource dataSource,
            CustomData... expectationsConfigs
    ) {
        ComponentRegistry componentRegistry = ComponentRegistry.getInstance();

        // 获取要加载的所有组件
        Collection<Class<MatchComp>> ComponentClasses = componentRegistry.getAllComponentClasses();

        // 加载组件本身
        for (Class<MatchComp> componentClass : ComponentClasses) {
            // 实例化
            ComponentRegistry.InstantiateResult instantiateResult = componentRegistry.newComponent(componentClass);
            MatchComp componentInstance = instantiateResult.getValue();
            if (!instantiateResult.isSuccess() || componentInstance == null) {
                Component msg = instantiateResult.getDisplayMessage() == null
                        ? Component.translatable(TranslateKeys.Match.MatchConfig.Init.FAILURE_UNKNOWN_CAUSE)
                        : Component.translatable(TranslateKeys.Match.MatchConfig.Init.FAILURE_SPECIFIED_CAUSE)
                          .arguments(instantiateResult.getDisplayMessage());
                return ConfigInitResult.failure(msg);
            }

            // 记录
            components.put(componentClass, componentInstance);
        }

        // 加载组件的自定义配置数据
        ConfigInitResult loadConfigResult = loadCustomConfig(dataSource, expectationsConfigs);
        if (!loadConfigResult.isSuccess()) return loadConfigResult;

        return new ConfigInitResult(false, null);
    }

    /**
     * 加载已加载所有组件的自定义配置数据
     *
     * @param expectationsConfigs 给所有组件的预期配置，允许组件收到{@code null}值（注意不是参数本身为{@code null}）
     * */
    private @NonNull ConfigInitResult loadCustomConfig(
            @NonNull ConfigDataSource dataSource,
            CustomData... expectationsConfigs
    ) {
        // 获取所有 ConfigurableComponent 并按优先级排序
        List<ConfigurableComp<?, ?>> configurableComponents = components.values().stream()
                .filter(ConfigurableComp.class::isInstance)
                .map(c -> (ConfigurableComp<?, ?>) c)
                .sorted((a, b) -> b.getConfigPriority() - a.getConfigPriority())
                .collect(Collectors.toList());

        Map<@NonNull Class<? extends MatchComp>, CustomData> expectationsConfigMap = null;
        if (dataSource == ConfigDataSource.COMMON) {
            // 构建期望配置映射
            expectationsConfigMap = new HashMap<>();
            for (CustomData expectationsConfig : expectationsConfigs) {
                expectationsConfigMap.put(expectationsConfig.getSource(), expectationsConfig);
            }
        }

        // 逐个处理，利用辅助方法捕获通配符
        for (ConfigurableComp<?, ?> component : configurableComponents) {
            ConfigInitResult result = loadSingleCustomConfig(dataSource, component, expectationsConfigMap);
            if (!result.isSuccess()) {
                return result;
            }
        }

        return ConfigInitResult.success();
    }

    private <E extends CustomData, C extends CustomData>
    @NonNull ConfigInitResult loadSingleCustomConfig(
            ConfigDataSource dataSource,
            ConfigurableComp<E, C> component,
            Map<Class<? extends MatchComp>, CustomData> expectationsMap
    ) {
        CustomData configValue = null;
        if (dataSource == ConfigDataSource.COMMON) {
            // 获取期望配置（根据组件类型）
            @SuppressWarnings("unchecked")
            E expected = (E) expectationsMap.get(component.getClass()); // 唯一的调用者在此分支的条件成立时不会传递null

            // 创建自定义配置
            ConfigurableComp.CustomConfigResult<C> customConfigResult = component.createCustomConfig(expected);
            configValue = customConfigResult.getValue();
            if (!customConfigResult.isSuccess() || configValue == null) {
                Component msg = customConfigResult.getDisplayMessage() == null
                        ? Component.translatable(TranslateKeys.Match.MatchConfig.LoadCustomConfig.FAILURE_UNKNOWN_CAUSE)
                        : Component.translatable(TranslateKeys.Match.MatchConfig.LoadCustomConfig.FAILURE_SPECIFIED_CAUSE)
                          .arguments(customConfigResult.getDisplayMessage());
                return ConfigInitResult.failure(msg);
            }
        } else if (dataSource == ConfigDataSource.DEFAULT) {
            ConfigurableComp.DefaultConfigResult<?> defaultConfigResult = component.createDefaultConfig();
            configValue = defaultConfigResult.getValue();
            if (!defaultConfigResult.isSuccess() || configValue == null) {
                Component msg = defaultConfigResult.getDisplayMessage() == null
                        ? Component.translatable(TranslateKeys.Match.MatchConfig.LoadDefaultConfig.FAILURE_UNKNOWN_CAUSE)
                        : Component.translatable(TranslateKeys.Match.MatchConfig.LoadDefaultConfig.FAILURE_SPECIFIED_CAUSE)
                          .arguments(defaultConfigResult.getDisplayMessage());
                return ConfigInitResult.failure(msg);
            }
        }

        // 存储配置，类型安全
        customData.put(component.getConfigKey(), configValue);
        return ConfigInitResult.success();
    }

    /**
     * 验证当前配置值是否有效<br>
     * 这将逐个调用所有已加载组件的{@link ConfigurableComp#validateConfig(CustomData)}方法
     * */
    public @NonNull ValidationConfigResult validate() {
        for (MatchComp component : components.values()) {
            if (!(component instanceof ConfigurableComp<?,?> configurableComponent)) continue;

            // 调用辅助方法，利用捕获转换
            ValidationConfigResult result = validateComponent(configurableComponent);
            if (!result.isSuccess()) {
                return result;
            }
        }
        return ValidationConfigResult.success();
    }

    @SuppressWarnings("unchecked")
    private <C extends CustomData>
    @NonNull ValidationConfigResult validateComponent(ConfigurableComp<?, C> component) {
        // 获取该组件的实际配置数据（类型安全）
        C config = getData(component);
        if (config == null) {
            // 没有配置数据则跳过验证（或者根据需求返回成功）
            return ValidationConfigResult.success();
        }
        return component.validateConfig(config);
    }

    /**
     * 获取指定组件添加的自定义配置数据
     *
     * @param component 指定组件
     *
     * @return 如果指定组件添加了自定义配置数据将返回它添加的数据，否则返回{@code null}
     * */
    public <K extends CustomData> K getData(ConfigurableComp<?, K> component) {
        return getData(component.getConfigKey());
    }

    /**
     * 获取指定自定义配置数据
     *
     * @param key 数据对应的键
     *
     * @return 如果包含与键对应的数据将返回它，否则返回{@code null}
     * */
    @SuppressWarnings("unchecked")
    public @Nullable <K extends CustomData> K getData(DataKey<K> key) {
        return (K) customData.get(key);
    }

    /**
     * 将配置实例标记为已使用过，防止重复使用配置实例
     * */
    public void use() {
        used = true;
    }

    /**
     * 检查配置实例是否已经被某场比赛使用过，防止重复使用配置实例
     *
     * @return 如果配置实例已被使用过将返回 {@code true}，否则返回{@code false}
     * */
    public boolean isUsed() {
        return used;
    }

    /**
     * 获取此配置中指定已加载的比赛组件实例
     *
     * @return 指定类型的组件实例
     * */
    @SuppressWarnings("unchecked")
    public @NonNull <C extends MatchComp> C getComp(@NonNull Class<C> componentClass) {
        return (C) components.get(componentClass);
    }

    /**
     * 获取此配置中已加载的所有组件实例
     *
     * @return 值为一个已加载的组件，键为对应组件的类型。修改返回值不会同步到配置对象状态上
     * */
    public @NonNull Map<Class<? extends MatchComp>, MatchComp> getAllComponents() {
        return new HashMap<>(components);
    }

    // ------------ 辅助类 ------------

    /**
     * 创建比赛配置的结果
     *
     * @see MatchConfig#create(CustomData...)
     * */
    public static final class CreateConfigResult extends ValueResult<MatchConfig> {
        public CreateConfigResult(boolean success, @org.jetbrains.annotations.Nullable Component displayMessage, MatchConfig value) {
            super(success, displayMessage, value);
        }

        public static CreateConfigResult success(MatchConfig value) {
            return new CreateConfigResult(true, null, value);
        }

        public static CreateConfigResult failure(Component displayMessage) {
            return new CreateConfigResult(false, displayMessage, null);
        }
    }

    /**
     * 创建默认配置的结果
     *
     * @see MatchConfig#createDefault()
     * */
    public static final class DefaultConfigResult extends ValueResult<MatchConfig> {
        public DefaultConfigResult(boolean success, @org.jetbrains.annotations.Nullable Component displayMessage, MatchConfig value) {
            super(success, displayMessage, value);
        }

        public static DefaultConfigResult success(MatchConfig value) {
            return new DefaultConfigResult(true, null, value);
        }

        public static DefaultConfigResult failure(Component displayMessage) {
            return new DefaultConfigResult(false, displayMessage, null);
        }
    }

    /**
     * 检查配置有效性的结果
     *
     * @see MatchConfig#validate()
     * */
    public static final class ValidationConfigResult extends OperationResult {
        public ValidationConfigResult(boolean success, @Nullable Component displayMessage) {
            super(success, displayMessage);
        }

        public static ValidationConfigResult success() {
            return new ValidationConfigResult(true, null);
        }

        public static ValidationConfigResult failure(Component displayMessage) {
            return new ValidationConfigResult(false, displayMessage);
        }
    }

    /**
     * 配置实例初始化的结果
     *
     * @see MatchConfig#init(ConfigDataSource, CustomData...)
     * */
    public static final class ConfigInitResult extends OperationResult {
        public ConfigInitResult(boolean success, @Nullable Component displayMessage) {
            super(success, displayMessage);
        }

        public static ConfigInitResult success() {
            return new ConfigInitResult(true, null);
        }

        public static ConfigInitResult failure(Component displayMessage) {
            return new ConfigInitResult(false, displayMessage);
        }
    }

    /**
     * 初始化配置实例时如何获取自定义配置项
     * */
    private enum ConfigDataSource {
        /**
         * 调用组件的 {@link ConfigurableComp#createCustomConfig(CustomData)} 获取自定义配置值
         * */
        COMMON,

        /**
         * 调用组件的 {@link ConfigurableComp#createDefaultConfig()} 获取自定义配置值
         * */
        DEFAULT
    }
}
