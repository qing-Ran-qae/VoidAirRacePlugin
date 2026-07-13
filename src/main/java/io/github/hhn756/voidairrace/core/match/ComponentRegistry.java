package io.github.hhn756.voidairrace.core.match;

import io.github.hhn756.voidairrace.VoidAirRace;
import io.github.hhn756.voidairrace.constants.TranslateKeys;
import io.github.hhn756.voidairrace.core.match.componentbase.MatchComp;
import io.github.hhn756.voidairrace.core.result.base.OperationResult;
import io.github.hhn756.voidairrace.core.result.base.ValueResult;
import io.github.hhn756.voidairrace.infrastructure.util.ClassScanner;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 比赛组件注册表
 * */
public class ComponentRegistry {
    private static ComponentRegistry instance;

    static void load() {
        instance = new ComponentRegistry();
    }

    static void unload() {
        instance = null;
    }

    public static @NonNull ComponentRegistry getInstance() throws NullPointerException {
        if (instance == null) throw new NullPointerException("比赛组件注册表实例不存在");
        return instance;
    }

    // ------------------------------------------------
    private final Logger logger;

    /** 键为组件类，值为对应组件类的无参构造器 */
    private final Map<Class<MatchComp>, Constructor<MatchComp>> componentConstructors = new HashMap<>();

    private ComponentRegistry() {
        this.logger = VoidAirRace.getInstance().getLogger();
        scanComponents();
    }

    /**
     * 扫描并注册插件中所有比赛组件实现类
     */
    private void scanComponents() {
        componentConstructors.clear();

        Collection<Class<MatchComp>> componentClasses = ClassScanner.scanSubclasses(MatchComp.class);
        for (Class<MatchComp> componentClass : componentClasses) {
            // 注册组件的无参构造器
            try {
                Constructor<MatchComp> constructor = componentClass.getConstructor();
                componentConstructors.put(componentClass, constructor);
            } catch (NoSuchMethodException e) {
                logger.warning("注册比赛组件 '" + componentClass.getName() + "' 失败，此组件类型没有公开的无参构造器！这可能是开发者的疏忽");
            }
        }
    }

    /**
     * 注册一个比赛组件，它将在比赛开始时自动加载<br>
     * 如果重复注册同一个组件类型将直接返回成功的结果而不重复注册
     */
    public ComponentRegistry.@NonNull RegisterResult registerComponent(@NonNull Class<MatchComp> componentClass) {
        if (isRegistered(componentClass)) {
            return RegisterResult.success();
        }

        // 注册组件的无参构造器
        try {
            Constructor<MatchComp> constructor = componentClass.getConstructor();
            componentConstructors.put(componentClass, constructor);
        } catch (NoSuchMethodException e) {
            return RegisterResult.failure(
                    Component.translatable(TranslateKeys.ComponentRegistry.RegisterComponent.FAILURE)
            );
        }
        return RegisterResult.success();
    }

    /**
     * 获取已注册的所有组件类的元数据
     *
     * @return 类元数据集合
     * */
    public @NonNull Collection<Class<MatchComp>> getAllComponentClasses() {
        return componentConstructors.keySet();
    }

    /**
     * 创建一个指定类型的新比赛组件实例
     *
     * @param type 指定组件类型
     * @return 新组件实例
     */
    public ComponentRegistry.@NonNull InstantiateResult newComponent(@NonNull Class<MatchComp> type) {
        Constructor<MatchComp> constructor = componentConstructors.get(type);
        if (constructor == null) {
            return InstantiateResult.failure(
                    Component.translatable(TranslateKeys.ComponentRegistry.NewComponent.FAILURE_NOT_FOUND)
                            .arguments(Component.text(type.toString()))
            );
        }
        try {
            MatchComp instance = constructor.newInstance();
            return InstantiateResult.success(instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return InstantiateResult.failure(
                    Component.translatable(TranslateKeys.ComponentRegistry.NewComponent.FAILURE_INSTANTIATION_FAILED)
            );
        }
    }

    /**
     * 检查指定组件类型是否已注册
     *
     * @param componentClass 指定组件类型
     *
     * @return 如果指定类型已注册将返回{@code true}，否则返回{@code false}
     * }
     */
    private boolean isRegistered(@NonNull Class<MatchComp> componentClass) {
        return componentConstructors.containsKey(componentClass);
    }

    /**
     * 实例化组件的结果
     * 
     * @see ComponentRegistry#newComponent(Class) 
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

    /**
     * 注册新组件类型的结果
     * 
     * @see ComponentRegistry#registerComponent(Class)
     * */
    public static final class RegisterResult extends OperationResult {
        public RegisterResult(boolean success, @Nullable Component displayMessage) {
            super(success, displayMessage);
        }

        public static RegisterResult success() {
            return new RegisterResult(true, null);
        }

        public static RegisterResult failure(Component displayMessage) {
            return new RegisterResult(false, displayMessage);
        }
    }
}
