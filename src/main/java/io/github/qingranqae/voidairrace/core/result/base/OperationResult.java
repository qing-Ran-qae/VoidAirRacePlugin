package io.github.qingranqae.voidairrace.core.result.base;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

/**
 * 操作结果的基类，包含 成功状态 和 用户友好 的 显示消息。
 */
public class OperationResult {
    private final boolean success;
    private final @Nullable Component displayMessage;

    /**
     * 受保护构造器，供子类或内部静态方法调用。
     */
    protected OperationResult(boolean success, @Nullable Component displayMessage) {
        this.success = success;
        this.displayMessage = displayMessage;
    }

    /**
     * 操作是否成功。
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 获取用户友好的显示消息。成功时可能为 null 或成功提示，失败时通常返回错误消息。
     */
    public @Nullable Component getDisplayMessage() {
        return displayMessage;
    }

    /**
     * 创建一个表示成功的操作结果（无附加消息）。
     */
    public static OperationResult success() {
        return new OperationResult(true, null);
    }

    /**
     * 创建一个表示失败的操作结果，并附带错误消息。
     *
     * @param displayMessage 错误消息
     */
    public static OperationResult failure(Component displayMessage) {
        return new OperationResult(false, displayMessage);
    }
}