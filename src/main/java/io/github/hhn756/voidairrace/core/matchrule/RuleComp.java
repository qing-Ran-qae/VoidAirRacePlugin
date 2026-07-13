package io.github.hhn756.voidairrace.core.matchrule;

import io.github.hhn756.voidairrace.VoidAirRace;
import io.github.hhn756.voidairrace.constants.TranslateKeys;
import io.github.hhn756.voidairrace.core.match.ComponentPriority;
import io.github.hhn756.voidairrace.core.match.Match;
import io.github.hhn756.voidairrace.core.match.componentbase.*;
import io.github.hhn756.voidairrace.core.result.base.OperationResult;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class RuleComp extends MatchComp
        implements StartableComp<CustomData, CustomData>,
        EndableComp<CustomData, CustomData> {

    private Match match;
    private BukkitTask tickTask;
    private final Set<MatchRule> activeRules = new HashSet<>();
    private final Map<MatchRule, Listener> ruleListeners = new HashMap<>();

    @Override
    public @NonNull DataKey<?> getSCK() {
        return DataKey.of(RuleComp.class, CustomData.class);
    }

    @Override
    public int getInstallPriority() {
        return ComponentPriority.EXTREMELY_LOW.getValue();
    }

    @Override
    public StartableComp.@NonNull InstallResult<CustomData> install(@NonNull Match match, CustomData startArg) {
        this.match = match;
        // 启动 tick 调度器
        tickTask = Bukkit.getScheduler().runTaskTimer(VoidAirRace.getInstance(), () -> {
            new ArrayList<>(activeRules).forEach(rule -> rule.tick(match));
        }, 0L, 1L);
        return InstallResult.success(null);
    }

    @Override
    public @NonNull DataKey<?> getECK() {
        return DataKey.of(RuleComp.class, CustomData.class);
    }

    @Override
    public int getUninstallPriority() {
        return ComponentPriority.HIGH.getValue();
    }

    @Override
    public @NonNull ComponentUninstallResult<CustomData> uninstall(@NonNull Match match, CustomData endArg) {
        if (tickTask != null) {
            tickTask.cancel();
            tickTask = null;
        }
        disableAllRules();
        return ComponentUninstallResult.success(null);
    }

    /**
     * 启用指定id的规则<br>
     * 如果规则是 Bukkit 事件监听器，那么会自动 注册 它
     *
     * @param ruleId 指定规则id
     *
     * @return 启用结果
     */
    public ManagerEnableRuleResult enableRule(NamespacedKey ruleId) {
        RuleRegistry.CreateRuleResult createResult = RuleRegistry.getInstance().createRule(ruleId);
        if (!createResult.isSuccess()) {
            return new ManagerEnableRuleResult(false, createResult.getDisplayMessage());
        }
        MatchRule rule = createResult.getValue();
        return enableRule(rule);
    }

    /**
     * 启用一个已实例化的规则<br>
     * 如果规则是 Bukkit 事件监听器，那么会自动 注册 它
     *
     * @param rule 指定规则
     */
    private ManagerEnableRuleResult enableRule(MatchRule rule) {
        if (activeRules.contains(rule)) {
            return new ManagerEnableRuleResult(false, Component.translatable(TranslateKeys.Rule.RuleComp.ALREADY_ENABLED));
        }
        MatchRule.RuleEnableResult enableResult = rule.onEnable(match);
        if (!enableResult.isSuccess()) {
            return new ManagerEnableRuleResult(false, enableResult.getDisplayMessage());
        }
        activeRules.add(rule);
        if (rule instanceof Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, VoidAirRace.getInstance());
            ruleListeners.put(rule, listener);
        }
        return ManagerEnableRuleResult.success(null);
    }

    /**
     * 禁用所有规则
     */
    public void disableAllRules() {
        new ArrayList<>(activeRules).forEach(this::disableRule);
    }

    /**
     * 禁用指定规则实例<br>
     * 如果规则是 Bukkit 事件监听器，那么会自动 移除 它
     *
     * @param rule 指定规则实例
     */
    private void disableRule(MatchRule rule) {
        if (!activeRules.contains(rule)) return;
        rule.onDisable(match);
        if (ruleListeners.containsKey(rule)) {
            HandlerList.unregisterAll(ruleListeners.get(rule));
            ruleListeners.remove(rule);
        }
        activeRules.remove(rule);
    }

    /**
     * 获取当前启用的规则列表
     */
    public Set<MatchRule> getActiveRules() {
        return Collections.unmodifiableSet(activeRules);
    }

//    @Override
//    public void contributeToRecord(MatchRecord record) {
//        List<String> activeRuleIds = activeRules.stream()
//                .map(MatchRule::getId)
//                .toList();
//        record.getComponentData().put("ruleComponent.activeRuleIds", activeRuleIds);
//    }

    public static class ManagerEnableRuleResult extends OperationResult {
        /**
         * 构造一个操作结果
         *
         * @param success        操作是否成功，用{@code true}表示成功；用{@code false}表示失败
         * @param displayMessage 结果消息
         */
        public ManagerEnableRuleResult(boolean success, @Nullable Component displayMessage) {
            super(success, displayMessage);
        }

        public static ManagerEnableRuleResult success(@Nullable Component displayMessage) {
            return new ManagerEnableRuleResult(true, displayMessage);
        }
    }
}
