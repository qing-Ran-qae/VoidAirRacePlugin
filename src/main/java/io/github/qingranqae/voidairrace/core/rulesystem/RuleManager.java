package io.github.qingranqae.voidairrace.core.rulesystem;

import io.github.qingranqae.voidairrace.VoidAirRace;
import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import io.github.qingranqae.voidairrace.exception.ProhibitEnablingRuleException;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;
import java.util.logging.Logger;

/**
 * 绑定到一局比赛的规则管理器<br>
 * 负责管理规则的启用和禁用
 * */
public class RuleManager {
    private Match match;
    private final VoidAirRace mainClass;
    private final List<MatchRule> activeRuleList = new ArrayList<>();
    /** 记录监听器，方便注销事件 */
    private final Map<MatchRule, Listener> listeners = new HashMap<>();
    private Integer currentTickTaskId;
    private final Logger logger;

    public RuleManager(Match match) {
        this.match = match;
        this.mainClass = match.getMainClass();
        this.logger = match.getMainClass().getLogger();
    }

    /**
     * 启用或向比赛插入一个规则
     *
     * @param rule 指定规则
     *
     * @throws IllegalStateException 重复同一启用规则时抛出
     * @throws ProhibitEnablingRuleException 规则主动取消这次启用时抛出
     */
    public void enableRule(MatchRule rule) throws IllegalStateException, ProhibitEnablingRuleException {
        if (activeRuleList.contains(rule)) throw new IllegalStateException("重复启用规则");
        activeRuleList.add(rule);
        rule.onEnable(match);
        // 如果规则实现了事件监听器，注册并保存引用以便后续注销
        if (rule instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) rule, match.getMainClass());
            listeners.put(rule, (Listener) rule);
        }
    }

    /**
     * 禁用一个规则
     *
     * @param rule 指定规则
     */
    public void disableRule(MatchRule rule) {
        if (!activeRuleList.contains(rule)) return;
        rule.onDisable(match);
        // 注销事件监听
        if (listeners.containsKey(rule)) {
            HandlerList.unregisterAll(listeners.get(rule));
            listeners.remove(rule);
        }
        activeRuleList.remove(rule);
    }

    /**
     * 禁用所有规则（比赛结束时调用）
     */
    public void disableAll() {
        new ArrayList<>(activeRuleList).forEach(this::disableRule);
    }

    /**
     * 每 tick 调用所有规则的 tick 方法
     */
    private void tickAll() {
        // 防止 规则 更改 规则列表 导致 bug
        new ArrayList<>(activeRuleList).forEach(rule -> rule.tick(match));
    }

    /**
     * 获取所有已启用的规则
     *
     * @return 启用规则列表
     * */
    public List<MatchRule> getActiveRuleList() {
        return Collections.unmodifiableList(activeRuleList);
    }

    /**
     * 启动规则的 tick 调度器
     * 用于每 tick 调用所有已启用规则的 tick 方法
     */
    private void startTick() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        currentTickTaskId = scheduler.scheduleSyncRepeatingTask(mainClass, this::tickAll, 0L, 1L);
    }

    /**
     * 启动规则的 tick 调度器
     *
     * @throws IllegalStateException 在未运行 tick 任务时调用此方法
     */
    private void stopTick() throws IllegalStateException {
        if (currentTickTaskId == null) {
            throw new IllegalStateException("未运行 tick 任务，无法停止");
        }
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.cancelTask(this.currentTickTaskId);
        this.currentTickTaskId = null;
    }

    /**
     * 比赛开始时调用
     * */
    public void setup() {
        startTick();
    }

    /**
     * 比赛结束时调用
     * */
    public void shutdown() {
        stopTick();
        disableAll();
        this.match = null; // 防止循环引用影响 GC
    }
}