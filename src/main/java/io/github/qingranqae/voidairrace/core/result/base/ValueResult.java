package io.github.qingranqae.voidairrace.core.result.base;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

/**
 * 携带值的操作结果，继承自 {@link OperationResult}。
 *
 * @param <T> 值的类型
 */
public class ValueResult<T> extends OperationResult {
    private final @Nullable T value;

    /**
     * 受保护构造器，供子类或静态工厂方法调用。
     */
    protected ValueResult(boolean success, @Nullable T value, @Nullable Component displayMessage) {
        super(success, displayMessage);
        this.value = value;
    }

    /**
     * 获取操作结果携带的值。仅在成功时有意义，失败时通常为 null。
     */
    public @Nullable T getValue() {
        return value;
    }

    /**
     * 创建一个携带值的成功结果。
     *
     * @param value 成功时携带的值
     * @param <T>   值类型
     */
    public static <T> ValueResult<T> success(T value) {
        return new ValueResult<>(true, value, null);
    }
}