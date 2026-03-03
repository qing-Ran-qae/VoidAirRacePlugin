package io.github.qingranqae.voidairrace.core.playerstatemanager;

import io.github.qingranqae.voidairrace.core.config.GameSettingKey;
import io.github.qingranqae.voidairrace.event.ConfigFieldChangeEvent;
import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
    private final PlayerInitializer playerInitializer = PlayerInitializer.getInstance();

    @EventHandler
    public void onConfigChange(ConfigFieldChangeEvent event) {
        if (event.getField().equals(GameSettingKey.SPAWN_LOCATION)) {
            Util.updateSpawnLocation();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerInitializer.initializePlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPluginEnable(PluginEnableEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerInitializer.initializePlayer(player);
        }
    }
}
