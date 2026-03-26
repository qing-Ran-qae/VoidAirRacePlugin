package io.github.qingranqae.voidairrace.core.matchsystem;

import io.github.qingranqae.voidairrace.core.mapsystem.PlayableGameMap;
import io.github.qingranqae.voidairrace.core.result.map.MapSelectedStartResult;
import io.github.qingranqae.voidairrace.core.result.match.MatchOnStartResult;
import io.github.qingranqae.voidairrace.core.rulesystem.RuleManager;
import io.github.qingranqae.voidairrace.core.teamsystem.TeamRoster;
import io.github.qingranqae.voidairrace.event.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * 代表一局正在进行或已安排的比赛。
 * 包含比赛的配置、剩余时间、关联的规则管理器以及插件主类实例。
 */
public class Match {
    /** 比赛的配置信息（地图、时长、参赛者等）。 */
    private final MatchConfig config;

    /** 比赛剩余时间（单位：tick）。 */
    private int remainingTime;

    /** 插件主类实例，用于调度任务和访问插件全局资源。 */
    private final JavaPlugin mainClass;

    /** 管理本局比赛中所有启用规则的规则管理器。 */
    private final RuleManager ruleManager;

    /** 所有存活玩家构成的列表 */
    private final ArrayList<Player> survivingPlayerList = new ArrayList<>();

    /** 所有存活玩家构成的哈希表，用于{@code O(1)}检查玩家是否在比赛 */
    private final HashMap<Player, Boolean> survivingPlayerMap = new HashMap<>();

    /** 队伍存活情况，键为队伍，值为对应队伍的存活玩家总量 */
    private final HashMap<Team, Byte> survivingTeams = new HashMap<>();

    /** 比赛涉及到的竞技场 */
    private final Set<MatchScope> scopes = new HashSet<>();

    /**
     * 记录已被淘汰、不可加入比赛的所有玩家<br>
     * 键表示一个已淘汰的玩家，值用于占位
     * */
    private final HashMap<Player, Boolean> rankedExitedPlayers;

    /**
     * 构造一场新的比赛
     *
     * @param config    比赛配置（不可为 null）
     * @param mainClass 插件主类实例
     */
    public Match(MatchConfig config, JavaPlugin mainClass) {
        this.config = config;
        this.remainingTime = config.duration();
        this.mainClass = mainClass;
        this.ruleManager = new RuleManager(this);
        this.rankedExitedPlayers = new HashMap<>(
                this.config.contestants().size()
        );

        TeamRoster teamRoster = TeamRoster.getInstance();
        for (Player player : config.contestants()) {
            survivingPlayerMap.put(player, true);

            Team playerTeam = teamRoster.getEntityTeam(player);
            if (!survivingTeams.containsKey(playerTeam)) {
                survivingTeams.put(playerTeam, (byte) 0);
            }
            survivingTeams.put(playerTeam, (byte)(survivingTeams.get(playerTeam) + 1));
        }
    }

    /**
     * 比赛开始时执行一次
     * */
    public MatchOnStartResult onStart() {
        // 调用选中地图的 selectedStart 方法
        PlayableGameMap matchMap = this.getConfig().gameMap();
        MapSelectedStartResult mapStartResult = matchMap.selectedStart(this);
        if (!mapStartResult.isSuccess()) {
            Component message = mapStartResult.getDisplayMessage();
            if (message == null) return MatchOnStartResult.failure(
                    Component.translatable("void_air_race.match.match_coordinator.start_match.map_operation_denied.unknown_cause"));
            return MatchOnStartResult.failure(
                    Component.translatable("void_air_race.match.match_coordinator.start_match.map_operation_denied.specified_reason")
                            .arguments(message));
        }

        // 注册是监听器的游戏地图
        if (matchMap instanceof Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, mainClass);
        }

        // 发布比赛开始事件
        new MatchStartedEvent(this).callEvent();

        // 启动规则管理器
        this.getRuleManager().setup();

