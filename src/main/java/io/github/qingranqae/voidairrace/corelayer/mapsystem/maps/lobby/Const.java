package io.github.qingranqae.voidairrace.corelayer.mapsystem.maps.lobby;

import io.github.qingranqae.voidairrace.corelayer.teamroster.Teams;
import io.github.qingranqae.voidairrace.util.worldutil.Region;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;

public class Const {
    private static final String MAP_ID = "Lobby";
    private static final World MAP_WORLD = Bukkit.getWorld("world");

    private static final HashMap<Region, Teams> regionToTeam = new HashMap<>();

    static {
        double firstStartX = 27.0;
        double teamAreaStartY = 64.0;
        double teamAreaStartZ = 39.0;
        double teamAreaStartX;
        int xOffset = 0;
        for (Teams team: Teams.values()) {
            teamAreaStartX = firstStartX + xOffset;
            Region region = new Region(
                    MAP_WORLD,
                    teamAreaStartX, teamAreaStartY, teamAreaStartZ,
                    teamAreaStartX - 4, teamAreaStartY + 4, teamAreaStartZ + 4
            );
            regionToTeam.put(region, team);
            xOffset -= 7;
        }
    }

    public static HashMap<Region, Teams> getRegionToTeam() {
        return regionToTeam;
    }

    public static String getMapId() {
        return MAP_ID;
    }
}
