package io.github.qingranqae.voidairrace.core.rulesystem.generalrules;

import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import io.github.qingranqae.voidairrace.core.matchsystem.MatchCoordinator;
import io.github.qingranqae.voidairrace.core.rulesystem.MatchRule;
import io.github.qingranqae.voidairrace.event.MatchStatusChangedEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 在比赛时间小于等于0或存活玩家不足时结束游戏
 * */
public class BasicEndDetermination implements MatchRule, Listener {
    private static final Collection<String> tags = new ArrayList<>();

    @EventHandler
    public void onMatchStatusChanged(MatchStatusChangedEvent event) {
        Match match = event.getMatch();
        if (match.getRemainingTime() <= 0) {
            MatchCoordinator.getInstance().stopMatch(false);
            return;
        }
        if (match.getSurvivingTeamNum() <= 1) {
            MatchCoordinator.getInstance().stopMatch(false);
            return;
        }
    }

    @Override
    public @NonNull Collection<String> getTags() {
        return tags;
    }

    @Override
    public @NonNull Component getDisplayName() {
        return Component.translatable("void_air_race.match_rule.basic_end_determination.display_name");
    }

    @Override
    public @NonNull Component getDescription() {
        return Component.translatable("void_air_race.match_rule.basic_end_determination.description");
    }
}