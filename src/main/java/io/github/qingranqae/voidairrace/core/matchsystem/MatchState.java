package io.github.qingranqae.voidairrace.core.matchsystem;

/**
 * 比赛生命周期的各个状态。
 */
public enum MatchState {
    /** 无比赛进行，等待开始。 */
    SCHEDULED,

    /** 比赛正在启动中（配置验证、地图准备等）。 */
    STARTING,

    /** 比赛正在进行中。 */
    IN_PROGRESS,

    /** 比赛正在结束中（清理资源、发布事件等）。 */
    ENDING
}