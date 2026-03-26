package io.github.qingranqae.voidairrace.core.result.match;

import io.github.qingranqae.voidairrace.core.result.base.OperationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public final class CoordinatorStopMatchResult extends OperationResult {
    private CoordinatorStopMatchResult(boolean success, @Nullable Component displayMessage) {
        super(success, displayMessage);
    }

    public static CoordinatorStopMatchResult success() {
        return new CoordinatorStopMatchResult(true, null);
    }

    public static CoordinatorStopMatchResult failure(Component displayMessage) {
        return new CoordinatorStopMatchResult(false, displayMessage);
    }
}