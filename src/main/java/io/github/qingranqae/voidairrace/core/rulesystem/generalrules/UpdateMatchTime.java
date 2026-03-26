package io.github.qingranqae.voidairrace.core.rulesystem.generalrules;

import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import io.github.qingranqae.voidairrace.core.result.matchrule.MatchRuleEnableResult;
import io.github.qingranqae.voidairrace.core.rulesystem.MatchRule;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

/**
 * 每 tick 将剩余比赛时间减1
 * */
public class UpdateMatchTime implements MatchRule {

    @Override
    public void tick(Match match) {
        match.setRemainingTime(match.getRemainingTime() - 1);
    }

    @Override
    public @NonNull Collection<String> getTags() {
        return List.of(new String[]{});
    }

    @Override
    public @NonNull Component getDisplayName() {
        return Component.translatable("void_air_race.match_rule.update_match_time.display_name");
    }

    @Override
    public @NonNull Component getDescription() {
        return Component.translatable("void_air_race.match_rule.update_match_time.description");
    }

    @Override
    public @NonNull MatchRuleEnableResult onEnable(Match match) {
        match.getMainClass().getLogger().fine("已启用 UpdateMatchTime 规则！");
        return MatchRuleEnableResult.success();
    }

    @Override
    public void onDisable(Match match) {
        match.getMainClass().getLogger().fine("已禁用 UpdateMatchTime 规则");
    }
}