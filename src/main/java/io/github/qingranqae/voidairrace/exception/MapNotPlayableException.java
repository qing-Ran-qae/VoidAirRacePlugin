package io.github.qingranqae.voidairrace.exception;

/**
 * 当尝试使用一个不可游玩的地图（例如大厅地图）作为比赛地图时抛出的异常。
 */
public class MapNotPlayableException extends RuntimeException {
    /**
     * 构造一个“地图不可游玩”异常。
     *
     * @param message 异常信息描述
     */
    public MapNotPlayableException(String message) {
        super(message);
    }
}