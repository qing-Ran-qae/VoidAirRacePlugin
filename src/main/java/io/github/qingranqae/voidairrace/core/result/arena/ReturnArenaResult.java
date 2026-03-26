package io.github.qingranqae.voidairrace.core.result.arena;

import io.github.qingranqae.voidairrace.core.result.base.OperationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class ReturnArenaResult extends OperationResult {
    private ReturnArenaResult(boolean success, @Nullable Component displayMessage) {
        super(success, displayMessage);
    }

    public static ReturnArenaResult success() {
        return new ReturnArenaResult(true, null);
    }

    public static ReturnArenaResult failure(Component displayMessage) {
        return new ReturnArenaResult(false, displayMessage);
    }
}
