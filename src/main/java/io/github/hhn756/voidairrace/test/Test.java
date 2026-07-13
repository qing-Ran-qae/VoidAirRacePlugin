package io.github.hhn756.voidairrace.test;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * 测试用
 * */
public class Test implements Listener {

    @EventHandler
    public void onPluginEnable(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.DIAMOND_BLOCK) {
            event.setCancelled(true);
        }
    }

    public static void a() {
    }

    public static void b() {
    }
}
