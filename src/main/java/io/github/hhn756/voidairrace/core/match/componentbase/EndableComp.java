package io.github.hhn756.voidairrace.core.match.componentbase;

import io.github.hhn756.voidairrace.core.match.ComponentPriority;
import io.github.hhn756.voidairrace.core.match.Match;
import io.github.hhn756.voidairrace.core.result.base.ValueResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * 赋予比赛组件在结束游戏时执行自定义操作和增加自定义结束上下文的能力
 * @param <EA> 组件的结束游戏参数类型
 * @param <EC> 组件的结束游戏上下文数据类型
 * */
public interface EndableComp<
        EA extends CustomData,
        EC extends CustomData>
{
    /**
     * 当比赛卸载此组件时执行一次<br>
     * 组件实现的操作发生错误时应自行解决或输出错误日志
     *
     * @param match 组件实例被卸载前所在的比赛
     * @param endArg 结束游戏方法调用方传入的参数，用于在结束时控制模块的行为。组件可以定义自己的参数格式和行为<br>
     *                   如果调用方没有给特定组件传入此参数，那么组件将收到 {@code null}
     * */
    @NonNull ComponentUninstallResult<EC> uninstall(@NonNull Match match, @Nullable EA endArg);

    /**
     * 获取比赛结束时卸载此组件的优先级，值越大越先卸载<br>
     * 此方法返回值应在每个实例、每次调用时都一致
     *
     * @return 组件卸载优先级
     * */
    default @Range(from = 0, to = Integer.MAX_VALUE) int getUninstallPriority() {
        return ComponentPriority.NORMAL.getValue();
    }

    /**
     * 获取组件的结束上下文数据键<br>
     * 一般情况下此方法应始终返回同一个对象<br>
     * 如果组件没有自定义结束上下文将不会实现此方法
     *
     * @see DataKey
     * */
    default @NonNull DataKey<?> getECK() {
        return DataKey.of(null, null);
    };

    // ------ 结果类型 ------

    /**
     * 组件卸载结果
     *
     * @see EndableComp#uninstall(Match, CustomData)
     * */
    class ComponentUninstallResult<SC extends CustomData> extends ValueResult<SC> {
        public ComponentUninstallResult(
                boolean success,
                @Nullable Component displayMessage,
                @Nullable SC endContext
        ) {
            super(success, displayMessage, endContext);
            this.endContext = endContext;
        }

        private final CustomData endContext;

        /**
         * 返回成功的组件加载结果，不包含自定义结束上下文
         * */
        public static ComponentUninstallResult<?> success() {
            return new ComponentUninstallResult<>(true, null, null);
        }

        /**
         * 返回成功的组件加载结果，包含自定义结束上下文
         *
         * @param endContext 组件的自定义结束上下文
         * */
        public static <SC extends CustomData> ComponentUninstallResult<SC> success(@Nullable SC endContext) {
            return new ComponentUninstallResult<>(true, null, endContext);
        }

        public static ComponentUninstallResult<?> failure(@Nullable Component displayMessage) {
            return new ComponentUninstallResult<>(false, displayMessage, null);
        }

        /**
         * @return 此组件提供的自定义结束上下文
         * */
        public CustomData getEndContext() {
            return endContext;
        }
    }
}
