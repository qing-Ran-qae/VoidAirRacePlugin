package io.github.qingranqae.voidairrace.core.result.arena;

import io.github.qingranqae.voidairrace.core.result.base.OperationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class LoadArenaResult extends OperationResult {
    private LoadArenaResult(boolean success, @Nullable Component displayMessage) {
        super(success, displayMessage);
    }

    public static LoadArenaResult success() {
        return new LoadArenaResult(true, null);
    }

    public static LoadArenaResult failure(Component displayMessage) {
        return new LoadArenaResult(false, displayMessage);
    }
}
