package io.github.qingranqae.voidairrace.core.result.match;

import io.github.qingranqae.voidairrace.core.result.base.OperationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class MatchOnStartResult extends OperationResult {
    private MatchOnStartResult(boolean success, @Nullable Component displayMessage) {
        super(success, displayMessage);
    }

    public static MatchOnStartResult success() {
        return new MatchOnStartResult(true, null);
    }

    public static MatchOnStartResult failure(Component displayMessage) {
        return new MatchOnStartResult(false, displayMessage);
    }
}
