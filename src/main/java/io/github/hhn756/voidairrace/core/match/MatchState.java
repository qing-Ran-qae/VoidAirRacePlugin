package io.github.hhn756.voidairrace.core.match;

/**
 * 包含所有比赛状态的枚举
 */
public enum MatchState {
    /** 代表等待游戏中，随时可开始 */
    SCHEDULED,
    /** 代表比赛正在启动中（配置验证、准备资源等） */
    STARTING,
    /** 代表比赛正在进行中 */
    IN_PROGRESS,
    /** 代表比赛正在结束中（清理资源、发布事件等） */
    ENDING
}
