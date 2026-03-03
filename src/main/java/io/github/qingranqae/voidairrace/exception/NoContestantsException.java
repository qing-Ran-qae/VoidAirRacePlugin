package io.github.qingranqae.voidairrace.exception;

/**
 * 没有足够的参赛选手时抛出。
 * 可能在开始比赛的方法中抛出
 * */
public class NoContestantsException extends RuntimeException {
    /**
     * 构造一个无参赛者异常。
     *
     * @param message 异常信息描述
     */
    public NoContestantsException(String message) {
        super(message);
    }
}