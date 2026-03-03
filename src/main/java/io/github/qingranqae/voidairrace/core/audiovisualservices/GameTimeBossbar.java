package io.github.qingranqae.voidairrace.core.audiovisualservices;

import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import io.github.qingranqae.voidairrace.event.MatchStartedEvent;
import io.github.qingranqae.voidairrace.event.MatchStatusChangedEvent;
import io.github.qingranqae.voidairrace.util.Percentage;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameTimeBossbar implements Listener {
    BossBar bossbar;

    public GameTimeBossbar() {
        bossbar = BossBar.bossBar(
                Component.translatable("void_air_race.audiovisualservices.game_time_bossbar.bossbar_name")
                        .arguments(Component.text(0)),
                1.0f,
                BossBar.Color.BLUE,
                BossBar.Overlay.PROGRESS
        );
    }

    @EventHandler
    public void onMatchStarted(MatchStartedEvent event) {
        event.getMatch().getConfig().contestants().forEach(
                contestant -> contestant.showBossBar(bossbar)
        );
        updateBossbar(event.getMatch());
    }

    @EventHandler
    public void onMatchStatusChanged(MatchStatusChangedEvent event) {
        updateBossbar(event.getMatch());
    }

    private void updateBossbar(Match match) {
        bossbar.name(
                Component.translatable("void_air_race.audiovisualservices.game_time_bossbar.bossbar_name")
                        .arguments(Component.text(match.getRemainingTime() / 20))
        );
        bossbar.progress(
                Percentage.toPercentage(
                        match.getConfig().duration(),
                        match.getRemainingTime()
                )
        );
    }
}
