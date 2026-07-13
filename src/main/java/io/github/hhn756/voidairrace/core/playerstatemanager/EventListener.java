package io.github.hhn756.voidairrace.core.playerstatemanager;

import io.github.hhn756.voidairrace.event.PluginEnableEvent;
import io.github.hhn756.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * 玩家状态管理器的事件监听器
 * */
@AutoRegistration
public class EventListener implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerInitializer.getInstance().initializePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        StateRegistry.load();
        PlayerInitializer.load();
        PlayerStateManager.load();
        // 初始化玩家
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerInitializer.getInstance().initializePlayer(player);
        }
    }
}
