package io.github.qingranqae.voidairrace.core.mapsystem;

import io.github.qingranqae.voidairrace.core.arenasystem.ArenaManager;
import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.github.qingranqae.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@AutoRegistration
public class EventListener implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onPluginEnable(PluginEnableEvent event) {
        JavaPlugin mainClass = event.getMainClass();
        // 初始化 竞技场管理器
        ArenaManager.getInstance(mainClass);
        // 初始化 地图注册表
        MapRegistry.getInstance(mainClass);
        // 初始化 所有地图
        MapInitializer.getInstance(mainClass).initAllMapsAsync();
    }
}
