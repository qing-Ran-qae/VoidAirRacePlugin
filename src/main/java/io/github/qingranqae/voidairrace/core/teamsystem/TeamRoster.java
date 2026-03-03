package io.github.qingranqae.voidairrace.core.teamsystem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class TeamRoster {
    private static TeamRoster instance;

    public static TeamRoster getInstance() {
        if (instance == null) instance = new TeamRoster();
        return instance;
    }

    // ------

    private final Scoreboard teamScb;
    private final LinkedHashMap<Teams, Team> enumToTeamMap = new LinkedHashMap<>();
    private final LinkedHashMap<Team, Teams> teamToEnumMap = new LinkedHashMap<>();

    private TeamRoster() {
        // 创建队伍
        teamScb = Bukkit.getScoreboardManager().getNewScoreboard();
        for (Teams teamConfig : Teams.values()) {
            // 创建
            Team teamInst = teamScb.registerNewTeam(teamConfig.getName());

            // 记录
            enumToTeamMap.put(teamConfig, teamInst);
            teamToEnumMap.put(teamInst, teamConfig);

            // 设置
            teamInst.prefix(teamConfig.getPrefix());                                     // 前缀
            teamInst.displayName(teamConfig.getDisplayName());                           // 显示名
            teamInst.setAllowFriendlyFire(false);                                        // 友伤
            teamInst.setCanSeeFriendlyInvisibles(true);                                  // 隐身队友可见性
            teamInst.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);     // 碰撞规则
            teamInst.color(teamConfig.getColor());                                       // 队伍颜色
        }
    }

    /**
     * 添加实体到队伍
     *
     * @return 成功添加时返回{@code true}，如果之前就在队伍里则返回{@code false}
     */
    public boolean join(Entity entity, Teams team) {
        Team teamInst = enumToTeam(team);
        if (onTeam(entity, team)) {
            return false;
        }
        if (entity instanceof Player player) {
            player.setScoreboard(teamScb);
        }
        teamInst.addEntity(entity);
        return true;
    }

    /**
     * 使实体离开它的队伍
     *
     * @return 成功离队返回{@code true}，如果之前没有加入任何队伍返回 {@code false}
     */
    public boolean leave(Entity entity) {
        Team entityTeam = getEntityTeam(entity);
        if (entityTeam == null) {
            return false;
        }
        entityTeam.removeEntity(entity);
        return true;
    }

    /**
     * 检查实体是否在指定队伍中
     *
     * @return 如果在队伍中则返回{@code true}，否则返回{@code false}
     */
    public boolean onTeam(Entity entity, Teams team){
        return enumToTeam(team).hasEntity(entity);
    }

    /**
     * 获取指定实体所在的队伍
     *
     * @param entity 指定实体
     * @return 队伍实例
     * */
    public Team getEntityTeam(Entity entity) throws IllegalStateException {
        return teamScb.getEntityTeam(entity);
    }

    /**
     * 获取队伍枚举值对应的队伍实例
     * */
    public Team enumToTeam(Teams team) {
        return enumToTeamMap.get(team);
    }

    /**
     * 获取队伍实例对应的队伍枚举值，
     * 如果传入的对象不是本类实例化的队伍实例则返回 {@code null}
     * */
    public Teams teamToEnum(Team team) {
        return teamToEnumMap.get(team);
    }

    /**
     * 获取所有队伍实例
     *
     * @return 由队伍实例构成的集合
     * */
    public List<Team> getAllTeams() {
        return new ArrayList<>(enumToTeamMap.values());
    }

    /**
     * 获取队伍集合中的前 n 个队伍实例
     * */
    public List<Team> getFirstNTeams(int n) {
        if (n <= 0) return Collections.emptyList();
        List<Team> allTeams = new ArrayList<>(enumToTeamMap.values()); // teamMap 按枚举顺序插入，所以顺序固定
        return allTeams.subList(0, Math.min(n, allTeams.size()));
    }
}
