package io.github.hhn756.voidairrace.result.base;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

/**
 * 操作结果的基类，包含 成功状态 和 用户友好 的 显示消息
 */
public class OperationResult {
    private final boolean success;
    private final @Nullable Component displayMessage;

    /**
     * 构造一个操作结果
     *
     * @param success 操作是否成功，用{@code true}表示成功；用{@code false}表示失败
     * @param displayMessage 结果消息
     * */
    public OperationResult(boolean success, @Nullable Component displayMessage) {
        this.success = success;
        this.displayMessage = displayMessage;
    }

    /**
     * 操作是否成功
     *
     * @return 如果操作成功那么会返回{@code true}，否则返回{@code false}
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 获取 用户友好 的 显示消息。成功时可能为 {@code null} 或结果消息，失败时通常返回错误消息
     */
    public @Nullable Component getDisplayMessage() {
        return displayMessage;
    }

    /**
     * 创建一个表示成功的操作结果（无附加消息）
     */
    public static OperationResult success() {
        return new OperationResult(true, null);
    }

    /**
     * 创建一个表示失败的操作结果，并附带错误消息
     *
     * @param displayMessage 错误消息
     */
    public static OperationResult failure(Component displayMessage) {
        return new OperationResult(false, displayMessage);
    }
}
