package io.github.qingranqae.voidairrace.corelayer.mapsystem.maps.lobby;

import io.github.qingranqae.voidairrace.corelayer.teamroster.TeamRoster;
import io.github.qingranqae.voidairrace.corelayer.teamroster.Teams;
import io.github.qingranqae.voidairrace.event.MatchOverEvent;
import io.github.qingranqae.voidairrace.event.MatchStartedEvent;
import io.github.qingranqae.voidairrace.util.worldutil.Region;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;

public class Listener implements org.bukkit.event.Listener {
    private boolean matchRunning;

    @EventHandler
    public void onMatchStarted(MatchStartedEvent event) {
        matchRunning = true;
    }

    @EventHandler
    public void onMatchOver(MatchOverEvent event) {
        matchRunning = false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (matchRunning) {
            return;
        }

        Player player = event.getPlayer();

        for (HashMap.Entry<Region, Teams> i: Const.getRegionToTeam().entrySet()) {
            Region region = i.getKey();
            Teams team = i.getValue();
            if (region.contains(player.getLocation())) { // 将玩家加入区域对应的队伍
                TeamRoster.getInstance().join(player, team);
                break;
            }
        }
    }
}
