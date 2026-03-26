package io.github.qingranqae.voidairrace.core.playerstatemanager.systems.play;

import io.github.qingranqae.voidairrace.core.playerstatemanager.DefaultState;
import io.github.qingranqae.voidairrace.core.playerstatemanager.PlayerState;
import io.github.qingranqae.voidairrace.core.playerstatemanager.PlayerStateManager;
import io.github.qingranqae.voidairrace.core.teamsystem.TeamRoster;
import io.github.qingranqae.voidairrace.event.MatchStartedEvent;
import io.github.qingranqae.voidairrace.event.PlayerJoinMatchEvent;
import io.github.qingranqae.voidairrace.service.spawnutil.SpawnUtil;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class Free implements PlayerState, DefaultState, Listener {
    private static final NamespacedKey id = PlayState.FREE.getValue();

    @Override
    public NamespacedKey getId() {
        return id;
    }

    @Override
    public void onCutin(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        SpawnUtil.tpToSpawnPoint(player);
        TeamRoster.getInstance().leave(player);
        player.getInventory().clear();
        player.clearActivePotionEffects();
        player.clearActiveItem();
    }

    @EventHandler
    public void onPlayerJoinMatch(PlayerJoinMatchEvent event) {
        PlayerStateManager playerStateManager = PlayerStateManager.getInstance();
        Player player = event.getPlayer();
        if (!playerStateManager.onState(player, id)) return;
        playerStateManager.toggleStatus(player, PlayState.PLAYING.getValue());
    }

    @EventHandler
    public void onMatchStarted(MatchStartedEvent event) {
        PlayerStateManager playerStateManager = PlayerStateManager.getInstance();
        event.getMatch().getConfig().contestants().forEach(contestant -> {
            playerStateManager.toggleStatus(contestant, PlayState.PLAYING.getValue());
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SpawnUtil.tpToSpawnPoint(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(SpawnUtil.getSpawnLocation());
    }
}