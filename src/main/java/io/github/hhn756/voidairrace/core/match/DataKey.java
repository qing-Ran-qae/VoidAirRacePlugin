package io.github.hhn756.voidairrace.core.match;

import io.github.hhn756.voidairrace.core.match.componentbase.CustomData;
import io.github.hhn756.voidairrace.core.match.componentbase.MatchComp;

import java.util.Objects;

/**
 * 用于在各种比赛相关的自定义数据的容器（如比赛、比赛配置等）中类型安全地访问特定组件添加的自定义数据
 * */
public final class DataKey<T extends CustomData> {
    private final Class<?> Source;
    private final Class<T> customDataType;

    private DataKey(Class<?> componentClass, Class<T> dataClass) {
        this.Source = componentClass;
        this.customDataType = dataClass;
    }

    /**
     * 创建一个新的数据键实例
     *
     * @param componentClass 键的持有者类型
     * @param dataClass 键所代表的类型
     *
     * @return 新数据键实例
     * */
    public static <T extends CustomData> DataKey<T> of(
            Class<? extends MatchComp> componentClass,
            Class<T> dataClass
    ) {
        return new DataKey<>(componentClass, dataClass);
    }

    /**
     * @return 此键所有者的类型
     * */
    public Class<?> getSource() {
        return Source;
    }

    /**
     * @return 此键所索引到数据的类型
     * */
    public Class<T> getDataType() {
        return customDataType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataKey<?> that = (DataKey<?>) o;
        return Objects.equals(Source, that.Source)
                && Objects.equals(customDataType, that.customDataType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Source, customDataType);
    }
}
