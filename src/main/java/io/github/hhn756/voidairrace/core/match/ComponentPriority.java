package io.github.hhn756.voidairrace.core.match;

import org.jetbrains.annotations.Range;

/**
 * 比赛组件添加配置数据、加载和卸载等操作的优先级数值参考点
 * */
public enum ComponentPriority {
    /**
     * 此优先级的操作在 {@link ComponentPriority#HIGH} 优先级的操作执行前执行
     * */
    COMPONENT_PRIORITY(1000),

    /**
     * 此优先级的操作在 {@link ComponentPriority#COMPONENT_PRIORITY} 优先级的操作执行后执行
     * */
    HIGH(800),

    /**
     * 此优先级的操作在 {@link ComponentPriority#HIGH} 优先级的操作执行后执行
     * */
    NORMAL(600),

    /**
     * 此优先级的操作在 {@link ComponentPriority#NORMAL} 优先级的操作执行后执行
     * */
    LOW(400),

    /**
     * 此优先级的操作在 {@link ComponentPriority#LOW} 优先级的操作执行后执行
     * */
    EXTREMELY_LOW(200);

    private final int value;

    ComponentPriority(int value) {
        this.value = value;
    }

    /**
     * @return 整数形式的优先级值
     * */
    public @Range(from = 0, to = Integer.MAX_VALUE) int getValue() {
        return value;
    }
}
