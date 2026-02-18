package io.github.qingranqae.voidairrace.command;

import io.github.qingranqae.voidairrace.uilayer.menu.MainMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class MenuCommand implements CommandExecutor {
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
        if (sender instanceof Player player) {
            player.sendMessage("你打开了菜单");
            MainMenu.open(player);
            return true;
        }
        return false;
    }
}
