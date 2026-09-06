package io.github.hhn756.voidairrace.core.match;

import io.github.hhn756.voidairrace.constants.TranslateKeys;
import io.github.hhn756.voidairrace.core.match.componentbase.MatchComp;
import io.github.hhn756.voidairrace.result.base.ValueResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 用于在注册表中记录一个比赛组件
 * */
public class CompEntry {
    public @NonNull Class<MatchComp> getKey() {
        return compType;
    }

    /**
     * 构造一个记录指定比赛组件的组件注册项对象
     *
     * @param compType 新实例将要代表的比赛组件类型，其必须要有一个无参数的构造器
     *
     * @throws NoSuchMethodException 如果指定组件类型没有无参构造器
     * */
    CompEntry(@NonNull Class<MatchComp> compType) throws NoSuchMethodException {
        this.compType = compType;
        constructor = compType.getConstructor();
    }

    /**
     * 所记录的组件类型
     * */
    private final @NonNull Class<MatchComp> compType;

    /**
     * 所记录组件类型的构造器
     * */
    private final @NonNull Constructor<MatchComp> constructor;

    /**
     * @return 所记录的组件类型
     * */
    public @NonNull Class<MatchComp> getCompType() {
        return compType;
    }

    /**
     * 创建一个此元数据所代表组件类型的新实例
     *
     * @return 新组件实例
     */
    public @NonNull InstantiateResult newInstance() {
        try {
            MatchComp instance = constructor.newInstance();
            return InstantiateResult.success(instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return InstantiateResult.failure(
                    Component.translatable(TranslateKeys.Match.COMP_ENTRY_INSTANTIATE_FAILURE)
            );
        }
    }

    /**
     * 实例化组件的结果
     *
     * @see CompEntry#newInstance()
     * */
    public static final class InstantiateResult extends ValueResult<MatchComp> {
        public InstantiateResult(boolean success, @Nullable Component displayMessage, @Nullable MatchComp value) {
            super(success, displayMessage, value);
        }

        public static InstantiateResult success(MatchComp component) {
            return new InstantiateResult(true, null, component);
        }

        public static InstantiateResult failure(Component displayMessage) {
            return new InstantiateResult(false, displayMessage, null);
        }
    }
}
