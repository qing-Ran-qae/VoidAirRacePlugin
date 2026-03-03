package io.github.qingranqae.voidairrace.core.mapsystem.maps.grassland;

import io.github.qingranqae.voidairrace.core.mapsystem.PlayableGameMap;
import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import io.github.qingranqae.voidairrace.core.rulesystem.generalrules.UpdateMatchTime;
import io.github.qingranqae.voidairrace.core.teamsystem.TeamRoster;
import io.github.qingranqae.voidairrace.util.WorldCreatorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;

public class GrassLand implements PlayableGameMap {

    private static final String ID = "GrassLand";
    private static final World mapWorld = WorldCreatorUtil.createVoidWorld(ID);

    private static final HashMap<Team, Location> teamToSpawnLocation = new HashMap<>();

    static {
        List<Team> first4Teams = TeamRoster.getInstance().getFirstNTeams(4);
        teamToSpawnLocation.put(first4Teams.get(0), new Location(mapWorld, 0.5d, 65d, 0.5d));
        teamToSpawnLocation.put(first4Teams.get(1), new Location(mapWorld, -11.5d, 65d, 0.5d));
        teamToSpawnLocation.put(first4Teams.get(2), new Location(mapWorld, -22.5d, 65d, 0.5d));
        teamToSpawnLocation.put(first4Teams.get(3), new Location(mapWorld, -33.5d, 65d, 0.5d));
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("void_air_race.map.grassland.name");
    }

    @Override
    public Component getDescription() {
        return Component.translatable("void_air_race.map.grassland.description.line1");
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void selectedStart(Match match) {
        TeamRoster teamRoster = TeamRoster.getInstance();

        // 玩家进场
        for (Player player : match.getConfig().contestants()) {
            Team playerTeam = teamRoster.getEntityTeam(player);
            if (playerTeam != null) {
                player.teleport(teamToSpawnLocation.get(playerTeam));
            }
        }

        match.getRuleManager().enableRule(new UpdateMatchTime());
    }

    @Override
    public void selectedOver(Match match) {

    }

    @Override
    public int maxTeamsNumber() {
        return 4;
    }
}
