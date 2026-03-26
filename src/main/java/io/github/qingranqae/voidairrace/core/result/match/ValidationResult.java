package io.github.qingranqae.voidairrace.core.result.match;

import io.github.qingranqae.voidairrace.core.result.base.OperationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public final class ValidationResult extends OperationResult {
    private ValidationResult(boolean success, @Nullable Component displayMessage) {
        super(success, displayMessage);
    }

    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult failure(Component displayMessage) {
        return new ValidationResult(false, displayMessage);
    }
}