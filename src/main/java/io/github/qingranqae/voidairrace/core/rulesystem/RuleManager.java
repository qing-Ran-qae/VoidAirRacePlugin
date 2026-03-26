package io.github.qingranqae.voidairrace.core.rulesystem;

import io.github.qingranqae.voidairrace.core.matchsystem.Match;
import io.github.qingranqae.voidairrace.core.result.matchrule.ManagerEnableRuleResult;
import io.github.qingranqae.voidairrace.core.result.matchrule.MatchRuleEnableResult;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

/**
 * 绑定到一局比赛的规则管理器<br>
 * 负责管理规则的启用和禁用
 * */
public class RuleManager {
    private Match match;
    private final JavaPlugin mainClass;
    private final List<MatchRule> activeRuleList = new ArrayList<>();
    /** 用于 O(1) 查询已启用的规则，键为一个已启用的规则，值固定为{@code true} */
    private final HashMap<MatchRule, Boolean> activeRuleMap = new HashMap<>();
    /** 记录监听器，方便注销事件 */
    private final Map<MatchRule, Listener> listeners = new HashMap<>();
    private Integer currentTickTaskId;

    public RuleManager(Match match) {
        this.match = match;
        this.mainClass = match.getMainClass();
    }

    /**
     * 启用或向比赛插入一个规则<br>
     * 如果目标规则是 Bukkit 事件监听器，那么会自动 注册 它
     *
     * @param rule 要启用的规则
     *
     * @return 规则是否成功启用
     */
    public ManagerEnableRuleResult enableRule(MatchRule rule) {
        if (isEnabled(rule)) return  ManagerEnableRuleResult.failure(
                Component.translatable("void_air_race.match_rule.rule_manager.try_re_enable")
        );

        MatchRuleEnableResult ruleEnableResult = rule.onEnable(match);
        if (!ruleEnableResult.isSuccess()) {
            return ManagerEnableRuleResult.failure(
                    Component.translatable("void_air_race.match_rule.rule_manager.prohibit_enable"));
        }

        // 记录启用的规则
        activeRuleList.add(rule);
        activeRuleMap.put(rule, true);

        // 如果规则实现了事件监听器，注册并保存引用以便后续注销
        if (rule instanceof Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, match.getMainClass());
            listeners.put(rule, listener);
        }

        return ManagerEnableRuleResult.success();
    }

    /**
     * 禁用一个规则<br>
     * 如果目标规则是 Bukkit 事件监听器，那么会自动 注销 它
     *
     * @param rule 指定规则
     */
    public void disableRule(MatchRule rule) {
        if (!activeRuleMap.containsKey(rule)) return;
        rule.onDisable(match);
        // 注销事件监听器
        if (listeners.containsKey(rule)) {
            HandlerList.unregisterAll(listeners.get(rule));
            listeners.remove(rule);
        }
        activeRuleList.remove(rule);
        activeRuleMap.remove(rule);
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
     * @return 启用规则列表的不可变视图
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

    /**
     * 检查指定的规则实例是否已启用
     *
     * @param rule 指定规则实例
     *
     * @return {@code true}表示已启用，{@code false}表示未启用
     * */
    public boolean isEnabled(MatchRule rule) {
        return activeRuleMap.containsKey(rule);
    }

    /**
     * 检查是否已启用指定规则类型的任意一个实例
     *
     * @param clazz 指定规则类型
     *
     * @return {@code true}表示指定的规则类型至少有一个实例已在此规则管理器实例中启用，{@code false}表示没有任何实例启用
     * */
    public boolean isEnabledType(Class<? extends MatchRule> clazz) {
        boolean result = false;
        for (MatchRule rule : activeRuleList) {
            if (rule.getClass().equals(clazz)) {
                result = true;
                break;
            }
        }
        return result;
    }
}