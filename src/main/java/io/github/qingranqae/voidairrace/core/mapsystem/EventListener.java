package io.github.qingranqae.voidairrace.core.mapsystem;

import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class EventListener implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onPluginEnable(PluginEnableEvent event) {
        // 初始化 地图注册表
        MapRegistry.getInstance(event.getMainClass());
        // 初始化 所有地图
        MapInitializer.getInstance().initAllMaps();
    }
}
