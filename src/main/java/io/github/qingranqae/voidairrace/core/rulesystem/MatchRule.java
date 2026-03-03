package io.github.qingranqae.voidairrace.core.rulesystem;

import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

import java.util.Collection;

public interface MatchRule {
    /**
     * 规则被启用时调用（例如比赛开始时或中途添加）
     *
     * @param match 当前比赛实例
     */
    default void onEnable(Match match) {}

    /**
     * 规则被禁用时调用（例如比赛结束或中途移除）
     */
    default void onDisable(Match match) {}

    /**
     * 每 tick 调用（可选，用于持续检查）<br>
     * 注意：避免耗时操作
     */
    default void tick(Match match) {}

    /**
     * 获取规则的所有标签
     */
    @NonNull
    Collection<String> getTags();

    /**
     * 获取规则显示名称
     *
     * @return 规则的显示名称
     * */
    Component getDisplayName();

    /**
     * 获取规则描述
     *
     * @return 规则描述文本
     * */
    Component getDescription();
}