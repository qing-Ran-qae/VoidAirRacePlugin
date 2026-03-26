package io.github.qingranqae.voidairrace.test;

import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.github.qingranqae.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@AutoRegistration
public class Test implements Listener {
    private static JavaPlugin plugin;

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        plugin = event.getMainClass();
    }

    public static void a() {

    }

    public static void b() {
    }
}
