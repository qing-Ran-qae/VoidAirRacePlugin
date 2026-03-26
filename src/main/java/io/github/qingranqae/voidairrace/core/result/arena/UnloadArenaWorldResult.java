package io.github.qingranqae.voidairrace.core.result.arena;

import io.github.qingranqae.voidairrace.core.result.base.OperationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class UnloadArenaWorldResult extends OperationResult {
    private UnloadArenaWorldResult(boolean success, @Nullable Component displayMessage) {
        super(success, displayMessage);
    }

    public static UnloadArenaWorldResult success() {
        return new UnloadArenaWorldResult(true, null);
    }

    public static UnloadArenaWorldResult failure(Component displayMessage) {
        return new UnloadArenaWorldResult(false, displayMessage);
    }
}