        return MatchOnStartResult.success();
    }

    /**
     * 比赛结束时执行一次
     * */
    public void onOver() {
        // 调用选中地图的 selectedOver 方法
        this.getConfig().gameMap().selectedOver(this);

        // 关闭规则管理器
        this.getRuleManager().shutdown();

        // 注销是监听器的游戏地图
        if (this.getConfig().gameMap() instanceof Listener listener) {
            HandlerList.unregisterAll(listener);
        }

        // 发布比赛结束事件
        new MatchOverEvent(
                this,
                rankedExitedPlayers.keySet().stream().toList()
        ).callEvent();

        // 归还所有涉及的竞技场
        for (MatchScope scope : scopes) {
            scope.token().returnArena();
        }
    }

    /**
     * 获取比赛配置
     *
     * @return 比赛配置对象
     */
    public MatchConfig getConfig() {
        return config;
    }

    /**
     * 获取剩余比赛时间
     *
     * @return 剩余时间（单位为tick）
     */
    public int getRemainingTime() {
        return remainingTime;
    }

    /**
     * 设置剩余比赛时间
     *
     * @param newValue 新剩余时间，必须大于等于 0（单位：tick）
     */
    public void setRemainingTime(int newValue) {
        int oldValue = this.remainingTime;
        this.remainingTime = newValue;
        if (oldValue != newValue) {
            new MatchStatusChangedEvent(this).callEvent();
        }
    }

    /**
     * 获取插件主类实例
     *
     * @return 插件主类实例
     */
    public JavaPlugin getMainClass() {
        return mainClass;
    }

    /**
     * 获取本局比赛的规则管理器
     *
     * @return 规则管理器
     */
    public RuleManager getRuleManager() {
        return ruleManager;
    }

    /**
     * 获取存活玩家列表
     *
     * @return 存活玩家列表的不可变视图。淘汰玩家或中场加入需通过{@link Match#leaveMatch(Player)}和{@link Match#joinMatch(Player)}方法
     * */
    public List<Player> getSurvivingPlayerList() {
        return Collections.unmodifiableList(this.survivingPlayerList);
    }

    /**
     * 获取所有存活玩家构成的哈希表
     *
     * @return 存活玩家哈希表的只读视图，无法修改。淘汰玩家或中场加入需通过{@link Match#leaveMatch(Player)}和{@link Match#joinMatch(Player)}方法
     * */
    public Map<Player, Boolean> getSurvivingPlayerMap() {
        return Collections.unmodifiableMap(this.survivingPlayerMap);
    }

    /**
     * 使指定玩家加入这场比赛，如果指定玩家之前就在比赛中或已被淘汰那么不会执行任何操作
     *
     * @param player 指定玩家
     * */
    public void joinMatch(Player player) {
        if (survivingPlayerMap.containsKey(player)) return;
        if (rankedExitedPlayers.containsKey(player)) return;

        // 更新存活玩家
        survivingPlayerList.add(player);
        survivingPlayerMap.put(player, true);

        // 更新存活队伍
        Team playerTeam = TeamRoster.getInstance().getEntityTeam(player);
        survivingTeams.put(playerTeam, (byte) (survivingTeams.get(playerTeam) + 1));

        // 发布事件
        new PlayerJoinMatchEvent(player, this).callEvent();
        new MatchStatusChangedEvent(this).callEvent();
    }

    /**
     * 使指定玩家离开这场比赛（即淘汰），如果指定玩家之前 不在 比赛中那么不会执行任何操作
     *
     * @param player 指定玩家
     * */
    public void leaveMatch(Player player) {
        if (!survivingPlayerMap.containsKey(player)) {
            return;
        };

        // 更新存活玩家
        survivingPlayerList.remove(player);
        survivingPlayerMap.remove(player);
        rankedExitedPlayers.put(player, true);

        // 更新存活队伍
        Team playerTeam = TeamRoster.getInstance().getEntityTeam(player);
        survivingTeams.put(playerTeam, (byte) (survivingTeams.get(playerTeam) - 1));

        // 发布事件
        new PlayerLeaveMatchEvent(player, this).callEvent();
        new MatchStatusChangedEvent(this).callEvent();
    }

    /**
     * 检查指定玩家是否在这场比赛中
     *
     * @return {@code true}表示在比赛中，{@code false}表示不在
     * */
    public boolean isOnMatch(Player player) {
        return survivingPlayerMap.containsKey(player);
    }

    public Map<Team, Byte> getSurvivingTeams() {
        return Collections.unmodifiableMap(survivingTeams);
    }

    /**
     * 获取比赛中存活的（起码有1名玩家存活）队伍总数
     * */
    public Byte getSurvivingTeamNum() {
        Byte result = 0;
        for (Byte survivingPlayerNum : survivingTeams.values()) {
            if (survivingPlayerNum > 0) result++;
        }
        return result;
    }

    /**
     * 获取比赛涉及到的所有竞技场
     *
     * @return 竞技场借据列表的不可变视图
     * */
    public Set<MatchScope> getScopes() {
        return Collections.unmodifiableSet(scopes);
    }

    /**
     * 获取具有指定的所有标签的 “比赛涉及范围”
     * */
    public List<MatchScope> getScopeByTags(String[] tags) {
        List<MatchScope> result = new ArrayList<>();
        for (MatchScope scope : scopes) {
            // 检查是否包含所有指定的标签
            for (String tag : tags) {
                if (!scope.tags().containsKey(tag)) {
                    continue;
                }
                // 匹配成功时添加结果项
                result.add(scope);
            }
        }
        return result;
    }

    /**
     * 拓宽比赛的涉及范围，让比赛涉及到指定竞技场
     */
    public void addScope(MatchScope scope) {
        scopes.add(scope);
    }
}