package io.github.qingranqae.voidairrace.infrastructure.util.schedulingutil;

import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.github.qingranqae.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AutoRegistration
public class EventListener implements Listener {
    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        SchedulingUtil.init(event.getMainClass());
    }
}