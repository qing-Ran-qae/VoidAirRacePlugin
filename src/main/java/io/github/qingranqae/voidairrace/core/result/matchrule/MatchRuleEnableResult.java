package io.github.qingranqae.voidairrace.core.result.matchrule;

import io.github.qingranqae.voidairrace.core.result.base.OperationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class MatchRuleEnableResult extends OperationResult {
    public MatchRuleEnableResult(boolean success, @Nullable Component displayMessage) {
        super(success, displayMessage);
    }

    public static MatchRuleEnableResult success() {
        return new MatchRuleEnableResult(true, null);
    }

    public static MatchRuleEnableResult failure(Component displayMessage) {
        return new MatchRuleEnableResult(false, displayMessage);
    }
}