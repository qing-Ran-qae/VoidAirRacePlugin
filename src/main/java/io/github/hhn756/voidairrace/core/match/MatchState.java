package io.github.hhn756.voidairrace.core.match;

/**
 * 包含所有比赛状态的枚举
 */
public enum MatchState {
    /** 等待游戏中，随时可开始 */
    SCHEDULED,
    /** 比赛正在启动中（配置验证、地图准备等） */
    STARTING,
    /** 比赛正在进行中 */
    IN_PROGRESS,
    /** 比赛正在结束中（清理资源、发布事件等） */
    ENDING
}
