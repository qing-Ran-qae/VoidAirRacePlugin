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
    private static final NamespacedKey stateId = PlayState.FREE.getValue();

    @Override
    public NamespacedKey getId() {
        return stateId;
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

    // ------ 切出到其他状态 ------

    @EventHandler
    public void onPlayerJoinMatch(PlayerJoinMatchEvent event) {
        PlayerStateManager playerStateManager = PlayerStateManager.getInstance();
        Player player = event.getPlayer();
        if (!playerStateManager.onState(player, stateId)) return;
        playerStateManager.toggle(player, PlayState.PLAYING.getValue());
    }

    @EventHandler
    public void onMatchStarted(MatchStartedEvent event) {
        PlayerStateManager playerStateManager = PlayerStateManager.getInstance();
        event.getMatch().getConfig().contestants().forEach(contestant -> {
            if (!PlayerStateManager.getInstance().onState(contestant, stateId)) return;
            playerStateManager.toggle(contestant, PlayState.PLAYING.getValue());
        });
    }

    // ------ 其他内部行为 ------

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!PlayerStateManager.getInstance().onState(event.getPlayer(), stateId)) return;
        SpawnUtil.tpToSpawnPoint(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!PlayerStateManager.getInstance().onState(event.getPlayer(), stateId)) return;
        event.setRespawnLocation(SpawnUtil.getSpawnLocation());
    }
}