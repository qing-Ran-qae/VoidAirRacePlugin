package io.github.qingranqae.voidairrace.core.teamsystem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

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
     */
    public void join(Entity entity, Teams team) {
        Team teamInst = enumToTeam(team);
        if (onTeam(entity, team)) return;
        if (entity instanceof Player player) player.setScoreboard(teamScb);
        teamInst.addEntity(entity);
    }

    /**
     * 使实体离开它的队伍
     */
    public void leave(Entity entity) {
        Team entityTeam = getEntityTeam(entity);
        if (entityTeam == null) return;
        if (entity instanceof Player player) player.setScoreboard(teamScb);
        entityTeam.removeEntity(entity);
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
     * @return 队伍实例列表的不可变视图
     * */
    public Collection<Team> getAllTeams() {
        return Collections.unmodifiableCollection(enumToTeamMap.values());
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
