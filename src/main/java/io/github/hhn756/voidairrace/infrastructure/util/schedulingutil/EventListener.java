package io.github.hhn756.voidairrace.infrastructure.util.schedulingutil;

import io.github.hhn756.voidairrace.event.PluginEnableEvent;
import io.github.hhn756.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * 调度工具的事件监听器
 * */
@AutoRegistration
public class EventListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPluginEnable(PluginEnableEvent event) {
        SchedulingUtil.init(event.getMainClass());
    }
}
