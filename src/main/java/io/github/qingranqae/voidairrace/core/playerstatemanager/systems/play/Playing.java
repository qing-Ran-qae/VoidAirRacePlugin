package io.github.qingranqae.voidairrace.core.playerstatemanager.systems.play;

import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import io.github.qingranqae.voidairrace.core.matchsystem.MatchCoordinator;
import io.github.qingranqae.voidairrace.core.playerstatemanager.PlayerState;
import io.github.qingranqae.voidairrace.core.playerstatemanager.PlayerStateManager;
import io.github.qingranqae.voidairrace.event.MatchOverEvent;
import io.github.qingranqae.voidairrace.service.spawnutil.SpawnUtil;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class Playing implements PlayerState, Listener {
    private static final NamespacedKey id = PlayState.PLAYING.getValue();

    @Override
    public NamespacedKey getId() {
        return id;
    }

    @Override
    public void onCutin(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.clearActivePotionEffects();
        player.clearActiveItem();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!PlayerStateManager.getInstance().onState(player, id)) return;

        event.setRespawnLocation(SpawnUtil.getSpawnLocation());
        leaveMatch(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!PlayerStateManager.getInstance().onState(player, id)) return;

        leaveMatch(player);
    }

    @EventHandler
    public void onMatchOver(MatchOverEvent event) {
        event.getMatch().getSurvivingPlayerList().forEach(this::leaveMatch);
    }

    private void leaveMatch(Player player) {
        Match currentMatch = MatchCoordinator.getInstance().getCurrentMatch();
        if (currentMatch != null) currentMatch.leaveMatch(player);
        PlayerStateManager.getInstance().toggleStatus(player, PlayState.FREE.getValue());
    }
}