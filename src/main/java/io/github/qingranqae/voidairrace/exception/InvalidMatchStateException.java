package io.github.qingranqae.voidairrace.exception;

/**
 * 当尝试执行的操作与当前比赛状态不兼容时抛出的异常。
 * 例如，在比赛尚未开始时尝试结束比赛，或在比赛进行中尝试再次开始比赛。
 * 该异常继承自 {@link IllegalStateException}，表示对象状态非法。
 */
public class InvalidMatchStateException extends IllegalStateException {
    /**
     * 构造一个非法比赛状态异常。
     *
     * @param message 异常信息描述
     */
    public InvalidMatchStateException(String message) {
        super(message);
    }
}