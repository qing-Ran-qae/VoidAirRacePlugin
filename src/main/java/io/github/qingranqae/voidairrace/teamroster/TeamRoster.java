package io.github.qingranqae.voidairrace.teamroster;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;

public class TeamRoster {
    private TeamRoster() {};

    private static Scoreboard teamScb;
    private static HashMap<Teams, Team> teamMap;

    static {
        // 创建队伍
        teamScb = Bukkit.getScoreboardManager().getNewScoreboard();
        for (Teams teamConfig : Teams.values()) {
            // 创建
            Team teamInst = teamScb.registerNewTeam(teamConfig.getName());

            // 记录
            teamMap.put(teamConfig, teamInst);

            // 设置
            teamInst.prefix(teamNameToPrefix(teamInst.getName()));
            teamInst.displayName(teamNameToDisplayName(teamInst.getName()));
        }
    }

    /**
     * 添加实体到队伍
     *
     * @return 成功添加时返回`true`，如果之前就在队伍里则返回`false`
     * */
    public static boolean join(Entity entity, Teams team) {
        Team teamInst = enumToTeam(team);
        if (onTeam(entity, team)) { return false; }
        teamInst.addEntity(entity);
        return true;
    }

    /**
     * 使实体离队
     *
     * @return 成功离队返回`true`，如果之前不在队伍里返回`false`
     * */
    public static boolean leave(Entity entity, Teams team) {
        Team teamInst = enumToTeam(team);
        if (onTeam(entity, team)) { return false; }
        teamInst.removeEntity(entity);
        return true;
    }

    /**
     * 检查实体是否在指定队伍中
     *
     * @return 如果在队伍中则返回`true`，否则返回`false`
     * */
    public static boolean onTeam(Entity entity, Teams team) throws IllegalStateException {
        Team teamInst = enumToTeam(team);
        if (entity == null) { throw new IllegalStateException("Entity is null"); }

        return teamInst.hasEntity(entity);
    }

    private static @NonNull Team enumToTeam(@NonNull Teams team) {
        return teamMap.get(team);
    }

    private static @NonNull TranslatableComponent teamNameToPrefix(@NonNull String name) {
        String key = "void_air_race.teamroster.team" + name + ".prefix";
        return Component.translatable(key);
    }

    private static @NonNull TranslatableComponent teamNameToDisplayName(@NonNull String name) {
        String key = "void_air_race.teamroster.team" + name + ".display_name";
        return Component.translatable(key);
    }
}
