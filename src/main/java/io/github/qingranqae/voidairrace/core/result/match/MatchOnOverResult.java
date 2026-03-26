package io.github.qingranqae.voidairrace.core.result.match;

import io.github.qingranqae.voidairrace.core.result.base.OperationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class MatchOnOverResult extends OperationResult {
    private MatchOnOverResult(boolean success, @Nullable Component displayMessage) {
        super(success, displayMessage);
    }

    public static MatchOnOverResult success() {
        return new MatchOnOverResult(true, null);
    }

    public static MatchOnOverResult failure(Component displayMessage) {
        return new MatchOnOverResult(false, displayMessage);
    }
}
