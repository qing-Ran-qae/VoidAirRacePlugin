package io.github.qingranqae.voidairrace.core.mapsystem.maps.lobby;

import io.github.qingranqae.voidairrace.core.teamsystem.Teams;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;

class Const {
    private static final String MAP_ID = "Lobby";
    private static final World MAP_WORLD = Bukkit.getWorld("world");

    private static final HashMap<BoundingBox, teamArea> regionToTeam = new HashMap<BoundingBox, teamArea>();

    static {
        double firstStartX = 27.0;
        double teamAreaStartY = 64.0;
        double teamAreaStartZ = 42.0;
        double teamAreaStartX;
        double xOffset = 0d;
        int areaId = 1;
        for (Teams team: Teams.values()) {
            teamAreaStartX = firstStartX + xOffset;
            BoundingBox region = new BoundingBox(
                    teamAreaStartX, teamAreaStartY, teamAreaStartZ,
                    teamAreaStartX - 4, teamAreaStartY + 4, teamAreaStartZ + 4
            );
            regionToTeam.put(region, new teamArea(team, areaId));
            xOffset -= 7;
            areaId++;
        }
    }

    public static HashMap<BoundingBox, teamArea> getRegionToTeam() {
        return regionToTeam;
    }

    public static String getMapId() {
        return MAP_ID;
    }

    public static World getMapWorld() {
        return MAP_WORLD;
    }
}
