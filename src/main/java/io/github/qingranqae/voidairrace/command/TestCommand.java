package io.github.qingranqae.voidairrace.command;

import io.github.qingranqae.voidairrace.mapregistry.maps.GrassLand;
import io.github.qingranqae.voidairrace.match.Match;
import io.github.qingranqae.voidairrace.match.MatchCoordinator;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor{

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Match match = MatchCoordinator.getInstance().getCurrentMatch();
        if (match != null) {
            if (match.getConfig().gameMap() instanceof GrassLand gl) {
                Bukkit.getServer().broadcast(Component.text(gl.test()));
            }
        }
        return true;
    }
}
