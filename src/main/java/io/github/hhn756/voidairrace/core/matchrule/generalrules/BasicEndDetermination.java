package io.github.hhn756.voidairrace.core.matchrule.generalrules;

import io.github.hhn756.voidairrace.constants.Namespace;
import io.github.hhn756.voidairrace.constants.TranslateKeys;
import io.github.hhn756.voidairrace.core.match.Match;
import io.github.hhn756.voidairrace.core.match.MatchCoordinator;
import io.github.hhn756.voidairrace.core.match.basecomponents.contestant.ContestantComp;
import io.github.hhn756.voidairrace.core.match.basecomponents.gametime.GameTimeComp;
import io.github.hhn756.voidairrace.core.matchrule.MatchRule;
import io.github.hhn756.voidairrace.custom.GameElementMeta;
import io.github.hhn756.voidairrace.event.MatchStatusChangedEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 在比赛时间小于等于0或存活玩家不足时结束游戏
 * */
public class BasicEndDetermination implements MatchRule, Listener {
    private static final Collection<String> tags = new ArrayList<>();
    private static final String ID = "basic_end_determination";
    private static final GameElementMeta meta = new GameElementMeta(
            Namespace.of(ID),
            List.of(Component.translatable(
                    TranslateKeys.MatchComp.BasicEndDetermination.NAME
            )),
            null,
            List.of(Component.translatable(
                    TranslateKeys.MatchComp.BasicEndDetermination.AUTHOR
            )),
            Component.translatable(
                    TranslateKeys.MatchRule.BasicEndDetermination.DISPLAY_VERSION
            ),
            1L,
            null
    );

    @EventHandler
    public void onMatchStatusChanged(MatchStatusChangedEvent event) {
        Match match = event.getMatch();
        // 只剩一队 <- 这个优先，应对时间归零同刻击败玩家
        if (match.getComp(ContestantComp.class).getSurvivingTeamCount() <= 1) {
            MatchCoordinator.getInstance().stopMatch();
        }
        // 时间归零
        if (match.getComp(GameTimeComp.class).getRemaining() <= 0) {
            MatchCoordinator.getInstance().stopMatch();
            return;
        }
    }

    @Override
    public @NonNull Collection<String> getTags() {
        return tags;
    }

    @Override
    public @NonNull GameElementMeta getElementMeta() {
        return meta;
    }
}
