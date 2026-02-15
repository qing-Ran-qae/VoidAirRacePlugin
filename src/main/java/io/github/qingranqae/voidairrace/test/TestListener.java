package io.github.qingranqae.voidairrace.test;

import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;

public class TestListener implements Listener {
    @EventHandler
    public void onPlayerDamageSheep(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity wounded = event.getEntity();
        if (damager instanceof Player && wounded instanceof Sheep sheep) {
            event.setCancelled(true);
            DyeColor[] colors = DyeColor.values();
            int idx = new Random().nextInt(colors.length - 1);
            sheep.setColor(colors[idx]);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(Component.text("Hello, " + event.getPlayer().getName() + "!"));
    }
}
