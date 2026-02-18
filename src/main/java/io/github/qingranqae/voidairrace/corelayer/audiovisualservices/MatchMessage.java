package io.github.qingranqae.voidairrace.corelayer.audiovisualservices;

import io.github.qingranqae.voidairrace.event.MatchStartedEvent;
import io.github.qingranqae.voidairrace.corelayer.matchsystem.Match;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MatchMessage implements Listener {
    @EventHandler
    public void onMatchStarted(MatchStartedEvent event) {
        Server server = Bukkit.getServer();
        Match match = event.getMatch();
        server.broadcast(Component.translatable("void_air_race.audiovisualservices.match_message.match_start.line1"));
        server.broadcast(
                Component.translatable("void_air_race.audiovisualservices.match_message.match_start.line2")
                        .arguments(match.getConfig().gameMap().getDisplayName())
        );
        server.broadcast(
                Component.translatable("void_air_race.audiovisualservices.match_message.match_start.line3")
                        .arguments(Component.text(match.getConfig().duration() / 20d))
        );
        server.broadcast(Component.translatable("void_air_race.audiovisualservices.match_message.match_start.line4"));
    }
}
