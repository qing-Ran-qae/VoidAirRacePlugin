package io.github.qingranqae.voidairrace.core.matchsystem;

import io.github.qingranqae.voidairrace.VoidAirRace;
import io.github.qingranqae.voidairrace.core.rulesystem.RuleManager;
import io.github.qingranqae.voidairrace.event.MatchStatusChangedEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

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
    private final VoidAirRace mainClass;

    /** 管理本局比赛中所有启用规则的规则管理器。 */
    private final RuleManager ruleManager;

    /** 所有存活玩家构成的列表 */
    private final ArrayList<Player> survivingPlayerList;

    /** 所有存活玩家构成的哈希表，用于{@code O(1)}检查玩家是否在比赛 */
    private final HashMap<Player, Boolean> survivingPlayerMap;

    /**
     * 构造一场新的比赛。
     *
     * @param config    比赛配置（不可为 null）
     * @param mainClass 插件主类实例
     */
    public Match(MatchConfig config, VoidAirRace mainClass) {
        this.config = config;
        this.remainingTime = config.duration();
        this.mainClass = mainClass;
        this.survivingPlayerList = new ArrayList<Player>(this.config.contestants());
        this.survivingPlayerMap = new HashMap<>();
        for (Player player : this.survivingPlayerList) {
            survivingPlayerMap.put(player, true);
        }
        this.ruleManager = new RuleManager(this);
    }

    /**
     * 获取比赛配置。
     *
     * @return 比赛配置对象
     */
    public MatchConfig getConfig() {
        return config;
    }

    /**
     * 获取剩余比赛时间。
     *
     * @return 剩余时间（单位为tick）
     */
    public int getRemainingTime() {
        return remainingTime;
    }

    /**
     * 设置剩余比赛时间。
     *
     * @param newValue 新剩余时间，必须大于等于 0（单位：tick）
     */
    public void setRemainingTime(int newValue) {
        int oldValue = this.remainingTime;
        this.remainingTime = newValue;
        if (oldValue != newValue) {
            new MatchStatusChangedEvent(this);
        }
    }

    /**
     * 获取插件主类实例。
     *
     * @return 插件主类实例
     */
    public VoidAirRace getMainClass() {
        return mainClass;
    }

    /**
     * 获取本局比赛的规则管理器。
     *
     * @return 规则管理器
     */
    public RuleManager getRuleManager() {
        return ruleManager;
    }

    /**
     * 获取存活玩家列表<br>
     * 注：返回值是克隆体，无法修改。淘汰玩家或中场加入需通过{@link Match#leaveMatch(Player)}和{@link Match#joinMatch(Player)}方法
     *
     * @return 存活玩家列表
     * */
    public ArrayList<Player> getSurvivingPlayerList() {
        return survivingPlayerList;
    }

    /**
     * 使指定玩家加入这场比赛
     * */
    public void joinMatch(Player player) {
        survivingPlayerList.add(player);
        survivingPlayerMap.put(player, true);
    }

    /**
     * 使指定玩家离开这场比赛
     * */
    public void leaveMatch(Player player) {
        survivingPlayerList.remove(player);
        survivingPlayerMap.remove(player);
    }

    /**
     * 检查指定玩家是否在这场比赛中
     *
     * @return {@code true}表示在比赛中，{@code false}表示不在
     * */
    public boolean onMatch(Player player) {
        return survivingPlayerMap.get(player) != null;
    }
}