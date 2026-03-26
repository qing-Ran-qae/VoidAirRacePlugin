package io.github.qingranqae.voidairrace.core.result.match;

import io.github.qingranqae.voidairrace.core.result.base.OperationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public final class CoordinatorStartMatchResult extends OperationResult {
    private CoordinatorStartMatchResult(boolean success, @Nullable Component displayMessage) {
        super(success, displayMessage);
    }

    public static CoordinatorStartMatchResult success() {
        return new CoordinatorStartMatchResult(true, null);
    }

    public static CoordinatorStartMatchResult failure(Component displayMessage) {
        return new CoordinatorStartMatchResult(false, displayMessage);
    }
}