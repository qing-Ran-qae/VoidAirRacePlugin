package io.github.qingranqae.voidairrace.core.mapsystem.maps.lobby;

import io.github.qingranqae.voidairrace.core.teamsystem.Teams;
import io.github.qingranqae.voidairrace.util.worldutil.Region;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;

class Const {
    private static final String MAP_ID = "Lobby";
    private static final World MAP_WORLD = Bukkit.getWorld("world");

    private static final HashMap<Region, teamArea> regionToTeam = new HashMap<>();

    static {
        double firstStartX = 27.0;
        double teamAreaStartY = 64.0;
        double teamAreaStartZ = 39.0;
        double teamAreaStartX;
        double xOffset = 0d;
        int areaId = 1;
        for (Teams team: Teams.values()) {
            teamAreaStartX = firstStartX + xOffset;
            Region region = new Region(
                    MAP_WORLD,
                    teamAreaStartX, teamAreaStartY, teamAreaStartZ,
                    teamAreaStartX - 4, teamAreaStartY + 4, teamAreaStartZ + 4
            );
            regionToTeam.put(region, new teamArea(team, areaId));
            xOffset -= 7;
            areaId++;
        }
    }

    public static HashMap<Region, teamArea> getRegionToTeam() {
        return regionToTeam;
    }

    public static String getMapId() {
        return MAP_ID;
    }

    public static World getMapWorld() {
        return MAP_WORLD;
    }
}
