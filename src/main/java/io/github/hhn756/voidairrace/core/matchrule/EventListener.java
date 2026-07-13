package io.github.hhn756.voidairrace.core.matchrule;

import io.github.hhn756.voidairrace.event.PluginEnableEvent;
import io.github.hhn756.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AutoRegistration
public class EventListener implements Listener {
    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        RuleRegistry.load();
    }
}
