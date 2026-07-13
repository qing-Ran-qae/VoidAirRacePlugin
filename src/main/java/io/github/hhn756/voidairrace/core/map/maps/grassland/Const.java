package io.github.hhn756.voidairrace.core.map.maps.grassland;

import io.github.hhn756.voidairrace.constants.Namespace;
import io.github.hhn756.voidairrace.core.matchrule.MatchRule;
import io.github.hhn756.voidairrace.core.matchrule.generalrules.BasicEndDetermination;
import io.github.hhn756.voidairrace.core.team.TeamRoster;
import io.github.hhn756.voidairrace.infrastructure.util.world.BlockRegion;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;

class Const {
    static final HashMap<Team, Location> TEAM_TO_SPAWN_LOCATION = new HashMap<>();
    static final BlockRegion[] SUPPLY_BOX_SEARCH_RANGE = {
            new BlockRegion(-35, 65, 2, -2, 65, 2),
            new BlockRegion(-26, 64, 69, -4, 64, 71),
            new BlockRegion(-25, 43, 344, -25, 43, 344),
            new BlockRegion(-25, 51, 351, -25, 51, 351),
            new BlockRegion(-38, 48, 353, 5, 60, 376),
            new BlockRegion(13, 50, 356, 13, 50, 356),
            new BlockRegion(-33, 65, 357, 0, 65, 357),
            new BlockRegion(-18, 61, 364, -12, 81, 372)
    };
    static final NamespacedKey MAP_ID = Namespace.of("grass_land");

    static {
        List<Team> first4Teams = TeamRoster.getInstance().getFirstNTeams(4);
        TEAM_TO_SPAWN_LOCATION.put(first4Teams.get(0), new Location(null, 1.5d, 65d, 0.5d));
        TEAM_TO_SPAWN_LOCATION.put(first4Teams.get(1), new Location(null, -0.5d, 65d, 0.5d));
        TEAM_TO_SPAWN_LOCATION.put(first4Teams.get(2), new Location(null, -21.5d, 65d, 0.5d));
        TEAM_TO_SPAWN_LOCATION.put(first4Teams.get(3), new Location(null, -32.5d, 65d, 0.5d));
    }

    static final List<Class<? extends MatchRule>> USE_RULES = List.of(
            BasicEndDetermination.class
    );
}
