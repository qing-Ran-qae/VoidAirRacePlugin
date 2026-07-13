package io.github.hhn756.voidairrace.core.matchrule;

import io.github.hhn756.voidairrace.VoidAirRace;
import io.github.hhn756.voidairrace.constants.TranslateKeys;
import io.github.hhn756.voidairrace.core.result.base.ValueResult;
import io.github.hhn756.voidairrace.infrastructure.util.ClassScanner;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 规则注册表：扫描所有 MatchRule 实现类，按 ID 存储构造器，统一实例化规则。
 */
public class RuleRegistry {
    private static RuleRegistry instance;

    static void load() {
        instance = new RuleRegistry();
    }

    static void unload() {
        instance = null;
    }

    public static @NonNull RuleRegistry getInstance() throws NullPointerException {
        if (instance == null) throw new NullPointerException("比赛规则注册表实例不存在");
        return instance;
    }

    private final Map<NamespacedKey, Constructor<? extends MatchRule>> idToConstructor = new HashMap<>();
    private final Map<Class<MatchRule>, NamespacedKey> ruleClassToId = new HashMap<>();
    private final Logger logger = VoidAirRace.getInstance().getLogger();

    private RuleRegistry() {
        scanRules();
    }

    private void scanRules() {
        Collection<Class<MatchRule>> ruleClasses = ClassScanner.scanSubclasses(MatchRule.class);
        for (Class<MatchRule> clazz : ruleClasses) {
            try {
                // 尝试实例化一次以获取 ID（并验证无参构造器是否存在）
                MatchRule instance = clazz.getConstructor().newInstance();
                NamespacedKey id = instance.getElementMeta().id();

                if (id.getKey().isBlank()) {
                    logger.warning("比赛规则 " + clazz.getName() + "的 ID 缺失命名空间（字符串值存在但不包含字符），跳过注册");
                    continue;
                }
                if (idToConstructor.containsKey(id)) {
                    logger.warning("比赛规则ID '" + id + "' 已被 " + idToConstructor.get(id).getDeclaringClass().getName()
                            + " 注册，忽略重复的 " + clazz.getName());
                    continue;
                }

                idToConstructor.put(id, clazz.getConstructor());
                ruleClassToId.put(clazz, id);

                logger.fine("已注册比赛规则: " + id + " -> " + clazz.getName());
            } catch (NoSuchMethodException e) {
                logger.warning("比赛规则 " + clazz.getName() + " 没有公共无参构造器，无法注册");
            } catch (Exception e) {
                logger.warning("注册比赛规则 " + clazz.getName() + " 时发生异常: " + e.getMessage());
            }
        }
    }

    /**
     * 检查某个规则 ID 是否已注册
     */
    public boolean isRegistered(@NonNull NamespacedKey id) {
        return idToConstructor.containsKey(id);
    }

    /**
     * 获取所有已注册的规则 ID
     */
    public @NonNull Collection<NamespacedKey> getAllIds() {
        return idToConstructor.keySet();
    }

    /**
     * 根据规则 ID 创建规则实例
     * @return 成功时返回 ValueResult 包含规则实例，失败时返回失败消息
     */
    public @NonNull CreateRuleResult createRule(@NonNull NamespacedKey id) {
        Constructor<? extends MatchRule> constructor = idToConstructor.get(id);
        if (constructor == null) {
            return CreateRuleResult.failure(
                    Component.translatable(TranslateKeys.Rule.Registry.ID_NOT_FOUND)
                            .arguments(Component.text(id.toString()))
            );
        }
        try {
            MatchRule rule = constructor.newInstance();
            // 验证实例的 ID 与请求的 ID 一致（防止实现类错误）
            if (!rule.getElementMeta().id().equals(id)) {
                return CreateRuleResult.failure(
                        Component.translatable(TranslateKeys.Rule.Registry.ID_MISMATCH)
                                .arguments(Component.text(
                                        rule.getElementMeta().id().toString()
                                ))
                );
            }
            return CreateRuleResult.success(rule);
        } catch (Exception e) {
            return CreateRuleResult.failure(
                    Component.translatable(TranslateKeys.Rule.Registry.INSTANTIATION_FAILED)
                            .arguments(
                                    Component.text(id.toString()),
                                    Component.text(e.getMessage())
                            )
            );
        }
    }

    /**
     * 获取指定类型规则的id
     *
     * @param ruleType 指定规则类型
     *
     * @return 指定规则类型的id，如果此类规则未注册则返回 {@code null}
     * */
    public @Nullable NamespacedKey ruleClassToId(@NonNull Class<? extends MatchRule> ruleType) {
        return ruleClassToId.get(ruleType);
    }

    public static final class CreateRuleResult extends ValueResult<MatchRule> {
        private CreateRuleResult(boolean success, @Nullable Component displayMessage, @Nullable MatchRule rule) {
            super(success, displayMessage, rule);
        }

        public static CreateRuleResult success(MatchRule rule) {
            return new CreateRuleResult(true, null, rule);
        }

        public static CreateRuleResult failure(Component displayMessage) {
            return new CreateRuleResult(false, displayMessage, null);
        }
    }
}
