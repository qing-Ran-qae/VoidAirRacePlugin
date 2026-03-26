package io.github.qingranqae.voidairrace.core.result.arena;

import io.github.qingranqae.voidairrace.core.result.base.OperationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class LoadArenaWorldResult extends OperationResult {
    private LoadArenaWorldResult(boolean success, @Nullable Component displayMessage) {
        super(success, displayMessage);
    }

    public static LoadArenaWorldResult success() {
        return new LoadArenaWorldResult(true, null);
    }

    public static LoadArenaWorldResult failure(Component displayMessage) {
        return new LoadArenaWorldResult(false, displayMessage);
    }
}
