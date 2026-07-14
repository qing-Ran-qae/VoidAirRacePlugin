package io.github.hhn756.voidairrace.core.matchrule;

import io.github.hhn756.voidairrace.constants.Namespace;
import io.github.hhn756.voidairrace.constants.TranslateKeys;
import io.github.hhn756.voidairrace.core.custom.GameElement;
import io.github.hhn756.voidairrace.core.custom.GameElementMeta;
import io.github.hhn756.voidairrace.core.match.Match;
import io.github.hhn756.voidairrace.result.base.OperationResult;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface MatchRule extends GameElement {
    /**
     * 规则类游戏元素的默认元数据
     * */
    GameElementMeta defaultMeta = new GameElementMeta(
            Namespace.of("default"),
            List.of(Component.translatable(
                    TranslateKeys.MatchComp.CompBase.DEFAULT_NAME
            )),
            null, null, null, null, null
    );

    /**
     * 获取规则的所有标签
     */
    @NonNull Collection<String> getTags();

    /**
     * 规则被启用时调用（例如比赛开始时或中途添加）
     *
     * @param match 当前比赛实例
     *
     * @return 如果返回失败的结果那么将会取消这次启用规则操作
     */
    default @NonNull RuleEnableResult onEnable(@NonNull Match match) {
        return RuleEnableResult.success();
    }

    /**
     * 规则被禁用时调用（例如比赛结束或中途移除）
     */
    default void onDisable(@NonNull Match match) {}

    /**
     * 规则加载时每游戏刻自动执行一次（注意：避免耗时操作）
     */
    default void tick(@NonNull Match match) {}

    // ------ 结果类型 ------

    class RuleEnableResult extends OperationResult {
        public RuleEnableResult(boolean success, @Nullable Component displayMessage) {
            super(success, displayMessage);
        }

        public static @NonNull RuleEnableResult success() {
            return new RuleEnableResult(true, null);
        }

        public static @NonNull RuleEnableResult failure(@Nullable Component displayMessage) {
            return new RuleEnableResult(false, displayMessage);
        }
    }
}
