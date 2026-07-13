package io.github.hhn756.voidairrace.core.result.base;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

/**
 * 携带值的操作结果，继承自 {@link OperationResult}
 *
 * @param <T> 值的类型
 */
public class ValueResult<T> extends OperationResult {
    private final @Nullable T value;

    /**
     * 构造一个带值的操作结果
     *
     * @param success 操作是否成功，用{@code true}来表示成功；用{@code false}来表示失败
     * @param displayMessage 结果消息
     * @param value 操作结果值
     */
    public ValueResult(boolean success, @Nullable Component displayMessage, @Nullable T value) {
        super(success, displayMessage);
        this.value = value;
    }

    /**
     * 获取操作结果携带的值。仅在成功时有意义，失败时通常为 {@code null}
     */
    public @Nullable T getValue() {
        return value;
    }

    /**
     * 创建一个携带值的成功结果
     *
     * @param value 成功时携带的值
     * @param <T>   值类型
     */
    public static <T> ValueResult<T> success(T value) {
        return new ValueResult<>(true, null, value);
    }
}
