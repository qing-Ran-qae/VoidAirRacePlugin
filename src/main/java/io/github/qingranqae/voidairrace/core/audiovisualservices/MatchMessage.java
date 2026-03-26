package io.github.qingranqae.voidairrace.core.audiovisualservices;

import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import io.github.qingranqae.voidairrace.event.MatchOverEvent;
import io.github.qingranqae.voidairrace.event.MatchStartedEvent;
import io.github.qingranqae.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@AutoRegistration
public class MatchMessage implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onMatchStarted(MatchStartedEvent event) {
        Server server = Bukkit.getServer();
        Match match = event.getMatch();
        server.broadcast(Component.translatable("void_air_race.audiovisualservices.match_message.match_started.line1"));
        server.broadcast(
                Component.translatable("void_air_race.audiovisualservices.match_message.match_started.line2")
                        .arguments(match.getConfig().gameMap().getDisplayName())
        );
        server.broadcast(
                Component.translatable("void_air_race.audiovisualservices.match_message.match_started.line3")
                        .arguments(Component.text(match.getConfig().duration() / 20d))
        );
        server.broadcast(Component.translatable("void_air_race.audiovisualservices.match_message.match_started.line4"));
        Sound sound = Sound.sound(
                org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                Sound.Source.MASTER,
                1.0f,
                1.0f
        );
        event.getMatch().getConfig().contestants().forEach(contestant -> contestant.playSound(sound));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMatchOver(MatchOverEvent event) {
        Server server = Bukkit.getServer();
        server.broadcast(Component.translatable("void_air_race.audiovisualservices.match_message.match_over.line1"));
        Sound sound = Sound.sound(
                org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                Sound.Source.MASTER,
                1.0f,
                1.0f
        );
        event.getMatch().getConfig().contestants().forEach(contestant -> contestant.playSound(sound));
    }
}
