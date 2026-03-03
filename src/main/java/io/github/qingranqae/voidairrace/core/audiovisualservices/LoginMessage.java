package io.github.qingranqae.voidairrace.core.audiovisualservices;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginMessage implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(Component.translatable("void_air_race.audiovisualservices.login_message.line1"));
        player.playSound(Sound.sound(
                org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                Sound.Source.MASTER,
                1.0f,
                1.0f
        ));
    }
}
