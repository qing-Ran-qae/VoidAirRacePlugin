package io.github.qingranqae.voidairrace.core.playerstatemanager;

import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.github.qingranqae.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AutoRegistration
public class EventListener implements Listener {
    private final PlayerInitializer playerInitializer = PlayerInitializer.getInstance();

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerInitializer.initializePlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPluginEnable(PluginEnableEvent event) {
        // 初始化状态类型注册表
        StateRegistry.getInstance(event.getMainClass());

        // 初始化玩家
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerInitializer.initializePlayer(player);
        }
    }
}
