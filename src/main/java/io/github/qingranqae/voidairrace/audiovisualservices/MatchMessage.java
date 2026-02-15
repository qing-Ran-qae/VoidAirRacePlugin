package io.github.qingranqae.voidairrace.audiovisualservices;

import io.github.qingranqae.voidairrace.match.MatchStartedEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MatchMessage implements Listener {
    @EventHandler
    public void onMatchStarted(MatchStartedEvent event) {
        Server server = Bukkit.getServer();
        server.broadcast(Component.translatable(""));
    }
}
