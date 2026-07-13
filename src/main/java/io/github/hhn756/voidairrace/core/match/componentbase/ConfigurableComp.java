package io.github.hhn756.voidairrace.core.match.componentbase;

import io.github.hhn756.voidairrace.core.match.ComponentPriority;
import io.github.hhn756.voidairrace.core.match.MatchConfig;
import io.github.hhn756.voidairrace.core.result.base.ValueResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * 赋予比赛组件增加自定义比赛配置数据的能力
 *
 * @param <ECFG> 组件的预期配置类型
 * @param <CFG> 组件的自定义配置类型
 * */
public interface ConfigurableComp<
        ECFG extends CustomData,
        CFG extends CustomData>
{
    /**
     * 获取该组件用来存储配置数据的 Key<br>
     * 如果组件没有配置数据将不会实现此方法
     *
     * @return 该组件用来存储配置数据的 Key
     * */
    default DataKey<CFG> getConfigKey() {
        return DataKey.of(null, null);
    };

    /**
     * 创建一个由该组件提供的自定义配置数据对象
     *
     * @param expected 调用者在创建比赛配置创建时传入的它期望的配置内容。当此值为{@code null}时推荐回退到{@link ConfigurableComp#createDefaultConfig()}而不是返回失败结果
     *
     * @return 如果组件添加了自定义配置，那么返回结果的值将是比赛配置数据对象，否则返回结果的值为{@code null}
     * */
    default @NonNull CustomConfigResult<CFG> createCustomConfig(@Nullable ECFG expected) {
        // 默认忽略预期配置
        return CustomConfigResult.success(createDefaultConfig().getValue());
    }

    /**
     * 根据当前系统状态（配置、其他模块状态等）创建一个由该组件提供的自定义比赛配置数据对象（简称“默认配置”/“默认比赛配置”）
     *
     * @return 如果组件添加了自定义配置，那么返回结果的值将是比赛配置数据对象，否则返回结果的值为{@code null}
     * */
    @NonNull DefaultConfigResult<CFG> createDefaultConfig();

    /**
     * 检查此组件添加的自定义配置字段的实际值是否合法
     *
     * @param config 此组件添加的自定义配置数据类型的实例
     *
     * @return 如果配置值合法将返回成功的结果，否则返回失败的结果
     * */
    default MatchConfig.@NonNull ValidationConfigResult validateConfig(@NonNull CFG config) {
        // 默认不检查配置，始终成功
        return MatchConfig.ValidationConfigResult.success();
    }

    // ------ 操作优先级控制 ------

    /**
     * 获取比赛配置创建时此组件向其添加自定义数据的优先级，值越大越先添加数据<br>
     * 一般情况下，此方法返回值应在每个实例、每次调用时都一致
     *
     * @return 添加配置优先级
     * */
    default @Range(from = 0, to = Integer.MAX_VALUE) int getConfigPriority() {
        return ComponentPriority.NORMAL.getValue();
    }

    // ------ 结果类型 ------

    /**
     * 组件创建自定义配置数据对象的结果
     *
     * @param <CFG> 组件的自定义配置类型
     *
     * @see ConfigurableComp#createCustomConfig(CustomData)
     * */
    class CustomConfigResult<CFG extends CustomData> extends ValueResult<CFG> {
        public CustomConfigResult(
                boolean success,
                @Nullable CFG config,
                @Nullable Component displayMessage
        ) {
            super(success, displayMessage, config);
        }

        public static <CFG extends CustomData> CustomConfigResult<CFG> success(CFG config) {
            return new CustomConfigResult<>(true, config, null);
        }

        public static CustomConfigResult<?> failure(Component displayMessage) {
            return new CustomConfigResult<>(false, null, displayMessage);
        }
    }

    /**
     * 组件创建默认配置（即根据系统状态创建的自定义配置）的结果
     *
     * @see ConfigurableComp#createDefaultConfig()
     * */
    class DefaultConfigResult<CFG extends CustomData> extends ValueResult<CFG> {
        public DefaultConfigResult(
                boolean success,
                @Nullable CFG config,
                @Nullable Component displayMessage
        ) {
            super(success, displayMessage, config);
        }

        public static <CFG extends CustomData> DefaultConfigResult<CFG> success(CFG config) {
            return new DefaultConfigResult<>(true, config, null);
        }

        public static DefaultConfigResult<CustomData> failure(Component displayMessage) {
            return new DefaultConfigResult<>(false, null, displayMessage);
        }
    }
}
