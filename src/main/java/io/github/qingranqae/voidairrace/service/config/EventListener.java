package io.github.qingranqae.voidairrace.service.config;

import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.github.qingranqae.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import io.github.qingranqae.voidairrace.service.config.files.PublicFiles;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * 配置模块的事件监听器，负责在插件启用时初始化配置管理器并加载所有配置文件。
 */
@AutoRegistration
public class EventListener implements Listener {
    /**
     * 在插件启用事件触发时（优先级最低）初始化配置管理器并加载所有配置文件。
     *
     * @param event 插件启用事件
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPluginEnable(PluginEnableEvent event) {
        Config config = Config.getInstance(event.getMainClass());
        // 确保所有配置文件实例被创建
        for (PublicFiles file : PublicFiles.values()) {
            config.getConfig(file);
        }
    }
}