package io.github.qingranqae.voidairrace.core.result.matchrule;

import io.github.qingranqae.voidairrace.core.result.base.OperationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public final class ManagerEnableRuleResult extends OperationResult {
    private ManagerEnableRuleResult(boolean success, @Nullable Component displayMessage) {
        super(success, displayMessage);
    }

    public static ManagerEnableRuleResult success() {
        return new ManagerEnableRuleResult(true, null);
    }

    public static ManagerEnableRuleResult failure(Component displayMessage) {
        return new ManagerEnableRuleResult(false, displayMessage);
    }
}