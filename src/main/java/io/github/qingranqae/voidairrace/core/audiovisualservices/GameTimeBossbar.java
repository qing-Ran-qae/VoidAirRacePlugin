package io.github.qingranqae.voidairrace.core.audiovisualservices;

import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import io.github.qingranqae.voidairrace.core.matchsystem.MatchCoordinator;
import io.github.qingranqae.voidairrace.event.MatchOverEvent;
import io.github.qingranqae.voidairrace.event.MatchStartedEvent;
import io.github.qingranqae.voidairrace.event.MatchStatusChangedEvent;
import io.github.qingranqae.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import io.github.qingranqae.voidairrace.infrastructure.util.Percentage;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AutoRegistration
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
        Bukkit.getServer().getOnlinePlayers().forEach(
                player -> player.showBossBar(bossbar)
        );
        updateBossbar(event.getMatch());
    }

    @EventHandler
    public void onMatchOver(MatchOverEvent event) {
        // 使 bossbar 对所有玩家不可见
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.hideBossBar(bossbar);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (MatchCoordinator.getInstance().matchIsRunning()) {
            player.showBossBar(bossbar);
        } else {
            player.hideBossBar(bossbar);
        }
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
