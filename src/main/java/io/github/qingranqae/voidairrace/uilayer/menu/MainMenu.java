package io.github.qingranqae.voidairrace.uilayer.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MainMenu implements Listener {
    private static class MenuInv implements InventoryHolder {
        Inventory inventory;

        public MenuInv() {
             Inventory inv = Bukkit.getServer().createInventory(this, 27, "Menu");
             this.inventory = inv;

             inv.setItem(18, new ItemStack(Material.DIAMOND, 1));
        }

        @Override
        @NotNull
        public Inventory getInventory() {
            return this.inventory;
        }
    }
    private static final Inventory menuInv = new MenuInv().getInventory();

    public static void open(Player player) {
        player.openInventory(menuInv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder(false) instanceof MenuInv) {
            event.setCancelled(true);
            Bukkit.getServer().broadcast(Component.text("点击了菜单按钮！"));
        }
    }
}
